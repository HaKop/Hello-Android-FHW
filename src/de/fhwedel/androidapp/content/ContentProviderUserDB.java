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
// ContentProvider-Klasse für Benutzer-DB
// Datei: ContentProviderUserDB.java
// Version: 1.1
//
// Created by Harald Koppay on 01.09.11.
// Copyright 2011 Harald Koppay. All rights reserved.
//
package de.fhwedel.androidapp.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import de.fhwedel.androidapp.database.DBManagerClass;

public class ContentProviderUserDB extends ContentProvider {
	// URI unter welcher dieser ContentProvider für jede interessierte Anwendung erreichbar ist
	public static final Uri CONTENT_URI = Uri.parse("content://de.fhwedel.androidapp.content.userprovider");
	// DBManager- und SQL-DB-Objekte für den Zugriff auf die User-DB
	private DBManagerClass mDBManager;
	private SQLiteDatabase mDatabase;
	// Bekanntmachung der Tabellen-Spalten
	public static String[] CONTENT_COLUMNS = {"_id","user","modified"};

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not supported by this provider"); 
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not supported by this provider"); 
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not supported by this provider"); 
	}

	// DBManager wird initialisiert
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		mDBManager = new DBManagerClass(this.getContext());
		return true;
	}
	
	// Methode für den Zugriff auf die zugrundelegende Benutzer-Datenbank
	// Ergebnis wird in einem Cursor zurückgegeben
	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		// TODO Auto-generated method stub
		// lesender Zugriff auf Benutzer-DB
		mDatabase = mDBManager.getReadableDatabase();
		// einfache DB-Abfrage mit Weiterleitung der Parameter 
		Cursor c = mDatabase.query( "user", 
									arg1, 
									arg2, 
									arg3, 
									null, 
									null, 
									arg4);
		c.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
		//mDatabase.close();
		return c;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not supported by this provider"); 
	}

}
