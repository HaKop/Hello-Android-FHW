//
// Dies ist die Android Demonstrations-App zu der schriftlichen Arbeit:
// "Mobile Applikationen: Grundlagen, Entwicklung und Vermarktung"
//
// Zweck der Applikation ist eine begleitende Demonstration fundamentaler Entwicklungs-Konzepte der Android-Entwicklung.
// Die zugehörigen, ausführlichen Erläuterungen sind in der schriftlichen Arbeit zu finden.
//
// Einige Konzepte, die hier demonstriert werden:
// * Activities
// * Intents und Intent-Filter
// * Broadcast Intents und Broadcast Receiver
// * Services, Threads, Prozesse
// * Content Provider und SQLite Datenbank
// außerdem:
// Grafik, Animation, Video, Hardware-Zugriffe (Sensoren), GPS, TCP-Server
//
// Eine Funktionalität dieser App ist die Bereitstellung eines TCP-Servers, welcher Kommandos von Clients zur Steuerung
// des Musik-Services entgegennehmen kann. Die entsprechenden TCP-Clients werden in der iOS- und Windows Phone App realisiert.
//
// Komponenten dieses Android-Projektes:
// * Main-Activity, Einstiegs- und Kern-Komponente
// * Wichtige Activity-Klassen: PreferencesActivity, IntentsDemo-Activity (Activities & Intents), 
//   ServiceDemo-Activity (Broadcast Receiver & Services), ContentProviderDemo-Activity
// * Wichtige Service-Klassen: SimpleService (Local Service), ComplexService (Remote Service, Musik Service)
// * ContentProvider-Klasse für Benutzer-DB
// * DBManager-Klasse (SQLiteOpenHelper) für Benutzer-DB
// * CustomView-Klassen, für individuelle Views
// * Manifest-Datei
// * Diverse Layout-Dateien und Ressourcen
//
// Dieser Teil:
// ContenProvider-Activity
// Datei: ContentProviderDemo.java
// Version: 1.1
//
// Created by Harald Koppay on 01.09.11.
// Copyright 2011 Harald Koppay. All rights reserved.
//
package de.fhwedel.androidapp.content;

import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import de.fhwedel.androidapp.Main;
import de.fhwedel.androidapp.R;

public class ContentProviderDemo extends ListActivity {
	
	// Content-Resolver
	private ContentResolver music_cr;
	private ContentResolver user_cr;
	// Cursors
	private Cursor cursorMusicProvider;
	private Cursor cursorUserProvider;
	// Adapters
	private SimpleCursorAdapter adapterMusicList;
	private SimpleCursorAdapter adapterUserList;
	// URIs
	final static Uri music_uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	final static Uri user_uri = ContentProviderUserDB.CONTENT_URI;
	// MediaPlayer für die Wiedergabe von ausgewählten Musik-Dateien
	MediaPlayer mPlayer = null;
	// Switch Music Provider <-> User DB Provider
	private Boolean CHOICE_MUSIC =false;
	// Variable für die Bereitstellung der SharedPreferences
	private SharedPreferences prefs;
	// Steuerung der Activity-Namensanzeige
	private boolean showActivityName;
	// GUI-Elemente
	private TextView tv_provider_activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// Laden des XML-Layouts
		setContentView(R.layout.activity_content_contentproviderdemo);
		// Laden der GUI-Elemente
		tv_provider_activity = (TextView) findViewById(R.id.tv_provider_activity);
		
		// ContentResolver holen und Anfrage an den Media-Library-Provider starten
		music_cr = getContentResolver();
		cursorMusicProvider = music_cr.query(music_uri, 
				new String[] {Media._ID,Media.ARTIST,Media.TITLE,Media.ALBUM},
				null, 
				null, 
				null);
		// Ergebniscursor automatisch verwalten lassen
		startManagingCursor(cursorMusicProvider);
		// Verbindung von Ergebniscursor und Listen-Dartellung in der Activity
		// mittels eines Adapters herstellen
		// Layout für die Liste ist in der music_list_item.xml definiert
		adapterMusicList = new SimpleCursorAdapter(this, R.layout.music_list_item, 
				cursorMusicProvider, 
				new String[] { Media.ARTIST, Media.TITLE, Media.ALBUM }, 
				new int[] { R.id.txt_music_layout_interpret, 
							R.id.txt_music_layout_title,
							R.id.txt_music_layout_album});
		
		// ContentResolver holen und Anfrage an den Benutzer-DB-Provider starten
		// Benutzer-DB-Provider ist in Klasse ContentProviderUserDB definiert
		user_cr = getContentResolver();
		cursorUserProvider = user_cr.query(user_uri, 
				new String[] {"_id","user","modified"},
				null, 
				null, 
				"modified DESC");
		// Ergebniscursor automatisch verwalten lassen
		startManagingCursor(cursorUserProvider);
		// Verbindung von Ergebniscursor und Listen-Dartellung in der Activity
		// mittels eines Adapters herstellen
		adapterUserList = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, 
				cursorUserProvider, 
				new String[] {"user","modified"}, 
				new int[] { android.R.id.text1, android.R.id.text2});
		
		// Steuerung Begrüßungs-PopUps
		if(getIntent().getExtras().getBoolean("popup")) {hello();}
	}
	
	// Erstellung des Begrüßungs-PopUps (Anleitung und Informationen)
	private void hello(){
		Builder builder = new Builder(this);
		builder.setTitle(R.string.txt_main_dialog_titleContent)
			.setView(getLayoutInflater().inflate(R.layout.dialog_provider, null));
		builder.setIcon(android.R.drawable.ic_dialog_info)
		.setPositiveButton("OK", null)
		.show();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// MediaPlayer-Instanz wird freigegeben, Wiedergabe gestoppt
		if (mPlayer != null && mPlayer.isPlaying()){
			mPlayer.stop();
		}
		mPlayer = null;
		super.onDestroy();
	}

	// Event-Handler-Methode für Click-Events in der Listen-Ansicht
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// Findet der Click in der Media-Provider-Liste statt, so soll das ausgewählte
		// Musikstück abgespielt werden
		if (CHOICE_MUSIC) {
			Log.d("Music Content", "Listview pos: "+position+" ID: "+id);
			// Erstellung der URI anhand der Listen-ID
			Uri uriWithID = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,id);
			if (mPlayer != null && mPlayer.isPlaying()){
				mPlayer.stop();
				mPlayer = null;
			}
			mPlayer = MediaPlayer.create(this, uriWithID);
			mPlayer.start(); 
		}
		super.onListItemClick(l, v, position, id);
	}
	
	// Event-Handler-Methode für Auswahl-Events in der RadioButton-Gruppe
	public void onRadioButtonClick (final View view){
		switch (view.getId()){
		// Auswahl Music-Provider
		case R.id.rb_provider_music:
			CHOICE_MUSIC = true;
			// entsprechender Adapter wird an Listenansicht angeheftet
			setListAdapter(adapterMusicList);
			break;
		// Auswahl User-DB-Provider
		case R.id.rb_provider_users:
			CHOICE_MUSIC = false;
			// entsprechender Adapter wird an Listenansicht angeheftet
			setListAdapter(adapterUserList);
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		prefs = getSharedPreferences(Main.MAIN_PACKAGE_NAME+"_preferences", MODE_PRIVATE);
		// Steuerung der Activity-Namensanzeige
		showActivityName = prefs.getBoolean("show_activity", false);
		if (showActivityName) { tv_provider_activity.setText("ListActivity: "+getClass().getSimpleName()+".class"); }
		else { tv_provider_activity.setText(""); }
	}

}
