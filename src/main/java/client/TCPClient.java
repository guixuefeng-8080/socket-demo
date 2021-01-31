package client;

import client.bean.ServerInfo;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TCPClient {

    public static void linkWith(ServerInfo serverInfo) throws IOException {
        Socket socket = new Socket();
        //读取的超时时间
        socket.setSoTimeout(3000);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(Inet4Address.getByName(serverInfo.getAddress()), serverInfo.getPort());
        socket.connect(inetSocketAddress, 3000);
        System.out.println("已经发起对服务器的连接。。");
        System.out.println("客户端信息：IP:"+socket.getLocalAddress().getHostAddress()+"\tPORT:"+socket.getLocalPort());
        System.out.println("服务端信息：IP:"+socket.getInetAddress().getHostAddress()+"\tPORT:"+socket.getPort());
        todo(socket);
        socket.close();
        System.out.println("客户端已经退出链接");
    }

    private static void todo(Socket socket) throws IOException {
        //键盘的输入流
        InputStream in = System.in;
        InputStreamReader inreader = new InputStreamReader(in);
        BufferedReader reader = new BufferedReader(inreader);
        //socket输出流
        OutputStream outputStream = socket.getOutputStream();
        PrintStream socketPrintStream = new PrintStream(outputStream);
        //socket读取流
        InputStream inputStream = socket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader socketBufferedReader = new BufferedReader(inputStreamReader);
        boolean done = false;
        do {
            String str = reader.readLine();
            socketPrintStream.println(str);

            String echo = socketBufferedReader.readLine();
            if("bye".equalsIgnoreCase(echo)){
                done = true;
            }else {
                System.out.println(echo);
            }

        }while (!done);
        reader.close();
        socketPrintStream.close();
        socketBufferedReader.close();
    }
}
