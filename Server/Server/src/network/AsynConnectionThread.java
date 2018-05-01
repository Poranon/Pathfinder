/**
 * 
 */
package network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

// TODO 
// e.getMessage() remove line break at the end

/**
 * @author pollux
 *
 */
public class AsynConnectionThread extends Thread {
	private Socket client;
	private PrintWriter out;
	private BufferedReader in;
	private DataOutputStream out_data;
	private DataInputStream in_data;
	private int ref = 0;
	
	public AsynConnectionThread(Socket _client, int _ref) {
		client = _client;
		ref = _ref;
		try {
			out = new PrintWriter(_client.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(_client.getInputStream()));
			
			out_data = new DataOutputStream(new BufferedOutputStream(_client.getOutputStream()));
			in_data = new DataInputStream(new BufferedInputStream(_client.getInputStream()));
			
		} catch (IOException e) {
			e.printStackTrace();
			OSDepPrint.error("Failed to create client streams", ref);
			// TODO stop thread
		}
		
	}

	public void run(){
		
		int response = getCode(5000);
		if(response >= 1){
			switch(response){
	        case ConnectionCodes.REQUEST: 	request();
											return;
	        }
		} else if(response==-2){
			OSDepPrint.error("Exception occurred", ref);
		} else {
			OSDepPrint.error("No response within time limit", ref);
		}
		
	}
	
	/**
	 * Closes the socket that is bound to the current connection.
	 * If the operation fails we assume that the connection has
	 * already been closed by the client.
	 * 
	 * @return	0 for success or -1 if the client is already disconnected
	 */
	private int disconnect(){
		try {
			client.shutdownOutput();
			OSDepPrint.info("connection closed", ref);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			OSDepPrint.info("connection closed by client", ref);
			return -1;
		}
		return 0;
	}
	
