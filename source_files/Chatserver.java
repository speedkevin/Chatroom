package chatsystem;

import java.net.*;
import java.util.*;
import java.io.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import chatsystem.Chatclient.Connection;

public class Chatserver {
  /*****************************************/
  /*********** Global Variables ***********/
  /***************************************/
  /* ---Room and Guest List--- */ 
  /*** Record the key: room name, and value: guest list ***/
  static HashMap<String, ArrayList<String>> MapList = new HashMap<String,ArrayList<String>>(10);
 
  /*** Record the room name list ***/
  static ArrayList<String> roomList = new ArrayList<String>(100);
  
  /*** Record the guest name list ***/
  static ArrayList<String> guestList = new ArrayList<String>(100);

  /* ---Message related--- */  
  /*** Record the key: guest name, and value: message set ***/
  static HashMap<String, ArrayList<String>> msgMap = new HashMap<String,ArrayList<String>>(50);
  
  /*** Record the message list ***/
  static ArrayList<String> msgList = new ArrayList<String>(100);
  
  /*** Record the key: previous time that client send message
   * value: the time that client send message ***/
  static HashMap<String, ArrayList<Integer>> timestamp = new HashMap<String, ArrayList<Integer>>(50);
  
  /*** Record the key: guest name, and value: guest from set ***/
  static HashMap<String, ArrayList<String>> msgFrom = new HashMap<String,ArrayList<String>>(50);
  
  /* ---Owner related--- */  
  /*** Record the key: guest name, and value: message set ***/
  static HashMap<String, String> ownerMap = new HashMap<String, String>(50);
  
  /* ------ */
  public static void clearGuest(String oldRoomName, String guestName){
	  GuestListDeletion(oldRoomName, guestName);
	  if(guestList.contains(joinPreID)){
		  guestList.remove(joinPreID);
	  }
	  if(msgMap.containsKey(joinPreID)){
		  msgMap.remove(joinPreID);
	  }
	  if(timestamp.containsKey(joinPreID)){
		  timestamp.remove(joinPreID);
	  }
	  if(msgFrom.containsKey(joinPreID)){
		  msgFrom.remove(joinPreID);
	  }
	  
	  Iterator<Map.Entry<String,ArrayList<String>>> iter = msgFrom.entrySet().iterator();
	  while (iter.hasNext()) {
		  System.out.println("While....");
		    Map.Entry<String,ArrayList<String>> entry = iter.next();
		    ArrayList<String> a = entry.getValue();
		    if(a.contains(joinPreID)){
		    	for(String it: a){
		    		if(it.equalsIgnoreCase(joinPreID)){
		    			int index = it.indexOf(joinPreID);
		    			a.remove(index);
		    			ArrayList<String> mylist = MapList.get(oldRoomName);
		    			mylist.remove(index);
		    		}
		    	}
		    }
	  }
  }
  
  public static void changeID(String roomID, String oldName, String newName){
	  // sub mapList
	  GuestListSubstitution(roomID, oldName, newName);
	  
	  // sub guest list
	  if(guestList.contains(oldName)){
		  guestList.remove(oldName);
		  guestList.add(newName);
	  }
	  
	  // swap msgMap's key
	  if(msgMap.containsKey(oldName)){
		  ArrayList<String> a = msgMap.get(oldName);
		  msgMap.remove(oldName);
		  msgMap.put(newName, a);
	  }
	  
	  // swap timestamp's key
	  if(timestamp.containsKey(oldName)){
		  ArrayList<Integer> a = timestamp.get(oldName);
		  timestamp.remove(oldName);
		  timestamp.put(newName, a);
	  }
	  
	  // swap msgFrom's key
	  if(msgFrom.containsKey(oldName)){
		  ArrayList<String> a = msgFrom.get(oldName);
		  msgFrom.remove(oldName);
		  msgFrom.put(newName, a);
	  }
	  
	  // scan msgFrom's old value and replace newName
	  Iterator<Map.Entry<String,ArrayList<String>>> iter = msgFrom.entrySet().iterator();
	  while (iter.hasNext()) {
		    Map.Entry<String,ArrayList<String>> entry = iter.next();
		    ArrayList<String> a = entry.getValue();
		    if(a.contains(oldName)){
		    	Collections.replaceAll(a, oldName, newName);
		    }
	  }
  }
/* ------ */ 
  /*** Record the guest name, e.g. guest1 when guestNum = 1 ***/
  static String guest;
  static int guestNum = 1;
  static String reqGuest;
  
