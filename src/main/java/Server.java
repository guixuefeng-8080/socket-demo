import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

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

                //获取socket输入流
                InputStream inputStream = socket.getInputStream();

                do {
                    byte[] buff = new byte[128];
                    //读取客户端信息
                    int length = inputStream.read(buff);
                    ByteBuffer byteBuffer = ByteBuffer.wrap(buff,0,length);
                    byte a = byteBuffer.get();
                    char b = byteBuffer.getChar();
                    int c = byteBuffer.getInt();
                    double d = byteBuffer.getDouble();
                    float e = byteBuffer.getFloat();
                    boolean f = byteBuffer.get()==1;
                    int pos = byteBuffer.position();
                    String str = new String(buff,pos,length-pos-1);
                    if(length>0){
                        System.out.println("收到数据：length "+length+"数量："+a+"\n"+b+"\n"+c+"\n"+d+"\n"+d+"\n"+e+"\n"+f+str);
                        outputStream.write(length);
                    }else {
                        flag = false;
                    }
                }while (flag);
                outputStream.close();
                inputStream.close();
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
