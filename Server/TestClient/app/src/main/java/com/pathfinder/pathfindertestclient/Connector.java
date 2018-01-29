package com.pathfinder.pathfindertestclient;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import com.pathfinder.pathfindertestclient.ConnectionCodes;

/**
 * Created by pollux on 29.01.18.
 */

public class Connector {

    public Connector(){
    }

    public static int registerUID(){
        new Thread(new ThreadRegister()).start();
        return 0;
    }

}


class ThreadRegister implements Runnable {
    private Socket client;
    private PrintWriter out;
    private BufferedReader in;
    private int timeout = 0;
    private String response = "";

    public ThreadRegister(){
    }

    @Override
    public void run() {
        try {
            client = new Socket(ConnectionCodes.serverIP, ConnectionCodes.port);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            // TODO Fehlerbehandlung
        }

        out.write(ConnectionCodes.REGISTER);
        out.println();
        Log.d("DEBUG1", "[SEND]: register");

        while(timeout<5000){
            try {
                while(in.ready()){
                    String tmp_char;
                    int tmp_byte = in.read();
                    tmp_char = Character.toString((char) tmp_byte);
                    if("\n".equals(tmp_char)){
                        Log.d("DEBUG1", "[RECV]: " + response);
                        timeout = 5000;
                        // TODO besserer abbruch
                    }
                    response += tmp_char;

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

        // TODO Auswertung der Daten
        Log.d("DEBUG1", "[INFO]: ThreadRegister beendet");

    }
}


