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
// IntentsDemo-Activity (Activities & Intents)
// Datei: IntentsDemo.java
// Version: 1.1
//
// Created by Harald Koppay on 01.09.11.
// Copyright 2011 Harald Koppay. All rights reserved.
//
package de.fhwedel.androidapp.intents;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import de.fhwedel.androidapp.Main;
import de.fhwedel.androidapp.R;

public class IntentsDemo extends Activity {
	
	// Auswahl-Codes für die RadioButton-Elemente
	private static final int WEB_CHOICE = 1;
	private static final int SMS_CHOICE = 2;
	private static final int CALL_CHOICE = 3;
	private static final int GEO_CHOICE = 4;
	private static int CHOICE = 0;
	
	// Bereitstellung der SharedPrefs
	private SharedPreferences prefs;
	
	// GUI-Elemente
	private EditText ed_intent_content;
	private RadioButton rb_intent_call;
	private RadioButton rb_intent_web;
	private TextView tv_intents_activity;
	
	// Activity-Namensanzeige
	private boolean showActivityName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// Laden des XML-Layouts
		setContentView(R.layout.activity_intents_intentsdemo);
		// Laden der GUI-Elemente
		rb_intent_call = (RadioButton) findViewById(R.id.rb_intent_call);
		rb_intent_web = (RadioButton) findViewById(R.id.rb_intent_web);
		ed_intent_content = (EditText) findViewById(R.id.ed_intent_content);
		tv_intents_activity = (TextView) findViewById(R.id.tv_intents_activity);
		// Laden der Shared Preferences
		prefs=getSharedPreferences(Main.MAIN_PACKAGE_NAME+"_preferences", MODE_PRIVATE);
		SharedPreferences.Editor ed = prefs.edit();
		ed.putBoolean("show_activity", true).commit();
		
		// Steuerung der Begrüßungs-/Hilfs-PopUps
		if(getIntent().getExtras().getBoolean("popup")) {hello();}
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		prefs = getSharedPreferences(Main.MAIN_PACKAGE_NAME+"_preferences", MODE_PRIVATE);
		showActivityName = prefs.getBoolean("show_activity", false);
		// Steuerung der Activity-Namensanzeige
		if (showActivityName) { tv_intents_activity.setText("Activity: "+getClass().getSimpleName()+".class"); }
		else { tv_intents_activity.setText(""); }
	}

	// Methode zum Aufbau des Begrüßungs-/Hilfs-PopUps
	private void hello(){
		// Aufbau und Anzeige des Begrüßungs-Dialogs
		Builder builder = new Builder(this);
		builder.setTitle(R.string.txt_main_dialog_titleIntents)
			.setView(getLayoutInflater().inflate(R.layout.dialog_intents, null));
		builder.setIcon(android.R.drawable.ic_dialog_info)
		.setPositiveButton("OK", null)
		.show();
	}
	
	// Event-Handler-Methode für den Click auf das Eingabe-Textfeld
	public void onEditTextClick (final View view){
		switch (view.getId()){
		case R.id.ed_intent_content:
				if (!rb_intent_call.isChecked() && !rb_intent_web.isChecked()){
					ed_intent_content.setText("");
				}
			break;
		}
	}

	// Event-Handler-Methode für Click-Events innerhalb der RadionButton-Gruppe
	public void onRadioButtonClick (final View view){
		switch (view.getId()) {
		// RadioButton "Internet-Suche"
		case R.id.rb_intent_web:
			CHOICE = WEB_CHOICE;
			// Eingabefeld wird für Eingabe einer Internet-Adresse vorbereitet
			ed_intent_content.setText(R.string.ed_intent_WebChoice);
			ed_intent_content.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_URI);
			break;
		// RadioButton "SMS"
		case R.id.rb_intent_sms:
			CHOICE = SMS_CHOICE;
			// Eingabefeld wird geleert und für die einfache Texteingabe vorbereitet
			ed_intent_content.setText(R.string.ed_intent_SMSChoice);
			ed_intent_content.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
			break;
		// RadioButton "Anruf"
		case R.id.rb_intent_call:
			CHOICE = CALL_CHOICE;
			// Eingabefeld wird für die Eingabe einer Telefon-Nr. vorbereitet
			ed_intent_content.setText(R.string.ed_intent_CallChoice);
			ed_intent_content.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
			break;
		// RadioButton "Map-Suche"
		case R.id.rb_intent_geo:
			CHOICE = GEO_CHOICE;
			// Eingabefeld wird geleert und für die einfache Texteingabe vorbereitet
			ed_intent_content.setText(R.string.ed_intent_GeoChoice);
			ed_intent_content.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
		}
	}
	
	// Event-Handler-Methode für den Click auf den "Abschicken"-Button
	// Je nach Auswahl in der RadioButton-Gruppe wird ein impliziter Intent abgeschickt
	// Android sucht Anwendungen mit dem passenden Intent-Filter
	public void onButtonClick (final View view){
		switch (view.getId()){
		case R.id.btn_intent_submit:
			// Inhalt des Eingabefeldes wird geholt
			String content = ed_intent_content.getText().toString();
			switch (CHOICE){
			// Internet-Suche
			case WEB_CHOICE:
				// Impliziter Intent des Typs ACTION_VIEW und URI-Schema "http"
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(content)));
				break;
			// SMS
			case SMS_CHOICE:
				// Impliziter Intent des Typs ACTION_VIEW und speziellen SMS-Parametern 
				Intent intentSMS = new Intent(Intent.ACTION_VIEW);
				intentSMS.putExtra("sms_body", content);
				intentSMS.setType("vnd.android-dir/mms-sms");
				startActivity(intentSMS);
				break;
			// Telefon-Anruf
			case CALL_CHOICE:
				// Impliziter Intent des Typs ACTION_DIAL und URI-Schema "tel"
				startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+content)));
				break;
			// Map-Suche
			case GEO_CHOICE:
				// Impliziter Intent des Typs ACTION_BOEW und URI-Schema "geo"
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="+content)));
			}
		}
	}

}
