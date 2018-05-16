package org.kenny.agent.handlers.agent;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.kenny.agent.domain.AgentRequest;

public class AgentRequestEncoder extends MessageToByteEncoder<AgentRequest> {
    @Override
    protected void encode(ChannelHandlerContext ctx, AgentRequest msg, ByteBuf out) throws Exception {
        out.writeLong(msg.getRequestId());
        byte[] service = msg.getService().getBytes();
        out.writeInt(service.length);

        byte[] method = msg.getMethod().getBytes();
        out.writeInt(method.length);

        byte[] parameterTypesString = msg.getParameterTypesString().getBytes();
        out.writeInt(parameterTypesString.length);

        byte[] parameter = msg.getParameter().getBytes();
        out.writeInt(parameter.length);
        out.writeBytes(service);
        out.writeBytes(method);
        out.writeBytes(parameterTypesString);
        out.writeBytes(parameter);
    }
}
