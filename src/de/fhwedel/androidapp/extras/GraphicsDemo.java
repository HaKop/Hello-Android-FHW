package de.fhwedel.androidapp.extras;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.VideoView;
import de.fhwedel.androidapp.Main;
import de.fhwedel.androidapp.R;
import de.fhwedel.androidapp.content.ContentProviderUserDB;
import de.fhwedel.androidapp.customviews.CustomViewForDiagram;
import de.fhwedel.androidapp.customviews.CustomViewForTouchDraw;

public class GraphicsDemo extends Activity {
	
	private int[] values = {0,0,0,0};
	
	private String[] times_sql = {
		"modified LIKE '%, 00:%' OR modified LIKE '%, 01:%' OR modified LIKE '%, 02:%' OR modified like '%, 03:%' OR modified like '%, 04:%' OR modified like '%, 05:%'",
		"modified LIKE '%, 06:%' OR modified like '%, 07:%' OR modified like '%, 08:%' OR modified like '%, 09:%' OR modified like '%, 10:%' OR modified like '%, 11:%'",
		"modified LIKE '%, 12:%' OR modified like '%, 13:%' OR modified like '%, 14:%' OR modified like '%, 15:%' OR modified like '%, 16:%' OR modified like '%, 17:%'",
		"modified LIKE '%, 18:%' OR modified like '%, 19:%' OR modified like '%, 20:%' OR modified like '%, 21:%' OR modified like '%, 22:%' OR modified like '%, 23:%'"};
	
	private SharedPreferences prefs;
	
	private CustomViewForDiagram view2d;
	private CustomViewForTouchDraw view2dTouch;
	private ImageView imgForAnim;
	private boolean showActivityName;
	private TextView tv_graphics_activity;
	private VideoView vv_media_video;
	private ToggleButton btn_media_togglevideo;
	private ToggleButton btn_extras_graphics_togglediagram;
	private ToggleButton btn_extras_graphics_toggletouch;
	private ToggleButton btn_extras_graphics_toggleanim;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_extras_graphicsdemo);
		
		btn_extras_graphics_togglediagram = (ToggleButton) findViewById(R.id.btn_extras_graphics_togglediagram);
		btn_extras_graphics_toggletouch = (ToggleButton)findViewById(R.id.btn_extras_graphics_toggletouch);
		btn_extras_graphics_toggleanim = (ToggleButton)findViewById(R.id.btn_extras_graphics_toggleanim);
		view2dTouch = (CustomViewForTouchDraw)findViewById(R.id.cv_extras_graphics_touch);
		imgForAnim = (ImageView)findViewById(R.id.img_extras_graphics_android);	
		vv_media_video = (VideoView) findViewById(R.id.vv_media_video);
		btn_media_togglevideo =(ToggleButton)findViewById(R.id.btn_media_togglevideo);
		

		vv_media_video.setMediaController(new MediaController(this));
		vv_media_video.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.vid));
		
		ContentResolver cr = getContentResolver();
		Cursor c = null;
		for (int i=0; i<times_sql.length;i++ ){
			c = cr.query(ContentProviderUserDB.CONTENT_URI,
							ContentProviderUserDB.CONTENT_COLUMNS, 
							times_sql[i], 
							null, null);
			values[i]=c.getCount();
		}
		startManagingCursor(c);
		
		view2d = new CustomViewForDiagram(this, values);
		view2d.setMinimumHeight(250);
		
		tv_graphics_activity = (TextView) findViewById(R.id.tv_graphics_activity);
		((LinearLayout) findViewById(R.id.lay_2d_demo)).addView(view2d);
		
		if(getIntent().getExtras().getBoolean("popup")) {hello();}
	}
	
	private void hello(){
		Builder builder = new Builder(this);
		builder.setTitle(R.string.txt_extras_dialog_titleGraphics)
			.setView(getLayoutInflater().inflate(R.layout.dialog_graphics, null));
		builder.setIcon(android.R.drawable.ic_dialog_info)
		.setPositiveButton("OK", null)
		.show();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		prefs = getSharedPreferences(Main.MAIN_PACKAGE_NAME+"_preferences", MODE_PRIVATE);
		showActivityName = prefs.getBoolean("show_activity", false);
		if (showActivityName) { tv_graphics_activity.setText("Activity: "+getClass().getSimpleName()+".class"); }
		else { tv_graphics_activity.setText(""); }
		
		vv_media_video.setVisibility(android.view.View.GONE);
		imgForAnim.setVisibility(android.view.View.GONE);
		view2dTouch.setVisibility(android.view.View.GONE);
		view2d.setVisibility(android.view.View.GONE);
		btn_media_togglevideo.setChecked(false);
		btn_extras_graphics_togglediagram.setChecked(false);
		btn_extras_graphics_toggletouch.setChecked(false);
		btn_extras_graphics_toggleanim.setChecked(false);
	}

	public void onImgClick (final View view){
		final Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim);
		
		imgForAnim.startAnimation(anim);
	}
	
	public void onButtonClick (final View view){
		switch (view.getId()){
		case R.id.btn_media_togglevideo:
			if (btn_media_togglevideo.isChecked()) { 
				vv_media_video.setVisibility(android.view.View.VISIBLE);
				vv_media_video.requestFocus();
				vv_media_video.start();
			} else { 
				vv_media_video.stopPlayback();
				vv_media_video.setVisibility(android.view.View.GONE);
			}
			break;
		case R.id.btn_extras_graphics_togglediagram:
			if (btn_extras_graphics_togglediagram.isChecked()){
				view2d.setVisibility(android.view.View.VISIBLE);
			} else {
				view2d.setVisibility(android.view.View.GONE);
			}
			break;
		case R.id.btn_extras_graphics_toggletouch:
			if (btn_extras_graphics_toggletouch.isChecked()){
				view2dTouch.setVisibility(android.view.View.VISIBLE);
			} else {
				view2dTouch.setVisibility(android.view.View.GONE);
			}
			break;
		case R.id.btn_extras_graphics_toggleanim:
			if (btn_extras_graphics_toggleanim.isChecked()){
				imgForAnim.setVisibility(android.view.View.VISIBLE);
			} else {
				imgForAnim.setVisibility(android.view.View.GONE);
			}
			break;
		}
	}

}
