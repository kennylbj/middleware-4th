package org.kenny.agent.handlers.agent;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import org.kenny.agent.domain.AgentRequest;

import java.util.List;

public class AgentRequestDecoder extends ByteToMessageDecoder {
    private static final int FIX_LEN = 8 + 4 * 4;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < FIX_LEN) {
            return;
        }

        in.markReaderIndex();
        long requestId = in.readLong();
        int serviceLength = in.readInt();
        int methodLength = in.readInt();
        int typeLength = in.readInt();
        int parameterLength = in.readInt();

        if (in.readableBytes() < (serviceLength + methodLength + typeLength + parameterLength)) {
            in.resetReaderIndex();
            return;
        }

        ByteBuf serviceBuf = in.readBytes(serviceLength);
        String service = serviceBuf.toString(CharsetUtil.UTF_8);
        serviceBuf.release();

        ByteBuf methodBuf = in.readBytes(methodLength);
        String method = methodBuf.toString(CharsetUtil.UTF_8);
        methodBuf.release();

        ByteBuf typeBuf = in.readBytes(typeLength);
        String type = typeBuf.toString(CharsetUtil.UTF_8);
        typeBuf.release();

        ByteBuf parameterBuf = in.readBytes(parameterLength);
        String parameter = parameterBuf.toString(CharsetUtil.UTF_8);
        parameterBuf.release();

        AgentRequest request = new AgentRequest();
        request.setRequestId(requestId);
        request.setService(service);
        request.setMethod(method);
        request.setParameterTypesString(type);
        request.setParameter(parameter);

        out.add(request);
    }
}
