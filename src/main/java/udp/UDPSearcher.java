package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class UDPSearcher {

    private static final int LISTENPORT = 30000;
    public static void main(String args[]) throws Exception {
        Listener listener = listen();
        sendBroadcast();
        System.in.read();
        List<Device> devices = listener.getDeviceAndClose();
        for(Device device:devices ){
            System.out.println(device.toString());
        }
    }

    private static Listener listen() throws InterruptedException {
        System.out.println("UDPSearcher start!");
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Listener listener = new Listener(LISTENPORT,countDownLatch);
        listener.start();
        countDownLatch.await();
        return listener;
    }


    private static class Device{
        final int port;
        final String ip;
        final String sn;

        private Device(int port, String ip, String sn) {
            this.port = port;
            this.ip = ip;
            this.sn = sn;
        }

        @Override
        public String toString() {
            return "Device{" +
                    "port=" + port +
                    ", ip='" + ip + '\'' +
                    ", sn='" + sn + '\'' +
                    '}';
        }
    }


    private static class Listener extends Thread {
        private final int listenPort;
        private final CountDownLatch countDownLatch;
        private final List<Device> devices = new ArrayList<>();
        private boolean done = false;
        private DatagramSocket ds  = null;

        public Listener(int listenPort, CountDownLatch countDownLatch) {
            this.listenPort = listenPort;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            super.run();
            countDownLatch.countDown();
            try{
                ds = new DatagramSocket(listenPort);

                while (!done){
                    byte buf[] = new byte[512];
                    final DatagramPacket receivePacket = new DatagramPacket(buf,buf.length);
                    //接收
                    ds.receive(receivePacket);
                    byte data[]= receivePacket.getData();
                    String receivesStr = new String(data,data.length);
                    String IP = receivePacket.getAddress().getHostAddress();
                    int PORT = receivePacket.getPort();
                    System.out.println("searcherSocket 接收到 ip: "+IP+"\tport:"+PORT+"\tLength"+data.length+"\tDATA"+receivesStr);
                    String SN = MessageCreator.parseSN(receivesStr);
                    if(SN!=null){
                        Device d  = new Device(PORT,IP,SN);
                        devices.add(d);
                    }
                }
                System.out.println("UDPSearcher finish!");

            }catch (Exception e){

            }finally {
                close();
            }
        }
        private void close(){
            done = true;
            if(ds!=null){
                ds.close();
                ds = null;
            }
        }
        private List<Device> getDeviceAndClose(){
            close();
            return devices;
        }
    }

    public static void sendBroadcast() throws Exception {
        System.out.println("UDPSearcher sendBroadcast started.");
        DatagramSocket searcherSocket = new DatagramSocket();
        String sendStr = MessageCreator.buildWithPort(LISTENPORT);

        final DatagramPacket searcherPacket = new DatagramPacket(sendStr.getBytes(),sendStr.getBytes().length);
        searcherPacket.setAddress(InetAddress.getByName("255.255.255.255"));
        searcherPacket.setPort(20000);
        searcherSocket.send(searcherPacket);
        searcherSocket.close();
        System.out.println("UDPSearcher sendBroadcast finish.");

    }
}
