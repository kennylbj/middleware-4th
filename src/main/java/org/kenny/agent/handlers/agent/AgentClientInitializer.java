package org.kenny.agent.handlers.agent;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class AgentClientInitializer extends ChannelInitializer<SocketChannel> {
    private final Channel inboundChannel;

    public AgentClientInitializer(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new AgentRequestEncoder());
        p.addLast(new AgentResponseDecoder());
        p.addLast(new AgentClientHandler(inboundChannel));
    }
}
