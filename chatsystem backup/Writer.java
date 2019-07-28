package chatsystem;

import java.net.*;
import java.util.*;
import java.io.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Writer extends Thread{
	static BufferedReader in;
	//static DataOutputStream out;
	static OutputStreamWriter out;
	
	public static void main (String args[]){
		try{
			int port = 4445;
			String host = "127.0.0.1";
			Socket mSocket = new Socket(host, port);
			//Lock lock = new Lock(5);

			try{
			in = new BufferedReader(new InputStreamReader(mSocket.getInputStream(), "UTF-8"));	
			//out = new DataOutputStream(mSocket.getOutputStream());  
			out = new OutputStreamWriter(mSocket.getOutputStream(), "UTF-8");
			
			String socket = "socket";
			synchronized(socket) {
				write(out, "Hello World!!");
				

				read(in);
			}
		 
			} catch(IOException e) {
				System.out.println("Connection:"+e.getMessage());
			} finally {
				if(mSocket != null) 
					try {
						System.out.println("Socket close");
						mSocket.close();
					}catch (IOException e){
						System.out.println("close:"+e.getMessage());
					}
			}
		}catch (IOException e){
			System.out.println("readline:"+e.getMessage());
		}finally{
		
		}
	}
	
	//public static void write(DataOutputStream outputdata, String message){
	public static void write(OutputStreamWriter outputdata, String message){
		try{
			System.out.println("Writing.......");
			//outputdata.write(message.getBytes("UTF-8"));
			outputdata.write(message + "\n");
			outputdata.flush();
			outputdata.close();
		}catch(IOException e) {
			System.out.println("Write Input Exception: "+e.getMessage());
		}
	}
	
	static String thisLine;
	public static void read(BufferedReader inputdata){
		try{
			//System.out.println("Reading.......");
			while ((thisLine = inputdata.readLine()) != null) { // while loop begins here
		         System.out.println(thisLine);
		    }
		}catch(IOException e) {
			System.out.println("Read Input Exception: "+e.getMessage());
		}
	}

}
