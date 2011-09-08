package de.fhwedel.androidapp.services;

import java.util.Random;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;

public class ComplexService extends Service {
	
	public static final int MUSIC_PLAY = 101;
	public static final int MUSIC_STOP = 102;
		
	private MediaPlayer mPlayer = null;
	
	private Message msgSave = null;
	
	private Handler incomingHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Log.d("ComplexService","Handler reached");
			msgSave = Message.obtain(msg);
			switch (msg.what) {
			case MUSIC_PLAY:
				musicPlay();
				break;
			case MUSIC_STOP:
				musicStop(false);
				break;
			default:
				super.handleMessage(msg);		
			}
		}
	};
	
	final Messenger mMessenger = new Messenger(incomingHandler);
		
	private Thread mPlayerThread = null;
	private OnCompletionListener listener = new OnCompletionListener() {
		
		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			Log.d("ComplexService","CompletionListener called");
			musicStop(false);
		}
	};

	ContentResolver cr;
	Cursor musicCursor;
	Random randGen;
	final static Uri musicURI = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		cr = getContentResolver();
		musicCursor = cr.query(musicURI,
						new String[] {Media._ID,Media.ARTIST,Media.TITLE,Media.ALBUM,Media.DURATION},
						null, null, null);
		randGen = new Random();
	}
	
	private void musicPlay(){
		if (mPlayer == null && mPlayerThread == null){
			Log.d("ComplexService","musicPlay(): preparing mPlayer & Thread");
			
			int count = musicCursor.getCount();
			int pos = randGen.nextInt(count);
			musicCursor.moveToPosition(pos);
			int playNo = musicCursor.getInt(musicCursor.getColumnIndex(Media._ID));
			String playTitle = musicCursor.getString(musicCursor.getColumnIndex(Media.TITLE));
			String playArtist = musicCursor.getString(musicCursor.getColumnIndex(Media.ARTIST));
			String playAlbum = musicCursor.getString(musicCursor.getColumnIndex(Media.ALBUM));
			Log.d("Music","Sollte spielen: "+playTitle+" mit ID: "+playNo);
			
			Uri contentURI = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,playNo);
			mPlayer = MediaPlayer.create(ComplexService.this, contentURI);
			int playDurMil = mPlayer.getDuration();
			int playDurMin = ((playDurMil/1000)/60);
			int playDurSec = ((playDurMil/1000)%60);
			
			mPlayer.setOnCompletionListener(listener);
			try {
				Message msg = Message.obtain();
				msg.what=0;
				Bundle data = new Bundle();
				data.putString("msg", "Playing: "+playTitle+", "+playDurMin+":"+String.format("%02d",playDurSec)+"min");
				data.putString("artist", "Artist: "+playArtist);
				data.putString("album", "Album: "+playAlbum);
				msg.setData(data);
				msgSave.replyTo.send(msg);
			} catch (Exception e) {
				Log.d("ComplexService","musicPlay(): SendMsgToClient failed");
				e.printStackTrace();
			}
			mPlayerThread= new Thread (){

				@Override
				public void destroy() {
					// TODO Auto-generated method stub
					Log.d("ComplexService MPlayerThread", "Thread destroyed");
					super.destroy();
				}

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Log.d("ComplexService","Thread reached, going to start Music");
					mPlayer.start();
					super.run();
				}
			};
			mPlayerThread.start();
		} else {
			musicStop(true);
		}
	}
	
	private void musicStop(boolean playNext){
		Log.d("ComplexService","musicStop(): Preparing Stop");
		if (mPlayer != null){
			mPlayer.stop();
			mPlayer.release();
		}
		mPlayer=null;
		mPlayerThread = null;
		try {
			Message msg = Message.obtain();
			msg.what=1;
			Bundle data = new Bundle();
			data.putString("msg", "Playback stopped!");
			msg.setData(data);
			msgSave.replyTo.send(msg);
		} catch (Exception e) {
			Log.e("ComplexService","musicPlay(): SendMsgToClient failed");
			e.printStackTrace();
		}
		if (playNext){ musicPlay(); }
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mMessenger.getBinder();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.d("ComplexService","onDestroy() called");
		if (mPlayer != null){
			mPlayer.stop();
			mPlayer.release();
		}
		mPlayer = null;
		mPlayerThread = null;
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		try {
			Message msg = Message.obtain();
			msg.what=1;
			Bundle data = new Bundle();
			data.putString("msg", "Music Service unbound!");
			msg.setData(data);
			msgSave.replyTo.send(msg);
		} catch (Exception e) {
			Log.e("ComplexService","musicPlay(): SendMsgToClient failed");
			e.printStackTrace();
		}
		return super.onUnbind(intent);
	}

}
