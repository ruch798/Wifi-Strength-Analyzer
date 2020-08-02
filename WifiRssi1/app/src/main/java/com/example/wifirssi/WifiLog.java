package com.example.wifirssi;

import java.util.Vector;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WifiLog extends Activity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_log);
		
		SQLiteDatabase sqlDB=openOrCreateDatabase("wifi12",MODE_PRIVATE,null);
		
        Vector RSSIDetails=new Vector();
        Cursor getRssis=sqlDB.rawQuery("SELECT * FROM rssiLog12",null);
        
        String a=null;
        String b=null;
        getRssis.moveToFirst();
        if(getRssis!=null)
        {
            do {
                a=getRssis.getString(0);
                b=getRssis.getString(1);
                String n=a+"dbMs  -> "+b;
                RSSIDetails.add(n);
            }
            while (getRssis.moveToNext());
        }
        final String x[]=new String[RSSIDetails.size()];
        RSSIDetails.toArray(x);
        ListView listView = (ListView) findViewById(R.id.rssDisp);
        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,android.R.id.text1,RSSIDetails);
        listView.setAdapter(arrayAdapter);
}
}
