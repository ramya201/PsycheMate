package com.cmu.edu.psychemate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.neurosky.thinkgear.TGDevice;
import com.neurosky.thinkgear.TGEegPower;

public class PsycheReaderService extends Service {

	private BluetoothAdapter bluetoothAdapter;
	private TGDevice device;
	private final boolean rawEnabled = false;

	private static final String BLUETOOTH_TAG = "BLUETOOTH_TEST";
	private static final String CONNECTION_TAG = "CONNECTION_STATUS";
	private static final String SIGNAL_TAG = "SIGNAL_STATUS";
	private static final String EVENT_TAG = "EVENT_TYPE";

	public static final int RECORD_DATA = 0;
	public static final int START_SERVICE = 1;
	
	public static final int RECORDING_TIME_IN_MS = 10000;
	public static final int NO_OF_SAMPLES = 1000;

	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	
	private Context context;
	
	private ArrayList<Integer> raw_eeg = new ArrayList<Integer>(NO_OF_SAMPLES);
	private ArrayList<Integer> theta = new ArrayList<Integer>(NO_OF_SAMPLES);
	private ArrayList<Integer> delta = new ArrayList<Integer>(NO_OF_SAMPLES);
	private ArrayList<Integer> alpha1 = new ArrayList<Integer>(NO_OF_SAMPLES);
	private ArrayList<Integer> alpha2 = new ArrayList<Integer>(NO_OF_SAMPLES);
	private ArrayList<Integer> beta1 = new ArrayList<Integer>(NO_OF_SAMPLES);
	private ArrayList<Integer> beta2 = new ArrayList<Integer>(NO_OF_SAMPLES);
	private ArrayList<Integer> gamma1 = new ArrayList<Integer>(NO_OF_SAMPLES);
	private ArrayList<Integer> gamma2 = new ArrayList<Integer>(NO_OF_SAMPLES);
	private ArrayList<Integer> attention = new ArrayList<Integer>(NO_OF_SAMPLES);
	private ArrayList<Integer> meditation = new ArrayList<Integer>(NO_OF_SAMPLES);
	private ArrayList<Integer> blink = new ArrayList<Integer>(NO_OF_SAMPLES);

