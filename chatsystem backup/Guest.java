package chatsystem;

import java.util.ArrayList;

public class Guest {
	private int id;
	private String name;
	ArrayList<String> guestList = new ArrayList<String>(20);
	
	// Constructor
	public Guest(){}
	
	public Guest(ArrayList<String> myList) {
		super();
		this.guestList = myList;
	}
	
	public void addGuest(String s){
		guestList.add(s);
	}
	
	public void printGuest(String myRoom){
		for(String entry : guestList){
			System.out.println("Room:" + myRoom + " has Guest List: " + entry);
		}
	}
	
	public Guest(String myName) {
		super();
		this.name = myName;
	}
	
	public Guest(int myID, String myName) {
		super();
		this.id = myID;
		this.name = myName;
	}

	// setter and getter
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
