package org.kenny.agent.handlers.dubbo;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.kenny.agent.domain.AgentResponse;

public class DubboClientHandler extends SimpleChannelInboundHandler<AgentResponse> {
    private final Channel inboundChannel;

    public DubboClientHandler(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;

    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, AgentResponse response) {
        // System.out.println("dubbo response " + response.toString());
        // write response to consumer agent
        inboundChannel.writeAndFlush(response);
    }
}
