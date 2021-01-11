import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {


    public static void main(String[] args) throws IOException {

        Socket socket = new Socket();
        socket.setSoTimeout(3000);
        //连接本地端口 2000，超时3000ms
        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(),2000),3000);
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
