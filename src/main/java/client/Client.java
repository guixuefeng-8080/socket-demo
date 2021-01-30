package client;

import client.bean.ServerInfo;

public class Client {
    public static void main(String[] args){
        ServerInfo searchServer  = ClientSearcher.searcherServer(3000);
        
        System.out.println(searchServer);
    }
}
