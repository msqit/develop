package com.git.svn.bean;

public class User {
	
	private String userName;
	private String password;
	private String message;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return "User [userName=" + this.userName + ",password=" + this.password + ",message=" + this.message +"]";
	}
}
