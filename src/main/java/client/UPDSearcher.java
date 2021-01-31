package client;

import client.bean.ServerInfo;
import client.net.qiujuer.clink.ByteUtils;
import constants.TCPConstants;
import constants.UDPConstants;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class UPDSearcher {

    private static final int LISTENPORT = UDPConstants.PORT_CLIENT_RESPONSE;
    public static ServerInfo searcherServer(int timeout){
        System.out.println("searcherServer start!");
        CountDownLatch receiveLatch = new CountDownLatch(1);
        Listener listener =null;
        try {
            listener = listen(receiveLatch);
            sendBroadcast();
            receiveLatch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("UDPSearcher finish!");
        if(listener==null){
            return null;
        }
        List<ServerInfo> devices = listener.getDeviceAndClose();
        if(devices.size()>0){
            return  devices.get(0);
        }
        return null;
    }
    public static void sendBroadcast() throws Exception {
        System.out.println("sendBroadcast started.");
        //作为搜索方，让系统自动分配端口
        DatagramSocket ds = new DatagramSocket();
        byte[] buffer = new byte[128];
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        byteBuffer.put(UDPConstants.HEADER);
        byteBuffer.putShort((short)1);
        byteBuffer.putInt(LISTENPORT);
        final DatagramPacket requestPacket = new DatagramPacket(byteBuffer.array(),byteBuffer.position()+1);
        requestPacket.setAddress(InetAddress.getByName("255.255.255.255"));
        requestPacket.setPort(TCPConstants.PORT_SERVER);
        ds.send(requestPacket);
        ds.close();
        System.out.println("UDPSearcher sendBroadcast finish.");

    }
    private static Listener listen(CountDownLatch receiveLatch) throws InterruptedException {
        System.out.println("UDPSearcher Listener start!");
        CountDownLatch startDownLatch = new CountDownLatch(1);
                                        //202
        Listener listener = new Listener(LISTENPORT,startDownLatch,receiveLatch);
        listener.start();
        startDownLatch.await();
        return listener;
    }
    private static class Listener extends Thread {
        private final int listenPort;
        private final CountDownLatch startDownLatch;
        private final CountDownLatch receiveDownLatch;
        private final List<ServerInfo> devices = new ArrayList<>();
        private final byte[] buffer = new byte[128];
        private final int minLen = UDPConstants.HEADER.length+2+4;
        private boolean done = false;
        private DatagramSocket ds  = null;

        public Listener(int listenPort, CountDownLatch startDownLatch,CountDownLatch receiveDownLatch) {
            this.listenPort = listenPort;
            this.receiveDownLatch = receiveDownLatch;
            this.startDownLatch = startDownLatch;
        }

        @Override
        public void run() {
            super.run();
            startDownLatch.countDown();
            try{
                ds = new DatagramSocket(listenPort);

                DatagramPacket receivePacket = new DatagramPacket(buffer,buffer.length);
                while (!done){
                    //接收
                    ds.receive(receivePacket);
                    byte data[]= receivePacket.getData();
                    String IP = receivePacket.getAddress().getHostAddress();
                    int PORT = receivePacket.getPort();
                    int dataLen = receivePacket.getLength();
                    System.out.println("UDPSearcher Listener 接收到 ip: "+IP+"\tport:"+PORT+"\tLength"+data.length);
                    ByteBuffer byteBuffer = ByteBuffer.wrap(buffer,UDPConstants.HEADER.length,dataLen);
                    final int cmd = byteBuffer.getShort();
                    boolean isvalid = dataLen>minLen&& ByteUtils.startsWith(data,UDPConstants.HEADER) && cmd==2;
                    System.out.println("isvalid\t"+isvalid);
                    if(!isvalid){
                        continue;
                    }
                    final int serverPort = byteBuffer.getInt();
                    String sn = new String(data,minLen,dataLen-minLen);
                    devices.add(new ServerInfo(sn,serverPort,IP));
                    receiveDownLatch.countDown();
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
        private List<ServerInfo> getDeviceAndClose(){
            close();
            return devices;
        }
    }
}
