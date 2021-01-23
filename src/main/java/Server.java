import java.io.*;
import java.net.*;

public class Server {
    public static final int PORT = 20000;
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = creatServerSocket();
        initServerSocket(serverSocket);
        //backlog:允许等待的最大连接队列，第51个客户端连接时将有异常
        serverSocket.bind(new InetSocketAddress(Inet4Address.getLocalHost(),PORT),50);
        System.out.println("服务端准备就绪~");
        System.out.println("服务端信息："+serverSocket.getInetAddress()+"P:"+serverSocket.getLocalPort());

        while (true){

            //等待客户端的连接
            Socket client = serverSocket.accept();
            //客户端构建异步线程
            ClientHandle clientHandle = new ClientHandle(client);
            //启动线程
            clientHandle.start();

        }
    }

    private static void initServerSocket(ServerSocket serverSocket) throws SocketException {
        //close时IP和端口并没有释放，true是立刻释放
        serverSocket.setReuseAddress(true);
        //设置接收缓冲区大小,即服务端接收到了一个客户端时给这个客户端设置了接收缓冲区大小//需要在拿到客户端之前设置
        serverSocket.setReceiveBufferSize(64*1024*1024);
        //设置serverSocket的accept的超时时间//超时则会抛异常，如果时无限循环则可以处理异常后再进行accept//一般不设置超时时间
        //serverSocket.setSoTimeout(2000);
        //设置性能参数//服务端得到客户端之前设置，所以只能设置到serverSocket上
        serverSocket.setPerformancePreferences(1,1,1);
    }

    private static ServerSocket creatServerSocket() throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        //等效
        //创建本地的服务端Socket，并绑定
        //ServerSocket serverSocket = new ServerSocket(PORT_LOCATION,50);
        return serverSocket;
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
