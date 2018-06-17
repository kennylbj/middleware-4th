package org.kenny.agent.handlers.agent;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.ReferenceCountUtil;
import org.kenny.agent.ThreadLocalHolder;
import org.kenny.agent.domain.AgentResponse;

import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class AgentClientHandler extends SimpleChannelInboundHandler<AgentResponse> {
    private final ThreadLocalHolder holder = ThreadLocalHolder.getInstance();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AgentResponse msg) throws Exception {
        String result = String.valueOf(msg.getResult());
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(result.getBytes()));

        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

        Map<Long, Channel> threadLocalMap = holder.getIdChannelMapLocal().get();
        Channel inboundChannel = threadLocalMap.get(msg.getRequestId());
        if (inboundChannel != null && inboundChannel.isActive()) {
            // write data back to original channel
            inboundChannel.writeAndFlush(response);

            // remove this item
            threadLocalMap.remove(msg.getRequestId());
        } else {
            ReferenceCountUtil.release(msg);
        }
    }
}
