package messenger.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
/** This class accesses the sqlite database in order to retrieve and insert message and user data
 * 
 * @author vpanagiot
 *
 */
public class MessengerDBInterface {
	public MessengerDBInterface() {
		// TODO Auto-generated constructor stub
			Connection c = null;
		    Statement stmt = null;
		    try {
		      Class.forName("org.sqlite.JDBC");
		      c = DriverManager.getConnection("jdbc:sqlite:messengerdb"); 
		      stmt=c.createStatement();
		      String createUsers="CREATE TABLE USERS(username TEXT PRIMARY KEY NOT NULL,ID INTEGER NOT NULL UNIQUE,"+
									"pass TEXT NOT NULL,online  INTEGER NOT NULL,sessiondata TEXT);";
		      String createUserContacts="CREATE TABLE USERCONTACTS("+
		    		  "user_ID int  not null,contact_ID int not null,relation int check(relation>0 and relation<3),PRIMARY KEY(user_ID,contact_ID));";
		      String createMessages="CREATE TABLE MESSAGES(sender_ID int not null,receiver_ID int not null,"+
		    		  "message TEXT not null,senddate TEXT not null,readstatus int check(readstatus>=0 and readstatus<2)); ";
		      String messageView="CREATE VIEW  MESSAGES_P AS "+
		    		  "SELECT usersender.username as sender,userreceiver.username as receiver,message,senddate,readstatus "+
		    		  "FROM MESSAGES "+
		    		  "JOIN USERS as usersender on usersender.ID=sender_ID "+
		    		  "JOIN USERS as userreceiver on userreceiver.ID=receiver_ID "+
		    		  "ORDER BY senddate;";
		      try{
		      if(stmt.execute(createUsers)) System.out.println("Users created succesfully");
		      }
		      catch(SQLException e){
		    	  System.out.println("Exeption on create Users");
		    	  e.printStackTrace();
		      }
		      try{
		      if(stmt.execute(createUserContacts)) System.out.println("User contacts created succesfully");
		      }
		      catch(SQLException e){
		    	  System.out.println("Exeption on create Users contacts");
		    	  e.printStackTrace();
		      }
		      try{
		      if(stmt.execute(createMessages)) System.out.println("Messages created succesfully");
		      }
		      catch(SQLException e){
		    	  System.out.println("Exeption on create messages");
		    	  e.printStackTrace();
		      }
		      try{
		      if(stmt.execute(messageView)) System.out.println("message view created succesfully");
		      }
		      catch(SQLException e){
		    	  System.out.println("Exeption on create messageView");
		    	  e.printStackTrace();
		      }
		      stmt.close();
		      c.close();
		    } catch ( Exception e ) {
		    	System.out.println("Exception occured at the constructor");
		    	e.printStackTrace();
		      System.exit(0);
		    }
		  
		}
	
	
	public boolean dbCreateUser(String username, String password,boolean online,String session_data){
		Connection c=null;
		PreparedStatement stmt=null;
		int id;
		ResultSet queryResult=null;
		/////////////////////////////////
		try {
			Class.forName("org.sqlite.JDBC");
			c=DriverManager.getConnection("jdbc:sqlite:messengerdb");
			//check if the username exists
			String usernameCheck="SELECT * FROM USERS WHERE username=?";
			stmt=c.prepareStatement(usernameCheck);
			stmt.setString(1, username);
			queryResult=stmt.executeQuery();
			if(queryResult.next()){
				System.out.println("username already exists");
				return false;
			}
			else{
				System.out.println("Username valid");
			}
			stmt.close();
			
			//find largest id
			stmt=c.prepareStatement("SELECT MAX(ID) from USERS");
			
			queryResult=stmt.executeQuery();
			if(queryResult.next()){
				id=queryResult.getInt(1)+1;
				System.out.println("max ID found and is:"+id);
			}
			else{
				id=0;
				System.out.println("nothing on table");
			}		
			stmt.close();
			
			//insert new user
			String newUserstmt="INSERT INTO USERS VALUES(?,?,?,?,?)";
			stmt=c.prepareStatement(newUserstmt);
			
			stmt.setString(1, username);
			stmt.setInt(2, id);
			stmt.setString(3, password);
			stmt.setInt(4, (online)?1:0);
			stmt.setString(5, session_data);
			stmt.execute();
		    stmt.close();
		    c.close();
		    
		    return(true);
		}
		catch(Exception e){
			System.out.println("Exception at user creation");
			e.printStackTrace();
			return(false);
		}
	}
	
	
	/** Sets the uses online status as well as the sessiondata*/
	public boolean dbSetUserStatus(String username,boolean online,String session_data){
		Connection c=null;
		PreparedStatement stmt=null;
		
		ResultSet queryResult=null;
		/////////////////////////////////
		try {
			Class.forName("org.sqlite.JDBC");
			c=DriverManager.getConnection("jdbc:sqlite:messengerdb");
			
			String changeUserState="UPDATE USERS SET online=? , sessiondata=? where username=?";
			stmt=c.prepareStatement(changeUserState);
			
			stmt.setString(3, username);
			stmt.setInt(1, (online)?1:0);
			stmt.setString(2, session_data);
			stmt.executeUpdate();
		    stmt.close();
		    c.close();
		    
		    return(true);
		}
		catch(Exception e){
			System.out.println("Exception at user state modification creation");
			e.printStackTrace();
			return(false);
		}
	}
	
