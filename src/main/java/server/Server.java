package server;

import constants.TCPConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Server {
    public static void main(String[] args) throws IOException {
        //给client提供serverInfo
        //服务器端要监听的端口号
        UDPProvider.start(TCPConstants.PORT_SERVER);
        //建立TCPServer与client建立TCP链接
        TCPServer tcpServer = new TCPServer(TCPConstants.PORT_SERVER);
        boolean isSuccess = tcpServer.start();
        if(!isSuccess){
            System.out.println("TCPServer.start failed");
        }
        InputStream inputStream = System.in;
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        //服务端给 已经链接到服务端的所有的客户端发送消息
        String str = "";
        do {
            str = bufferedReader.readLine();
            tcpServer.broadCase(str);
        }while (!"00bye00".equalsIgnoreCase(str));
        //401
        UDPProvider.stop();
        tcpServer.stop();
    }
}
