package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class UDPSearcher {

    public static void main(String args[]) throws IOException {
        System.out.println("UDPSearcher started.");

        //作为一个搜索者， 不需要指定端口，让系统自动分配
        DatagramSocket searcherSocket = new DatagramSocket();

        //发送
        String sendStr = "Hello Word!";

        final DatagramPacket searcherPacket = new DatagramPacket(sendStr.getBytes(),sendStr.getBytes().length);
        searcherPacket.setAddress(InetAddress.getLocalHost());
        searcherPacket.setPort(20000);
        searcherSocket.send(searcherPacket);
        //构建接收实体
        byte buf[] = new byte[512];
        final DatagramPacket receivePacket = new DatagramPacket(buf,buf.length);
        //接收
        searcherSocket.receive(receivePacket);
        byte data[]= receivePacket.getData();
        String receivesStr = new String(data,data.length);
        String IP = receivePacket.getAddress().getHostAddress();
        int PORT = receivePacket.getPort();
        System.out.println("searcherSocket 接收到 ip: "+IP+"\tport:"+PORT+"\tLength"+data.length+"\tDATA"+receivesStr);

        System.out.println("UDPSearcher finish!");
        searcherSocket.close();
    }
}
