package org.kenny.agent.handlers.agent;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.kenny.agent.domain.AgentResponse;

public class AgentResponseEncoder extends MessageToByteEncoder<AgentResponse> {
    @Override
    protected void encode(ChannelHandlerContext ctx, AgentResponse msg, ByteBuf out) throws Exception {
        out.writeLong(msg.getRequestId());
        out.writeInt(msg.getResult());
    }
}
