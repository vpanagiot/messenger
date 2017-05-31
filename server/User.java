package messenger.server;

import java.util.List;

public class User {
	private String username;
	private String password;
	private String session_data;
	private List<String> friendList;
	private boolean online;
	
	public User(String username,String password){
		this.username=username;
		this.password=password;
	}
	
	public void setFriendList(List<String> friendList){
		this.friendList=friendList;
	}
	
	public void setSession_data(String session_data){
		this.session_data=session_data;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public String getSession_data() {
		return session_data;
	}

	public List<String> getFriendList() {
		return friendList;
	}
	
	public String toString(){
		String data="Username    : "+username+ "\n"+
					"Password    : "+password+ "\n"+
					"Session data: "+((session_data==null)?"No data":session_data)+ "\n"+
					"Status      : "+((online)?"Online":"Offline")+"\n"+
					"Friend list :  \n"+
					"             "+((friendList==null)?"No friend List":friendList.toString());
		return data;
	}
}
