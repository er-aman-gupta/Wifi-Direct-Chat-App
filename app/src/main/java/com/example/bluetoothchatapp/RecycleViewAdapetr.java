package com.example.bluetoothchatapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecycleViewAdapetr extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<DataClass> arrayList;
    static int pos=0;
    public RecycleViewAdapetr(Context context, ArrayList<DataClass> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getItemViewType(int position) {
        DataClass dataClass=arrayList.get(position);
        if(dataClass.who==true)
            return 1;
        else return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v;
        if(i==1)
        {
            v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lay2,viewGroup,false);
            return new RecieveViewHolder(v);
        }
        else{
            v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lay1,viewGroup,false);
            return new SendViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

        DataClass dataClass=arrayList.get(i);
        if(viewHolder.getItemViewType()==1)
        {
            ((RecieveViewHolder)viewHolder).textView.setText(dataClass.s);
        }
        else
        {
            ((SendViewHolder)viewHolder).textView.setText(dataClass.s);
        }

    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class RecieveViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        public RecieveViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.senderTextId);
        }
        public void bind(DataClass dataClass)
        {
            textView.setText(dataClass.s);
        }
    }
    public  class SendViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.receiveTextId);
        }
        public void bind(DataClass dataClass)
        {
            textView.setText(dataClass.s);
        }
    }

}
