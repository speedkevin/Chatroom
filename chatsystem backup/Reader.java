package chatsystem;

import java.net.*;
import java.util.*;
import java.io.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import chatsystem.Chatserver.Connection;

public class Reader {
	static Socket myclientSocket[] = new Socket[50];
	static Socket socketLock;
	
	  public static void main (String args[]) throws IOException, ParseException {
		    try{
		    	// Initialisation
		    	int i = 0;
		    	int serverPort = 4445; // the server port
		    	System.out.println("Open Server Socket");
		      	ServerSocket serverSocket = new ServerSocket(serverPort);

		      while(true) {
		    	// (1) build socket connection: create thread
		    	System.out.println("Server listening for a connection");
		    	synchronized (serverSocket){
		    		myclientSocket[i] = serverSocket.accept();
		    	}
		        socketLock = myclientSocket[i];
		        
		        System.out.println("Received connection " + i );
		        Connection c = new Connection(myclientSocket[i]);
		        System.out.println("After Connection");
		        i++;
		      } 
		    }
		    catch(IOException e)
		    {
		      System.out.println("Listen socket:"+e.getMessage());
		    }
		  }//end main 

static class Connection extends Thread {
	
	BufferedReader in;
	//static DataOutputStream out;
	OutputStreamWriter out;
	
	public Connection (Socket aClientSocket){
	try{
		in = new BufferedReader(new InputStreamReader(aClientSocket.getInputStream(), "UTF-8"));	
		//in = new BufferedReader(new InputStreamReader(new BufferedInputStream(aClientSocket.getInputStream())));
		//out = new DataOutputStream(aClientSocket.getOutputStream());
		out = new OutputStreamWriter(aClientSocket.getOutputStream(), "UTF-8");
	    this.start();
	} catch(IOException e) {
    	System.out.println("Connection:"+e.getMessage());
    }
	}
	
	public void run() {
		try{
		System.out.println("run.......");
		String socket = "socket";
		
		try{
			this.wait();
		}catch(InterruptedException e){
			System.out.println("Wait Exception:"+e.getMessage());
		}

		
		synchronized (socket){
			read(in);
			write(out, "Server got Message");
		}
		} finally{
	    	try {
	    		System.out.println("Socket close");
	    		socketLock.close();
	    	}catch (IOException e){/*close failed*/}
	    }
	}

	//public static void write(DataOutputStream outputdata, String message){
	public static void write(OutputStreamWriter outputdata, String message){
		try{
			//outputdata.write(message.getBytes("UTF-8"));
			outputdata.write(message + "\n");
			outputdata.flush();
			outputdata.close();
		}catch(IOException e) {
			System.out.println("Input Exception: "+e.getMessage());
		}
	}

	static String thisLine;
	public static void read(BufferedReader inputdata){
		try{
			System.out.println("Reading.......");
			while ((thisLine = inputdata.readLine()) != null) { // while loop begins here
		         System.out.println(thisLine);
		    }
		}catch(IOException e) {
			System.out.println("Input Exception: "+e.getMessage());
		}
	}
}

}