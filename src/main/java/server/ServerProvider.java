package server;

import client.net.qiujuer.clink.ByteUtils;
import constants.UDPConstants;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ServerProvider {
    private static Provider PROVIDER_INSTANCE;
    public static void start(int portServer) {
        stop();
        String sn = UUID.randomUUID().toString();
        Provider provider = new Provider(sn,portServer);
        provider.start();
        PROVIDER_INSTANCE = provider;

    }

    public static void stop() {
        if (PROVIDER_INSTANCE!=null){
            PROVIDER_INSTANCE.exit();
            PROVIDER_INSTANCE = null;
        }

    }
    private static class  Provider extends Thread{

        private byte[] sn;
        private int portServer;
        private boolean done = false;
        private DatagramSocket ds = null;
        final byte[] buffer = new byte[128];
        public Provider(String sn,int portServer) {
            super();
            this.sn = sn.getBytes();
            this.portServer = portServer;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("UDPProvider started.");
            try{
                //监听端口
                ds = new DatagramSocket(portServer);
                while(!done){
                    //建立用来接收消息的packet
                    final DatagramPacket receivePacket = new DatagramPacket(buffer,buffer.length);
                    //接收
                    ds.receive(receivePacket);
                    //打印接收到的客户端的信息
                    String IP = receivePacket.getAddress().getHostAddress();
                    int clientPort = receivePacket.getPort();
                    int length = receivePacket.getLength();
                    byte[] data = receivePacket.getData();
                    //                                      校验头     +cmd +port
                    boolean isValid = length>=(UDPConstants.HEADER.length+2+4)&& ByteUtils.startsWith(data,UDPConstants.HEADER);
                    System.out.println("UDPProvider 接收到 ip: "+IP+"\tclientPort:"+clientPort+"\tLength"+length+"\tisvalid"+isValid);
                    if(!isValid){
                        continue;
                    }
                    int index = UDPConstants.HEADER.length;
                    ByteBuffer byteBuffer = ByteBuffer.wrap(buffer,index,length);
                    short cmd = byteBuffer.getShort();
                    int responsePort = byteBuffer.getInt();
                    byteBuffer.clear();
                    if(cmd==1&&responsePort>0){
                        byte[] bytes = new byte[128];
                        ByteBuffer byteBufferSend = ByteBuffer.wrap(bytes);

                        byteBufferSend.put(UDPConstants.HEADER);
                        //CMD
                        byteBufferSend.putShort((short)2);
                        byteBufferSend.putInt(portServer);
                        byteBufferSend.put(sn);
                        DatagramPacket sendGramPacket = new DatagramPacket(
                                bytes,
                                0,
                                byteBufferSend.position(),
                                receivePacket.getAddress(),
                                responsePort);
                        ds.send(sendGramPacket);
                        System.out.println("Server response client :"+IP+"port"+clientPort);
                    }else{
                        System.out.println("Server receive message error");
                    }
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }finally {
                exit();
            }
        }


        public void exit(){
            done = true;
            if(ds!=null){
                System.out.println("\t连接断开！");
                ds.close();
                ds = null;
            }
        }
    }

}
