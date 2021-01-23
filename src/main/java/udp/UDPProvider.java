package udp;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.UUID;

/**
 * UDP 提供者，用于提供服务
 */
public class UDPProvider {

    public static void main(String args[]) throws IOException {

        String sn = UUID.randomUUID().toString();
        Provider provider = new Provider(sn);
        provider.run();

        //读取任意字符退出
        System.in.read();
        provider.exit();
    }

    private static class  Provider extends Thread{

        private String sn;
        private boolean done = false;
        private DatagramSocket ds = null;
        public Provider(String sn) {
            super();
            this.sn = sn;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("UDPProvider started.");
            try{
                ds = new DatagramSocket(20000);
                while(!done){
                    //构建接收实体
                    byte buf[] = new byte[512];
                    final DatagramPacket receivePacket = new DatagramPacket(buf,buf.length);
                    //接收
                    ds.receive(receivePacket);
                    //打印接收到的信息
                    String IP = receivePacket.getAddress().getHostAddress();
                    int PORT = receivePacket.getPort();
                    int length = receivePacket.getLength();
                    String data = new String(receivePacket.getData(),0,length);
                    System.out.println("UDPProvider 接收到 ip: "+IP+"\tport:"+PORT+"\tLength"+length+"\tDATA"+data);

                    int port = MessageCreator.parsePort(data);
                    if(port!=-1){
                        String sendMessage = MessageCreator.buildWithPort(port);
                        byte[] udpProviderSendByte = sendMessage.getBytes();
                        DatagramPacket sendGramPacket = new DatagramPacket(udpProviderSendByte,0,udpProviderSendByte.length,
                                receivePacket.getAddress(),PORT);
                        ds.send(sendGramPacket);
                    }
                }
            }catch (Exception e){
            }finally {
                exit();
            }
        }


        public void exit(){
            done = true;
            if(ds!=null){
                System.out.println(ds.getInetAddress().getHostAddress()+"\t连接断开！");
                ds.close();
                ds = null;
            }
        }
    }
}
