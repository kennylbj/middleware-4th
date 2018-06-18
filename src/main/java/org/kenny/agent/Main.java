package org.kenny.agent;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import org.kenny.agent.discovery.Discovery;
import org.kenny.agent.discovery.EtcdDiscovery;
import org.kenny.agent.handlers.ConsumerAgentInitializer;
import org.kenny.agent.handlers.ProducerAgentInitializer;

public class Main {
    // 2000 for consumer agent and 3000 for provider agent
    private static final int PORT = Integer.parseInt(System.getProperty("server.port"));
    private static final String CONSUMER = "consumer";
    private static final String PROVIDER = "provider";

    public static void main(String[] args) throws Exception {
        String type = System.getProperty("type", "consumer");
        EventLoopGroup bossGroup = new EpollEventLoopGroup(1);
        EventLoopGroup workerGroup = new EpollEventLoopGroup();

        Discovery discovery = new EtcdDiscovery();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 600);
            // FIXME switch to epoll Channel
            b.group(bossGroup, workerGroup)
                    .channel(EpollServerSocketChannel.class);
            if (CONSUMER.equals(type)) {
                b.childHandler(new ConsumerAgentInitializer(discovery));
            } else if (PROVIDER.equals(type)) {
                b.childHandler(new ProducerAgentInitializer());
            } else {
                throw new IllegalStateException("Environment variable type should set to provider or consumer.");
            }

            Channel ch = b.bind(PORT).sync().channel();

            System.err.println("Agent start at http://127.0.0.1:" + PORT + '/');

            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
