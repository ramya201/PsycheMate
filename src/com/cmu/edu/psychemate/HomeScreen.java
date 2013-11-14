package com.cmu.edu.psychemate;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class HomeScreen extends Activity {
	private boolean isOn = false;
	private boolean isRecording = false;
	private Button startStopBtn;
	private Button recordBtn;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homescreen);

		startStopBtn = (Button) findViewById(R.id.startStopBtn);
		recordBtn = (Button) findViewById(R.id.recordBtn);
		
		context = getApplicationContext();
	}

	public void toggleService(View view)
	{
		if (isOn) {
			startStopBtn.setText("Start");
			Intent serviceIntent = new Intent("com.cmu.edu.psychemate.PsycheReaderService");
			serviceIntent.setClass(context, PsycheReaderService.class);
			stopService(serviceIntent);
			isOn = false;
		} else {
			startStopBtn.setText("Stop");
			Intent serviceIntent = new Intent("com.cmu.edu.psychemate.PsycheReaderService");
			serviceIntent.setClass(context, PsycheReaderService.class);
			serviceIntent.putExtra("EventType", PsycheReaderService.START_SERVICE);
			startService(serviceIntent);
			isOn = true;
		}
	}

	public void recordData(View view)
	{
		if (isRecording) {
			Toast.makeText(context, "Recording in progress..", Toast.LENGTH_SHORT).show();
		} else {
			Intent serviceIntent = new Intent(context, PsycheReaderService.class);
			serviceIntent.putExtra("EventType", PsycheReaderService.RECORD_DATA);
			startService(serviceIntent);
			recordBtn.setText("Recording...");
			recordBtn.setTextColor(Color.RED);
			isRecording = true;
			Runnable action = new Runnable() {

				@Override
				public void run() {
					recordBtn.setText("Record Data");
					recordBtn.setTextColor(Color.parseColor("#42A4CD"));
					isRecording = false;
				}

			};
			recordBtn.postDelayed(action, PsycheReaderService.RECORDING_TIME_IN_MS + 3000);
		}
	}

}