	private final class ServiceHandler extends Handler {

		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case RECORD_DATA:
				Log.d(EVENT_TAG,"Recording Data");
				device.start();
				try {
					Thread.sleep(RECORDING_TIME_IN_MS + 3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				device.stop();
				generateCsvFiles();
				stopSelf();
				break;
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if(device!= null && device.getState() != TGDevice.STATE_CONNECTING && device.getState() != TGDevice.STATE_CONNECTED)
		{
			device.connect(rawEnabled);
			Log.d(CONNECTION_TAG,"Connection established");
		}
		
		raw_eeg.clear();
		delta.clear();
		theta.clear();
		beta1.clear();
		beta2.clear();
		alpha1.clear();
		alpha2.clear();
		gamma1.clear();
		gamma2.clear();
		attention.clear();
		meditation.clear();
		blink.clear();

		Message msg = mServiceHandler.obtainMessage();
		int eventType = intent.getIntExtra("EventType", -1);
		msg.what = eventType;
		mServiceHandler.sendMessage(msg);

		return START_REDELIVER_INTENT;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		context = getApplicationContext();
		
		HandlerThread thread = new HandlerThread("PsycheServiceHandler", Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if(bluetoothAdapter == null) {			
			Log.d(BLUETOOTH_TAG,"Bluetooth not available");       	
		} else {
			Log.d(BLUETOOTH_TAG,"Bluetooth found");
			device = new TGDevice(bluetoothAdapter, deviceHandler);
		}  
	}

	private final Handler deviceHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TGDevice.MSG_STATE_CHANGE:

				switch (msg.arg1) {
				case TGDevice.STATE_IDLE:
					break;
				case TGDevice.STATE_CONNECTING:		                	
					Log.d(CONNECTION_TAG,"Device connecting...!");
					break;		                    
				case TGDevice.STATE_CONNECTED:
					Log.d(CONNECTION_TAG,"Device connected.");
					break;
				case TGDevice.STATE_NOT_FOUND:
					Log.d(CONNECTION_TAG,"Can't find device");
					break;
				case TGDevice.STATE_NOT_PAIRED:
					Log.d(CONNECTION_TAG,"Not paired");
					break;
				case TGDevice.STATE_DISCONNECTED:
					Log.d(CONNECTION_TAG,"Device disconnected\n");
				}
				break;
			case TGDevice.MSG_POOR_SIGNAL:
				Log.d(SIGNAL_TAG,"PoorSignal: " + msg.arg1);
				break;
			case TGDevice.MSG_RAW_DATA:
				if (raw_eeg.size() < NO_OF_SAMPLES)
					raw_eeg.add(msg.arg1);
				Log.d(SIGNAL_TAG,"Got raw: " + msg.arg1);
				break;
			case TGDevice.MSG_EEG_POWER:
				TGEegPower eegPower = (TGEegPower) msg.obj;
				if (delta.size() < NO_OF_SAMPLES)
					delta.add(eegPower.delta);
				if (theta.size() < NO_OF_SAMPLES)
					theta.add(eegPower.theta);
				if (beta1.size() < NO_OF_SAMPLES)
					beta1.add(eegPower.lowBeta);
				if (beta2.size() < NO_OF_SAMPLES)
					beta2.add(eegPower.highBeta);
				if (alpha1.size() < NO_OF_SAMPLES)
					alpha1.add(eegPower.lowAlpha);
				if (alpha2.size() < NO_OF_SAMPLES)
					alpha2.add(eegPower.highAlpha);
				if (gamma1.size() < NO_OF_SAMPLES)
					gamma1.add(eegPower.lowGamma);
				if (gamma2.size() < NO_OF_SAMPLES)
					gamma2.add(eegPower.midGamma);
				Log.d(SIGNAL_TAG,"Got EEG Powers");
				break;
			case TGDevice.MSG_HEART_RATE:
				Log.d(SIGNAL_TAG,"Heart rate: " + msg.arg1);
				break;
			case TGDevice.MSG_ATTENTION:
				if (attention.size() < NO_OF_SAMPLES)
					attention.add(msg.arg1);
				Log.d(SIGNAL_TAG,"Attention: " + msg.arg1);
				break;
			case TGDevice.MSG_MEDITATION:
				if (meditation.size() < NO_OF_SAMPLES)
					meditation.add(msg.arg1);
				Log.d(SIGNAL_TAG,"Meditation: " + msg.arg1);
				break;
			case TGDevice.MSG_BLINK:
				if (blink.size() < NO_OF_SAMPLES)
					blink.add(msg.arg1);
				Log.d(SIGNAL_TAG,"Blink: " + msg.arg1);
				break;
			case TGDevice.MSG_RAW_COUNT:
				break;
			case TGDevice.MSG_LOW_BATTERY:
				Log.d(CONNECTION_TAG,"Low Battery");
				break;
			case TGDevice.MSG_RAW_MULTI:
				break;
			default:
				break;
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public void generateCsvFiles()
	{
		try {
			File dir = new File(context.getFilesDir() + "/Data");
			dir.mkdir();
			File file1 = new File(dir,"RawEEG.txt");
			FileWriter writer1 = new FileWriter(file1.getAbsolutePath());
			
			for (Integer i: raw_eeg) {
				writer1.append(i.toString());
				writer1.append("\n");
			}
			
			writer1.flush();
			writer1.close();
			
			File file2 = new File(context.getFilesDir() + "/Data/Delta.txt");
			if (!file2.exists())
			{
				file2.createNewFile();
			}
			FileWriter writer2 = new FileWriter(file2.getAbsolutePath());
			for (Integer i: delta) {
				writer2.append(i.toString());
				writer2.append("\n");
			}
			
			writer2.flush();
			writer2.close();
			
			File file3 = new File(context.getFilesDir() + "/Data/Theta.txt");
			if (!file3.exists())
			{
				file3.createNewFile();
			}
			FileWriter writer3 = new FileWriter(file3.getAbsolutePath());
			for (Integer i: theta) {
				writer3.append(i.toString());
				writer3.append("\n");
			}
			
			writer3.flush();
			writer3.close();
			
			File file4 = new File(context.getFilesDir() + "/Data/Beta1.txt");
			if (!file4.exists())
			{
				file4.createNewFile();
			}
			FileWriter writer4 = new FileWriter(file4.getAbsolutePath());
			for (Integer i: beta1) {
				writer4.append(i.toString());
				writer4.append("\n");
			}
			
			writer4.flush();
			writer4.close();
			
			File file5 = new File(context.getFilesDir() + "/Data/Beta2.txt");
			if (!file5.exists())
			{
				file5.createNewFile();
			}
			FileWriter writer5 = new FileWriter(file5.getAbsolutePath());
			for (Integer i: beta2) {
				writer5.append(i.toString());
				writer5.append("\n");
			}
			
			writer5.flush();
			writer5.close();
			
			File file6 = new File(context.getFilesDir() + "/Data/Alpha1.txt");
			if (!file6.exists())
			{
				file6.createNewFile();
			}
			FileWriter writer6 = new FileWriter(file6.getAbsolutePath());
			for (Integer i: alpha1) {
				writer6.append(i.toString());
				writer6.append("\n");
			}
			
			writer6.flush();
			writer6.close();
			
			File file7 = new File(context.getFilesDir() + "/Data/Alpha2.txt");
			if (!file7.exists())
			{
				file7.createNewFile();
			}
			FileWriter writer7 = new FileWriter(file7.getAbsolutePath());
			for (Integer i: alpha2) {
				writer7.append(i.toString());
				writer7.append("\n");
			}
			
			writer7.flush();
			writer7.close();
			
			File file8 = new File(context.getFilesDir()+ "/Data/Gamma1.txt");
			if (!file8.exists())
			{
				file8.createNewFile();
			}
			FileWriter writer8 = new FileWriter(file8.getAbsolutePath());
			for (Integer i: gamma1) {
				writer8.append(i.toString());
				writer8.append("\n");
			}
			
			writer8.flush();
			writer8.close();
			
			File file9 = new File(context.getFilesDir() + "/Data/Gamma2.txt");
			if (!file9.exists())
			{
				file9.createNewFile();
			}
			FileWriter writer9 = new FileWriter(file9.getAbsolutePath());
			for (Integer i: gamma2) {
				writer9.append(i.toString());
				writer9.append("\n");
			}
			
			writer9.flush();
			writer9.close();
			
			File file10 = new File(context.getFilesDir() + "/Data/Attention.txt");
			if (!file10.exists())
			{
				file10.createNewFile();
			}
			FileWriter writer10 = new FileWriter(file10.getAbsolutePath());
			for (Integer i: attention) {
				writer10.append(i.toString());
				writer10.append("\n");
			}
			
			writer10.flush();
			writer10.close();
			
			File file11 = new File(context.getFilesDir() + "/Data/Meditation.txt");
			if (!file11.exists())
			{
				file11.createNewFile();
			}
			FileWriter writer11 = new FileWriter(file11.getAbsolutePath());
			for (Integer i: meditation) {
				writer11.append(i.toString());
				writer11.append("\n");
			}
			
			writer11.flush();
			writer11.close();
			
			File file12 = new File(context.getFilesDir() + "/Data/Blink.txt");
			if (!file12.exists())
			{
				file12.createNewFile();
			}
			FileWriter writer12 = new FileWriter(file12.getAbsolutePath());
			for (Integer i: blink) {
				writer12.append(i.toString());
				writer12.append("\n");
			}
			
			writer12.flush();
			writer12.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