	/**
	 * Puts the thread to sleep for a certain amount of time.
	 * 
	 * @param ms	time in milliseconds
	 */
	private void sleep(int ms){
		try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
            // TODO Fehlerbehandlung
        }
	}
	
	/**
	 * DEPRECATED
	 * Puts the thread to sleep for a certain amount of time.
	 * 
	 * @param mics	time in microseconds
	 */
	private void tick(int mics){
		try {
            TimeUnit.MICROSECONDS.sleep(mics);
        } catch (InterruptedException e) {
            e.printStackTrace();
            // TODO Fehlerbehandlung
        }
	}
	
	/**
	 * Waits a certain amount of time for a response from the 
	 * connected client. The response should be in form of a code
	 * as seen in class 'ConnectionCodes'. Transmissions are always 
	 * terminated by the END-sequence.
	 * 
	 * @param timeout_ms	time we wait for the response in milliseconds
	 * @return				the code on success, -1 for timeout, -2 for error
	 */
	private int getCode(int timeout_ms){
		int timeout = 0;
		int ret_byte = -2;
		
		while(timeout<timeout_ms){
            try {
                while(in_data.available()>0){
                    int tmp_byte = in_data.read();
                    //OSDepPrint.debug("ready byte: " + tmp_byte, ref);
                    if(tmp_byte==ConnectionCodes.END) return ret_byte;
                    ret_byte = tmp_byte;
                    
                }
            } catch (IOException e) {
                e.printStackTrace();
                // TODO Fehlerbehandlung
                return -2;
            }

            sleep(250);
            timeout += 250;

        }
		return -1;
	}
	
	// DEPRECATED
	private String getTextResponse(){
		String response = "";
		int timeout = 0;
		
		while(timeout<5000){
            try {
                while(in.ready()){
                    int tmp_char = in.read();
                    String tmp_string = Character.toString((char) tmp_char);
                    response += tmp_string;
                    if("\n".equals(tmp_string)) return response;
                }
            } catch (IOException e) {
                e.printStackTrace();
                // TODO Fehlerbehandlung
                return null;
            }

            sleep(250);
            timeout += 250;

        }
		return null;
	}
	

	/**
	 * Filters the next incoming code and calls the corresponding function.
	 * Performs a disconnect after the called functions finish.
	 */
	private void request(){
		switch(getCode(5000)){
		case ConnectionCodes.REGISTER:	OSDepPrint.net("UID requested", ref);
										register();
										break;
		case ConnectionCodes.MAP: 		map();
										break;	
		case ConnectionCodes.MAPPART: 	OSDepPrint.net("Mappart requested", ref);
										OSDepPrint.info("Terminating", ref);
										// TODO Implement
										break;
		case ConnectionCodes.RETRY:		OSDepPrint.net("Retry requested", ref);
										retry();
										break;
		case -1:						// TODO Fehlerbehandlung
										break;
		case -2:						// TODO Fehlerbehandlung
										break;
		}
		
		disconnect();
	}
	
	/**
	 * Generates a UID and sends it to the client in plaintext
	 */
	private void register(){
		
		// TODO IMPLEMENT UID GENERATION
		out.println("890fu8928f2893kat4g1q");
		OSDepPrint.debug("successfully send UID to client", ref);
		// TODO besserer check?
		
	}
	
	/**
	 * Filters the next incoming code and starts the upload
	 * of the requested map by calling 'sendFile'.
	 * TODO implement a switch statement for the different file paths
	 *  
	 * @return	0 on success, -1 on invalid code, -2 on error
	 */
	private int map(){
		
		int mapcode = getCode(5000);
		if(mapcode<=0){
			// TODO Fehlerbehandlung
			return -1;
		}
		OSDepPrint.net("Map requested (" + mapcode + ")", ref);
		if(sendFile("/home/michael/pathfinder/video.mp4", 0)<0){
			OSDepPrint.error("File transfer incomplete", ref);
			return -2;
		}
		OSDepPrint.net("Map successfully uploaded", ref);
		
		return 0;
	}
	
	/**
	 * Waits for the next 9 bytes containing a 'long' value and the
	 * terminating END-sequence. The long value represents the remaining
	 * bytes needed by the client for the requested file/map. The function
	 * then waits to receive the map code. It then starts
	 * the upload of the requested map by calling 'sendFile'.
	 * 
	 * @return	0 on success, -2 on error
	 */
	private int retry(){
		int timeout = 0;
		long remainingBytes = -1;
		while(timeout<5000){
            try {
                while(in_data.available()>8){
                    long tmp_byte = in_data.readLong();
                    remainingBytes = tmp_byte;
                    tmp_byte = in_data.read();
                    if(tmp_byte!=ConnectionCodes.END){																	// TODO check if integer long comp works
                    	remainingBytes = -2;
                    }
                    timeout = 5000;
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                // TODO Fehlerbehandlung
                return -2;
            }

            if(timeout<5000) sleep(250);
            timeout += 250;

        }
		if(remainingBytes<0) return -2;
		
		int code = getCode(5000);
		// TODO implement switch statement
		if(sendFile("/home/michael/pathfinder/video.mp4", remainingBytes)<0){
			OSDepPrint.error("File transfer incomplete", ref);
			return -2;
		}
		OSDepPrint.net("Map successfully reuploaded", ref);
		
        return 0;
	}

	/**
	 * Sends out the file size as type long. The function then proceeds
	 * to write 1 MB of data to the stream and wait for an 'ACK' from the
	 * client. The whole file is transferred as packages of 1 MB size.
	 * TODO implement a method to send out more than 1 MB
	 * 
	 * @param filepath			path to the file that should be sent
	 * @param remainingBytes	bytes remaining for the client (for retries)
	 * @return					0 on success, -1 on error
	 */
	private int sendFile(String filepath, long remainingBytes){
		int curr = 0;
		long progress = 0;
		File filedata = new File(filepath);
		long fileSize = filedata.length();
		int timeout = 130 * 1000; 																						// 130s initial timeout
		float dlspeed = 0.0f;
		
		
		// send file size
		try {
			out_data.writeLong(fileSize);
			OSDepPrint.debug("filesize: " + fileSize, ref);
			out_data.write(ConnectionCodes.END);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// send many 1mb packages
		try {
			DataInputStream in_file = new DataInputStream(new FileInputStream(filedata));
			
			byte[] buffer = new byte[1048576];
			do {
				long offset = 0;
				if(remainingBytes>0){
					do{
						curr = in_file.read(buffer, 0, buffer.length);
						progress += curr;
					} while((fileSize-remainingBytes)>progress);
					offset = (1024*1024) - (progress - (fileSize - remainingBytes));
					remainingBytes = 0;
				} else {
					curr = in_file.read(buffer, 0, buffer.length);
					progress += curr;
				}
			
				if(curr==-1) {
					in_file.close();
					break;
				}
				long seconds = sendCustomPackage(buffer, (int)offset, curr, timeout);
				if(seconds<0){
					out_data.close();
					OSDepPrint.printProgressStop();
					return -1;
				}
				dlspeed = 1024.0f/((float)seconds/1000.0f);

				
				OSDepPrint.printProgress(progress, fileSize-remainingBytes, dlspeed, ref);
				
			} while(curr>0);
			
			out_data.close();
			OSDepPrint.printProgress(progress, fileSize-remainingBytes, dlspeed, ref);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			OSDepPrint.error("(231) " + e.getMessage(), ref);
			OSDepPrint.printProgressStop();
			return -1;
		}
		OSDepPrint.printProgressStop();
		return 0;
	}	
	
	/**
	 * Writes bytes from 'bytePackage' with the specified 'offset' 
	 * 
	 * @param bytePackage		byte-array containing the data
	 * @param offset			where to start in the byte-array
	 * @param packageSize		amount of bytes the function should write
	 * @param delay				DEPRECATED
	 * @return					time needed by the client to receive the file, -1 on error
	 */
	private long sendCustomPackage(byte[] bytePackage, int offset, int packageSize, int delay) {
		long time_start = System.currentTimeMillis();
		try {
			// send the packet
			out_data.write(bytePackage, offset, packageSize-offset);
			out_data.flush();
			
			// receive the time needed by the client (initial timeout of 130 seconds)
			// 128 seconds are needed to transmit 1 MB of data with 64KBit/s
			// 64KBit/s is the lowest acceptable bandwidth
			if(getCode(delay)==ConnectionCodes.ACK){
				long time_needed = (System.currentTimeMillis() - time_start); 
				return time_needed;
			} else {
				return -1;
			}
		} catch (IOException e) {
			e.printStackTrace();
			OSDepPrint.error("(304) " + e.getMessage(), ref);
		}
		
		return -1;
	}
	

}
