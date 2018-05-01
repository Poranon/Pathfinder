package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class AsynServerListener extends Thread {
	private ServerSocket server;
	private Socket client;
	private int port;
	private int connCounter = 0;
	
	public AsynServerListener(int port) {

		// Server initialisieren
		try {
			//server = new ServerSocket(port);
			server = new ServerSocket(port, 0, InetAddress.getByName("62.113.206.126")); //192.168.188.55 //62.113.206.126
		} catch (IOException e1) {
			e1.printStackTrace();
			OSDepPrint.error("Failed to bind Server");
			// TODO stop thread
			// TODO timeout for sockets

		}	
	}

	public void run(){
		
		System.out.println("Server listening on " + server.getInetAddress().toString().replace("/", "") + ":" + server.getLocalPort());
		while(true){															// abbruch von konsole möglich machen
			try {
				client = server.accept();
				Socket newClient = new Socket();
				newClient = client;												// ist das ne deep copy?
				connCounter++;
				System.out.println(" [ #" + connCounter + " ] " +  client.getInetAddress().toString().replace("/", "") + ":" + client.getLocalPort() + " connected");
				AsynConnectionThread conn = new AsynConnectionThread(newClient, connCounter);
				conn.start();
				
				
			} catch (IOException e) {
				e.printStackTrace();
				OSDepPrint.error("Failed to accept client");
				// TODO stop thread
			}
			
		}
				
	}
	



}
