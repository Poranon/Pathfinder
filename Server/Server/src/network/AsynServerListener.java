package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class AsynServerListener extends Thread {
	private ServerSocket server;
	private Socket client;
	private int port;
	
	public AsynServerListener(int port) {

		// Server initialisieren
		try {
			server = new ServerSocket(port);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("[ERROR]: Failed to bind server");
			// TODO stop thread

		}	
	}

	public void run(){
		
		// Auf Verbindungen warten
				while(true){															// abbruch von konsole möglich machen
					try {
						client = server.accept();
						Socket newClient = new Socket();
						newClient = client;												// ist das ne deep copy?
						AsynConnectionThread conn = new AsynConnectionThread(newClient);
						
						
					} catch (IOException e) {
						e.printStackTrace();
						System.out.println("[ERROR]: Failed to accept client");
						// TODO stop thread
					}
					
				}
	}
	



}
