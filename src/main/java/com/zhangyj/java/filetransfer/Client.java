package com.zhangyj.java.filetransfer;

import com.zhangyj.java.util.AESUtil;
import com.zhangyj.java.util.SocketUtil;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;

public class Client {

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("127.0.0.1", 9000);
        System.out.println("已连接服务端");

        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        // 接收文件夹路径
        String rootPath = new String(
                AESUtil.decrypt(SocketUtil.receive(in))
        );

        Path root = Paths.get(rootPath);
        Files.walk(root)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try {
                        String relativePath = root.relativize(path).toString();
                        byte[] content = Files.readAllBytes(path);

                        SocketUtil.send(out,
                                AESUtil.encrypt(relativePath.getBytes()));
                        SocketUtil.send(out,
                                AESUtil.encrypt(content));

                        System.out.println("发送文件：" + relativePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        // 结束标记
        SocketUtil.send(out,
                AESUtil.encrypt("__END__".getBytes()));

        socket.close();
    }
}
