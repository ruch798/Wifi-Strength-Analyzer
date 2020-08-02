package com.example.wifirssi;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.content.ContentValues;

import android.database.SQLException;


import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyAdapterWifi  {

    myHelper helper;
    Context context;

   
    long insertData(String ts, String rs)
    {
        long id=0;
        try
        {
            SQLiteDatabase db=helper.getWritableDatabase();
        
        ContentValues cv=new ContentValues();
        cv.put(myHelper.TIMESTAMP, ts);
        cv.put(myHelper.RSSI_ST, rs);

         id=db.insert(myHelper.TABLE_NAME,null,cv);

        }
        catch(SQLiteException e)
        {
        	Toast.makeText(context, "Exception", Toast.LENGTH_LONG).show();
        }
        return id;
    }


    public Cursor showData()
    {
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor c=db.query(myHelper.TABLE_NAME, null, null, null, null,null, null);
        //passing NULL TO SELECT ALL
        return c;

    }

    public MyAdapterWifi(Context ctx)
    {
        this.context=ctx;
        helper=new myHelper(context);
    }

    class myHelper extends SQLiteOpenHelper 
    
    {
        private static final String DATABASE_NAME="wifi12";
        private static final int DATABASE_VERSION=1;

        private static final String TABLE_NAME="rssiLog12";
        
        private static final String TIMESTAMP="timestamp";
        private static final String RSSI_ST="rssi_st";

        private static final String TABLE_DROP="DROP TABLE "+TABLE_NAME;

        private static final String TABLE_CREATE="create table "+TABLE_NAME+" ( "+TIMESTAMP+" varchar(30), "+RSSI_ST+" varchar(20));";
        
        public myHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            try
            {
                db.execSQL(TABLE_CREATE);
                Toast.makeText(context, "TABLE CREATED!", Toast.LENGTH_SHORT).show();	
				

            }
            catch (SQLException e)
            {
            		Toast.makeText(context, "TABLE NOT CREATED!", Toast.LENGTH_SHORT).show();	
				
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            try
            {
                db.execSQL(TABLE_DROP);
                onCreate(db);
            }
            catch (SQLException e)
            {

            }
        }

    }
}
