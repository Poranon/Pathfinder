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
	
	public AsynServerListener(int port) {

		// Server initialisieren
		try {
			//server = new ServerSocket(port);
			server = new ServerSocket(port, 0, InetAddress.getByName("192.168.188.55"));
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("[ERROR]: Failed to bind server");
			// TODO stop thread

		}	
	}

	public void run(){
		
		System.out.println("Server listening on " + server.getInetAddress().toString() + ":" + server.getLocalPort());
		while(true){															// abbruch von konsole möglich machen
			try {
				client = server.accept();
				Socket newClient = new Socket();
				newClient = client;												// ist das ne deep copy?
				System.out.println(client.getInetAddress().toString() + ":" + client.getLocalPort() + " connected");
				AsynConnectionThread conn = new AsynConnectionThread(newClient);
				conn.start();
				
				
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("[ERROR]: Failed to accept client");
				// TODO stop thread
			}
			
		}
				
	}
	



}