	/**Gets user data as a User object*/
	public User dbGetUserData(String username){
		Connection c=null;
		PreparedStatement stmt=null;
		ResultSet queryResult=null;
		User curUser=null;
		/////////////////////////////////
		try {
			Class.forName("org.sqlite.JDBC");
			c=DriverManager.getConnection("jdbc:sqlite:messengerdb");
			//check if the username exists
			String usernameCheck="SELECT * FROM USERS WHERE username=?";
			stmt=c.prepareStatement(usernameCheck);
			stmt.setString(1, username);
			queryResult=stmt.executeQuery();
			if(!queryResult.next()){
				System.out.println("user doesn't exist");
				stmt.close();
				c.close();
				return null;
			}
			else{
				curUser=new User(queryResult.getString("username"),queryResult.getString("pass"));
				curUser.setOnline((queryResult.getInt("online")==1));
				curUser.setSession_data(queryResult.getString("sessiondata"));
				
			}
			stmt.close();
		    c.close();
		    return(curUser);
		    
		}
		catch(Exception e){
			System.out.println("Exception at user creation");
			e.printStackTrace();
			return(null);
		}
	}
	
	/** adds a contact as friend or blocked
	 * 
	 * @param username
	 * @param contact
	 * @param status 1 for friend, 2 for blocked
	 * @return
	 */	
	public boolean dbSetContact(String username,String contact,int status){
		Connection c=null;
		PreparedStatement stmt=null;
		try {
			Class.forName("org.sqlite.JDBC");
			c=DriverManager.getConnection("jdbc:sqlite:messengerdb");
			
			String contactSet="INSERT INTO USERCONTACTS VALUES((SELECT ID from USERS where username=?),(SELECT ID from USERS where username=?),?);";
			stmt=c.prepareStatement(contactSet);
			stmt.setString(1, username);
			stmt.setString(2, contact);
			stmt.setInt(3, status);
			stmt.execute();
				stmt.close();
			    c.close();
			    return true;    
		}
		catch(Exception e){
			System.out.println("Exception at contact creation");
			e.printStackTrace();
			return(false);
		}
	}
	/**returns contacts. For now returns a list with only friends and not blocked
	 * full return to be implemented later
	 * @param username
	 * @return
	 */
	public List<String> dbGetContacts(String username){
		Connection c=null;
		PreparedStatement stmt=null;
		ResultSet queryResult=null;
		List<String> friendList=new ArrayList<>();
		
		try {
			Class.forName("org.sqlite.JDBC");
			c=DriverManager.getConnection("jdbc:sqlite:messengerdb");
			
			String contactSet="SELECT contacts.username as contact,relation FROM USERCONTACTS "+
								"JOIN USERS as user on USERCONTACTS.user_ID=user.ID "+
								"JOIN USERS as contacts on USERCONTACTS.contact_ID=contacts.ID "+
								"WHERE user.username=?";
			stmt=c.prepareStatement(contactSet);
			stmt.setString(1, username);
			queryResult=stmt.executeQuery();
			while(queryResult.next()){
				if(queryResult.getInt("relation")==1){
					friendList.add(queryResult.getString("contact"));
				}
			}
			stmt.close();
			c.close();
			return friendList;    
		}
		catch(Exception e){
			System.out.println("Exception at getting contacts");
			e.printStackTrace();
			return(null);
		}
	}
	
	/** It writes a new message record in MESSAGES
	 * 
	 * @param sender
	 * @param receiver
	 * @param message
	 * @param sendDate
	 * @param readStatus
	 * @return
	 */
	public boolean dbNewMessage(String sender,String receiver,String message,String sendDate,boolean readStatus){
		Connection c=null;
		PreparedStatement stmt=null;
		
		
		try {
			Class.forName("org.sqlite.JDBC");
			c=DriverManager.getConnection("jdbc:sqlite:messengerdb");
			
			String newMessage="INSERT INTO MESSAGES VALUES((SELECT ID from USERS where username=?),"+
			"(SELECT ID from USERS where username=?),?,?,?);";
			stmt=c.prepareStatement(newMessage);
			stmt.setString(1, sender);
			stmt.setString(2, receiver);
			stmt.setString(3, message);
			stmt.setString(4, sendDate);
			stmt.setInt(5, ((readStatus)?1:0));
			stmt.execute();
			
			stmt.close();
			c.close();
			return true;    
		}
		catch(Exception e){
			System.out.println("Exception at new message");
			e.printStackTrace();
			return(false);
		}
	}
	
