package org.kenny.agent.handlers.agent;

import com.google.common.primitives.Ints;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import org.kenny.agent.domain.AgentResponse;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class AgentClientHandler extends SimpleChannelInboundHandler<AgentResponse> {

    private final Channel inboundChannel;

    public AgentClientHandler(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AgentResponse msg) throws Exception {
        String result = String.valueOf(msg.getResult());
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(result.getBytes()));

        // response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/x-www-form-urlencoded");
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

        inboundChannel.writeAndFlush(response);
    }
}
