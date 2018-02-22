package com.pathfinder.pathfindertestclient;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import android.content.Context;
import android.support.v4.app.NotificationCompat.Builder;

/**
 * Created by pollux on 29.01.18.
 */

public class Connector {
    public Connector(){
    }

    public static int request(Context context, byte requestCode){
        new Thread(new ThreadRequest(context, requestCode)).start();
        return 0;
    }

    public static int request(Context context, byte requestCode, String filepath, boolean retry){
        new Thread(new ThreadRequest(context, requestCode, filepath, retry)).start();
        return 0;
    }
}

class ThreadRequest implements Runnable {
    private Socket client;
    private DataOutputStream out_data;
    private DataInputStream in_data;
    private String filepath = "";
    private int clientSpeed = 5120;
    private byte requestCode;
    private boolean is_retry = false;
    private Context context = null;
    private NotificationManager notifcationManager = null;
    private NotificationCompat.Builder notificationCompatBuilder = null;

    public ThreadRequest(Context _context, byte _code) {
        requestCode = _code;
        context = _context;
    }
    public ThreadRequest(Context _context, byte _code, String _filepath, boolean _retry) {
        requestCode = _code;
        filepath = _filepath;
        is_retry = _retry;
        context = _context;
    }

    @Override
    public void run() {

        initialize();
        writeCode(ConnectionCodes.REQUEST);

        if(requestCode==ConnectionCodes.REGISTER){                                                  // REGISTER

        }
        if((requestCode <=117)&&(requestCode >=100)){                                               // MAP DOWNLOAD

            writeCode(ConnectionCodes.MAP);
            writeCode(requestCode);
            Log.d("DEBUG1", "[SEND]: request code (" + requestCode + ")");
            int ret = receiveFile(filepath, is_retry);
            if(ret==-2){
                Log.d("DEBUG1", "Download failed, connection interrupted");
            } else if(ret==-1){
                Log.d("DEBUG1", "Download failed, something went wrong");
            }
        } else {                                                                                    // REST
            writeCode(requestCode);
            Log.d("DEBUG1", "[SEND]: request code (" + requestCode + ")");
            whatever();
        }

    }

    private void initialize(){
        try {
            client = new Socket(ConnectionCodes.serverIP, ConnectionCodes.port);

            out_data = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
            in_data = new DataInputStream(new BufferedInputStream(client.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
            // TODO Fehlerbehandlung
        }
    }

    private void writeCode(int code){
        try {
            out_data.write(code);
            out_data.write(ConnectionCodes.END);
            out_data.flush();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO Fehlerbehandlung
        }

    }
    private void writeCode(int code, long bytes){
        try {
            out_data.write(code);
            out_data.write(ConnectionCodes.END);
            out_data.writeLong(bytes);
            out_data.write(ConnectionCodes.END);
            out_data.flush();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO Fehlerbehandlung
        }

    }

    private void sleep(int milliseconds){
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
            // TODO Fehlerbehandlung
        }
    }

    private long getFileSize(){
        int timeout = 0;
        long fileSize = -2;

        // get file size
        while(timeout<5000){
            try {
                while(in_data.available()>8){
                    Log.d("DEBUG1", "available size: " + in_data.available());
                    long tmp_byte = in_data.readLong();
                    Log.d("DEBUG1", "byte size: " + tmp_byte);
                    fileSize = tmp_byte;
                    tmp_byte = in_data.read();
                    if(tmp_byte!=ConnectionCodes.END){
                        fileSize = -2;
                    }
                    timeout = 5000;
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                // TODO Fehlerbehandlung
            }

            if(timeout<5000) sleep(250);
            timeout += 250;

        }
        if(fileSize<0) {
            Log.d("DEBUG1", "invalid file size: " + fileSize);
            return -1;
        }
        Log.d("DEBUG1", "file size: " + fileSize);
        return fileSize;
    }

    private int getSegment(String filepath, boolean append, long bytes, int tries){
        FileOutputStream output = null;
        int timeout = 0;

        try {
            output = new FileOutputStream(filepath, append);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // TODO Fehlerbehandlung
        }

        // receive file
        int fileSizeCounter = 0;
        int oldFileSizeCounter = 0;
        while(timeout<10000){
            try {
                byte[] buffer = new byte[1048576];
                while(in_data.available()>0){
                    int curr = 0;
                    while((curr = in_data.read(buffer))>0) {
                        output.write(buffer, 0, curr);
                        fileSizeCounter += curr;
                        if((fileSizeCounter-oldFileSizeCounter)>=(clientSpeed*1024)){
                            float value = (100.0f / (float)bytes) * (float) fileSizeCounter;
                            notificationCompatBuilder.setProgress(100, (int)value, false);
                            notifcationManager.notify(1, notificationCompatBuilder.build());
                            oldFileSizeCounter = fileSizeCounter;
                            //Log.d("DEBUG1", "refresh notification bar");
                        }
                    }
                    timeout = 0;
                }
            } catch (IOException e) {
                e.printStackTrace();
                // TODO Fehlerbehandlung
            }
            if(fileSizeCounter == bytes) {
                Log.d("DEBUG1", "File completely downloaded");
                notificationCompatBuilder.setContentText("Download complete").setProgress(0, 0, false);
                notifcationManager.notify(1, notificationCompatBuilder.build());
                return fileSizeCounter;
            }

            sleep(250);
            timeout += 250;

        }
        try {
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(fileSizeCounter==0){
            // connection timed out or still no connection
            Log.d("DEBUG1", "no connection, retry (" + tries + ")");
            return fileSizeCounter;
        } else if(fileSizeCounter < bytes){
            // something went wrong
            Log.d("DEBUG1", "File part downloaded");
            return fileSizeCounter;
        } else {
            // something went  completely wrong
            Log.d("DEBUG1", "File download corrupt");
            notificationCompatBuilder.setContentText("Download file corrupt").setProgress(0, 0, false);
            notifcationManager.notify(1, notificationCompatBuilder.build());
            return -1;
        }
    }

    private int receiveFile(String filepath, boolean retry){
        long fileSize = getFileSize();
        long curr = 0;
        int retries = 0;
        boolean initAppend = false;

        if(fileSize<0) {
            // TODO Fehlerbehandlung
            return -1;
        }

        // cleanup
        if(retry){
            // append mode
            initAppend = true;
        } else {
            File file = new File(filepath);
            file.delete();
        }

        // initialise notification manager
        notifcationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationCompatBuilder = new NotificationCompat.Builder(context);
        notificationCompatBuilder.setContentTitle("Map Download");
        notificationCompatBuilder.setContentText("Download in progress");
        notificationCompatBuilder.setSmallIcon(R.drawable.download);


        // grabbing data
        curr += getSegment(filepath, initAppend, fileSize, 0);
        while((retries < 3)&&(curr<fileSize)){
            writeCode(ConnectionCodes.REQUEST);
            writeCode(ConnectionCodes.RETRY, fileSize-curr);
            writeCode(ConnectionCodes.MAP_HB);
            if((fileSize = getFileSize())<0) return -1;
            curr += getSegment(filepath, true, fileSize-curr, retries+1);
            retries++;
        }

        if(curr<fileSize){
            return -2;
        } else {
            return 0;
        }
    }

    private int whatever(){
        return 0;
    }
}
