package network;

import java.net.*;
import java.io.*;

public class main {


	public static void main(String[] args) {
		
		if(args.length==2){
			if("--debug".equals(args[1])) OSDepPrint.init(true);
		} else if(args.length==1){
			OSDepPrint.init(false);
		} else {
			System.out.println("Usage: PathfinderServer <Port> [options...]");
			System.out.println("   --debug     Enables debug ouput");
		}
		// asynchroner listener
		AsynServerListener server = new AsynServerListener(Integer.parseInt(args[0]));
		server.start();
		// TODO fehlerfälle
		// TODO interaktive Konsole
		// TODO implement
	}
	

}