  /*** Record the owner name, and number of owners ***/
  static String owner;
  static int ownerIndex = 0;

  /*** Record the new room name when it was created ***/
  static String newRoomID;
  
  /*** Record the new identity and previous identity when joining a room ***/
  static String joinNewRoomID;
  static String joinPreID;
  
  /*** number of rooms in total ***/
  static int roomIndex = 0;
  
  /*** If the first client connect to server, add to map of MainHall ***/
  /*** otherwise add to value of MainHall ***/
  static boolean clientFirst = true;
  
  // First client connect to server
  static boolean firstNewConnection;
  
  // Clients other than the fist one connect to server
  static boolean getNewConnection;
  
  /*****************************************/
  /*** Setter & Getter: owner and guest ***/
  /***************************************/
  public static void setOwner(String room, String myOwner){
	  System.out.println("Room / Owner: " + room + ", " + myOwner);
	  ownerMap.put(room, myOwner);
	  owner = myOwner;
  }
  
  public static String getOwner(){
	  return owner;
  }
  
  // The latest client connected to server
  public static void setGuest(){
	  guest = "guest" + guestNum;
	  guestList.add(guest);
	  guestNum++;
  }
  
  public static String getGuest(){
	  return guest;
  }
  /*******************************************/
  /*********** Print Key and Value ***********/
  /*******************************************/
  
  public static void printMap(HashMap<String, ArrayList<String>> mymap){
	  System.out.println("");
	  System.out.println("Room Name and its Guests ... ");
	  System.out.println(mymap.keySet());
	  System.out.println(mymap.values());  
  }
  
  public static void printArrayList(ArrayList<String> mylist){
	  int i = 0;
	  for(String entry:mylist){
		  System.out.println(mylist.get(i));
		  i++;
	  }
  }
  
  /*******************************************/
  /*** Operations on individual ArrayList ***/
  /*****************************************/
  public static void ArrayListAddition(ArrayList<String> mylist){
	  setGuest();
	  mylist.add(getGuest());
  }
  
  public static String getArrayValue(ArrayList<String> mylist, int i){
	  return mylist.get(i);
  }
  
  public static boolean isArrayListContain(ArrayList<String> mylist, String myValue){
	  if(mylist.contains(myValue))
		  return true;
	  else
		  return false;
  }
  
  /***************************************************/
  /*** Operations on MapList (sync with roomList) ***/
  /*************************************************/
  public static void MapListAddition(String roomName, ArrayList<String> mylist){
	  MapList.put(roomName, mylist);
	  roomList.add(roomName);
  }
  
  public static void MapListDeletion(String roomName, ArrayList<String> mylist){
	  MapList.remove(roomName, mylist);
	  roomList.remove(roomName);
  }
  
  public static void MapListKeySub(String oldRoomName, String newRoomName, ArrayList<String> mylist){
	  ArrayList<String> a = MapList.get(oldRoomName);
	  MapList.remove(oldRoomName, mylist);
	  MapList.put(newRoomName, a);
	  roomList.remove(oldRoomName);
	  roomList.add(newRoomName);
  }
  
  public static ArrayList<String> getMapList(String keyName){
	  return MapList.get(keyName);
  }
  
  public static String getUserOnMap(String keyName, int index){
	  ArrayList<String> guests = MapList.get(keyName);
	  return guests.get(index);
  }
  
  public static boolean isKeyOnMap(String key){
	  if(MapList.containsKey(key))
		  return true;
	  else
		  return false;
  }
  
