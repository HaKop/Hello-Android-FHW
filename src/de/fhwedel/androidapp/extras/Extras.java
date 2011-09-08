package de.fhwedel.androidapp.extras;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import de.fhwedel.androidapp.Main;
import de.fhwedel.androidapp.R;

public class Extras extends Activity {
	
	private SharedPreferences prefs;
	
	private boolean showActivityName;
	private TextView tv_extras_activity;
	private boolean popup = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_extras_extras);
		
		tv_extras_activity = (TextView) findViewById(R.id.tv_extras_activity);
		
		popup = getIntent().getExtras().getBoolean("popup");
	}

	public void onButtonClick (final View view) {
		Intent intent = new Intent();
		intent.putExtra("popup", popup);
		switch (view.getId()){
		case R.id.btn_extras_graphics:
			intent.setClass(this, GraphicsDemo.class);
			startActivity(intent);
			break;
		case R.id.btn_extras_sensor:
			intent.setClass(this, SensorDemo.class);
			startActivity(intent);
			break;
		case R.id.btn_extras_gps:
			intent.setClass(this, GpsDemo.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		prefs = getSharedPreferences(Main.MAIN_PACKAGE_NAME+"_preferences", MODE_PRIVATE);
		showActivityName = prefs.getBoolean("show_activity", false);
		if (showActivityName) { tv_extras_activity.setText("Activity: "+getClass().getSimpleName()+".class"); }
		else { tv_extras_activity.setText(""); }
	}
	
}
