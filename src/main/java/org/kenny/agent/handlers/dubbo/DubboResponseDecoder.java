package org.kenny.agent.handlers.dubbo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import org.kenny.agent.domain.AgentResponse;
import java.util.List;

public class DubboResponseDecoder extends ByteToMessageDecoder {
    // header length.
    private static final int HEADER_LENGTH = 4 + 8 + 4;


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < HEADER_LENGTH) {
            return;
        }

        in.markReaderIndex();

        // skip header flag part
        in.skipBytes(4);

        long requestId = in.readLong();
        int dataLength = in.readInt();

        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }

        // skip response value type and char \t
        in.skipBytes(2);

        // read result
        int resultLength = dataLength - 3;
        ByteBuf resultBuf = in.readBytes(resultLength);
        String result = resultBuf.toString(CharsetUtil.UTF_8);
        resultBuf.release();

        // skip tailing char \t
        in.skipBytes(1);

        AgentResponse response = new AgentResponse();
        response.setRequestId(requestId);
        response.setResult(Integer.parseInt(result));

        out.add(response);
    }
}