  public static boolean isValueOnMap(String value){
	  if(guestList.contains(value))
		  return true;
	  else
		  return false;
  }

  /*******************************************/
  /*** Operation on ArrayList from MapList ***/
  /*******************************************/
  static int firstEnterRoom = 0;
  static boolean firstEnter = false;
  public static void GuestListInsertion(String roomName, String guest){
	  ArrayList<String> mylist = MapList.get(roomName);
  	  if(firstEnter){
  		  if(mylist.contains("")){ 
  			  mylist.set(firstEnterRoom, guest);
  			  firstEnterRoom = 0;
  			  firstEnter = false;  
  		  }else{
  			  mylist.add(guest);
  		  }
  	  }
  	  else{
  		  mylist.add(guest);
  	  }
  }
  
  public static void GuestListDeletion(String roomName, String guest){
	  ArrayList<String> mylist = MapList.get(roomName);
  	  mylist.remove(guest);
  }
  
  public static void GuestListDeletionAll(String roomName){
	  ArrayList<String> mylist = MapList.get(roomName);
  	  mylist.removeAll(mylist);
  }
  
  public static void GuestListSubstitution(String roomName, 
		  String oldGuest, String newGuest){
	  ArrayList<String> mylist = MapList.get(roomName);
	  int index = mylist.indexOf(oldGuest);
  	  mylist.remove(oldGuest);
  	  mylist.add(index, newGuest);
  }
  
  public static boolean isUserOnMap(String roomName, String user){
	  ArrayList<String> mylist = MapList.get(roomName);
	  if(MapList.containsKey(roomName)){
		  if(mylist.contains(user))
			  return true;
		  else
			  return false;
	  }else{
		  return false;
	  }
  }
  
  public static int numberOfGuest(String roomName){
	  ArrayList<String> mylist = MapList.get(roomName);
	  int i = 0;
	  for(String entry:mylist){
		  if(!entry.equalsIgnoreCase(""))
			  i++;
	  }
	  return i; //other than guest0, the record of owner
  }
  
  /*******************************************/
  /*** Operation on Owner Map ***/
  /*******************************************/
  
  public static boolean isOwnerOnMap(String owner){
	  if(ownerMap.containsValue(owner))
		  return true;
	  else
		  return false;
  }
  
  /*******************************************/
  /*** Operation on msgList and timestamp ***/
  /*******************************************/
  
  public static void printAll(){
	  printMsgMap(msgMap);
	  printTimestamp(timestamp);
	  printMsgFrom(msgFrom);
  }
  
  public static void printMsgMap(HashMap<String, ArrayList<String>> mymap){
	  System.out.println("This is Guests and their Messages.");
	  System.out.println(mymap.keySet());
	  System.out.println(mymap.values());  
  }
  
  public static void printTimestamp(HashMap<String, ArrayList<Integer>> mymap){
	  System.out.println("This is Timestamp");
	  System.out.println(mymap.keySet());
	  System.out.println(mymap.values()); 
  }
  
  public static void printMsgFrom(HashMap<String, ArrayList<String>> mymap){
	  System.out.println("This is msgFrom");
	  System.out.println(mymap.keySet());
	  System.out.println(mymap.values());
	  System.out.println("=================");
  }
  
  public static boolean isKeyOnMsgMap(String key, HashMap<String, ArrayList<String>> map){
	  if(map.containsKey(key))
		  return true;
	  else
		  return false;
  }
  
  public static void MsgListInsertion(String key, String message){
	  ArrayList<String> mylist = msgMap.get(key);
  	  mylist.add(message);
  }
  
  public static void MsgMapAddition(String guestName, ArrayList<String> mylist){
	  msgMap.put(guestName, mylist);
  }
  
  public static int MsgIndex(String key, String message){
	  int index;
	  ArrayList<String> mylist = msgMap.get(key);
	  index = mylist.indexOf(message);
	  return index;
  }
  
