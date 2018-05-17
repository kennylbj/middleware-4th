package org.kenny.agent.handlers.agent;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.kenny.agent.domain.AgentRequest;
import org.kenny.agent.handlers.dubbo.DubboClientInitializer;

public class AgentServerHandler extends SimpleChannelInboundHandler<AgentRequest> {

    private Channel outboundChannel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // System.out.println("init AgentServerHandler by tid: " + Thread.currentThread().getId());

        Channel inboundChannel = ctx.channel();
        // disable auto read till connection complete
        inboundChannel.config().setAutoRead(false);

        // Start the connection attempt
        Bootstrap b = new Bootstrap();
        b.group(inboundChannel.eventLoop())
                .channel(ctx.channel().getClass())
                .handler(new DubboClientInitializer(inboundChannel));
        ChannelFuture f = b.connect("localhost", 20880);
        outboundChannel = f.channel();
        f.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                // connection complete start to read first data
                inboundChannel.config().setAutoRead(true);

            } else {
                // Close the connection if the connection attempt has failed.
                inboundChannel.close();
            }
        });

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AgentRequest msg) throws Exception {
        // write request through outbound channel
        outboundChannel.writeAndFlush(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (outboundChannel != null && outboundChannel.isActive()) {
            outboundChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
