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
// CustomView für Diagramm-Darstellung der Benutzer-Logins
// Datei: CustomViewForDiagram.java
// Version: 1.1
//
// Created by Harald Koppay on 01.09.11.
// Copyright 2011 Harald Koppay. All rights reserved.
//
package de.fhwedel.androidapp.customviews;

import de.fhwedel.androidapp.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

public class CustomViewForDiagram extends View {
	
	int []values;
	int max;
	int max_width_draw;
	
	final String entry1 = "00:00-06:00";
	final String entry2 = "06:00-12:00";
	final String entry3 = "12:00-18:00";
	final String entry4 = "18:00-00:00";
		
	Paint pen = new Paint();
	
	Rect bounds_entrytext = new Rect();
	Rect bounds_hittext = new Rect();
	
	int header_time_x = 5;
	int header_time_y = 35;
	int header_hits_x;
	int header_hits_y =35;
	
	int entry_base_x = 5;
	int entry1_base_y = 70;
	int entry2_base_y;
	int entry3_base_y;
	int entry4_base_y;

	int entry_offset_y;
	
	int offset_draw_x;
	
	final int spacer_x = 5;
	final int spacer_y = 15;
	
	float multiplier_entry1;
	float multiplier_entry2;
	float multiplier_entry3;
	float multiplier_entry4;
	
	public CustomViewForDiagram (Context context, int[] values) {
		super(context);
		this.values= values;
		
		max= getMaxValue(values);
		multiplier_entry1 = (float)values[0]/max;
		multiplier_entry2 = (float)values[1]/max;
		multiplier_entry3 = (float)values[2]/max;
		multiplier_entry4 = (float)values[3]/max;
		
		pen.setTextSize(24);
		pen.getTextBounds(entry1, 0, entry1.length(), bounds_entrytext);
		pen.getTextBounds("000", 0, 3, bounds_hittext);
		
		offset_draw_x = bounds_entrytext.width()+spacer_x;
		entry_offset_y = bounds_entrytext.height()+spacer_y;
		
		header_hits_x = header_time_x+offset_draw_x;
		entry2_base_y = entry1_base_y+entry_offset_y;
		entry3_base_y = entry1_base_y+entry_offset_y*2;
		entry4_base_y = entry1_base_y+entry_offset_y*3;
	}
	
	public static int getMaxValue(int[] numbers){  
	    int maxValue = numbers[0];  
	    for(int i=1;i<numbers.length;i++){  
	        if(numbers[i] > maxValue){  
	            maxValue = numbers[i];  
	        }  
	    }  
	    return maxValue;  
	}  
	
	float anim_entry1_max;
	float anim_entry2_max;
	float anim_entry3_max;
	float anim_entry4_max;
	float anim_entry1_actual=0;
	float anim_entry2_actual=0;
	float anim_entry3_actual=0;
	float anim_entry4_actual=0;
	boolean drawingIsFinished=false;
	
	public void onDraw(Canvas c){
		
		max_width_draw = getWidth() - entry_base_x - bounds_entrytext.width()- 2*spacer_x -bounds_hittext.width()-5;
		anim_entry1_max=multiplier_entry1*max_width_draw;
		anim_entry2_max=multiplier_entry2*max_width_draw;
		anim_entry3_max=multiplier_entry3*max_width_draw;
		anim_entry4_max=multiplier_entry4*max_width_draw;
		
		
		pen.setColor(getResources().getColor(R.color.white));
		
		pen.setAntiAlias(true);
		pen.setTextSize(28);
		c.drawText("Uhrzeit", header_time_x, header_time_y, pen);
		c.drawText("Logins", header_hits_x, header_hits_y, pen);
		pen.setTextSize(24);
		
		c.drawText(entry1, entry_base_x, entry1_base_y, pen);
		c.drawRect(entry_base_x+offset_draw_x, entry1_base_y, entry_base_x+offset_draw_x+anim_entry1_actual, entry1_base_y-(entry_offset_y-spacer_y), pen);

		c.drawText(entry2, entry_base_x, entry2_base_y, pen);
		c.drawRect(entry_base_x+offset_draw_x, entry2_base_y, entry_base_x+offset_draw_x+anim_entry2_actual, entry2_base_y-(entry_offset_y-spacer_y), pen);
		
		
		c.drawText(entry3, entry_base_x, entry3_base_y, pen);
		c.drawRect(entry_base_x+offset_draw_x, entry3_base_y, entry_base_x+offset_draw_x+anim_entry3_actual, entry3_base_y-(entry_offset_y-spacer_y), pen);

		c.drawText(entry4, entry_base_x, entry4_base_y, pen);
		c.drawRect(entry_base_x+offset_draw_x, entry4_base_y, entry_base_x+offset_draw_x+anim_entry4_actual, entry4_base_y-(entry_offset_y-spacer_y), pen);
		
		drawingIsFinished=true;
		if (anim_entry1_actual<anim_entry1_max){ anim_entry1_actual += 8; drawingIsFinished=false; }
			else { c.drawText(values[0]+"", entry_base_x+offset_draw_x+anim_entry1_max+spacer_x, entry1_base_y, pen); }
		if (anim_entry2_actual<anim_entry2_max){ anim_entry2_actual += 8; drawingIsFinished=false; } 
			else { c.drawText(values[1]+"", entry_base_x+offset_draw_x+anim_entry2_max+spacer_x, entry2_base_y, pen); }
		if (anim_entry3_actual<anim_entry3_max){ anim_entry3_actual += 8; drawingIsFinished=false; }
			else { c.drawText(values[2]+"", entry_base_x+offset_draw_x+anim_entry3_max+spacer_x, entry3_base_y, pen); }
		if (anim_entry4_actual<anim_entry4_max){ anim_entry4_actual += 8; drawingIsFinished=false; } 
			else { c.drawText(values[3]+"", entry_base_x+offset_draw_x+anim_entry4_max+spacer_x, entry4_base_y, pen); }
		try{ Thread.sleep(50); }
		catch(Exception e){ Log.e("Thread","Problem while sleeping");}
		if (!drawingIsFinished) {invalidate();}
	}

}
