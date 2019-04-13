
package com.example.bluetoothchatapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {


    Button sendbtn;
    EditText writemsg;
    ListView listView;
    static ArrayList<DataClass> arrayList=new ArrayList<>();
    static CustomAdapter customAdapter;
    static DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendbtn=findViewById(R.id.sendButton);
        writemsg=findViewById(R.id.writeMsg);
        listView=findViewById(R.id.chatList);
        customAdapter=new CustomAdapter(ChatActivity.this, arrayList);
        listView.setAdapter(customAdapter);

        MainActivity.connectedDeviceName=MainActivity.connectedDeviceName.substring(MainActivity.connectedDeviceName.lastIndexOf(']')+1);
        String tmp[]=MainActivity.connectedDeviceName.split(" ");
        MainActivity.connectedDeviceName="";
        for (String c:tmp)
        {
            MainActivity.connectedDeviceName+=c;
        }
        dbHelper=new DBHelper(ChatActivity.this);

        //Load Previous Chats
        Cursor cursor=dbHelper.readData();
        while (cursor.moveToNext())
        {
            String t=cursor.getString(0);
            boolean who;
            if(Integer.parseInt(cursor.getString(1))==1)
            {
                who=false;
            }
            else who=true;
            DataClass dataClass=new DataClass(t,who);
            arrayList.add(dataClass);
        }
        customAdapter.notifyDataSetChanged();

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String m=writemsg.getText().toString();
                if(MainActivity.sendReceiveThread==null)
                {
                    Toast.makeText(ChatActivity.this,"Thread Not Created Please wait",Toast.LENGTH_LONG).show();
                    MainActivity mainActivity=new MainActivity();
                    mainActivity.makeConnection(MainActivity.wifiP2pDevice);
                }
                else {
                    MainActivity.sendReceiveThread.write(m.getBytes());
                    DataClass dataClass = new DataClass(m, true);
                    arrayList.add(dataClass);
                    customAdapter.notifyDataSetChanged();
                    writemsg.setText("");
                    ContentValues contentValues=new ContentValues();
                    contentValues.put(DBHelper.colName[0],m);
                    contentValues.put(DBHelper.colName[1],0);
                    if(dbHelper.insertDB(contentValues))
                    {

                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this,"Not DONE",Toast.LENGTH_LONG).show();
                    }
                }
               /* InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);*/
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.sendReceiveThread = null;
        MainActivity.clientThread = null;
        MainActivity.serverSideThread = null;
    }
}