  public static void setTime(String name, int pre, int now){
	  ArrayList<Integer> a = new ArrayList<Integer>(100);
	  a.add(0, pre);
	  a.add(1, now);
	  timestamp.put(name, a);
  }
  
  public static int retriveTime(String name, int index){
	  ArrayList<Integer> a = timestamp.get(name);
	  return a.get(index);
  }
  
  public static String retrivePartMessage(String name, int index){
	  ArrayList<String> a = msgMap.get(name);
	  return a.get(index);
  }
  
  public static boolean isKeyOnFromMap(String key, HashMap<String, ArrayList<String>> map){
	  if(map.containsKey(key))
		  return true;
	  else
		  return false;
  }
  
  public static void msgFromAddition(String guestName, ArrayList<String> mylist){
	  msgFrom.put(guestName, mylist);
  }
  
  public static void msgFromInsertion(String toName, String fromName){
	  ArrayList<String> mylist = msgFrom.get(toName);
  	  mylist.add(fromName);
  }
	
  /***************************************/
  /************ Main Program *************/
  /***************************************/
  static Socket myclientSocket[] = new Socket[50];
  public static void main (String args[]) throws IOException, ParseException {
    try{
    	// Initialisation
    	int i = 0;
    	int serverPort = 4444; // the server port
    	System.out.println("Open Server Socket");
      	ServerSocket serverSocket = new ServerSocket(serverPort);
      	firstNewConnection = true;
      	
      while(true) {
    	// (1) Server Connection
    	getNewConnection = true;
    	
    	myclientSocket[i] = serverSocket.accept();
		System.out.println("");
		System.out.println("Received connection " + i );
	
		ServerConnection c = new ServerConnection(myclientSocket[i]);
		Thread newConnection = new Thread(c, "serverThread");
		newConnection.start();
		i++;
      } 
    }
    catch(IOException e)
    {
      System.out.println("Listen socket:"+e.getMessage());
    }
  }//end main 
	
  static class ServerConnection implements Runnable {
  		
	static BufferedReader in;
	static OutputStreamWriter out;
	static Socket clientSocket;
	static String quitID; // Record the guest name when socket close
	
	public ServerConnection (Socket aClientSocket) {
  	try {
    	clientSocket = aClientSocket;
		in = new BufferedReader(new InputStreamReader(aClientSocket.getInputStream(), "UTF-8"));
		out = new OutputStreamWriter(aClientSocket.getOutputStream(), "UTF-8");
    } catch(IOException e) {
    	System.out.println("Connection:"+e.getMessage());
    } 
  	} // end socket connection
	
