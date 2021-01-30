package server;

import constants.TCPConstants;

import java.io.IOException;

public class Server {
    public static void main(String[] args){
                             //服务器端要监听的端口号
        ServerProvider.start(TCPConstants.PORT_SERVER);
        try {
            System.in.read();
        }catch (IOException e){
            e.getMessage();
        }
        ServerProvider.stop();
    }
}
