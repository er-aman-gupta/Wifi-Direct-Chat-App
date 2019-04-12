package com.example.bluetoothchatapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    Context context;
    static String DBNAME="chatDb";
    final static String TABLE_NAME=MainActivity.connectedDeviceName;
    static int ver=1;
    static String colName[]=new String[]{"msg","who"};
    DBHelper(Context context)
    {
        super(context,DBNAME,null,ver);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME+"(msg TEXT,who INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public boolean insertDB(ContentValues contentValues)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        long result=sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        if(result==-1)
            return false;
        else return true;
    }

    public Cursor readData()
    {
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("select * from "+TABLE_NAME,null);
        return cursor;
    }
}
