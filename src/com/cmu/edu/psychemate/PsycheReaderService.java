package com.cmu.edu.psychemate;

import java.io.IOException;

import android.app.IntentService;
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
import android.view.KeyEvent;

import com.neurosky.thinkgear.TGDevice;

public class PsycheReaderService extends Service {

	private static BluetoothAdapter bluetoothAdapter;
	private static TGDevice device;
	private static final boolean rawEnabled = false;

	private static final String BLUETOOTH_TAG = "BLUETOOTH_TEST";
	private static final String CONNECTION_TAG = "CONNECTION_STATUS";
	private static final String SIGNAL_TAG = "SIGNAL_STATUS";
	private static final String EVENT_TAG = "EVENT_TYPE";

	public static final int INCOMING_CALL = 1;
	public static final int SONG_PLAYING = 2;

	private static int attemptCounter = 5;

	private static int attentionLevel;
	private static int meditationLevel;
	private static int eyeBlink;

	private static Context context;

	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;

	private final class ServiceHandler extends Handler {

		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case INCOMING_CALL:
			{
				device.start();
				Log.d(EVENT_TAG,"Incoming Call");
				Log.d(EVENT_TAG, "Attention Monitor:"+ attentionLevel);

				while (attemptCounter != 0) {
					try {
						Runtime runtime = Runtime.getRuntime();
						if (attentionLevel > 50) {						
							java.lang.Process process = runtime.exec("adb shell input keyevent 5");
							Log.d(EVENT_TAG, "Call Answered");
							break;
						} else {

						}

						Thread.sleep(2000);
					}catch (InterruptedException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} 
					attemptCounter--;
				}
				device.stop();
			}
			break;
			}
			stopSelf(msg.arg1);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if(device!= null && device.getState() != TGDevice.STATE_CONNECTING && device.getState() != TGDevice.STATE_CONNECTED)
		{
			device.connect(rawEnabled);
			Log.d(CONNECTION_TAG,"Connection established");
		}

		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;

		int eventType = intent.getIntExtra("EventType", -1);
		msg.what = eventType;
		mServiceHandler.sendMessage(msg);

		return START_STICKY;
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

	public void resetAttemptCounter(){
		attemptCounter = 5;
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
				Log.d(SIGNAL_TAG,"Got raw: " + msg.arg1);
				break;
			case TGDevice.MSG_HEART_RATE:
				Log.d(SIGNAL_TAG,"Heart rate: " + msg.arg1);
				break;
			case TGDevice.MSG_ATTENTION:
			{
				attentionLevel = msg.arg1;
				Log.d(SIGNAL_TAG,"Attention: " + attentionLevel);
				break;
			}
			case TGDevice.MSG_MEDITATION:
				Log.d(SIGNAL_TAG,"Meditation: " + msg.arg1);
				break;
			case TGDevice.MSG_BLINK:
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

}
