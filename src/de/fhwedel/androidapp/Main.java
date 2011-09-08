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
// Main-Activity, Einstiegs-Komponente
// Datei: Main.java
// Version: 1.1
//
// Created by Harald Koppay on 01.09.11.
// Copyright 2011 Harald Koppay. All rights reserved.
//
package de.fhwedel.androidapp;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import de.fhwedel.androidapp.content.ContentProviderDemo;
import de.fhwedel.androidapp.database.DBManagerClass;
import de.fhwedel.androidapp.extras.Extras;
import de.fhwedel.androidapp.intents.IntentsDemo;
import de.fhwedel.androidapp.services.ServicesDemo;

public class Main extends Activity {
	
	// Resultcodes zur Identifikation der Activity, von welcher zurückgekehrt wird
	private static final int PREFERENCES_REQUESTCODE = 9854752;
	private static final int INTENTS_REQUESTCODE = 89899889;
	private static final int SERVICES_REQUESTCODE = 78786546;
	private static final int PROVIDER_REQUESTCODE = 656546454;
	
	// Öffentliche Variable für die Bereitstellung des Package-Namen
	public static String MAIN_PACKAGE_NAME; 
	// Variable für die Bereitstellung der SharedPreferences
	private SharedPreferences prefs;
	
	// Aktueller Benutzername
	private String benutzerAktuell;
	
	// Variablen für die Level-Steuerung
	private int demoLevel;
	private boolean enableNextLevelController = false;
	private boolean levelUp=false;
	private boolean levelCtrlEnabled = true;
	private boolean helpPopUpsEnabled = true;
	private boolean showActivityName = false;
	
	// GUI-Elemente
	private TextView tv_main_hello;
	private TextView tv_main_level;
	private Button btn_main_hello;
	private Button btn_main_intents;
	private Button btn_main_provider;
	private Button btn_main_services;
	private Button btn_main_extras;
	private TextView tv_main_activity;
	
	// Datenbank-Zugriff
	private DBManagerClass mDBManager;
	private SQLiteDatabase mDatabase;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d("Main","onCreate aufgerufen");
        super.onCreate(savedInstanceState);
        
        MAIN_PACKAGE_NAME = getPackageName();
        // Ein DBManager für die internne User-DB wird initialisiert
        mDBManager = new DBManagerClass(this);
        // Preferences-Datei wird geladen
        prefs = getSharedPreferences(MAIN_PACKAGE_NAME+"_preferences", MODE_PRIVATE);
        
        //	Interner User-DB wird aktueller Benutzer angefügt
        String just_logged_in = prefs.getString("benutzer", null);
        if (just_logged_in != null){ writeUserToDB(just_logged_in); }
        
        // Das Layout activity_main.xml wird geladen und sichtbar
        setContentView(R.layout.activity_main);
        
