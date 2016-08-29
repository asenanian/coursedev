package com.mygdx.server;

public class Client {
	
	private String id;
	private String userName;
	private boolean hasName = false;
	private String password;
	
	public Client(String id){
		this.id = id;
	}
	
	public void setUserName(String name){
		this.userName = name;
		this.hasName = true;
	}
	
	public String getID(){
		return id;
	}
	
	public void setPassword(String pass){
		this.password = pass;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public boolean hasName(){
		return hasName;
	}
	
}
