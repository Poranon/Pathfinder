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
import java.util.concurrent.TimeUnit;

/**
 * @author pollux
 *
 */
public class AsynConnectionThread extends Thread {
	private Socket client;
	private PrintWriter out;
	private BufferedReader in;
	
	public AsynConnectionThread(Socket _client) {
		client = _client;
		try {
			out = new PrintWriter(_client.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(_client.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("[ERROR]: Failed to create client streams");
			// TODO stop thread
		}
		
	}

	public void run(){
		int timeout = 0;
		String response = "";
		System.out.println("ready to run");
		
		while(timeout<2500){
            try {
                while(in.ready()){
                	System.out.println("ready to read");
                    String tmp_char;
                    int tmp_byte = in.read();
                    switch(tmp_byte){
                    case ConnectionCodes.REGISTER: 	register();
                    								return;
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
                // TODO Fehlerbehandlung
            }

            try {
                TimeUnit.MILLISECONDS.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
                // TODO Fehlerbehandlung
            }
            timeout += 250;

        }
	}
	
	private void register(){
		// TODO IMPLEMENT UID GENERATION
		
		out.println("890fu8928f2893kat4g1q");
		System.out.println("send UID to client");
		try {
			client.shutdownOutput();
			System.out.println("disconnected");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
