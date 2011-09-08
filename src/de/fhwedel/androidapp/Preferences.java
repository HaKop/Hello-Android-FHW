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
// PreferencesActivity, Benutzeroberfläche für Einstellungen in den SharedPreferences
// Datei: Preferences.java
// Version: 1.1
//
// Created by Harald Koppay on 01.09.11.
// Copyright 2011 Harald Koppay. All rights reserved.
//
package de.fhwedel.androidapp;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;


public class Preferences extends PreferenceActivity {
	
	// GUI-Elemente
	private CheckBoxPreference pref_enableLvlCtrl;
	private CheckBoxPreference pref_enableHelpPopUps;
	private CheckBoxPreference pref_activityname;
	private ListPreference pref_level;
	
	// Implementierung eines PreferencesChangeListeners
	private OnPreferenceChangeListener changeListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference arg0, Object arg1) {
			// TODO Auto-generated method stub
			// Hilfs-Popups ebenfalls aktivieren wenn Level-Kontrolle aktiviert wird
			if (arg0 == pref_enableLvlCtrl) {
				if (!pref_enableLvlCtrl.isChecked()) { 
					pref_enableHelpPopUps.setChecked(true);
				}
			}
			// Activity-Namen sollen in den ersten beiden Demo-Leveln nicht angezeit werden
			// Entsprechendes Einstellungsfeld bleibt deaktiviert
			if (arg0 == pref_level){
				if (!((String)arg1).equalsIgnoreCase("1") && !((String)arg1).equalsIgnoreCase("2")) { 
					pref_activityname.setEnabled(true); }
			}
			return true;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// Layout des Preferences-Screens laden
		this.addPreferencesFromResource(R.xml.activity_preferences);
		// GUI-Elemente laden
		pref_enableLvlCtrl = (CheckBoxPreference) findPreference("enable_lvl_ctrl");
		pref_enableHelpPopUps = (CheckBoxPreference) findPreference("enable_help_popups");
		pref_activityname = (CheckBoxPreference) findPreference("show_activity");
		pref_level = (ListPreference) findPreference("lvl");
		// ChangeListener für LevelKontroll- und Level-Auswahl-Element aktivieren 
		pref_enableLvlCtrl.setOnPreferenceChangeListener(changeListener);
		pref_level.setOnPreferenceChangeListener(changeListener);
		// Activity-Namen sollen in den ersten beiden Demo-Leveln nicht angezeit werden
		// Entsprechendes Einstellungsfeld bleibt deaktiviert
		if (!pref_level.getValue().equalsIgnoreCase("1") && !pref_level.getValue().equalsIgnoreCase("2")) { 
			pref_activityname.setEnabled(true); }
	}

	
	
	

}
