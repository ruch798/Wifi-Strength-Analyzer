package com.example.wifirssi;

import java.util.Date;
import java.text.SimpleDateFormat;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import android.support.v4.content.ContextCompat;

public class MainActivity extends Activity implements Runnable
{

	private static final String FILE_NAME= "info1.txt";
	TextView tv,nm;
	int stop;
	Handler handler;
	WifiManager wifi;
	MyAdapterWifi adapter;

	String filepath;

	int rssi;
	String ss;
	String info="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		File temp = new File(getFilesDir() + "/"+FILE_NAME);
		if (temp.exists()) {
			RandomAccessFile raf = null;
			try {
				raf = new RandomAccessFile(temp, "rw");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				raf.setLength(0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		wifi = (WifiManager) getSystemService(MainActivity.this.WIFI_SERVICE);
		if (!wifi.isWifiEnabled()) {
			Toast.makeText(this, "Wifi is disbaled, enabling Wifi", Toast.LENGTH_SHORT).show();
			wifi.setWifiEnabled(true);
		}


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private boolean isExternalStorageWritable() {
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			Log.v("State","Yes");
			return true;
		}
		else { return false;}
	}

	public boolean checkPermission(String permission) {
		int check  = ContextCompat.checkSelfPermission(this, permission);
		return (check== PackageManager.PERMISSION_GRANTED);
	}

	public void display(View v) {

		File file = new File(Environment.getExternalStorageDirectory(),FILE_NAME);

		Uri selectedUri = Uri.parse(file.getAbsolutePath());
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(selectedUri, "text/csv");

		if (intent.resolveActivityInfo(getPackageManager(), 0) != null)
		{
			startActivity(intent);
		}
	}

	public void measure(View v)
		{
			tv = (TextView) findViewById(R.id.textView2);
			nm = (TextView) findViewById(R.id.networkDetails);
			handler = new Handler();
			adapter = new MyAdapterWifi(MainActivity.this);
//			while(stop==0) {
				final Runnable r = new Runnable() {
					public void run() {


						WifiInfo wifiInfo = wifi.getConnectionInfo();

						int signal_strength = wifiInfo.getRssi();
						String signal = null;
						if (signal_strength > -50) {
							signal = "Excellent";
						} else if (signal_strength < -50 && signal_strength > -60) {
							signal = "Good";
						} else if (signal_strength < -60 && signal_strength > -70) {
							signal = "Fair";
						} else if (signal_strength < -70 && signal_strength > -100) {
							signal = "Weak";
						}

						info = "SSID: " + wifiInfo.getSSID() + "\nStrength: " + wifiInfo.getRssi() + "dBm" + "\nSignal Level: " + WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5) + "/5" + "\nSignal Strength: " + signal + "\nSpeed: " + wifiInfo.getLinkSpeed() + "Mbps" + "\nIP Address: " + Formatter.formatIpAddress(wifiInfo.getIpAddress()) + "\nMAC Address: " + wifiInfo.getMacAddress() + "\nFrequency: " + (float) wifiInfo.getFrequency() / 1000 + "GHz" + "\nHidden SSID: " + wifiInfo.getHiddenSSID();
						rssi = wifi.getConnectionInfo().getRssi();
						ss = Integer.toString(rssi);
						nm.setText("" + info + "");

						tv.setText(ss + " dB");
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd, MM, yyyy hh:mm:ss");
						String sCertDate = dateFormat.format(new Date());
						adapter.insertData(sCertDate, ss);
						handler.postDelayed(this, 1000);


						if (isExternalStorageWritable() && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
							File file = new File(Environment.getExternalStorageDirectory(), FILE_NAME);
							Log.v("External file", file.toString());

							try {
								FileOutputStream fos = new FileOutputStream(file, true);
								StringBuilder log = new StringBuilder();

								for (int i = 0; i < 60; i++) {

									log.append(String.valueOf(ss) + " dbm\n");

								}
								fos.write(log.toString().getBytes());
								fos.close();
								Toast.makeText(MainActivity.this, "File Saved to: " + file.toString(), Toast.LENGTH_LONG).show();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}


						} else {
							Toast.makeText(MainActivity.this, "Cannot perform write operation, Permission denied", Toast.LENGTH_LONG).show();
						}

						File file = new File(Environment.getExternalStorageDirectory(), FILE_NAME);

						try {
							FileInputStream fis = new FileInputStream(file);
							InputStreamReader isr = new InputStreamReader(fis);
							BufferedReader br = new BufferedReader(isr);
							ArrayList<String> disp = new ArrayList<String>();
							String text;

							while ((text = br.readLine()) != null) {
								disp.add(text);

							}


							ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, disp);
							ListView listView = (ListView) findViewById(R.id.display);
							listView.setAdapter(adapter);

						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

					}

				};
				handler.postDelayed(r, 1000);


			//}


	}


//	public void stop(View v)
//	{
//		Button st=(Button)findViewById(R.id.stop);
//		st.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				stop=1;
//			}
//		});
//
//	}
	public void logshow(View v)
	{

		Long tsLong = System.currentTimeMillis()/1000;
		String ts = tsLong.toString();
		startActivity(new Intent(MainActivity.this,WifiLog.class));

	}
	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}