/**
 * 
 */
package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author pollux
 *
 */
public class AsynConnectionThread extends Thread {
	private PrintWriter out;
	private BufferedReader in;
	
	public AsynConnectionThread(Socket client) {
		try {
			out = new PrintWriter(client.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("[ERROR]: Failed to create client streams");
			// TODO stop thread
		}
	}

	public void run(){
		// TODO implement cases 
	}


}
