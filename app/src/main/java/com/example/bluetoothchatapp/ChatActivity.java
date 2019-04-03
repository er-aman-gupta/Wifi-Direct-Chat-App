
package com.example.bluetoothchatapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    Button sendbtn;
    EditText writemsg;
    ListView listView;
    static ArrayList<DataClass> arrayList=new ArrayList<>();
    static CustomAdapter customAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendbtn=findViewById(R.id.sendButton);
        writemsg=findViewById(R.id.writeMsg);
        listView=findViewById(R.id.chatList);
        customAdapter=new CustomAdapter(ChatActivity.this, arrayList);
        listView.setAdapter(customAdapter);
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
                }
               /* InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);*/
            }
        });
    }
}
