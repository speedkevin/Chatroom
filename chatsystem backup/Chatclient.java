package chatsystem;

import java.util.*;
import java.net.*;
import java.io.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Chatclient {
    /*****************************************/
    /********** Variables & Methods **********/
    /*****************************************/
	
	/*** Record the client name ***/
	static String guest;
	
	static BufferedReader in;
	static OutputStreamWriter out;
	
	/*** Setter & getter: client name ***/
    public static String setGuest(int i){
    	  guest = "guest" + i;
    	  return guest;
    }
      
    public static String getGuest(){
    	  return guest;
    }

	/*****************************************/
    /********** main and connection **********/
    /*****************************************/
    //public static Message message = new Message("Howdy");
    //static Chatserver sermsg;
    //public static Message message = new Message("Howdy");
	
    public static void main (String args[]) throws UnknownHostException {	
		int serverPort = 4444;
		//String host = "127.0.0.1";
		//Message message = sermsg.message;
		Connection c = new Connection(args[0], serverPort);
		Thread newConnection = new Thread(c, "clientThread");
		newConnection.start();
		
	} // end main
	
    static class Connection implements Runnable{//extends Thread {
	
	static Socket mSocket;
	
	public Connection(String host, int port) {
	  	try {
	  		mSocket = new Socket(host, port);
	  		mSocket.setSoTimeout(3000);
	    } catch(IOException e) {
	    	System.out.println("Connection:"+e.getMessage());
	    } 
	} // end socket connection
	
	public synchronized void run() {
		try{
		System.out.println("Connection Established");
		
		try{
			String identity; // the name of client
			String roomid; // the chatroom that client enter
			String receiveMessage;
			String command; // the command that client input
			String option; // the argument that client input
					
			in = new BufferedReader(new InputStreamReader(mSocket.getInputStream(), "UTF-8"));	 
			out = new OutputStreamWriter(mSocket.getOutputStream(), "UTF-8");
		
			// (2) newidentity
			// sending newidentity messages to server
			System.out.println("First connects: Sending data");
			
			write(out, "4444");
			identity = read(in);
			System.out.println("First connects Received: "+ identity);
			
			// parse message
			identity = parseRoutine(identity, "identity");
			System.out.println("Connected to localhost as " + identity);
		
			// (3) MainHall
			firstjoin(out);
			String mainhall = read(in);		
			identity = parseRoutine(mainhall, "identity");
			roomid = parseRoutine(mainhall, "roomid");
			System.out.println(identity + " moves to " + roomid);
			
			// get RoomList and RoomContents
			String MHList = read(in);
			System.out.println("MainHall List is: " + MHList);
			String roomContent = read(in);
			System.out.println("Room Content is: " + roomContent);
			
			// (4) run command or message (5) quit 
			while(true){
				//standard input
				Scanner keyboard = new Scanner(System.in);
				System.out.println("[" + roomid + "] " + identity + ">");
				
				command = keyboard.next();
				if(command.equalsIgnoreCase("#list")){
					option = "";
				}if(command.equalsIgnoreCase("#quit")){
					option = "";
				}else{
					option = keyboard.next();
				}
				
				if(command.startsWith("#")){
					command = command.substring(1);
					switch(command.toLowerCase()){
						case "message":
							JSONObject msg = new JSONObject();
							msg.put("type", "message");
							msg.put("content", option);
							msg.put("roomid", roomid);
							msg.put("identity", identity);
							write(out, msg.toString());
							
							receiveMessage = read(in);
							if(receiveMessage.isEmpty()){
								break;
							}
							System.out.println("Receive Message:" + receiveMessage);
							break;
						case "identitychange":
							identitychange(out, command, option, roomid, identity);
							receiveMessage = read(in);
							if(receiveMessage.isEmpty()){
								break;
							}
							System.out.println("Receive Message:" + receiveMessage);
							String formerID = parseRoutine(receiveMessage, "former");
							String newID = parseRoutine(receiveMessage, "identity");
							if(formerID.equalsIgnoreCase(newID))
								System.out.println("Requested Invalid or In Use");
							else {
								System.out.println(formerID + " is now " + newID);
								identity = option;
							}
							break;
						case "join":
							joinTo(out, command, option, roomid, identity);
							receiveMessage = read(in);	
							if(receiveMessage.isEmpty()){
								break;
							}
							if(option.equalsIgnoreCase("mainhall")){
								System.out.println("Receive Message:" + receiveMessage);
								System.out.println("User moves from " + identity + " to " + option);
								roomid = option;
							}else{
								System.out.println("Receive Message:" + receiveMessage);
								identity = parseRoutine(receiveMessage, "identity");
								roomid = parseRoutine(receiveMessage, "roomid");
								String former = parseRoutine(receiveMessage, "former");
								if(roomid.equalsIgnoreCase(""))
									System.out.println("The requested room is invalid or non existent");
								else if (roomid.equalsIgnoreCase(former))
									System.out.println("You are alreay in room \"" + roomid + "\"");
								else{
									System.out.println(identity + " moves to " + roomid);
								}
								roomid = option;
							}
							break;
						case "who":
							who(out, command, option);
							receiveMessage = read(in);		
							if(receiveMessage.isEmpty()){
								break;
							}
							System.out.println("Receive Message:" + receiveMessage);
							break;
						case "list":
							System.out.println("Into list Switch");
							list(out, command);
							receiveMessage = read(in);	
							if(receiveMessage.isEmpty()){
								break;
							}
							System.out.println("Receive Message:" + receiveMessage);
							break;
						case "createroom": //OK
							askcreateroom(out, command, option, identity);
							receiveMessage = read(in);
							if(receiveMessage.isEmpty()){
								break;
							}
							System.out.println("Receive Message:" + receiveMessage);
							break;
						case "kick":
							JSONObject obj = new JSONObject();
							obj.put("type", command);
							obj.put("roomid", roomid);
							obj.put("time", 3600);
							obj.put("identity", option);
							write(out, obj.toString());
							receiveMessage = read(in);
							if(receiveMessage.isEmpty()){
								break;
							}
							String kickreply = parseRoutine(receiveMessage, "type");
							if(kickreply.equalsIgnoreCase("roomlist")){
								System.out.println("Receive Message:" + receiveMessage);
							}else{
								System.out.println("You are not the owner. Or the room does not existent.");
							}
							break;
						case "delete":
							RoomChange(out, command, option, roomid, identity);
							receiveMessage = read(in);
							if(receiveMessage.isEmpty()){
								break;
							}
							String reply = parseRoutine(receiveMessage, "type");
							if(reply.equalsIgnoreCase("roomlist")){
								System.out.println("Receive Message:" + receiveMessage);
							}else{
								System.out.println("You are not the owner. Or the room does not existent.");
							}
							break;
						case "quit":
							JSONObject quitobj = new JSONObject();
							quitobj.put("type", command);
							quitobj.put("identity", identity);
							quitobj.put("roomid", roomid);
							write(out, quitobj.toString());
							receiveMessage = read(in);
							System.out.println("Receive Message:" + receiveMessage);
							if(receiveMessage.isEmpty()){
								break;
							}
							break;
						default:
							break;
					}// end switch
				}
				else {
					System.out.println("This is message");
					System.out.println("command = " + command);
					System.out.println("option = " + option);
				}
				if(command.equalsIgnoreCase("quit"))
					break;
			} // end while
		}catch (EOFException e){
			System.out.println("EOF:"+e.getMessage());
		}catch (IOException e){
			System.out.println("readline:"+e.getMessage());
		}finally {
			if(mSocket != null) 
				try {
					System.out.println("Socket close");
					mSocket.close();
				}catch (IOException e){
					System.out.println("close:"+e.getMessage());
				}
		} // end inner try-catch
	
	}finally{
		
	} // end outer try-catch
	} // end run
	  
  	// ********************Sub-routine***********************
	
	public static void write(OutputStreamWriter outputdata, String message){
		try{
			outputdata.write(message + "\n");
			outputdata.flush();
		}catch(IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static String read(BufferedReader inputdata){
		//System.out.println("Reading......");
		String msg = "";
		try{
			msg = inputdata.readLine();
		}catch(IOException e) {
			System.out.println(e.getMessage());
		}
		return msg;	
	}
	
	public static void RoomChange(OutputStreamWriter outputdata,
  			String type, String newRoomName, String oldRoomName, String identity) {
			JSONObject obj = new JSONObject();
			obj.put("type", type);
			obj.put("identity", identity);
			obj.put("former", oldRoomName);
			obj.put("roomid", newRoomName);		
			write(outputdata, obj.toString());
	}
	
	public static void deleteMsg(OutputStreamWriter outputdata,
			String command, String option){
			JSONObject obj = new JSONObject();
			obj.put("type", command);
			obj.put("roomid", option);
			write(outputdata, obj.toString());
	}
	
	public static void printNewRoomArrayList(ArrayList<String> NewRoom){
		for (String entry: NewRoom){
			System.out.println("Create room identity:" +entry
					+ " and its index: " +NewRoom.indexOf(entry));
		}
	}
	
	public static void askcreateroom(OutputStreamWriter outputdata, 
			String command, String option, String identity) {
			JSONObject obj = new JSONObject();
			obj.put("type", command);
			obj.put("roomid", option);
			obj.put("identity", identity);
			write(outputdata, obj.toString());
	}
	
	public static void list(OutputStreamWriter outputdata, 
			String command) {
			JSONObject obj = new JSONObject();
			obj.put("type", command);
			write(outputdata, obj.toString());
	}
	
	
	public static void who(OutputStreamWriter outputdata, 
			String command, String option) {		
			JSONObject obj = new JSONObject();
			obj.put("type", command);
			obj.put("roomid", option);
			write(outputdata, obj.toString());
	}
	
	public static void identitychange(OutputStreamWriter outputdata, 
			String command, String option, String roomid, String oldname) {
			JSONObject obj = new JSONObject();
			obj.put("type", command);
			obj.put("identity", option);
			obj.put("oldidentity", oldname);
			obj.put("roomid", roomid);
			write(outputdata, obj.toString());
	}
	
	public static void firstjoin(OutputStreamWriter outputdata) {
			JSONObject obj = new JSONObject();
			obj.put("type", "firstjoin");
			write(outputdata, obj.toString());
	}

	// room: the room that client wants to join
	public static void joinTo(OutputStreamWriter outputdata, 
			String command, String newroomid, String oldroomid, String identity) {		
			JSONObject obj = new JSONObject();
			obj.put("type", command);
			obj.put("roomid", newroomid);
			obj.put("oldroomid", oldroomid);
			obj.put("identity", identity);
			write(outputdata, obj.toString());
	}
	
    // Whole Message: parseMessage
    // Variable of the field: field
    // The field I want to parse: field
    public static String parseRoutine(String parseMessage, String field){
    	try{
    		JSONParser parser = new JSONParser();
    		JSONObject object = (JSONObject) parser.parse(parseMessage);
    		String fieldname = (String) object.get(field);
    		return fieldname;
    	} catch (ParseException e){
			System.out.println("Parse Exception: "+e.getMessage());
			return "";
		}
    }
}// end connection extends Thread

} // end class