        // GUI-Elemente werden geladen, so dass sie zugreifbar werden.
        tv_main_hello = (TextView) findViewById(R.id.tv_main_hello);
    	tv_main_level = (TextView) findViewById(R.id.tv_main_level);
    	tv_main_activity = (TextView) findViewById(R.id.tv_main_activity);
    	btn_main_hello = (Button) findViewById(R.id.btn_main_hello); 
    	btn_main_intents = (Button) findViewById(R.id.btn_main_intents);
    	btn_main_provider = (Button) findViewById(R.id.btn_main_provider);
    	btn_main_services = (Button) findViewById(R.id.btn_main_services);
    	btn_main_extras = (Button) findViewById(R.id.btn_main_extras);
    }
    
    // Hilfmethode zum Setzen von Benutzername und Level-Daten in den entsprechenden Views
    private void placeData () {
    	Log.d("Main","placeData aufgerufen");
    	
    	// Textfelder werden mit Benutzernamen und Level gefüllt.
    	tv_main_hello.setText("Hallo "+benutzerAktuell+"!");
		tv_main_level.setText("Demo-Level "+demoLevel);
		if (showActivityName) { tv_main_activity.setText("Activity: "+getClass().getSimpleName()+".class"); }
			else { tv_main_activity.setText(""); }
    }
    
    // Hilfsmethode zum Zurücksetzen aller Benutzerdaten
    private void resetData() { 
    	Log.d("Main","resetData aufgerufen");
    	// SharedPreferences-Datei wird für den Schreibzugriff vorbereitet
    	SharedPreferences.Editor editor = getSharedPreferences(MAIN_PACKAGE_NAME+"_preferences", MODE_PRIVATE).edit();
		demoLevel=1;
		benutzerAktuell="Welt";
		// Benutzerdaten werden im Ursprungszustand in die Preferences-Datei geschrieben.
		editor.putString("benutzer", "Welt")
			.putString("lvl",""+demoLevel)
			.putBoolean("enable_lvl_ctrl", true)
			.putBoolean("enable_help_popups", true)
			.putBoolean("show_activity", false).commit();
    }
    
    // Hilfsmethode, um die Steuerungselemete entsprechend des aktuellen Levels zu setzen.
	private void setLevelDependingControls () {
		Button btn = new Button(this);
		Log.d("Main","setLevelDependingControls aufgerufen");
		btn_main_intents.setVisibility(android.view.View.GONE);
		btn_main_intents.setEnabled(true);
		btn_main_services.setVisibility(android.view.View.GONE);
		btn_main_services.setEnabled(true);
		btn_main_provider.setVisibility(android.view.View.GONE);
		btn_main_provider.setEnabled(true);
		btn_main_extras.setVisibility(android.view.View.GONE);
		btn_main_extras.setEnabled(true);
		if (demoLevel >= 2) {
			btn = btn_main_intents;
			btn.setVisibility(android.view.View.VISIBLE);
		}
		if (demoLevel >= 3) {
			btn = btn_main_services;
			btn.setVisibility(android.view.View.VISIBLE);
		}	
		if (demoLevel >= 4) {
			btn = btn_main_provider;
			btn.setVisibility(android.view.View.VISIBLE);
		}	
		if (demoLevel >= 5) {
			btn = btn_main_extras;
			btn.setVisibility(android.view.View.VISIBLE);
		}	
		if (btn != null && demoLevel != 1) {
			btn_main_hello.setText("Hallo Android zum "+demoLevel+".");
			if (levelCtrlEnabled) {	btn.setEnabled(enableNextLevelController); }
		} else {
			btn_main_hello.setText("Hallo Android");
		}
	}
	
	// Hilfsmethode um die App in den Ursprungszustand zurück zu versetzen.
	private void restartApplication () {
		Log.d("Main","restartApplication aufgerufen");
		
		// Der Intent, der diese Activity gestartet hat wird geholt und erneut abgeschickt
		Intent intent = this.getIntent();
		// Diese Komponente wird beendet
		this.finish();
		startActivity(intent);
	}

	// Lebenszyklus-Methode onStart()
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		Log.d("Main","onStart aufgerufen");
		super.onStart();
		
	}

	// Lebenszyklus-Methode onResume()
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.d("Main","onResume aufgerufen");
		super.onResume();
		
		// Level und Benutzername sowie weitere Zustandsdaten werden aus dem Preferences-File geladen 
		// und den entsprechenden Klassen-Attributen zugewiesen.
		prefs = getSharedPreferences(MAIN_PACKAGE_NAME+"_preferences", MODE_PRIVATE);
		showActivityName = prefs.getBoolean("show_activity", false);
    	benutzerAktuell = prefs.getString("benutzer", "Welt");
        demoLevel = Integer.valueOf(prefs.getString("lvl", "1")).intValue();
        levelCtrlEnabled=prefs.getBoolean("enable_lvl_ctrl", true);
        helpPopUpsEnabled=prefs.getBoolean("enable_help_popups", true);
        enableNextLevelController = prefs.getBoolean("nextlevel", false);
        
        // Level-Kontrolle
        if (levelUp){
        	demoLevel++;
        	enableNextLevelController=false;
        	levelUp=false;
        }
        
        // Hilfsmethoden zum Füllen der TextViews 
        // und zum Setzen der levelabhängigen Kontrollelemente werden aufgerufen
		placeData();
		setLevelDependingControls();
	}

	// Lebenszyklus-Methode onPause()
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.d("Main","onPause aufgerufen");
		
		// Aktueller Zustand der Level- und der enableNextLevelController-Variable werden gespeichert.
		prefs = getSharedPreferences(MAIN_PACKAGE_NAME+"_preferences", MODE_PRIVATE);
    	SharedPreferences.Editor editor = prefs.edit(); 
		editor.putString("lvl",""+demoLevel).putBoolean("nextlevel", enableNextLevelController).commit();
		
		super.onPause();
	}

	// Lebenszyklus-Methode onDestroy()
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.d("Main","onDestroy aufgerufen");
		super.onDestroy();
	}


	// Automatische Zustandssicherung im Falle einer Systembedingten Beendigung 
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		Log.d("Main","onSaveInstance aufgerufen");
		super.onSaveInstanceState(outState);
	}
	
	// Methode zum Erstellen des Optionen-Menüs, eimalig und zu Beginn aufgerufen
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		Log.d("Main","onCreateOptionsMenu aufgerufen");
		
		// Das Optionen-Menü wird erstellt und mit dem Layout menu_main.xml geladen
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	// Behandlung der Benutzer-Auswahl im Optionen-Menü
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.men_main_preferences:
			// Preferences Activity wird gestartet.
			startActivityForResult((new Intent(this,Preferences.class)), PREFERENCES_REQUESTCODE);
			return true;
		case R.id.men_main_reset:
			// Hilfsmethoden zum Neustarten der App werden gerufen
			resetData();
			restartApplication();
			return true;
		case R.id.men_main_exit:
			// finish() beendet die aktuelle Activity
			finish();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	// Es wird von einer aufgerufenen Activity zurückgekehrt, die
	// über den requestCode identifiziert werden kann.
	// requestCode-abhängige Maßnahmen können ergriffen werden.
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("Main","onActivityResult aufgerufen");
		
		switch (requestCode) {
		case PREFERENCES_REQUESTCODE: 
			// Hilfsmethode für die Rückkehr aus der Preferences-Activity wird aufgerufen
			returnFromPreferencesMenu();
			break;
		case INTENTS_REQUESTCODE:
			if (demoLevel == 2) {
				levelUp=true;
			}
			break;
		case SERVICES_REQUESTCODE:
			if (demoLevel == 3) {
				levelUp=true;
			}
			break;
		case PROVIDER_REQUESTCODE:
			if (demoLevel == 4) {
				levelUp=true;
			}
			break;
		}
	}
	
	// Hilfsmethode zur Durchführung von Maßnahmen, die nach Rückkehr von den Preferences ergriffen
	// werden sollen.
	private void returnFromPreferencesMenu () {
		Log.d("Main", "returnFromPreferencesMenu aufgerufen");
		// Benutzername aus den SharedPreferences lesen
		String benutzer = getSharedPreferences(MAIN_PACKAGE_NAME+"_preferences", MODE_PRIVATE).getString("benutzer", "Welt");
		// Ein neuer Benutzername wurde eingegeben
		if (! benutzer.equals(benutzerAktuell)) {
			// Neuen Benutzernamen in die lokale DB schreiben
			writeUserToDB(benutzer);
			// Toast mit entsprechender Nachricht ausgeben
			Toast.makeText(this, "Benutzer '"+benutzerAktuell+"' durch Benutzer '"+benutzer+"' ausgetauscht!", Toast.LENGTH_LONG).show();
			benutzerAktuell=benutzer;
			// Levelsteuerung
			if (demoLevel == 1) levelUp=true;
		}
	}
	
	// Hilfsmethode zum Schreiben des aktuellen Benutzernamens und den aktuellen Zeitpunkt
	// in die lokale Datenbank
	// in: String benutzer: Name des Benutzers, der in die DB geschrieben werden soll
	private void writeUserToDB (String benutzer){
		// Der DB-Manager liefert eine beschreibbare Instanz der SQLite-Datenbank
		mDatabase = mDBManager.getWritableDatabase();
		// Datum und Zeit werden für den Eintrag als String in die DB vorbereitet
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss"); 
		Date date = new Date();
		ContentValues mValues = new ContentValues();
		// ContentValues werden gesetzt...
		mValues.put("user", benutzer);
		mValues.put("modified", "Login: "+dateFormat.format(date));
		// ...und in die DB überführt
		mDatabase.insert("user", null, mValues);
		mDatabase.close();
	}
	
	// Event-Handler-Methode für das Benutzer-Event Button-Click auf einem Button
	// der grafischen Oberfläche
	// in: View view: View-Element, welches angeklickt wurde. In diesem Fall immer ein ButtonView.
	public void onButtonClick (final View view) {
		Log.d("Main", "onButtonClick aufgerufen");
		// Vorbereiten des Intents, welcher zum Starten einer neuen Activity verwendet wird
		Intent intent = new Intent();
		intent.putExtra("popup", helpPopUpsEnabled);
		switch (view.getId()) {
		// Hello-Button wurde geklickt, Anweisungs-Dialog öffnen
		case R.id.btn_main_hello:
			helloRoutine();
			break;
		// Button "Activities & Intents"
		case R.id.btn_main_intents:
			intent.setClass(this, IntentsDemo.class);
			startActivityForResult(intent, INTENTS_REQUESTCODE);
			break;
		// Button "Broadcasts & Services"
		case R.id.btn_main_services:
			intent.setClass(this, ServicesDemo.class);
			startActivityForResult(intent, SERVICES_REQUESTCODE);
			break;
		// Button "Content Provider"
		case R.id.btn_main_provider:
			intent.setClass(this, ContentProviderDemo.class);
			startActivityForResult(intent, PROVIDER_REQUESTCODE);
			break; 	
		// Button "Extras"
		case R.id.btn_main_extras:
			intent.setClass(this, Extras.class);
			startActivity(intent);
			break;
		}
	}
    
	// Methode erstellt die Anweisungs-Dialoge, welche nach dem Betätigen
	// des "Hello"-Buttons erscheinen.
	public void helloRoutine () {
		// Klasse Builder hilft bei der Erstellung des Dialoges
		Builder builder = new Builder(this);
		// Text für das Dialogfenster wird in Abhängigkeit vom Demo-Level aus der strings.xml geladen
		switch (demoLevel){
		case 1:
			builder.setTitle(R.string.txt_main_dialog_title)
			.setView(getLayoutInflater().inflate(R.layout.dialog_hello, null));
			break;
		case 2:
			builder.setTitle(R.string.txt_main_dialog_title2)
			.setView(getLayoutInflater().inflate(R.layout.dialog_hello2, null));
			break;
		case 3:
			builder.setTitle(R.string.txt_main_dialog_title3)
			.setView(getLayoutInflater().inflate(R.layout.dialog_hello3, null));
			break;
		case 4:
			builder.setTitle(R.string.txt_main_dialog_title4)
			.setView(getLayoutInflater().inflate(R.layout.dialog_hello4, null));
			break;
		case 5:
			builder.setTitle(R.string.txt_main_dialog_title5)
			.setView(getLayoutInflater().inflate(R.layout.dialog_hello5, null));
			break;
		}	
		// Kontroll-ELemente für das nächste Level anzeigen
		enableNextLevelController=true;
		
		// Icon und Button des Dialoges werden gesetzt bevor er angezeigt wird
		builder.setIcon(android.R.drawable.ic_dialog_info)
		.setPositiveButton("OK", null)
		.show();
		
		placeData();
		setLevelDependingControls();
	}
    
}