  	public synchronized void run() {
    try {
    	// Declaration
    	String typename;
    	String reader;
    	boolean firstNewIdentity = true;
    	
    	// (2) First connects
    	String port;
    	port = read(in);
    	newidentity(out, firstNewIdentity, "", "");
    	
    	// (3) MainHall
    	String joinMsg = read(in);
    	typename = parseRoutine(joinMsg, "type");
    	RoomChange(out, typename, "", "");
    	
    	// set RoomList and RoomContents
    	RoomList(out,"list");
    	RoomContents(out, roomList.get(0)); //Hashmap
    	
    	while(true)
    	{
    	printMap(MapList);
    	System.out.println("==============================");
    	System.out.println("Waiting for clients messages....");
    	System.out.println("");

    	reader = read(in);	
    	if(clientSocket.isClosed()){
    		System.out.println("Socket Already Close");
    		break;
		}
    	if(reader == null){
			System.out.println("Reader is null");
			break;
		}
		typename = parseRoutine(reader, "type");
			
    	switch(typename.toLowerCase()){
		case "identitychange":
			String newIdentity = parseRoutine(reader, "identity");
			String myOldID = parseRoutine(reader, "oldidentity");
			String myOldRoomID = parseRoutine(reader, "roomid");
			if(isValid(typename, newIdentity, 3, 16, myOldRoomID)){
				if(newIdentity.equalsIgnoreCase(getGuest())) { //compare to previous name
					newidentity(out, false, newIdentity, myOldRoomID);
				}else{
					// **** broadcast ****
					changeID(myOldRoomID, myOldID, newIdentity);
						
					JSONObject obj = new JSONObject();
					obj.put("type", "newidentity");
					obj.put("former", myOldID);
					obj.put("identity", newIdentity);
					
					write(out, obj.toString());
				}
			}else{
					
				JSONObject obj = new JSONObject();
				obj.put("type", "Invalid");
				obj.put("former", "");
				obj.put("identity", "");
				
				write(out, obj.toString());
			}
			break;
		case "join":
			joinNewRoomID = parseRoutine(reader, "roomid");
			joinPreID = parseRoutine(reader, "identity");
			String oldRoomID = parseRoutine(reader, "oldroomid");
			if(isValid(typename, joinNewRoomID, 3, 32, oldRoomID)){
				if(roomList.contains(joinNewRoomID)) { //Hashmap
					typename = "roomExist";
					if(joinNewRoomID.equals("MainHall")){
						
						GuestListInsertion("MainHall", getGuest());
						GuestListDeletion(oldRoomID, getGuest());
						
						JSONArray listAll = new JSONArray();
						
						JSONObject obj1 = new JSONObject();
						JSONObject obj2 = new JSONObject();
						JSONObject obj3 = new JSONObject();
						
						// RoomChange
						obj1.put("type", "roomchange");
						obj1.put("identity", getGuest());
						obj1.put("former", oldRoomID);
						obj1.put("roomid", joinNewRoomID);
						
						// RoomList
						int i = 0;
						JSONArray list = new JSONArray();
						while(i<=roomIndex){
							JSONObject room2 = new JSONObject();
							room2.put("roomid", roomList.get(i)); //Hashmap
							room2.put("count", numberOfGuest(roomList.get(i)));
							i++;
							list.add(room2);
						}
						obj2.put("type", "roomlist");
						obj2.put("rooms", list);
						
						// RoomContents
						obj3.put("type", "roomcontents");
						obj3.put("roomid", roomList.get(0));  //Hashmap
						obj3.put("identities", ""); 
						obj3.put("owner", ""); 
						
						listAll.add(obj1);
						listAll.add(obj2);
						listAll.add(obj3);
						write(out, listAll.toString());
						
					}else{
						// **** broadcast ****
						RoomChange(out, typename, joinNewRoomID, oldRoomID);	
					}
				}else{
					typename = "roomNotExist";
					RoomChange(out, typename, joinNewRoomID, "");
				}
			}else{
				typename = "roomInvalid";
				RoomChange(out, typename, joinNewRoomID, "");
			}			
			break;
		case "who":
			String reqRoomID = parseRoutine(reader, "roomid");
			RoomContents(out, reqRoomID);
			break;
		case "list":
			RoomList(out, typename);
			break;
		case "createroom":
			newRoomID = parseRoutine(reader, "roomid");
			String identity = parseRoutine(reader, "identity");
			if(isValid(typename, newRoomID, 3, 32, "")){
				if(roomList.contains(newRoomID)) { //Hashmap
					typename = "hasExist";
					RoomList(out, typename);
				}else{
					// (1) Existing client create room again
					// (2) New client create room
					if(isValueOnMap(identity)){
						setOwner(newRoomID, getGuest());
						ArrayList<String> a = new ArrayList<String>(20);
						a.add("");
						MapListAddition(newRoomID, a);
					}else{
						setOwner(newRoomID, getGuest());
						ArrayList<String> a = new ArrayList<String>(20);
						a.add(getGuest());
						MapListAddition(newRoomID, a);
						GuestListDeletion("MainHall", getGuest());
					}
					
					RoomList(out, typename);
				}
			}else{
				typename = "roominvalid";
				RoomList(out, typename);
			}
			break;
		case "kick":
			String kickname = parseRoutine(reader, "identity");
			String roomid = parseRoutine(reader, "roomid");
			if(isKeyOnMap(roomid)){
				if(isUserOnMap(roomid, kickname)){
					GuestListDeletion(roomid, kickname);
					RoomList(out,"list");
				}else{
					nullRoomList(out);
				}
			}else{
				nullRoomList(out);
			}
			break;
		case "delete":
			String delRoom = parseRoutine(reader, "roomid");
			String roomowner = parseRoutine(reader, "identity");
			if(isKeyOnMap(delRoom)){
				if(isOwnerOnMap(roomowner)){
					MapListDeletion(delRoom, MapList.get(delRoom));
					roomIndex--;
					RoomList(out,"list");
				}else{
					// not owner
					nullRoomList(out);
				}
			}else{
				nullRoomList(out);
			}
			break;
		case "message":
			String msgIdentity = parseRoutine(reader, "identity");
			String content = parseRoutine(reader, "content");
			storeMessage(msgIdentity, content);
			ArrayList<String> a = retriveMessage(msgIdentity);
			
			JSONObject obj = new JSONObject();
			obj.put("type", "messgae");
			obj.put("identity", msgIdentity);
			
			int i = 0;
			JSONArray list = new JSONArray();
			
			if(msgFrom.isEmpty()){
				obj.put("content", "");
			}else{
				ArrayList<String> mylist = msgFrom.get(msgIdentity);
				for(String entry: mylist){
					JSONObject k = new JSONObject();
					k.put(mylist.get(i), a.get(i));
					i++;
					list.add(k);
				}
				obj.put("content", list);
			}
			write(out, obj.toString());

			break;
		case "quit":
			String quittype = parseRoutine(reader, "type");
			String qroomid = parseRoutine(reader, "roomid");
			quitID = parseRoutine(reader, "identity");
			// **** broadcast ****
			RoomChange(out, quittype, "", qroomid);
			break;
		default:
			break;
    	} // end switch
    	} //end while

    } finally{
    	try {
    		System.out.println(quitID + ", Before Socket Close ... ");
    		clientSocket.close();
    		System.out.println(quitID + " Socket is Close ... ");
    		// myclientSocket[i].close();
    		//mSocket.close();
    	}catch (IOException e){/*close failed*/}
    } // end try-catch
  	} // end run()
  
