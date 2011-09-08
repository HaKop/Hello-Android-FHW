package de.fhwedel.androidapp.services;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import de.fhwedel.androidapp.R;

public class SimpleService extends Service {
	
	public final static String BC_INTENT = "de.fhwedel.androidapp.intent.action.SERVICE_BC"; 
	
	private long startTime;
	private Timer mTimer;
	private TimerTask mTask = new TimerTask() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Intent bcIntent = new Intent(BC_INTENT);
			bcIntent.putExtra("Message", getResources().getString(R.string.txt_services_service1msg).replace("%DUR", ""+((System.currentTimeMillis()-startTime)/1000)));
			Log.d("Service",bcIntent.getExtras().getString("Message"));
			getApplicationContext().sendBroadcast(bcIntent);
		}
	};

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mTimer.cancel();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		startTime = System.currentTimeMillis();
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(mTask, 0, 1000);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
