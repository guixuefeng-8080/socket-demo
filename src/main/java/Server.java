import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException {

        //创建服务端，一般服务端都是在本机，所以只需提供端口。如果本机处于复杂的网络中，需要指定IP
        ServerSocket server = new ServerSocket(2000);

        System.out.println("服务端准备就绪~");
        System.out.println("服务端信息："+server.getInetAddress()+"P:"+server.getLocalPort());

        while (true){

            //等待客户端的连接
            Socket client = server.accept();
            //客户端构建异步线程
            ClientHandle clientHandle = new ClientHandle(client);
            //启动线程
            clientHandle.start();

        }
    }

    private static class ClientHandle extends Thread{

        private Socket socket;
        private boolean flag = true;
        ClientHandle(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("新客户端连接："+this.socket.getInetAddress()+"P: "+this.socket.getPort());

            try {
                //获取socket输出流，转打印流
                OutputStream outputStream = socket.getOutputStream();
                PrintStream socketPrintStream = new PrintStream(outputStream);

                //获取socket输入流
                InputStream inputStream = socket.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader socketBufferedReader = new BufferedReader(inputStreamReader);

                do {
                    //读取客户端信息
                    String str = socketBufferedReader.readLine();
                    if("bye".equalsIgnoreCase(str)){
                        flag = false;
                        socketPrintStream.println("bye");
                    }else {
                        //打印消息，并回送消息长度
                        System.out.println(str);
                        socketPrintStream.println("服务算回送： "+str.length());
                    }

                }while (flag);
                socketBufferedReader.close();
                socketPrintStream.close();
            }catch (Exception e){
                System.out.println("连接异常关闭~");
            }finally {
                //出现异常，或正常关闭，释放资源
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("客户端"+socket.getInetAddress()+"P: "+socket.getPort()+" 已退出~");

        }
    }


}
