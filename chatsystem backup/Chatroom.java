package chatsystem;

import java.util.HashMap;

public class Chatroom {
	private int id;
	private String ownerName; // owner name
	HashMap<Integer,Guest> guestList;
	
	public Chatroom(String owner) {
		super();
		this.ownerName = owner;
	}
	
	public Chatroom(int id, String name) {
		super();
		this.id = id;
		this.ownerName = name;
	}
	
	public Chatroom(int id, String name, HashMap<Integer,Guest> tempRoom) {
		super();
		this.id = id;
		this.ownerName = name;
		this.guestList = new HashMap<Integer,Guest>(10);
	}
	
	public void putValue(int key, Guest value){
		guestList.put(key, value);
	}
	
	public HashMap<Integer, Guest> getTempRoom() {
		return guestList;
	}

	public void setTempRoom(HashMap<Integer, Guest> tempRoom) {
		this.guestList = tempRoom;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String name) {
		this.ownerName = name;
	}	

}
/*
printMap(MapList);
printArrayList(guestList);
System.out.println("========");

// ArrayList => Hashmap
ArrayListAddition(guestList);
ArrayListAddition(guestList);
ArrayListAddition(guestList);
ArrayListAddition(guestList);

MapListAddition("roomA", guestList);

ArrayListAddition(tempList);
ArrayListAddition(tempList);

MapListAddition("roomB", tempList);

printMap(MapList);
printArrayList(guestList);
System.out.println("========");

// Hashmap => ArrayList
GuestListSubstitution("roomA", "guest2", "guest5");
GuestListDeletion("roomA","guest1");
GuestListInsertion("roomA","guest6");

printMap(MapList);
printArrayList(guestList);
System.out.println("========");

//MapListDeletion("roomA",guestList);
MapListKeySub("roomA", "roomC", guestList);

printMap(MapList);
printArrayList(guestList);*/