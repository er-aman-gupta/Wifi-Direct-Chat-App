package com.example.bluetoothchatapp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client_Thread extends Thread {
    Socket socket;
    String hostAddress;
    public Client_Thread(InetAddress hostAddress)
    {
        this.hostAddress=hostAddress.getHostAddress();
        this.socket=new Socket();
    }

    @Override
    public void run() {
        try {
            socket.connect(new InetSocketAddress(hostAddress,8888),500);
            MainActivity.sendReceiveThread=new SendReceiveThread(socket);
            MainActivity.sendReceiveThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
