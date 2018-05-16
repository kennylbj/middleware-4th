package org.kenny.agent;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ResourceLeakDetector;
import org.kenny.agent.handlers.ConsumerAgentInitializer;
import org.kenny.agent.handlers.ProducerAgentInitializer;

public class Main {
    private static final int PORT = Integer.parseInt(System.getProperty("port","8080"));
    private static final String CONSUMER = "consumer";
    private static final String PRODUCER = "producer";
    public static void main(String[] args) throws Exception {
        String type = System.getProperty("type", "consumer");
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);

            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO));
            if (CONSUMER.equals(type)) {
                b.childHandler(new ConsumerAgentInitializer());
            } else if (PRODUCER.equals(type)) {
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
