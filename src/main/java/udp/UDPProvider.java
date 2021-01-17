package udp;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * UDP 提供者，用于提供服务
 */
public class UDPProvider {

    public static void main(String args[]) throws IOException {
        System.out.println("UDPProvider started.");

        //作为一个接收者，指定一个端口 监听
        DatagramSocket providerSocket = new DatagramSocket(20000);
        //构建接收实体
        byte buf[] = new byte[512];
        final DatagramPacket receivePacket = new DatagramPacket(buf,buf.length);
        //接收
        providerSocket.receive(receivePacket);
        //打印接收到的信息
        String IP = receivePacket.getAddress().getHostAddress();
        int PORT = receivePacket.getPort();
        int length = receivePacket.getLength();
        String data = new String(receivePacket.getData(),0,length);
        System.out.println("UDPProvider 接收到 ip: "+IP+"\tport:"+PORT+"\tLength"+length+"\tDATA"+data);

        String sendStr = "UDPProvider Receive data with"+length;
        byte[] udpProviderSendByte = sendStr.getBytes();
        DatagramPacket sendGramPacket = new DatagramPacket(udpProviderSendByte,0,udpProviderSendByte.length,
                receivePacket.getAddress(),PORT);
        providerSocket.send(sendGramPacket);

        System.out.println("UDPProvider finish!");
        providerSocket.close();
    }
}
