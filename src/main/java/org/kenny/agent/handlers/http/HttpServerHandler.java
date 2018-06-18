package org.kenny.agent.handlers.http;

import com.google.common.base.Splitter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;
import org.kenny.agent.ThreadLocalHolder;
import org.kenny.agent.discovery.Discovery;
import org.kenny.agent.domain.AgentRequest;
import org.kenny.agent.handlers.agent.AgentClientInitializer;
import org.kenny.agent.loadbalancer.LoadBalancer;
import org.kenny.agent.loadbalancer.RoundRobinLoadBalancer;
import org.kenny.agent.domain.Agent;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final Discovery discovery;

    private final LoadBalancer loadBalancer = new RoundRobinLoadBalancer();

    private final List<AgentRequest> pendingTasks = new ArrayList<>();

    private final Map<Agent, Channel> agentChannelMap = ThreadLocalHolder.getInstance().getAgentChannelMapLocal().get();

    private final Map<Long, Channel> idChannelMap = ThreadLocalHolder.getInstance().getIdChannelMapLocal().get();

    public HttpServerHandler(Discovery discovery) {
        this.discovery = discovery;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        // parse http request
        AgentRequest request = parseHttpRequest(msg);

        List<Agent> agents = discovery.discover(request.getService());
        Agent agent = loadBalancer.balance(agents);
        if (agent == null) {
            pendingTasks.add(request);
            return;
        }

        Channel inboundChannel = ctx.channel();
        if (!agentChannelMap.containsKey(agent)) {
            // disable auto read till connection complete
            inboundChannel.config().setAutoRead(false);

            // set up connection
            Bootstrap b = new Bootstrap();

            // share the same EventLoop between both Channels.
            // this means all IO for both connected Channels are handled by the same thread.
            b.group(inboundChannel.eventLoop())
                    .channel(ctx.channel().getClass())
                    .handler(new AgentClientInitializer());

            ChannelFuture f = b.connect(agent.getHost(), agent.getPort());
            agentChannelMap.put(agent, f.channel());

            f.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    idChannelMap.put(request.getRequestId(), inboundChannel);

                    // connection complete start to read first data
                    inboundChannel.config().setAutoRead(true);

                    // write data while connection establish
                    future.channel().writeAndFlush(request);
                } else {
                    // close the connection if the connection attempt has failed.
                    inboundChannel.close();
                }
            });
        } else {
            Channel outboundChannel = agentChannelMap.get(agent);
            if (outboundChannel.isWritable()) {
                // execute pending tasks
                pendingTasks.forEach(task -> {
                    outboundChannel.writeAndFlush(task);
                    idChannelMap.put(task.getRequestId(), inboundChannel);
                });
                pendingTasks.clear();

                idChannelMap.put(request.getRequestId(), inboundChannel);
                outboundChannel.writeAndFlush(request);
            } else {
                pendingTasks.add(request);
            }
        }
    }

    private AgentRequest parseHttpRequest(FullHttpRequest request) throws IOException {
        String params = URLDecoder.decode(request.content().toString(CharsetUtil.UTF_8), "UTF-8");
        Map<String, String> pairs = Splitter.on('&').trimResults().withKeyValueSeparator("=").split(params);

        AgentRequest agentRequest = new AgentRequest();
        agentRequest.setService(pairs.get("interface"));
        agentRequest.setMethod(pairs.get("method"));
        agentRequest.setParameterTypesString(pairs.get("parameterTypesString"));
        agentRequest.setParameter(pairs.get("parameter"));

        return agentRequest;
    }

    /*
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        // System.out.println("channel inactive");
        holder.getIdChannelMapLocal().get().entrySet()
                .removeIf(entry -> entry.getValue() != ctx.channel());
    }
    */

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
