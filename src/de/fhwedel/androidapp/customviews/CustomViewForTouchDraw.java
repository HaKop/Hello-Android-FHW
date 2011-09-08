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
// CustomView für Touch-aktivierte Drawing-Operationen
// Datei: CustomViewForTouchDraw.java
// Version: 1.1
//
// Created by Harald Koppay on 01.09.11.
// Copyright 2011 Harald Koppay. All rights reserved.
//
package de.fhwedel.androidapp.customviews;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class CustomViewForTouchDraw extends View implements OnTouchListener {
	
	List<Circle> circles = new ArrayList<Circle>();
	Paint paint = new Paint();
	Random randomGen = new Random();
	final static int[] colors = {Color.CYAN,Color.WHITE,Color.WHITE,Color.WHITE,Color.GREEN,
									Color.MAGENTA, Color.RED, Color.GRAY, Color.YELLOW, Color.BLACK};
	

	public CustomViewForTouchDraw(Context context, AttributeSet attr) {
		// TODO Auto-generated constructor stub
		super(context, attr);
		setFocusable(true);
		setFocusableInTouchMode(true);

		this.setOnTouchListener(this);

		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		
	}

	@Override
	public void onDraw(Canvas canvas) {
		for (Circle circle : circles) {
			paint.setColor(colors[circle.c]);
			canvas.drawCircle(circle.x, circle.y, circle.d, paint);
			circle.d+=5;
			// Log.d(TAG, "Painting: "+point);
			invalidate();
		}
	}

	public boolean onTouch(View view, MotionEvent event) {
		// if(event.getAction() != MotionEvent.ACTION_DOWN)
		// return super.onTouchEvent(event);
		Circle circle = new Circle();
		circle.x = event.getX();
		circle.y = event.getY();
		circle.d = 10;
		circle.c = randomGen.nextInt(10);
		circles.add(circle);
		//invalidate();
		return true;
	}
}

class Circle {
	float x, y, d;
	int c;

	@Override
	public String toString() {
		return x + ", " + y + ", d: "+d;
	}

	
}
