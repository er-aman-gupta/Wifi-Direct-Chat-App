package com.example.bluetoothchatapp;

import android.util.Log;
import android.widget.Toast;

import java.net.ServerSocket;
import java.net.Socket;

public class Server_Side_Thread extends Thread {

    Socket socket;
    ServerSocket serverSocket;

    @Override
    public void run() {
        try{
            serverSocket=new ServerSocket(8888);
            socket=serverSocket.accept();
            MainActivity.sendReceiveThread=new SendReceiveThread(socket);
            MainActivity.sendReceiveThread.start();
        }
        catch (Exception e)
        {
            Log.d("EXception Caught ",e.toString());
        }
    }
}
