package com.example.bluetoothchatapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.junit.Test;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class MainActivity extends AppCompatActivity {

    Button enWIFIbtn, discoverbtn;
    TextView constate, readmsg;
    ListView listView;

    static WifiManager wifiManager;
    WifiP2pManager wifiP2pManager;
    static WifiP2pManager.Channel channel;
    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;

    List<WifiP2pDevice> list = new ArrayList<WifiP2pDevice>();
    String devicesName[];
    WifiP2pDevice devicearray[];

    static Handler handler;
    static WifiP2pDevice wifiP2pDevice;

    static public Server_Side_Thread serverSideThread;
    static public Client_Thread clientThread;
    static public SendReceiveThread sendReceiveThread;

    static String Channel_Id = "myChannel";
    static CharSequence name = "Channel";
    static String description = "MyChannelDescription";
    static String connectedDeviceName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initValues();
        //Used this coz the network thread was running on main thread which wud hv caused exception
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        discoverbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        constate.setText("Discovery started");
                    }

                    @Override
                    public void onFailure(int reason) {
                        constate.setText("Discovery failed " + reason);
                    }
                });
            }
        });

        enWIFIbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(true);
                    enWIFIbtn.setText("Turn Wifi Off");
                } else {
                    wifiManager.setWifiEnabled(false);
                    enWIFIbtn.setText("Turn Wifi On");
                }
            }
        });
    }

    void initValues() {

        constate = findViewById(R.id.connectionStatus);
        //Check location permission
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        enWIFIbtn = findViewById(R.id.WIFIButton);
        discoverbtn = findViewById(R.id.discoverbtn);


        listView = findViewById(R.id.peerListView);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled()) {
            enWIFIbtn.setText("Turn Wifi Off");
        } else {
            enWIFIbtn.setText("Turn Wifi On");
        }

        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(MainActivity.this, getMainLooper(), null);
        broadcastReceiver = new Wifi_P2P_Broadcast_Receiver(wifiP2pManager, channel, MainActivity.this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                wifiP2pDevice = devicearray[position];
                makeConnection(wifiP2pDevice);

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 1) {
                    byte[] readBuff = (byte[]) msg.obj;
                    String temp = new String(readBuff, 0, msg.arg1);
                    DataClass dataClass = new DataClass(temp, false);
                    ChatActivity.arrayList.add(dataClass);
                    ChatActivity.customAdapter.notifyDataSetChanged();

                    //#TODO Insert incomming msg to db
                    ContentValues contentValues=new ContentValues();
                    contentValues.put(DBHelper.colName[0],temp);
                    contentValues.put(DBHelper.colName[1],1);
                    ChatActivity.dbHelper.insertDB(contentValues);

                    //#TODO Create Notification for incomming message
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, Channel_Id);
                    builder.setContentTitle("New Message Received");
                    builder.setContentText(temp);
                    builder.setSmallIcon(R.mipmap.ic_launcher);
                    builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                    builder.setAutoCancel(false);
                    builder.setDefaults(Notification.DEFAULT_ALL);

                    if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        NotificationChannel notificationChannel = new NotificationChannel(Channel_Id, name, NotificationManager.IMPORTANCE_HIGH);
                        notificationChannel.setDescription(description);
                        notificationChannel.enableVibration(true);
                        notificationManager.createNotificationChannel(notificationChannel);
                    }
                    notificationManager.notify(0, builder.build());
                }
                return true;
            }
        });
    }

    void makeConnection(final WifiP2pDevice wifiP2pDevice) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = wifiP2pDevice.deviceAddress;
        wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Connected "+connectedDeviceName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this, "Connection failed " + reason, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Listener for Peerdevices discovery
    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            if (!peers.getDeviceList().equals(list)) {
                list.clear();
                list.addAll(peers.getDeviceList());

                devicesName = new String[(peers.getDeviceList().size())];
                devicearray = new WifiP2pDevice[(peers.getDeviceList().size())];
                for (int i = 0; i < peers.getDeviceList().size(); i++) {
                    devicesName[i] = list.get(i).deviceName;
                    devicearray[i] = list.get(i);
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, devicesName);
                listView.setAdapter(arrayAdapter);
            }
            if (peers.getDeviceList().size() == 0) {
                Toast.makeText(MainActivity.this, "No Devices Found", Toast.LENGTH_SHORT).show();
            }
        }
    };

    //when connection happens declare one side a server and other as client
    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            InetAddress inetAddress = info.groupOwnerAddress;
            if (info.groupFormed && info.isGroupOwner) {
                constate.setText("Host");
                Log.d("Reached", " Here 1");
                serverSideThread = new Server_Side_Thread();
                serverSideThread.start();
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            } else if (info.groupFormed) {
                constate.setText("Client");
                Log.d("Reached", " Here 2");
                clientThread = new Client_Thread(inetAddress);
                clientThread.start();
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }

        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Lets GO!!!!", Toast.LENGTH_LONG).show();

            } else {
                constate.setText("Cant run the app without location permission\nPlease Give The Permission");
                //Toast.makeText(MainActivity.this,"Cant run the app without location permission\nPlease Give The Permission",Toast.LENGTH_LONG).show();

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }
}