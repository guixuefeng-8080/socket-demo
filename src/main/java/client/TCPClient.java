package client;

import client.bean.ServerInfo;
import client.net.qiujuer.clink.CloseUtils;

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

        ReadHandler readHandler = new ReadHandler(socket.getInputStream());
        readHandler.start();
        write(socket);
        readHandler.exit();
        socket.close();
        System.out.println("客户端已经退出链接");
    }

    private static void write(Socket socket) throws IOException {
        //键盘的输入流
        InputStream in = System.in;
        InputStreamReader inreader = new InputStreamReader(in);
        BufferedReader reader = new BufferedReader(inreader);
        //socket输出流
        OutputStream outputStream = socket.getOutputStream();
        PrintStream socketPrintStream = new PrintStream(outputStream);
        do {
            String str = reader.readLine();
            if("00bye00".equalsIgnoreCase(str)){
                break;
            }
            socketPrintStream.println(str);
        }while (true);
        reader.close();
        socketPrintStream.close();
    }

    public static class ReadHandler extends Thread{
        private boolean done = false;
        private InputStream inputStream;

        public ReadHandler( InputStream inputStream){
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
                    String str = "";
                    try{
                        str = bufferedReader.readLine();

                    }catch (IOException e){
                        continue;
                    }
                    if(str==null){
                        System.out.println("客户端已经无法读取数据");
                        break;
                    } else {
                        System.out.println("服务端收到数据："+str);
                    }
                }while (!done);
            } catch (Exception e) {
                e.printStackTrace();
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
}