  	// ********************Sub-routine**********************

	public static void write(OutputStreamWriter outputdata, String message){
		try{
			outputdata.write(message + "\n");
			outputdata.flush();
		}catch(IOException e) {
			System.out.println("Input Exception: "+e.getMessage());
		}
	}
	
	public static String read(BufferedReader inputdata){
		String msg = "";
		try{
			msg = inputdata.readLine();
		}catch(IOException e) {
			System.out.println("Input Exception: "+e.getMessage());
		}
		return msg;	
		
	}
  	
  	static int preTimeIndex = 0, nowTimeIndex = 0;
  	static String preClient = "";
  	public static void storeMessage(String clientName, String message){
  		// Store data
  		if(isKeyOnMsgMap(clientName, msgMap)){
  			// store
  			MsgListInsertion(clientName, message);
  		}else{
  			// create
  			ArrayList<String> a = new ArrayList<String>(20);
			a.add(message);
  			MsgMapAddition(clientName, a);
  		}
  		if(isKeyOnFromMap(clientName, msgFrom)){
  			
  		}else{
  			ArrayList<String> a = new ArrayList<String>(20);
  			msgFromAddition(clientName, a);
  		}
  		// set timestamp, store into timestamp map
  		preTimeIndex = 0;
  		nowTimeIndex = 0;
  		nowTimeIndex = MsgIndex(clientName, message);
  		
  		// new client
  		if(!timestamp.containsKey(clientName)){
  			ArrayList<Integer> a = new ArrayList<Integer>(100);
  			a.add(0, 0);
  			a.add(1, 0);
  			timestamp.put(clientName, a);
  		}
  		
  		if(clientName.equalsIgnoreCase(preClient)){
  			ArrayList<Integer> a = timestamp.get(clientName);
  			a.set(0, nowTimeIndex);
  		}
  		preClient = clientName;
  		
  		ArrayList<Integer> x = timestamp.get(clientName);
  		setTime(clientName, x.get(0), nowTimeIndex);
  		
  		// syn with other message list
  		// add message to other clients' message list
  		Set<String> clientNameSet = msgMap.keySet();
  		for(String entry:clientNameSet){
  			if(!entry.equalsIgnoreCase(clientName)){ // choose the name, exclude clientName itself
  				MsgListInsertion(entry, message);
  				msgFromInsertion(entry, clientName);
  			}
  		}
  	}
  	
