package com.zhangyj.java.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class SocketUtil {

    public static void send(DataOutputStream out, byte[] data) throws Exception {
        out.writeInt(data.length);
        out.write(data);
        out.flush();
    }

    public static byte[] receive(DataInputStream in) throws Exception {
        int len = in.readInt();
        byte[] data = new byte[len];
        in.readFully(data);
        return data;
    }
}
