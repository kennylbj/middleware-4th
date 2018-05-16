package org.kenny.agent.handlers.dubbo;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class DubboClientInitializer extends ChannelInitializer<SocketChannel> {
    private final Channel inboundChannel;

    public DubboClientInitializer(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new DubboRequestEncoder());
        p.addLast(new DubboResponseDecoder());
        p.addLast(new DubboClientHandler(inboundChannel));
    }
}
