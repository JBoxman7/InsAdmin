package com.atlas.iac;

public class User {
	private int userID; // Unique ID to server
	private String name; // Name of the user
	private String uniqueID; // Unique ID to user's account
	private String connected; // Duration user has been connected to the server
	private short ping; // The user's ping
	private int loss; // Packet loss
	private String state; // Connection state to the user
	private int rate; // Tick rate of the user
	private String address; // IP address of the connected user
	
	public User() {}
	
	public User(int userID, String name, String uniqueID, String connected, short ping, int loss, String state, int rate, String address) {
		this.userID = userID;
		this.name = name;
		this.uniqueID = uniqueID;
		this.connected = connected;
		this.ping = ping;
		this.loss = loss;
		this.state = state;
		this.rate = rate;
		this.address = address;
	}
	
	public int getUserID() {
		return userID;
	}
	
	public void setUserID(int userID) {
		this.userID = userID;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUniqueID() {
		return uniqueID;
	}
	
	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}
	
	public String getConnected() {
		return connected;
	}
	
	public void setConnected(String connected) {
		this.connected = connected;
	}
	
	public short getPing() {
		return ping;
	}
	
	public void setPing(short ping) {
		this.ping = ping;
	}
	
	public int getLoss() {
		return loss;
	}
	
	public void setLoss(int loss) {
		this.loss = loss;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public int getRate() {
		return rate;
	}
	
	public void setRate(int rate) {
		this.rate = rate;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	@Override
	public String toString() {
		return String.format("%s <%s>:\n" +
		                     "\tUserID: %s\n" +
		                     "\tName: %s\n" +
		                     "\tUniqueID: %s\n" +
		                     "\tConnected: %s\n" +
		                     "\tPing: %s\n" +
		                     "\tLoss: %s\n" +
		                     "\tState: %s\n" +
		                     "\tRate: %s\n" +
		                     "\tAddress: %s",
		                     name, userID, userID, name, uniqueID, connected, ping, loss, state, rate, address);
	}
}
