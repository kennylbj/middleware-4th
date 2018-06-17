package org.kenny.agent.handlers;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.kenny.agent.discovery.Discovery;
import org.kenny.agent.handlers.http.HttpServerHandler;

public class ConsumerAgentInitializer extends ChannelInitializer<SocketChannel> {

    private final Discovery discovery;
    public ConsumerAgentInitializer(Discovery discovery) {
        this.discovery = discovery;
    }
    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(2 * 1024));
        p.addLast(new HttpServerHandler(discovery));
    }
}