  	public static ArrayList<String> retriveMessage(String clientName){
  		ArrayList<Integer> timeTag = timestamp.get(clientName);
  		if(timeTag.get(1) == 0){
  			// reply ""
  			// set time
  			ArrayList<String> mlist = new ArrayList<String>(10);
  			mlist.add("");
  			printMsgMap(msgMap);
  			return mlist;
  		}else{
  			// retrive pre, now
  			int pre = retriveTime(clientName, 0);
  			int now = retriveTime(clientName, 1);
  			// if pre > now, SWAP
  			int temp;
  			if(pre > now){
  				temp = pre;
  				pre = now;
  				now = temp;
  			}
  			// store message
  			int i = pre + 1;
  			String s;
  			ArrayList<String> mlist = new ArrayList<String>(100);
  			while(pre<i && i<now){
  				s = retrivePartMessage(clientName, i);
  				System.out.println("Part of Message: " + s);
  				mlist.add(s);
  				i++;
  			}
  			//preTimeIndex = nowTimeIndex;
  			ArrayList<Integer> a = timestamp.get(clientName);
  			a.set(0, now);
  			
  			printMsgMap(msgMap);
  			return mlist;
  		}
  		
  	}
  	
    // ******************** RoomList ***********************
  	public static void nullRoomList(OutputStreamWriter outputdata){
  			JSONObject object = new JSONObject();
  			object.put("type", "null"); // set no any room if no key
			write(outputdata, object.toString());
  	}
  	
	public static void RoomList(OutputStreamWriter outputdata, String type){			
			if(type.equalsIgnoreCase("roominvalid") || 
					type.equalsIgnoreCase("hasExist")){				
				JSONObject obj = new JSONObject();
				obj.put("message", "Room name is invalid or already in use");			
				write(outputdata, obj.toString());
			}
			
			// when create room
			if(type.equalsIgnoreCase("createroom")){
				roomIndex++;
				RoomListMessage(out, "createroom");		
			}
		
			// list command or first connect
			if(type.equalsIgnoreCase("list")){
				RoomListMessage(out, "list");
			}
	}
	
	public static void RoomListMessage(OutputStreamWriter outputdata,
			String type){
			int i = 0;
			JSONArray list = new JSONArray();
			while(i<=roomIndex){
				JSONObject room2 = new JSONObject();
				room2.put("roomid", roomList.get(i)); // Hashmap
				room2.put("count", numberOfGuest(roomList.get(i)));

				i++;
				list.add(room2);
			}
			JSONObject obj = new JSONObject();
			obj.put("type", "roomlist");
			obj.put("rooms", list);
			write(outputdata, obj.toString());
	}
	
	// ******************** RoomContents ***********************
  	public static void RoomContents(OutputStreamWriter outputdata,
  			String reqRoomID){		
			if(!isKeyOnMap(reqRoomID)){
				// requested room is NOT in the room list
				JSONObject obj = new JSONObject();
				obj.put("type", "");
				obj.put("roomid", "not exist"); 
				obj.put("identities", ""); 
				obj.put("owner", ""); 
				write(outputdata, obj.toString());
			} else{
				if(reqRoomID.equalsIgnoreCase("mainhall")){
					// requested room is MainHall
					JSONObject obj = new JSONObject();
					obj.put("type", "roomcontents");
					obj.put("roomid", "MainHall"); // Hashmap
					obj.put("identities", getMapList("MainHall")); 
					obj.put("owner", ""); 
					write(outputdata, obj.toString());
				} else{
					// requested room is other room
					JSONObject obj = new JSONObject();
					obj.put("type", "roomcontents");
					obj.put("roomid", reqRoomID); 
					// **** should be list of name ****
					obj.put("identities", getMapList(reqRoomID));
					obj.put("owner", getUserOnMap(reqRoomID,0));
					write(outputdata, obj.toString());
				}
			}
  	}
  	
