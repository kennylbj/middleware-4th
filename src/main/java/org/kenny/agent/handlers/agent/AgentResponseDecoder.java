package org.kenny.agent.handlers.agent;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import org.kenny.agent.domain.AgentRequest;
import org.kenny.agent.domain.AgentResponse;

import java.util.List;

public class AgentResponseDecoder extends ByteToMessageDecoder{

    private static final int FIX_LEN = 8 + 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < FIX_LEN) {
            return;
        }

        long requestId = in.readLong();
        int result = in.readInt();

        AgentResponse response = new AgentResponse();
        response.setRequestId(requestId);
        response.setResult(result);

        out.add(response);
    }
}
