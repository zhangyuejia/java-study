package com.zhangyj.java.filetransfer;

import com.zhangyj.java.util.AESUtil;
import com.zhangyj.java.util.SocketUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;
import java.util.Scanner;

public class Server {

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(9000);
        System.out.println("服务端启动，等待连接...");
        Socket socket = serverSocket.accept();
        System.out.println("客户端已连接");

        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        // 控制台输入文件夹路径
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入客户端文件夹路径：");
        String folderPath = scanner.nextLine();

        // 发送路径
        SocketUtil.send(out, AESUtil.encrypt(folderPath.getBytes()));

        while (true) {
            String relativePath = new String(
                    AESUtil.decrypt(SocketUtil.receive(in))
            );

            if ("__END__".equals(relativePath)) {
                System.out.println("文件接收完成");
                break;
            }

            byte[] fileContent = AESUtil.decrypt(SocketUtil.receive(in));

            Path savePath = Paths.get(folderPath, relativePath);
            Files.createDirectories(savePath.getParent());
            Files.write(savePath, fileContent);

            System.out.println("保存文件：" + savePath);
        }

        socket.close();
        serverSocket.close();
    }
}
