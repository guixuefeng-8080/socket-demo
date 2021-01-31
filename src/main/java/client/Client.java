package client;

import client.bean.ServerInfo;

import java.io.IOException;

public class Client {
    public static void main(String[] args){
        ServerInfo serverInfo  = UPDSearcher.searcherServer(3000);
        if(serverInfo!=null){
            System.out.println("Client 搜索到服务："+serverInfo.toString());
            try {
                TCPClient.linkWith(serverInfo);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Client linkWith failed :"+e.getMessage());
            }
        }
    }
}
