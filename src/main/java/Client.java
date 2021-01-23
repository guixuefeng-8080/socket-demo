import java.io.*;
import java.net.*;

public class Client {

    public static final int PORT = 20000;
    public static final int PORT_LOCATION = 20001;

    public static void main(String[] args) throws IOException {



        Socket socket = creatSocket();
        initSocket(socket);
        //客户端连接
        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(),PORT),3000);
        System.out.println("已经连接成功，并进入后续流程~");
        System.out.println("客户端信息："+socket.getLocalAddress()+"P:"+socket.getLocalPort());
        System.out.println("服务器端信息："+socket.getInetAddress()+"P:"+socket.getPort());
        try{
            //发送接受数据
            todo(socket);
        }catch (Exception e){
            System.out.println("异常关闭~");
        }
        //资源释放
        socket.close();
        System.out.println("客户端关闭~");
    }

    private static void initSocket(Socket socket) throws SocketException {
        //设置 读取 超时 时间
        socket.setSoTimeout(2000);
        //正常情况下，端口一旦被bind即使连接关闭，2min之内不能被重新bind，而设置为true的话，一旦连接完全关闭，端口就可以重新分配
        socket.setReuseAddress(true);
        //是否开启Nagle算法。优化网络空间，减少ACK次数。不是每次数据片收到后都发ACK，是攒到一定数量后一起发ACK
        socket.setTcpNoDelay(true);
        //心跳检测，2H内没有数据发送，则会发送一条数据，如果没有收到ACK则认为连接断开，会抛出异常
        socket.setKeepAlive(true);
        //对于close关闭操作怎样进行
        //1.false 0,默认。close时立即返回；底层系统接管输出流，将缓冲区的数据发送完成。
        //2.true 0,close时立即返回，缓冲区数据抛弃，直接发送RST结束命令到对放，无需经过2个MSL(数据等待时间)
        //3.true 2000,close时阻塞2S,随后按2.处理
        socket.setSoLinger(true,2000);
        //设置发送和接收缓冲区大小//默认32K
        socket.setReceiveBufferSize(64*1024*1024);
        socket.setSendBufferSize(64*1024*1024);
        //设置性能参数  连接时间，延迟，带宽的比例
        //如果建立的是一个比较长的链接，比如要传个文件，需要传一会。则connectionTime权重要设的高一些
        //如果对延迟不太能接受，要及时的送达，则把latency的权重设置的高一些
        //如果能接受延迟，则要把带宽提高，第一个包组装完成先不发，等第二个一起发，组包发送，带宽利用率较高
        socket.setPerformancePreferences(1,1,1);
    }

    private static Socket creatSocket() throws IOException {

        //代理模式
                                //代理模式       //代理的IP及端口
        //Proxy proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress("172.31.150.66",2000));
        //Socket socket = new Socket(proxy);

        //无代理模式
        //创建一个socket，并且直接连接到本地服务端20000端口上
        //Socket socket = new Socket(Inet4Address.getLocalHost(),PORT);
                                   //远程的地址                //远程的端口  //本地的地址            //本地的端口
        //Socket socket = new Socket(Inet4Address.getLocalHost(),PORT,Inet4Address.getLocalHost(),PORT_LOCATION);
        //等效
        Socket socket = new Socket();
        socket.bind(new InetSocketAddress(Inet4Address.getLocalHost(),PORT_LOCATION));
        return socket;
    }

    private static void todo(Socket client)throws IOException{
        //构建键盘输入流
        InputStream in  = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));

        //获取Socket输出流，转打印流
        OutputStream outputStream = client.getOutputStream();
        PrintStream socketPrintStream = new PrintStream(outputStream);

        //获取Socket输入流
        InputStream inputStream = client.getInputStream();
        BufferedReader socketBufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        boolean flag = true;
        do {
            //键盘读取一行，发送到服务器端
            String str = input.readLine();
            socketPrintStream.println(str);

            //从服务器读取一行
            String lineStr = socketBufferedReader.readLine();
            if("bye".equalsIgnoreCase(lineStr)){
                flag = false;
            }else {
                System.out.println(lineStr);
            }
        }while (flag);
        socketPrintStream.close();
        socketBufferedReader.close();
    }
}
