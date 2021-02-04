package server.handler;

import client.net.qiujuer.clink.CloseUtils;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler {

    private final Socket clientSocket;
    private final ClientReadHandler clientReadHandler;
    private final ClientWriteHandler clientWriteHandler;
    private final CloseNotify closeNotify;

    public ClientHandler(Socket clientSocket, CloseNotify closeNotify) throws IOException {
        this.clientSocket = clientSocket;
        this.clientReadHandler = new ClientReadHandler(clientSocket.getInputStream());
        this.clientWriteHandler = new ClientWriteHandler(clientSocket.getOutputStream());
        this.closeNotify = closeNotify;
        int clientPort = clientSocket.getPort();
        String ip = clientSocket.getInetAddress().getHostAddress();
        System.out.println("有新的客户端接入：clientPort-"+clientPort+"\tclientIP-"+ip);
    }
    public void exit() {
        clientReadHandler.exit();
        clientWriteHandler.exit();
        CloseUtils.close(clientSocket);
        System.out.println("client 已经退出，ip:"+clientSocket.getInetAddress().getHostAddress()+"\tport"+clientSocket.getPort());
    }
    public  void exitSelf() {
        exit();
        closeNotify.onCloseNotify(this);
    }

    public void send(String str) {
        clientWriteHandler.send(str);
    }

    public void readToPrint(Socket client) {
        clientReadHandler.start();
    }

    public class ClientReadHandler extends Thread{
        private boolean done = false;
        private InputStream inputStream;

        public ClientReadHandler( InputStream inputStream){
            this.inputStream = inputStream;
        }
        @Override
        public void run() {
            super.run();
            try {
                //得到输入流
                InputStreamReader reader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(reader);
                do {
                    String str = bufferedReader.readLine();
                    if(str==null){
                        System.out.println("客户端已经无法读取数据");
                        ClientHandler.this.exitSelf();
                        break;
                    } else {
                        System.out.println("服务端收到数据："+str);
                    }
                }while (!done);
            } catch (IOException e) {
                ClientHandler.this.exitSelf();
                System.out.println("链接异常关闭！");
            }finally {
                CloseUtils.close(inputStream);
            }
        }
        public void exit(){
            done = true;
            CloseUtils.close(inputStream);
        }
    }

    public class ClientWriteHandler {
        private boolean done = false;
        private PrintStream printStream;
        private final ExecutorService executorService;
        public ClientWriteHandler(OutputStream outputStream) {
            this.printStream = new PrintStream(outputStream);
            this.executorService = Executors.newSingleThreadExecutor();
        }

        public void exit(){
            done = true;
            executorService.shutdownNow();
            CloseUtils.close(printStream);
        }

        void send(String str) {
            executorService.execute(new WriteRunable(str));
        }
        class WriteRunable implements Runnable{
            private final String msg;

            WriteRunable(String msg) {
                this.msg = msg;
            }
            @Override
            public void run() {
                ClientWriteHandler.this.printStream.println(msg);
            }
        }
    }
    public interface CloseNotify{
        void onCloseNotify(ClientHandler handler);
    }
}
