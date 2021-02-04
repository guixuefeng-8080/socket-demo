package server;

import server.handler.ClientHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPServer {

    private final int port;
    private ClientListener clientListener;
    private List<ClientHandler> clientHandlers = new ArrayList<>();

    public TCPServer(int port) {
        this.port = port;
    }

    public boolean start(){
        try {
            clientListener = new ClientListener(port);
            clientListener.start();
        } catch (IOException e) {
            e.printStackTrace();
            return  false;
        }
        return true;
    }
    public void stop(){
        if(clientListener!=null){
            clientListener.exit();
        }
        clientListener = null;
        for (ClientHandler clientHandler:clientHandlers){
            clientHandler.exit();
        }
        clientHandlers.clear();
    }

    //给所有已经链接客户端发送消息
    public void broadCase(String str) {
        for (ClientHandler clientHandler:clientHandlers){
            clientHandler.send(str);
        }
    }

    public  class ClientListener extends Thread{

        private ServerSocket serverSocket;
        private boolean done = false;

        public ClientListener(int port) throws IOException {
            serverSocket = new ServerSocket(port);
            //创建了服务端socket并信息
            System.out.println("server.ip:"+serverSocket.getInetAddress().getHostAddress()+"\tserver.port"+
                    serverSocket.getLocalPort());
        }

        @Override
        public void run() {
            super.run();
            System.out.println("TCP-ClientListener run");
            do{

                Socket client;
                try {
                    client= serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(client, handler -> clientHandlers.remove(handler));
                    clientHandlers.add(clientHandler);
                    //读 发 分离
                    //读取数据并打印
                    clientHandler.readToPrint(client);
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
            }while (!done);
            System.out.println("服务器端已经关闭。");
        }
        void exit(){
            done = true;
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
