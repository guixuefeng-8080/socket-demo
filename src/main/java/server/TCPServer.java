package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

    private final int port;
    private ClientListener clientListener;

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
    }
    public static class ClientListener extends Thread{

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
                    ClientHandler clientHandler = new ClientHandler(client);
                    clientHandler.start();
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
        public class ClientHandler extends Thread{

            private Socket clientSocket;
            private boolean done = false;

            public ClientHandler(Socket clientSocket){
                this.clientSocket = clientSocket;
            }
            @Override
            public void run() {
                super.run();
                int clientPort = clientSocket.getPort();
                String ip = clientSocket.getInetAddress().getHostAddress();
                System.out.println("有新的客户端接入：clientPort-"+clientPort+"\tclientIP-"+ip);
                try {
                    //得到输出流
                    OutputStream outputStream = clientSocket.getOutputStream();
                    PrintStream printStream = new PrintStream(outputStream);
                    //得到输入流
                    InputStream inputStream = clientSocket.getInputStream();
                    InputStreamReader reader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(reader);

                     do {
                         String str = bufferedReader.readLine();
                         if("bye".equalsIgnoreCase(str)){
                             done = true;
                             printStream.println("bye");
                         }else {
                             System.out.println("服务端收到数据："+str);
                             printStream.println("服务端回送数据长度："+str.length());
                         }
                     }while (!done);
                    printStream.close();
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("链接异常关闭！");
                }finally {
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("client 已经退出，ip:"+ip+"\tport"+clientPort);
            }
        }
    }
}
