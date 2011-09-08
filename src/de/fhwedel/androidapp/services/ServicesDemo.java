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
// ServiceDemo-Activity (Broadcast Receiver & Services)
// TCP-Server Komponente
// Datei: ServicesDemo.java
// Version: 1.1
//
// Created by Harald Koppay on 01.09.11.
// Copyright 2011 Harald Koppay. All rights reserved.
//
package de.fhwedel.androidapp.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import de.fhwedel.androidapp.Main;
import de.fhwedel.androidapp.R;

public class ServicesDemo extends Activity {
	
	// Intent-Filter für den Empfang von SMS-Nachrichten
	public final static IntentFilter FILTER_SMS = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
	
	/* **************************************************************************************************/
	/* **************************************************************************************************/
	/* **************************************************************************************************/
	/*  BroadcastReceiver für SMS
	/* 
	/*  Ein dynamischer BroadcastReceiver wird registriert 
	/*  und wartet fortan auf das System-Ereignis "SMS empfangen".
	/*
	/*
	/*  [ANFANG] 
	 */
	
	// Receiver ist zurzeit am System registriert?
	private Boolean receiverSMSRegistered = false;
	
	// Implementierung eines BroadcastReceivers der auf den BroadcastIntent "SMS empfangen" wartet
	private BroadcastReceiver receiverSMS = new BroadcastReceiver() {
		@Override
		// Wird eine SMS empfangen, so wird die onReceive-Methode gerufen
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Bundle bundle = intent.getExtras();        
	        String str = "";            
	        if (bundle != null) {
	            // SMS-Inhalte werden aus dem Extras-Bundle geholt
	            Object[] pdus = (Object[]) bundle.get("pdus");
	            SmsMessage msg = SmsMessage.createFromPdu((byte[])pdus[0]);                
	            str += "SMS from " + msg.getOriginatingAddress();                     
	            str += " :";
	            str += msg.getMessageBody().toString();   
	            // Ausgabe des SMS-Inhaltes als Toast-Message
	            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	        }                         
		}
	};
	/*  BroadcastReceiver für SMS
	/*  [ENDE] 
	/****************************************************************************************************/
	/****************************************************************************************************/
	/****************************************************************************************************/
	
	
	
	
	/* **************************************************************************************************/
	/* **************************************************************************************************/
	/* **************************************************************************************************/
	/*  Einfacher Local Service mit BroadcastReceiver & eigenem BroadcastIntent
	 * 
	 *  Ein einfacher Local Service wird gestartet, der jede Sekunde einen eigenen BroadcastIntent 
	 *  mit der Nachricht "Service läuft seit x Sekunden" verschickt.
    /*	Gleichzeitig wird ein BroadcastReceiver registriert, der genau diesen BroadcastIntent abfängt 
     *  und dessen Nachricht hier in der Activity ausgibt.
     *
	 *
	/*  [ANFANG] 
	 */
	
	// Local Service läuft?
	private Boolean simpleServiceRunning =false;
	// Broadcast Receiver ist am System registriert?
	private Boolean receiverSimpleServiceRegistered =false;
	
	// Implementierung des BroadcastReceivers, der auf den eigenen Broadcast Intent 
	// "de.fhwedel.androidapp.intent.action.SERVICE_BC" wartet
	private BroadcastReceiver receiverSimpleService = new BroadcastReceiver() {
		
		// Bei Empfang des Broadcast Intents, wird die enthaltene Nachricht im entsprechenden 
		// TextView ausgegeben
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			txt_services_service1msg.setText(intent.getExtras().getString("Message"));
		}
	};
	/*  Einfacher Service mit BroadcastReceiver
	/*  [ENDE] 
	/****************************************************************************************************/
	/****************************************************************************************************/
	/****************************************************************************************************/
	
	
	
	
	/* **************************************************************************************************/
	/* **************************************************************************************************/
	/* **************************************************************************************************/
	/*  Komplexer Remote Service mit Binder & BroadcastReceiver für SMS
	 * 
	 * 	Ein komplexer Remote Service wird in einem eigenen Prozess gestartet. Er ist fortan unabhängig 
	 * 	von dieser Anwendung und kann nur noch mittels Interprozess-Kommunikation angesprochen werden.
    /*	Zu diesem Zweck wurde die Binder-Schnittstelle implementiert.
    /*	Gleichzeitig wurde ein SMS-BroadcastReceiver, wie in 1. registriert. SMS mit den Inhalten 
    /* 	"MUSIC PLAY" und "MUSIC STOP" werden als Kommandos erkannt und über die Binder-Schnittstelle 
    /*	an den Remote Service gesendet.
    /*	Dieser spielt eine zufällig ausgewählte Audio-Datei aus der Medienbibliothek ab.
	 */
	/*  [ANFANG] 
	 */
	
	// Remote Service läuft bereits?
	private Boolean complexServiceRunning= false;
	// Remote Service bereits angebunden?
	private Boolean complexServiceBound = false;
	// Broadcast Receiver für SMS bereits am System registriert?
	private Boolean receiverComplexServiceRegistered= false;
	
	// ServiceConnection ist das Stellvertreter-Objekt für die Verbindung mit dem Remote Service
	// Über die Methode onServiceConnected wird der Binder des Remote Services geholt
	private ServiceConnection complexServiceConnection = new ServiceConnection() {
		@Override
		// Service wird verbunden, Binder-Objekt wird in "service" übergeben
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			// Messenger dieser Activity zum Service wird mit Binder-Objekt initialisiert
			complexServiceMessenger = new Messenger(service);
			complexServiceBound = true;
		}
		// Service-Verbindung wird beendet
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			complexServiceMessenger = null;
			complexServiceBound = false;
		}
	};
	
	// Messenger dieser Activity für ausgehende Messages zum TCP-Server-Thread hin
	public Messenger mServerReplyTo = null;
	// Implementierung des Message-Handlers für den Message-Receiver dieser Activity
	// (für eingehende Messages vom Remote Service) 
	private Handler incomingHandler= new Handler () {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
			// Eingehende Message ist eine Erfolgsmeldung und enthält Angaben zum wiedergegebenen
			// Titel im anhängenden Datenteil. Angaben werden in TextViews ausgegeben
			case 0:
				txt_services_service2msg.setText((String)msg.getData().getString("msg"));
				txt_services_service2artist.setText((String)msg.getData().getString("artist"));
				txt_services_service2album.setText((String)msg.getData().getString("album"));
				txt_services_service2artist.setVisibility(android.view.View.VISIBLE);
				txt_services_service2album.setVisibility(android.view.View.VISIBLE);
				break;
			// Eingehende Message deutet auf einen Abbruch der Verbindung hin
			// Fehler-/Unterbrechungs-Meldung wird ausgegeben
			case 1:
				txt_services_service2msg.setText((String)msg.getData().getString("msg"));
				txt_services_service2artist.setText("");
				txt_services_service2album.setText("");
				txt_services_service2artist.setVisibility(android.view.View.GONE);
				txt_services_service2album.setVisibility(android.view.View.GONE);
				break;
			}
			// Weiterleitung der Message an den MessageReceiver der TCP-Server-Komponente,
			// falls diese läuft und den MessageReceiver initialisiert hat
			if (mServerReplyTo != null) {
				try{
					Message copyMsg = Message.obtain(msg);
					mServerReplyTo.send(copyMsg);						
				} catch (Exception e){
					e.printStackTrace();
				}
			}
			super.handleMessage(msg);
		}
	};
	// Message-Receiver dieser Activity für eingehende Messages vom Remote Service
	private Messenger complexServiceMessageReceiver = new Messenger(incomingHandler);
	// Messenger dieser Activity für ausgehende Messages zum Remote Service
	private Messenger complexServiceMessenger = null;
	
	// Broadcast Receiver für SMS analog zum ersten Broadcast Receiver
	// Eingehende SMS werden hier nicht ausgegeben sondern zur Überprüfung
	// auf Kommando-Strings an die parseCommand()-Methode übergeben
	private BroadcastReceiver receiverComplexService = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Bundle bundle = intent.getExtras();        
	        String str = "";            
	        if (bundle != null) {
	            // SMS-Inhalte holen
	            Object[] pdus = (Object[]) bundle.get("pdus");
	            SmsMessage msg = SmsMessage.createFromPdu((byte[])pdus[0]);      
	            str += msg.getMessageBody().toString();
	            // SMS-Inhalt zur Überprüfung auf Kommando-Strings 
	            // an die parseCommand()-Methode übergeben
	            parseCommand(str);
	        }
		}
	};
	
	// Prüfung eines übergebenen Strings auf das Vorkommen der Steuerungskommandos
	// "MUSIC PLAY" und "MUSIC STOP"
	// in: String str: zu überprüfender String
	private void parseCommand (String str) {
		Log.d("demoserv","parseCommand reached");
		str =  str.toLowerCase();
		if (str.startsWith("music ")) {
    		str=str.replace("music ", "");  
    		// Kommando "MUSIC PLAY" erkannt. Entsprechende Message wird an den Remote Service geschickt
			if (str.startsWith("play")) {
				Message msg = Message.obtain(null, ComplexService.MUSIC_PLAY,0,0);
				sendToService(msg);
			// Kommando "MUSIC STOP" erkannt. Entsprechende Message wird an den Remote Service geschickt
			} else if (str.startsWith("stop")) {
				Message msg = Message.obtain(null, ComplexService.MUSIC_STOP,0,0);
				sendToService(msg);
			// Kommando nicht erkannt -> Fehlermeldung als Toast
			} else {
				Toast.makeText(getApplicationContext(), "Command not recognized! Available Commands: MUSIC STOP, MUSIC PLAY", Toast.LENGTH_SHORT).show();
			}
		// Kommando nicht erkannt -> Fehlermeldung als Toast
		} else {
			Toast.makeText(getApplicationContext(), str+"not recognized! Available Commands: MUSIC STOP, MUSIC PLAY", Toast.LENGTH_SHORT).show();
		}
	}
	
	// Hilfsmethode zum Senden einer Message an den Remote Service
	private void sendToService (Message msg) {
		try {
			// Service ist gebunden, Message kann gesendet werden
			// Der Message an den Service wird eine Rückantwort-Adresse,
			// sprich der MessageReceiver dieser Activity mitgegeben
			if (complexServiceBound){
				msg.replyTo = complexServiceMessageReceiver;
				complexServiceMessenger.send(msg);
			// Service ist nicht gebunden, Fehlermeldung ausgeben
			} else
				Toast.makeText(getApplicationContext(), "Cannot play! Service not bound!", Toast.LENGTH_SHORT).show();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			Log.d("Service Activity","Send Message to Message-Handler fehlgeschlagen!");
			e.printStackTrace();
		}
	}
	/*  Service mit Thread, Binder & Broadcast
	/*  [ENDE] 
	/****************************************************************************************************/
	/****************************************************************************************************/
	/****************************************************************************************************/

	
	

	
	/* **************************************************************************************************/
	/* **************************************************************************************************/
	/* **************************************************************************************************/
	/*  Thread mit TCP-Server
	 * 
	/*  Es wird ein TCP-ServerSocket auf dem Port 30999 geöffnet mit dem sich fortan beliebige Clients 
	/*  verbinden können. Der Remote Music Service lässt sich nun auch von den TCP-Clients steuern.
    /*  Da Socket-Operationen den UI-Thread blockieren würden, werden die Server-Funktionalitäten in 
     *  einen eigenen Thread ausgelagert.
     *
	 *
	/*  [ANFANG] 
	 */
	
	// Läuft der TCP-Server-Thread bereits?
	private Boolean tcpThreadRunning=false;
	
	// Server-IP-Adresse und Port, wird von getLocalIP() gefüllt
	public static String SERVERIP= "";
	public final static int SERVERPORT = 30999;
	// Server Socket
	private ServerSocket serverSocket;
	// Client Socket
	private Socket client;
	
	// Implementierung des Message-Handlers für den TCP-Server-MessageReceiver dieser Activity 
	// (für eingehende Messages aus dem TCP-Server-Thread)
	public class ServerHandler extends Handler {
		public final static int LISTENING = 0;
		public final static int CONNECTED = 1;
		public final static int RECEIVING = 2;
		public final static int DISCONNECT = 3;
		public final static int ERROR = 4;
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			String serverStatus = new String();
			switch (msg.what){
			// TCP-Server-Thread meldet, dass er bereit für Verbindungen ist
			// Meldung wird in einem TextView der Activity ausgegeben
			case LISTENING:
				serverStatus="Listening on "+SERVERIP+":"+SERVERPORT+" / Commands available:"
								+"MUSIC PLAY / MUSIC STOP";
				break;
			// Meldung, dass sich ein Client verbunden hat
			case CONNECTED:
				serverStatus=(String)msg.obj+" connected";				
				break;
			// Meldung, dass Nachrichten vom Client eingetroffen sind
			// Nachricht wird an parseCommand() weitergeleitet um auf Kommandos geprüft zu werden
			// Messenger dieser Activity (für Messages zum TCP-Server-Thread) wird mit dem
			// MessageReceiver des TCP-Server-Threads initialisiert
			case RECEIVING:
				String cmd = ((Bundle)msg.obj).getString("msg");
				mServerReplyTo = msg.replyTo;
				serverStatus="Received from "+((Bundle)msg.obj).getString("sender")+": "+cmd;
				parseCommand(cmd);
				break;
			// Meldung, dass die Verbindung beendet wurde
			case DISCONNECT:
				serverStatus="Connection lost!";
				mServerReplyTo=null;
				break;
			// Fehlermeldung des TCP-Servers
			case ERROR:
				serverStatus="Error while establishing connection.";				
				mServerReplyTo=null;
				break;
			}
			// Ausgabe der Meldung im TextView
			txt_services_serverStatus.setText(serverStatus);
			super.handleMessage(msg);
		}
	};	
	private ServerHandler serverHandler = new ServerHandler();
	
	// Methode zur Ermittlung der IP des lokalen Netzwerk-Interface
	// return: IP in String
	private String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) { return inetAddress.getHostAddress().toString(); }
				}
			}
		} catch (SocketException ex) {
			Log.e("ServerActivity", ex.toString());
		}
		return null;
	}
	
	// Implementierung des TCP-Server-Threads inkl. MessageReceiver 
	// für eingehende Messages aus dieser Activity
	public class ServerThread implements Runnable {
		// Implemetierung des Message-Handler für eingehende Messages aus der Activity
		private Handler incomingHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				// Messages werden über den OutputStream des client-Sockets 
				// direkt an den verbundenen Client weitergeleitet
				if (client !=null && client.isConnected()){
					try {
						PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),true);
						switch (msg.what) {
						// Eine Erfolgsmessage des Remote Services wird weitergeleitet, 
						// welche Angaben zum wiedergegebenen Titel im Datenteil enthält
						case 0:
							out.println(msg.getData().getString("msg")+" # "
									+msg.getData().getString("artist")+" # "
									+msg.getData().getString("album"));
							break;
						// Die Unterbrechung-/Fehlermeldung wird weitergeleitet
						case 1:
							out.println(msg.getData().getString("msg"));
							break;
						default:
							super.handleMessage(msg);		
						}
					} catch (Exception e) {
						Log.e("TCP ServerThread", "TCP ServerThread OutStream failed.");
						e.printStackTrace();
					};
				}
			}
		};
		// MessageReceiver des TCP-Server-Threads für eingehende Messages aus dieser Activity
		final Messenger mMessenger = new Messenger(incomingHandler);
		
		// Implementierung des TCP-Server-Threads
		// Siehe Ausführungen in der schriftlichen Arbeit
		public void run() {
			try {
				// TCP-Funktionalitäten stehen nur bei bestehender NEtzwerkverbindung zur Verfügung
				if (SERVERIP != null) {
					// Message für SERVER ONLINE UND BEREIT FÜR VERBINDUNG wird an den MessageReceiver
					// der Activity geschickt
					Message.obtain(serverHandler,ServerHandler.LISTENING,null).sendToTarget();
					// 1. Es wird Server-seitig ein Socket auf dem Port 30999 geöffnet.
					serverSocket = new ServerSocket(SERVERPORT);
					while (true) {
						if (client == null){
							Log.e("ServerActivity", "Server UP before Connection");
							// 2. Es wird auf eingehende Verbindungen gewartet
							// 3. Wird Client-seitig eine Verbindung zum Server aufgebaut, so wird Server-seitig ein
							// weiterer Socket (client), für die Kommunikation mit dem verbundenen Client, initialisiert.
							client = serverSocket.accept();
							Log.e("ServerActivity", "Incoming Connection");
							// Message für CLIENT HAT SICH VERBUNDEN wird an den MessageReceiver
							// der Activity geschickt
							Message.obtain(serverHandler,ServerHandler.CONNECTED,client.getInetAddress().toString()).sendToTarget();

							try {
								// 4. Input-Stream wird eingerichtet. Die Kommunikation kann beginnen
								BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
								String line = null;
								Log.e("ServerActivity", "Waiting for lines");
								// Einkommende Nachrichten des Clients werden geholt und
								// via Message-Objekte an den MessageReceiver der Acitivity
								// weitergeleitet
								while ((line = in.readLine()) != null) {
									Log.e("ServerActivity", "Incoming Line: "+line);
									Bundle data = new Bundle();
									data.putString("sender", client.getInetAddress().toString());
									data.putString("msg", line);
									Message msg = Message.obtain(serverHandler,ServerHandler.RECEIVING,data);
									msg.replyTo = mMessenger;
									msg.sendToTarget();
								}
								Log.e("ServerActivity", "no lines");
								Message.obtain(serverHandler,ServerHandler.DISCONNECT,null).sendToTarget();
							} catch (Exception e) {
								Message.obtain(serverHandler,ServerHandler.DISCONNECT,null).sendToTarget();
								Log.e("ServerActivity", "Connection lost");
								e.printStackTrace();
							}
							// 5. Beendet der Anwender die Verbindung, so wird der Client-Socket geschlossen.
							client.close();
							client = null;

						}
					}
				} else {
					Message.obtain(serverHandler,ServerHandler.ERROR,null).sendToTarget();
				}
			} catch (Exception e) {
				Message.obtain(serverHandler,ServerHandler.ERROR,null).sendToTarget();
				e.printStackTrace();
			}
		}
	}
	/*  Thread mit TCP-Server
	/*  [ENDE] 
	/****************************************************************************************************/
	/****************************************************************************************************/
	/****************************************************************************************************/
	
	// GUI-Elemente
	private TextView txt_services_bcheader;
	private TextView txt_services_tcpheader;
	private TextView txt_services_service2header;
	private TextView txt_services_service1header;
	private TextView txt_services_service1msg;
	private TextView txt_services_service2msg;
	private TextView txt_services_service2artist;
	private TextView txt_services_service2album;
	private TextView txt_services_serverStatus;
	private Button btn_services_bcstart;
	private Button btn_services_bcstop;
	private Button btn_services_service1start;
	private Button btn_services_service1stop;
	private Button btn_services_service2start;
	private Button btn_services_service2stop;
	private Button btn_services_service2connect;
	private Button btn_services_service2disconnect;
	private Button btn_services_tcpServerStart;
	private Button btn_services_tcpServerStop;
	private TextView tv_services_activity;
	
	// Variable für die Bereitsstellung der Preferences	
	private SharedPreferences prefs;

	// Steuerung der Activity-Namensanzeige
	private boolean showActivityName;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// Laden des XML-Layouts
		setContentView(R.layout.activity_services_servicesdemo);
		// Laden der GUI-Elemente
		txt_services_bcheader = (TextView) findViewById(R.id.txt_services_bcheader);
		txt_services_service1header = (TextView) findViewById(R.id.txt_services_service1header);
		txt_services_service2header = (TextView) findViewById(R.id.txt_services_service2header);
		txt_services_tcpheader = (TextView) findViewById(R.id.txt_services_tcpheader);
		txt_services_service1msg = (TextView) findViewById(R.id.txt_services_service1msg);
		txt_services_service2msg = (TextView) findViewById(R.id.txt_services_service2msg);
		txt_services_service2artist = (TextView) findViewById(R.id.txt_services_service2artist);
		txt_services_service2album = (TextView) findViewById(R.id.txt_services_service2album);
		btn_services_bcstart = (Button) findViewById(R.id.btn_services_bcstart);
		btn_services_bcstop = (Button) findViewById(R.id.btn_services_bcstop);
		btn_services_service1start = (Button) findViewById(R.id.btn_services_service1start);
		btn_services_service1stop = (Button) findViewById(R.id.btn_services_service1stop);
		btn_services_service2start = (Button) findViewById(R.id.btn_services_service2start);
		btn_services_service2stop = (Button) findViewById(R.id.btn_services_service2stop);
		btn_services_service2connect = (Button) findViewById(R.id.btn_services_service2connect);
		btn_services_service2disconnect = (Button) findViewById(R.id.btn_services_service2disconnect);
		btn_services_tcpServerStart = (Button) findViewById(R.id.btn_services_tcpstart);
		btn_services_tcpServerStop = (Button) findViewById(R.id.btn_services_tcpstop);
		txt_services_serverStatus = (TextView)findViewById(R.id.txt_services_serverstatus);
		tv_services_activity = (TextView) findViewById(R.id.tv_services_activity);
		// Einige GUI-Elemente bekommen ein Kontext-Menü für die Anzeige
		// von weiteren Informationen
		registerForContextMenu(txt_services_bcheader);
		registerForContextMenu(txt_services_service1header);
		registerForContextMenu(txt_services_service2header);
		registerForContextMenu(txt_services_tcpheader);
		// IP des lokalen Netzwerk-Interfaces wird geladen
		SERVERIP = getLocalIpAddress();
		
		// Steuerung des Begrüßungs-/Hilfs-PopUps
		if(getIntent().getExtras().getBoolean("popup")) {hello();}
	}
	
	// Kontext-Menüs für die Überschriften-Label der einzelnen Sektionen
	// werden erstellt.
	// Die Funktion wird dazu verwendet, um Dialoge mit weiteren Infos
	// anzeigen zu lassen.
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		Builder builder = new Builder(this);
		switch (v.getId()){
		case R.id.txt_services_bcheader:
			builder.setTitle(R.string.txt_services_bcheader)
			.setMessage(R.string.txt_services_kontext_broadcast);
			break;
		case R.id.txt_services_service1header:
			builder.setTitle(R.string.txt_services_service1header)
			.setMessage(R.string.txt_services_kontext_simpleservice);
			break;
		case R.id.txt_services_service2header:
			builder.setTitle(R.string.txt_services_service2header)
			.setMessage(R.string.txt_services_kontext_complexservice);
			break;
		case R.id.txt_services_tcpheader:
			builder.setTitle(R.string.txt_services_tcpheader)
			.setMessage(R.string.txt_services_kontext_tcpserver);
			break;
		}
		builder.setIcon(android.R.drawable.ic_dialog_info)
		.setPositiveButton("OK", null)
		.show();
	}
	
	// Methode für die Erstellung des Begrüßungs-/Hilfs-PopUps
	private void hello(){
		Builder builder = new Builder(this);
		builder.setTitle(R.string.txt_main_dialog_titleServices)
			.setView(getLayoutInflater().inflate(R.layout.dialog_services, null));
		builder.setIcon(android.R.drawable.ic_dialog_info)
		.setPositiveButton("OK", null)
		.show();
	}
	
	@Override
	protected void onPause() {
		Log.d("Services","onPause aufgerufen");
		// TODO Auto-generated method stub
		// Shared Preferences werden geholt und für die Bearbeitung vorbereitet
		prefs = getSharedPreferences(Main.MAIN_PACKAGE_NAME+"_preferences", MODE_PRIVATE);
		Editor servicesPrefsEdit = prefs.edit();
		// Alle Zustandsvariablen werden gesichert
		servicesPrefsEdit.putBoolean("isRegistered", receiverSMSRegistered).commit();
		servicesPrefsEdit.putBoolean("receiverSimpleServiceisRegistered", receiverSimpleServiceRegistered).commit();
		servicesPrefsEdit.putBoolean("receiverComplexServiceisRegistered", receiverComplexServiceRegistered).commit();
		servicesPrefsEdit.putBoolean("service1isRunning", simpleServiceRunning).commit();
		servicesPrefsEdit.putBoolean("service2isRunning", complexServiceRunning).commit();
		servicesPrefsEdit.putBoolean("service2isBound", complexServiceBound).commit();
		servicesPrefsEdit.putBoolean("tcpThreadRunning", tcpThreadRunning).commit();
		
		// TCP-Server wird deaktiviert
		if (tcpThreadRunning)try {
				serverSocket.close();
				if (client != null && client.isConnected()){
					client.close();
				}
				client = null;} 
			catch (IOException e) {
				e.printStackTrace();
				Log.e("Services","closing serverSocket fehlgeschlagen");
		};
		// Broadcast Receiver für den Remote Service wird deaktiviert
		if (receiverComplexServiceRegistered) try {
			receiverComplexServiceRegistered=false;
			unregisterReceiver(receiverComplexService);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Services","unregister receiverComplexService fehlgeschlagen");				
		};
		// Verbindung zum Remote Service wird unterbrochen
		if (complexServiceBound){
			unbindService(complexServiceConnection);
			complexServiceBound=false;
		}
		// Broadcast Receiver des Local Services wird deaktiviert
		if (receiverSimpleServiceRegistered) try {
			receiverSimpleServiceRegistered=false;
			unregisterReceiver(receiverSimpleService);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Services","unregister receiverSimpleService fehlgeschlagen");				
		};
		// Broadcast Receiver für den SMS-Empfang wird deaktiviert
		if (receiverSMSRegistered) try{
			receiverSMSRegistered=false;
			unregisterReceiver(receiverSMS);	
		} catch (Exception e) {
			Log.e("Services","unregisterReceiverSMS failed");
			e.printStackTrace();
		};
				
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		prefs = getSharedPreferences(Main.MAIN_PACKAGE_NAME+"_preferences", MODE_PRIVATE);
		// Gesicherte Zustandsdaten werden aus den Shared Preferences geladen
		receiverSMSRegistered = prefs.getBoolean("isRegistered", false);
		receiverSimpleServiceRegistered = prefs.getBoolean("receiverSimpleServiceisRegistered", false);
		receiverComplexServiceRegistered = prefs.getBoolean("receiverComplexServiceisRegistered", false);
		simpleServiceRunning = prefs.getBoolean("service1isRunning", false);
		complexServiceRunning = prefs.getBoolean("service2isRunning", false);
		complexServiceBound = prefs.getBoolean("service2isBound", false);
		tcpThreadRunning = prefs.getBoolean("tcpThreadRunning",	false);
		
		// Es wird der letzte Komponenten-Zustand wiederhergestellt.
		// TCP-Server wird ggf. wieder aktiviert
		if (tcpThreadRunning){
			Thread tcpServerThread = new Thread(new ServerThread());
			tcpServerThread.start();
		}
		// Broadcast Receiver für den Remote Service wird ggf. wieder aktiviert
		if (receiverComplexServiceRegistered) {registerReceiver(receiverComplexService, FILTER_SMS);}
		// Verbindung zum Remote Service wird ggf. wiederhergestellt
		if (complexServiceBound){
			bindService(new Intent(this,ComplexService.class), complexServiceConnection, Context.BIND_AUTO_CREATE);
			complexServiceBound=true;
		}
		// Broadcast Receiver des Local Services wird ggf. wieder aktiviert
		if (receiverSimpleServiceRegistered) {registerReceiver(receiverSimpleService, new IntentFilter(SimpleService.BC_INTENT));}
		// Broadcast Receiver für SMS wird ggf. wieder aktiviert
		if (receiverSMSRegistered) {registerReceiver(receiverSMS, FILTER_SMS);}
		
		Log.d("Services","onResume: receiverSMSisRegistered= "+receiverSMSRegistered.toString()
				+" / receiverSimpleServRegistered: "+receiverSimpleServiceRegistered.toString()
				+" / receiverComplexServRegistered: "+receiverComplexServiceRegistered.toString()
				+" / service1isRunning: "+simpleServiceRunning.toString()
				+" / service2isBound: "+complexServiceBound.toString()
				+" / service2isRunning: "+complexServiceRunning.toString()
				+" / tcpServerRunning: "+tcpThreadRunning.toString());
		
		prefs = getSharedPreferences(Main.MAIN_PACKAGE_NAME+"_preferences", MODE_PRIVATE);
		showActivityName = prefs.getBoolean("show_activity", false);
		// Steuerung der Activity-Namensanzeige
		if (showActivityName) { tv_services_activity.setText("Activity: "+getClass().getSimpleName()+".class"); }
		else { tv_services_activity.setText(""); }
		
		// Letzten Zustand der GUI-Elemente wiederherstellen
		setControls();
	}
	
	// Hilfmethode um den letzten Zustand der GUI-Elemente wiederherszustellen
	private void setControls(){		
		if (receiverSMSRegistered){
			btn_services_bcstart.setEnabled(false);
			btn_services_bcstop.setEnabled(true);
		} else {
			btn_services_bcstart.setEnabled(true);
			btn_services_bcstop.setEnabled(false);
		}
		if (simpleServiceRunning){
			btn_services_service1start.setEnabled(false);
			btn_services_service1stop.setEnabled(true);
		} else {
			btn_services_service1start.setEnabled(true);
			btn_services_service1stop.setEnabled(false);
		}
		if (complexServiceRunning){
			btn_services_service2start.setEnabled(false);
			btn_services_service2stop.setEnabled(true);
			btn_services_service2connect.setVisibility(android.widget.Button.VISIBLE);
			btn_services_service2disconnect.setVisibility(android.widget.Button.VISIBLE);
			if (complexServiceBound){
				btn_services_service2connect.setEnabled(false);
				btn_services_service2disconnect.setEnabled(true);
			} else {
				btn_services_service2connect.setEnabled(true);
				btn_services_service2disconnect.setEnabled(false);
			}	
		} else {
			btn_services_service2start.setEnabled(true);
			btn_services_service2stop.setEnabled(false);
			btn_services_service2connect.setVisibility(android.widget.Button.GONE);
			btn_services_service2disconnect.setVisibility(android.widget.Button.GONE);
		}
		if (tcpThreadRunning){
			btn_services_tcpServerStart.setEnabled(false);
			btn_services_tcpServerStop.setEnabled(true);
		} else {
			btn_services_tcpServerStart.setEnabled(true);
			btn_services_tcpServerStop.setEnabled(false);
		}
	}

	// Event-Handler-Methode für die Verarbeitung von Click-Events auf den Button-Elementen
	// der grafischen Benutzeroberfläche
	public void onButtonClick (final View view) {
		switch (view.getId()) {
		// Broadcast Receiver für SMS aktivieren
		case R.id.btn_services_bcstart:
			if (!receiverSMSRegistered){
				registerReceiver(receiverSMS, FILTER_SMS);
				Log.d("Services", "receiver registered");
				receiverSMSRegistered = true;
			}
			break;
		// Broadcast Receiver für SMS deaktivieren
		case R.id.btn_services_bcstop:
			if (receiverSMSRegistered) try {
				receiverSMSRegistered = false;
				unregisterReceiver(receiverSMS);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("Services","unregisterSMSreceiver fehlgeschlagen");
			};
			break;
		// Local Service und Broadcast Receiver für Local Service aktivieren
		case R.id.btn_services_service1start:
			if (!simpleServiceRunning){
				startService(new Intent(this,SimpleService.class));
				simpleServiceRunning = true;
				}
			if (!receiverSimpleServiceRegistered){
				registerReceiver(receiverSimpleService, new IntentFilter(SimpleService.BC_INTENT));
				receiverSimpleServiceRegistered=true;
			}
			break;
		// Local Service und Broadcast Receiver für Local Service deaktivieren
		case R.id.btn_services_service1stop:
			if (simpleServiceRunning){
				stopService(new Intent(this,SimpleService.class));
				simpleServiceRunning = false;
				txt_services_service1msg.setText("");
			}
			if (receiverSimpleServiceRegistered) try {
				receiverSimpleServiceRegistered=false;
				unregisterReceiver(receiverSimpleService);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("Services","unregister receiverSimpleService fehlgeschlagen");				
			};
			break;
		// Remote Service aktivieren
		case R.id.btn_services_service2start:
			if (!complexServiceRunning){
				startService(new Intent(this,ComplexService.class));
				complexServiceRunning = true;
			}
			break;
		// Remote Service deaktivieren und vorab ggf. Verbindung trennen
		// und Broacast Receiver für Remote Service deaktivieren
		case R.id.btn_services_service2stop:
			if (complexServiceBound){
				unbindService(complexServiceConnection);
				complexServiceBound = false;
				txt_services_service2msg.setText("");
			}
			if (receiverComplexServiceRegistered) try {
				receiverComplexServiceRegistered=false;
				unregisterReceiver(receiverComplexService);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("Services","unregister receiverComplexService fehlgeschlagen");				
			};
			if (complexServiceRunning){
				stopService(new Intent(this,ComplexService.class));
				complexServiceRunning = false;
			}
			break;
		// Verbindung zum laufenden Remote Service aufbauen und
		// Broadcast Receiver für Remote Service aktivieren
		case R.id.btn_services_service2connect:
			if (!complexServiceBound){
				bindService(new Intent(this,ComplexService.class), complexServiceConnection, Context.BIND_AUTO_CREATE);
				complexServiceBound = true;
			}
			if (!receiverComplexServiceRegistered){
				registerReceiver(receiverComplexService, FILTER_SMS);
				receiverComplexServiceRegistered=true;
			}
			break;
		// Verbindung zum laufenden Remote Service kappen und
		// Broadcast Receiver für Remote Service deaktivieren
		case R.id.btn_services_service2disconnect:
			if (complexServiceBound){
				unbindService(complexServiceConnection);
				complexServiceBound = false;
				txt_services_service2msg.setText("");
			}
			if (receiverComplexServiceRegistered) try {
				receiverComplexServiceRegistered=false;
				unregisterReceiver(receiverComplexService);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("Services","unregister receiverComplexService fehlgeschlagen");				
			};
			break;
		// TCP-Server-Thread starten
		case R.id.btn_services_tcpstart:
			if (!tcpThreadRunning){
				Thread tcpServerThread = new Thread(new ServerThread());
				tcpServerThread.start();
				tcpThreadRunning=true;
			}
			break;
		// TCP Server-Socket und ggf. Client-Socket schliessen
		case R.id.btn_services_tcpstop:
			if (tcpThreadRunning){
				try {
					tcpThreadRunning=false;
					serverSocket.close();
					if (client != null && client.isConnected()){
						client.close();
					}
					client = null;
					txt_services_serverStatus.setText("");
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("Services","closing serverSocket fehlgeschlagen");
				};
			}
			break;
		}
		setControls();
	}

}
