package org.kenny.agent.handlers;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.kenny.agent.handlers.agent.AgentRequestDecoder;
import org.kenny.agent.handlers.agent.AgentResponseEncoder;
import org.kenny.agent.handlers.agent.AgentServerHandler;

public class ProducerAgentInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new AgentRequestDecoder());
        p.addLast(new AgentResponseEncoder());
        p.addLast(new AgentServerHandler());
    }
}
