package com.cmu.edu.psychemate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class IncomingCallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent incomingIntent) {
		
		Intent serviceIntent = new Intent("com.cmu.edu.psychemate.PsycheReaderService");
		serviceIntent.setClass(context, PsycheReaderService.class);
		serviceIntent.putExtra("EventType", PsycheReaderService.INCOMING_CALL);
		context.startService(serviceIntent);
	}

}
