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

        // String service = in.readBytes(serviceLength).toString(CharsetUtil.UTF_8);
        // byte[] serviceByte = new byte[serviceLength];
        // in.readBytes(serviceByte);
        // String service = new String(serviceByte);
        ByteBuf serviceBuf = in.readBytes(serviceLength);
        String service = serviceBuf.toString(CharsetUtil.UTF_8);
        serviceBuf.release();

        // String method = in.readBytes(methodLength).toString(CharsetUtil.UTF_8);
        // byte[] methodByte = new byte[methodLength];
        // in.readBytes(methodByte);
        // String method = new String(methodByte);
        ByteBuf methodBuf = in.readBytes(methodLength);
        String method = methodBuf.toString(CharsetUtil.UTF_8);
        methodBuf.release();

        // String type = in.readBytes(typeLength).toString(CharsetUtil.UTF_8);
        // byte[] typeByte = new byte[typeLength];
        // in.readBytes(typeByte);
        // String type = new String(typeByte);
        ByteBuf typeBuf = in.readBytes(typeLength);
        String type = typeBuf.toString(CharsetUtil.UTF_8);
        typeBuf.release();

        // String parameter = in.readBytes(parameterLength).toString(CharsetUtil.UTF_8);
        // byte[] parameterByte = new byte[parameterLength];
        // in.readBytes(parameterByte);
        // String parameter = new String(parameterByte);
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
