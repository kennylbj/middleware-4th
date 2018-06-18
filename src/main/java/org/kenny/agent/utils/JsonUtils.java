package org.kenny.agent.utils;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class JsonUtils {
    private static final SerializeWriter out = new SerializeWriter();
    private static final JSONSerializer serializer = new JSONSerializer(out);
    static {
        serializer.config(SerializerFeature.WriteEnumUsingToString, true);
    }
    public static void writeObject(Object obj, PrintWriter writer) throws IOException {
        serializer.write(obj);
        out.writeTo(writer);
        writer.println();
        writer.flush();
    }
}
