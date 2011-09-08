package de.fhwedel.androidapp.extras;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import de.fhwedel.androidapp.Main;
import de.fhwedel.androidapp.R;

public class SensorDemo extends Activity implements SensorEventListener {
	
	private SharedPreferences prefs;
	
	private boolean showActivityName;
	private TextView tv_sensor_activity;
	private TextView tv_extras_sensor_accX;
	private TextView tv_extras_sensor_accY;
	private TextView tv_extras_sensor_accZ;
	private TextView tv_extras_sensor_gyroX;
	private TextView tv_extras_sensor_gyroY;
	private TextView tv_extras_sensor_gyroZ;
	private TextView tv_extras_sensor_orientationX;
	private TextView tv_extras_sensor_orientationY;
	private TextView tv_extras_sensor_orientationZ;
	private TextView tv_extras_sensor_magneticX;
	private TextView tv_extras_sensor_magneticY;
	private TextView tv_extras_sensor_magneticZ;
	private TextView tv_extras_sensor_light;
	private TextView tv_extras_sensor_proximity;
	private TextView tv_extras_sensor_temperature;
	private SensorManager mSensorMng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_extras_sensordemo);
		
		tv_sensor_activity=(TextView)findViewById(R.id.tv_sensor_activity);
		tv_extras_sensor_accX=(TextView)findViewById(R.id.text_extras_sensor_accX);
		tv_extras_sensor_accY=(TextView)findViewById(R.id.text_extras_sensor_accY);
		tv_extras_sensor_accZ=(TextView)findViewById(R.id.text_extras_sensor_accZ);
		tv_extras_sensor_gyroX=(TextView)findViewById(R.id.txt_extras_sensor_gyroX);
		tv_extras_sensor_gyroY=(TextView)findViewById(R.id.txt_extras_sensor_gyroY);
		tv_extras_sensor_gyroZ=(TextView)findViewById(R.id.txt_extras_sensor_gyroZ);
		tv_extras_sensor_orientationX=(TextView)findViewById(R.id.txt_extras_sensor_orientationX);
		tv_extras_sensor_orientationY=(TextView)findViewById(R.id.txt_extras_sensor_orientationY);
		tv_extras_sensor_orientationZ=(TextView)findViewById(R.id.txt_extras_sensor_orientationZ);
		tv_extras_sensor_magneticX=(TextView)findViewById(R.id.txt_extras_sensor_magneticX);
		tv_extras_sensor_magneticY=(TextView)findViewById(R.id.txt_extras_sensor_magneticY);
		tv_extras_sensor_magneticZ=(TextView)findViewById(R.id.txt_extras_sensor_magneticZ);
		tv_extras_sensor_light=(TextView)findViewById(R.id.txt_extras_sensor_light);
		tv_extras_sensor_proximity=(TextView)findViewById(R.id.txt_extras_sensor_proximity);
		tv_extras_sensor_temperature=(TextView)findViewById(R.id.txt_extras_sensor_thermo);
		
		mSensorMng = (SensorManager)getSystemService(SENSOR_SERVICE);
		
		if(getIntent().getExtras().getBoolean("popup")) {hello();}
		
		senseAcceleration();
		senseGyroscope();
		senseOrientation();
		senseMagnetic();
		senseLight();
		senseProximity();
		senseTemperature();
	}
	
	private void hello(){
		Builder builder = new Builder(this);
		builder.setTitle(R.string.txt_extras_dialog_titleSensors)
			.setView(getLayoutInflater().inflate(R.layout.dialog_sensors, null));
		builder.setIcon(android.R.drawable.ic_dialog_info)
		.setPositiveButton("OK", null)
		.show();
	}

	private void senseAcceleration() {
		List<Sensor> sensors = mSensorMng.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (sensors.size()>0){
			mSensorMng.registerListener(this,
										mSensorMng.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
										SensorManager.SENSOR_DELAY_UI);
		} else {
			tv_extras_sensor_accX.setText("n/a");
			tv_extras_sensor_accY.setText("n/a");
			tv_extras_sensor_accZ.setText("n/a");
		}
	}

	private void senseGyroscope() {
		List<Sensor> sensors = mSensorMng.getSensorList(Sensor.TYPE_GYROSCOPE);
		if (sensors.size()>0){
			mSensorMng.registerListener(this,
					mSensorMng.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
					SensorManager.SENSOR_DELAY_UI);
		} else {
			tv_extras_sensor_gyroX.setText("n/a");
			tv_extras_sensor_gyroY.setText("n/a");
			tv_extras_sensor_gyroZ.setText("n/a");
		}
	}
	
	private void senseOrientation() {
		List<Sensor> sensors = mSensorMng.getSensorList(Sensor.TYPE_ORIENTATION);
		if (sensors.size()>0){
			mSensorMng.registerListener(this,
					mSensorMng.getDefaultSensor(Sensor.TYPE_ORIENTATION),
					SensorManager.SENSOR_DELAY_UI);
		} else {
			tv_extras_sensor_orientationX.setText("n/a");
			tv_extras_sensor_orientationY.setText("n/a");
			tv_extras_sensor_orientationZ.setText("n/a");
		}
	}
	
	private void senseMagnetic() {
		List<Sensor> sensors = mSensorMng.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
		if (sensors.size()>0){
			mSensorMng.registerListener(this,
					mSensorMng.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
					SensorManager.SENSOR_DELAY_UI);
		} else {
			tv_extras_sensor_magneticX.setText("n/a");
			tv_extras_sensor_magneticY.setText("n/a");
			tv_extras_sensor_magneticZ.setText("n/a");
		}
	}
	
	private void senseLight() {
		List<Sensor> sensors = mSensorMng.getSensorList(Sensor.TYPE_LIGHT);
		if (sensors.size()>0){
			mSensorMng.registerListener(this,
					mSensorMng.getDefaultSensor(Sensor.TYPE_LIGHT),
					SensorManager.SENSOR_DELAY_UI);
		} else {
			tv_extras_sensor_light.setText("n/a");
		}
	}
	
	private void senseProximity() {
		List<Sensor> sensors = mSensorMng.getSensorList(Sensor.TYPE_PROXIMITY);
		if (sensors.size()>0){
			mSensorMng.registerListener(this,
					mSensorMng.getDefaultSensor(Sensor.TYPE_PROXIMITY),
					SensorManager.SENSOR_DELAY_UI);
		} else {
			tv_extras_sensor_proximity.setText("n/a");
		}
	}
	
	private void senseTemperature() {
		List<Sensor> sensors = mSensorMng.getSensorList(Sensor.TYPE_TEMPERATURE);
		if (sensors.size()>0){
			mSensorMng.registerListener(this,
					mSensorMng.getDefaultSensor(Sensor.TYPE_TEMPERATURE),
					SensorManager.SENSOR_DELAY_UI);
		} else {
			tv_extras_sensor_temperature.setText("n/a");
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		prefs = getSharedPreferences(Main.MAIN_PACKAGE_NAME+"_preferences", MODE_PRIVATE);
		showActivityName = prefs.getBoolean("show_activity", false);
		if (showActivityName) { tv_sensor_activity.setText("Activity: "+getClass().getSimpleName()+".class"); }
		else { tv_sensor_activity.setText(""); }
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		switch (arg0.sensor.getType()){
		case Sensor.TYPE_ACCELEROMETER:
			tv_extras_sensor_accX.setText(""+arg0.values[0]);
			tv_extras_sensor_accY.setText(""+arg0.values[1]);
			tv_extras_sensor_accZ.setText(""+arg0.values[2]);
			break;
		case Sensor.TYPE_GYROSCOPE:
			tv_extras_sensor_gyroX.setText(""+arg0.values[0]);
			tv_extras_sensor_gyroY.setText(""+arg0.values[1]);
			tv_extras_sensor_gyroZ.setText(""+arg0.values[2]);
			break;
		case Sensor.TYPE_ORIENTATION:
			tv_extras_sensor_orientationX.setText(""+arg0.values[0]);
			tv_extras_sensor_orientationY.setText(""+arg0.values[1]);
			tv_extras_sensor_orientationZ.setText(""+arg0.values[2]);
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			tv_extras_sensor_magneticX.setText(""+arg0.values[0]);
			tv_extras_sensor_magneticY.setText(""+arg0.values[1]);
			tv_extras_sensor_magneticZ.setText(""+arg0.values[2]);
			break;
		case Sensor.TYPE_LIGHT:
			tv_extras_sensor_light.setText(""+arg0.values[0]);
			break;
		case Sensor.TYPE_PROXIMITY:
			tv_extras_sensor_proximity.setText(""+arg0.values[0]);
			break;
		case Sensor.TYPE_TEMPERATURE:
			tv_extras_sensor_temperature.setText(""+arg0.values[0]);
			break;
		}
	}


}
