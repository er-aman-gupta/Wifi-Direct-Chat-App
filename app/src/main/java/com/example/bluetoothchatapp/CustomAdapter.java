package com.example.bluetoothchatapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<DataClass> arrayList;

    public CustomAdapter(Context context, ArrayList<DataClass> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        if(arrayList.size()>0)
        return arrayList.size();
        else return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return arrayList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DataClass dataClass=arrayList.get(position);
        if(dataClass.who==true)     //sent by the user
        {
            View view=View.inflate(context,R.layout.lay2,null);
            TextView textView=view.findViewById(R.id.senderTextId);
            textView.setText(dataClass.s);

            return view;
        }
        else
        {
            View view=View.inflate(context,R.layout.lay1,null);
            TextView textView=view.findViewById(R.id.receiveTextId);
            textView.setText(dataClass.s);
            return view;
        }

    }
}
