package server;

import constants.TCPConstants;

import java.io.IOException;

public class Server {
    public static void main(String[] args){
        //给client提供serverInfo
        //服务器端要监听的端口号
        UDPProvider.start(TCPConstants.PORT_SERVER);
        //建立TCPServer与client建立TCP链接
        TCPServer tcpServer = new TCPServer(TCPConstants.PORT_SERVER);
        boolean isSuccess = tcpServer.start();
        if(!isSuccess){
            System.out.println("TCPServer.start failed");
        }
        try {
            System.in.read();
        }catch (IOException e){
            e.getMessage();
        }
        //401
        UDPProvider.stop();
        tcpServer.stop();
    }
}