  	// ******************** RoomChange ***********************
  	public static void RoomChange(OutputStreamWriter outputdata,
  			String type, String newRoomName, String oldRoomName) {
			JSONObject obj = new JSONObject();
			if(type.equalsIgnoreCase("firstjoin")){
				ArrayList<String> a = new ArrayList<String>(20);
				a.add(getGuest());
				if(clientFirst)
					MapListAddition("MainHall", a);
				else{
					GuestListInsertion("MainHall", getGuest());
				}
				clientFirst= false;

				obj.put("type", "roomchange");
				obj.put("identity", getGuest());
				obj.put("former", "");
				obj.put("roomid", "MainHall");
				
				write(outputdata, obj.toString());
			} else if(type.equalsIgnoreCase("roomInvalid")
					|| type.equalsIgnoreCase("roomNotExist")){
				
				obj.put("roomid", "");
				write(outputdata, obj.toString());
				
			} else if(type.equalsIgnoreCase("roomExist")){
				
				obj.put("type", "roomchange");
				obj.put("identity", joinPreID);
				obj.put("former", oldRoomName);
				obj.put("roomid", newRoomName);
				
				GuestListDeletion(oldRoomName, joinPreID);
				firstEnterRoom = 0;
				firstEnter = true;
				GuestListInsertion(newRoomName, joinPreID);
				
				write(outputdata, obj.toString());
				
			} else if(type.equalsIgnoreCase("quit")){
				obj.put("type", "roomchange");
				obj.put("identity", quitID);
				obj.put("former", oldRoomName);
				obj.put("roomid", "");
				
				clearGuest(oldRoomName, quitID);

				write(outputdata, obj.toString());
			}
	}
  	// ******************** newidentity ***********************
	// there are two ways to execute this sub-routine
	// One is when client first connect to server,
	// the other is when client request identity change
    public static void newidentity(OutputStreamWriter outputdata,
    		boolean firstConnected, String newName, String roomid) {
			if(firstConnected){
			
				JSONObject obj = new JSONObject();
				setGuest();
				reqGuest = getGuest();
				obj.put("type", "newidentity");
				obj.put("former", "");
				obj.put("identity", getGuest());
			
				write(outputdata, obj.toString());
				
			} else{
				GuestListSubstitution(roomid, getGuest(), newName);
				
				JSONObject obj = new JSONObject();
				obj.put("type", "newidentity");
				obj.put("former", getGuest());
				obj.put("identity", newName);
			
				write(outputdata, obj.toString());
			}
	}
    
    // ******************** Parse Message ***********************
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
 } // end thread
  
  // ******************** Check Character ***********************
  public static boolean isValid(String type, String option, 
		  int lowerbound, int upperbound, String roomid){
	  String pattern= "^[a-zA-Z0-9]*$";
	  
	  // identitychange or createroom
	  // option is new room name or new id
	  
	  if(option.isEmpty()) // invalid
		  return false;
	  else if(option.length() > upperbound && option.length() < lowerbound) // invalid
		  return false;
	  else if(type.equalsIgnoreCase("identitychange") && isUserOnMap(roomid, option))
		  return false;
	  else if(type.equalsIgnoreCase("createroom") && isKeyOnMap(option))
		  return false;
	  else if(type.equalsIgnoreCase("join") && !isKeyOnMap(option) ){
		  return false;
	  } else if(option.matches(pattern)) // alphanumeric
		  return true;
	  else{
		  System.out.println("Room name or new identity name is invalid");
		  return false;
	  }
  }

} // end Chatserver