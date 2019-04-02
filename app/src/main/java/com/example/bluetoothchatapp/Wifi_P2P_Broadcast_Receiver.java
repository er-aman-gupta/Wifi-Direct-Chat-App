package com.example.bluetoothchatapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

public class Wifi_P2P_Broadcast_Receiver extends BroadcastReceiver {

    WifiP2pManager wifiP2pManager;
    WifiP2pManager.Channel channel;
    MainActivity mainActivity;

    public Wifi_P2P_Broadcast_Receiver(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel,MainActivity mainActivity) {
        this.wifiP2pManager = wifiP2pManager;
        this.channel = channel;
        this.mainActivity=mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action=intent.getAction();
        if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action))
        {
            if(wifiP2pManager==null)
            {
                return;
            }
            NetworkInfo networkInfo=intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if(networkInfo.isConnected())
            {
                wifiP2pManager.requestConnectionInfo(channel,mainActivity.connectionInfoListener);

            }
            else {
                mainActivity.constate.setText("DEVICE DISCONNECTED");
            }
        }
        if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action))
        {
            if(wifiP2pManager!=null)
            {
                wifiP2pManager.requestPeers(channel,mainActivity.peerListListener);
            }
        }
        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action))
        {
            int state=intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);
            if(state==WifiP2pManager.WIFI_P2P_STATE_ENABLED)
            {
                Toast.makeText(context,"WIFI IS ON",Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(context,"WIFI IS OFF",Toast.LENGTH_SHORT).show();
        }
        if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action))
        {

        }

    }
}