	/**It gets all messages that the user has sent or received 
	 * Setting to read needs to be implemented
	 */
	public List<Message> dbGetHistory(String user){
		Connection c=null;
		PreparedStatement stmt=null;
		ResultSet queryResult=null;
		List<Message> messages=new ArrayList<>();
		Message message;
		try {
			Class.forName("org.sqlite.JDBC");
			c=DriverManager.getConnection("jdbc:sqlite:messengerdb");
			
			String history="SELECT * FROM MESSAGES_P WHERE SENDER=? OR RECEIVER=?;";
			stmt=c.prepareStatement(history);
			stmt.setString(1, user);
			stmt.setString(2, user);
			
			queryResult=stmt.executeQuery();
			while(queryResult.next()){
				message=new Message(queryResult.getString(3),queryResult.getString(2),queryResult.getString(1),queryResult.getString(4),(queryResult.getInt(5)==1));
				messages.add(message);
			}
			
			stmt.close();
			c.close();
			return messages;    
		}
		catch(Exception e){
			System.out.println("Exception at retrieving history");
			e.printStackTrace();
			return(null);
		}
	}
	
	/**retrieves unread messages sent to the user
	 * Setting to read needs to be implemented
	 * @param user
	 * @return
	 */
	public List<Message> dbGetNewMessages(String user){
		Connection c=null;
		PreparedStatement stmt=null;
		ResultSet queryResult=null;
		List<Message> messages=new ArrayList<>();
		Message message;
		try {
			Class.forName("org.sqlite.JDBC");
			c=DriverManager.getConnection("jdbc:sqlite:messengerdb");
			
			String history="SELECT * FROM MESSAGES_P WHERE RECEIVER=? AND readstatus=0;";
			stmt=c.prepareStatement(history);
			stmt.setString(1, user);
			
			
			queryResult=stmt.executeQuery();
			while(queryResult.next()){
				message=new Message(queryResult.getString(3),queryResult.getString(2),queryResult.getString(1),queryResult.getString(4),(queryResult.getInt(5)==1));
				messages.add(message);
			}
			
			stmt.close();
			c.close();
			return messages;    
		}
		catch(Exception e){
			System.out.println("Exception at retrieving history");
			e.printStackTrace();
			return(null);
		}
	}
	/** only for testing purposes
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		MessengerDBInterface db=new MessengerDBInterface();
		/*
		if(db.dbCreateUser("Veronica", "Veronica", true, "Veronica 2017")) System.out.println("User Addition success!");
		if(db.dbCreateUser("Sarah", "Sarah", true, "Sarah 2017")) System.out.println("User Addition success!");
		if(db.dbCreateUser("Jane", "Jane", true, "Jane 2017")) System.out.println("User Addition success!");
		if(db.dbCreateUser("Betty", "Betty", true, "Betty 2017")) System.out.println("User Addition success!");
		if(db.dbCreateUser("Michael", "Michael", true, "Michael 2017")) System.out.println("User Addition success!");
		db.dbSetUserStatus("Vasilis",true,"Got online again");
		System.out.println(db.dbGetUserData("Vasilis").toString());
		//System.out.println(db.dbGetUserData("adsff").toString());*/
		/*
		if(db.dbSetContact("Vasilis", "Sarah", 1)) System.out.println("succesful contact creation");
		if(db.dbSetContact("George", "Veronica", 1)) System.out.println("succesful contact creation");
		if(db.dbSetContact("George", "Michael", 1)) System.out.println("succesful contact creation");
		if(db.dbSetContact("Vasilis", "Veronica", 1)) System.out.println("succesful contact creation");
		if(db.dbSetContact("Vasilis", "Michael", 1)) System.out.println("succesful contact creation");
		if(db.dbSetContact("Veronica", "Vasilis", 1)) System.out.println("succesful contact creation");
		if(db.dbSetContact("Veronica", "Michael", 1)) System.out.println("succesful contact creation");*/
		System.out.println(db.dbGetContacts("Vasilis").toString());
		/*db.dbNewMessage("Michael", "Vasilis", "Hey how are you?", "2017", true);
		db.dbNewMessage("Vasilis", "Michael", "All fine", "2017", true);
		db.dbNewMessage("Michael", "Vasilis", "Nice", "2017", false);
		db.dbNewMessage("Vasilis", "Michael", "Yup", "2017", false);*/
		System.out.println(db.dbGetHistory("Vasilis").toString());
		System.out.println("And the undelivered messages:");
		System.out.println(db.dbGetNewMessages("Vasilis").toString());
	
	}

}
