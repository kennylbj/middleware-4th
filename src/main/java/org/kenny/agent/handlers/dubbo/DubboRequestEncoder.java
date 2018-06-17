package org.kenny.agent.handlers.dubbo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.kenny.agent.domain.AgentRequest;
import org.kenny.agent.utils.JsonUtils;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;

public class DubboRequestEncoder extends MessageToByteEncoder<AgentRequest> {
    // magic header.
    private static final short MAGIC = (short) 0xdabb;

    // 11000110 in binary
    private static final short REQUEST_HEAD_FLAGS = (short) 0xc6;

    @Override
    protected void encode(ChannelHandlerContext ctx, AgentRequest msg, ByteBuf out) throws Exception {
        // magic
        out.writeShort(MAGIC);

        // request, two-way, serialization id 6
        out.writeByte(REQUEST_HEAD_FLAGS);

        // status
        out.writeByte(0);

        // request id
        out.writeLong(msg.getRequestId());

        // serialize data to bytes
        // FIXME
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(bos));
        JsonUtils.writeObject("2.0.1", writer);
        JsonUtils.writeObject(msg.getService(), writer);
        JsonUtils.writeObject("", writer); // version
        JsonUtils.writeObject(msg.getMethod(), writer);
        JsonUtils.writeObject(msg.getParameterTypesString(), writer);
        JsonUtils.writeObject(msg.getParameter(), writer);
        // FIXME
        JsonUtils.writeObject(new HashMap<String, String>(), writer);

        // write data length
        int dataLength = bos.size();
        out.writeInt(dataLength);

        // write data
        out.writeBytes(bos.toByteArray());
    }
}
