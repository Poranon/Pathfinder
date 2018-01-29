package network;

import java.net.*;
import java.io.*;

public class main {


	public static void main(String[] args) {
		
		// asynchroner listener
		AsynServerListener server = new AsynServerListener(Integer.parseInt(args[0]));
		server.start();
		
		// interaktive Konsole
		// TODO implement
	}
	

}

