package de.fhwedel.androidapp.extras;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import de.fhwedel.androidapp.Main;
import de.fhwedel.androidapp.R;

public class GpsDemo extends Activity {

	private double last_lon = 0;
	private double last_lat = 0;
	private float way = 0;
	
	private SharedPreferences prefs;
	
	private boolean showActivityName;
	private TextView tv_gps_activity;
	
	private LocationManager mLocationMngr;
	private LocationListener mLocationListener = new LocationListener(){

		@Override
		public void onLocationChanged(Location arg0) {
			// TODO Auto-generated method stub
			final float[] results = new float[3];
			double new_lon = arg0.getLongitude();
			double new_lat = arg0.getLatitude();
			Location.distanceBetween(last_lat, last_lon, new_lat, new_lon, results);
			way = way + results[0]/1000;
			((TextView) findViewById(R.id.tv_gps_latitude)).setText("Breite: "+new_lat);
			((TextView) findViewById(R.id.tv_gps_longitude)).setText("Länge: "+new_lon);
			((TextView) findViewById(R.id.tv_gps_distance)).setText("Zurückgelegte Distanz: "+way);
			last_lon = new_lon;
			last_lat = new_lat;
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_extras_gpsdemo);
		tv_gps_activity = (TextView) findViewById(R.id.tv_gps_activity);
		
		if(getIntent().getExtras().getBoolean("popup")) {hello();}
	}
	
	private void hello(){
		Builder builder = new Builder(this);
		builder.setTitle(R.string.txt_extras_dialog_titleGps)
			.setView(getLayoutInflater().inflate(R.layout.dialog_gps, null));
		builder.setIcon(android.R.drawable.ic_dialog_info)
		.setPositiveButton("OK", null)
		.show();
	}

	public void onStartClick (final View view) {
		mLocationMngr = (LocationManager) getSystemService(LOCATION_SERVICE);
		//Location first = new Location(LocationManager.NETWORK_PROVIDER);
		//Location first = mLocationMngr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		//last_lon = first.getLongitude();
		//last_lat = first.getLatitude();
		Toast.makeText(this, "Länge "+last_lon, Toast.LENGTH_LONG).show();
		mLocationMngr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
		mLocationMngr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
		((Button) findViewById(R.id.btn_gps_start)).setEnabled(false);
		((Button) findViewById(R.id.btn_gps_stop)).setEnabled(true);
		
	}
	
	public void onStopClick (final View view) {
		mLocationMngr.removeUpdates(mLocationListener);
		((Button) findViewById(R.id.btn_gps_start)).setEnabled(true);
		((Button) findViewById(R.id.btn_gps_stop)).setEnabled(false);
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		prefs = getSharedPreferences(Main.MAIN_PACKAGE_NAME+"_preferences", MODE_PRIVATE);
		showActivityName = prefs.getBoolean("show_activity", false);
		if (showActivityName) { tv_gps_activity.setText("Activity: "+getClass().getSimpleName()+".class"); }
		else { tv_gps_activity.setText(""); }
	}
	
}
