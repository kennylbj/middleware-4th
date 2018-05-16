package org.kenny.agent.handlers.http;

import com.google.common.base.Splitter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;
import org.kenny.agent.discovery.Discovery;
import org.kenny.agent.discovery.EtcdDiscovery;
import org.kenny.agent.domain.AgentRequest;
import org.kenny.agent.handlers.agent.AgentClientInitializer;
import org.kenny.agent.loadbalancer.LoadBalancer;
import org.kenny.agent.loadbalancer.RoundRobinLoadBalancer;
import org.kenny.agent.domain.Agent;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    // maintain a map between agent endpoint and it's channel
    private final Map<Agent, Channel> agentChannelMap = new HashMap<>();

    private final Discovery discovery = new EtcdDiscovery();

    private final LoadBalancer loadBalancer = new RoundRobinLoadBalancer();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        // ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        // parse http request
        AgentRequest request = parseHttpRequest(msg);
        // System.out.println(request.toString());

        List<Agent> agents = discovery.discover(request.getService());
        Agent agent = loadBalancer.balance(agents);
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
                    .handler(new AgentClientInitializer(inboundChannel));

            ChannelFuture f = b.connect(agent.getHost(), agent.getPort());
            agentChannelMap.put(agent, f.channel());
            f.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
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
            outboundChannel.writeAndFlush(request);
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

        /*
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);

        Attribute serviceAttr = (Attribute) decoder.getBodyHttpData("interface");
        Attribute methodAttr = (Attribute) decoder.getBodyHttpData("method");
        Attribute parameterTypesStringAttr = (Attribute) decoder.getBodyHttpData("parameterTypesString");
        Attribute parameterAttr = (Attribute) decoder.getBodyHttpData("parameter");

        AgentRequest agentRequest = new AgentRequest();
        agentRequest.setService(serviceAttr.getValue());
        agentRequest.setMethod(methodAttr.getValue());
        agentRequest.setParameterTypesString(parameterTypesStringAttr.getValue());
        agentRequest.setParameter(parameterAttr.getValue());

        // release decoder resources
        decoder.destroy();
        */
        return agentRequest;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
