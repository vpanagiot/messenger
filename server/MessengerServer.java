package messenger.server;

import java.text.SimpleDateFormat;
import java.util.*;
import org.json.JSONObject;
import org.nanohttpd.util.ServerRunner;

import messenger.client.ClientMessage;
public class MessengerServer {
	
	private static Map<String,Map<String,List<Message>>> messageMap=new HashMap<>();
	private static Map<String,User> userMap=new HashMap<>();
	private static MessengerDBInterface  dbObject;
	private static SimpleDateFormat ft =  new SimpleDateFormat ("yyyy.MM.dd,HH:mm:ss");
	private static int timeout=5000;
	private static Timer timeoutTimer;
	
	
	public MessengerServer() {
		// TODO Auto-generated constructor stub
	}
	/** sendMessage handles an incoming send api request
	 * TODO check if receiver is friend else fail
	 * @param postData
	 * @return 200 if success 401 if fail
	 */
	public static String sendMessage(Map<String,Object> postMap){
		Map auxMap=new HashMap<>();

		if(userMap.containsKey((String)postMap.get("username"))){
			User auxUser=userMap.get(postMap.get("username"));
			String sender=(String)postMap.get("username");
			String receiver=(String)postMap.get("receiver");
			if(auxUser.getSession_data().equals(postMap.get("session_data"))){
				if((new Date()).getTime()-auxUser.getDate().getTime()<timeout){
					auxUser.updateDate();
					if(dbObject.dbNewMessage(sender,receiver,(String) postMap.get("message"),ft.format(new Date()),false)){
						auxMap.put("state", "200");
						return(new JSONObject(auxMap).toString());
					}
				}
			}
			
		}
		auxMap.put("state", "401");
		return(new JSONObject(auxMap).toString());

	}
	
	class TimeoutChecker extends TimerTask{
		public void run(){
			System.out.println("timer!!!");
			try{
				synchronized(userMap){
					if(!userMap.isEmpty()){
						User auxUser;
						for(Map.Entry<String, User> i:userMap.entrySet()){
							if((auxUser=i.getValue())!=null){
								if(auxUser.getDate()!=null){
									if((new Date()).getTime()-auxUser.getDate().getTime()>timeout){
										auxUser.setSession_data("");
										dbObject.dbSetUserStatus(auxUser.getUsername(), false, "");
										auxUser.setOnline(false);
										

									}
								}
								else{
									if(auxUser.isOnline()){
										auxUser.setSession_data("");
										dbObject.dbSetUserStatus(auxUser.getUsername(), false, "");
										auxUser.setOnline(false);
										
									}
								}
							}
						}
					
					}else{
						//a function to set all users offline
					}}
			}
			catch(Exception e){
				System.out.println("Exception at timer");
				e.printStackTrace();
			}
		}
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//MyHttpServer myserver=new MyHttpServer(8080);
		MyHttpServer.addHandler("/messenger/api/login", "POST", ((String uri,Map<String,List<String>> getMap,Map<String,Object> postMap)->login(postMap)));
		MyHttpServer.addHandler("/messenger/api/logout", "POST", ((String uri,Map<String,List<String>> getMap,Map<String,Object> postMap)->logout(postMap)));
		MyHttpServer.addHandler("/messenger/api/getfriends", "POST", (String uri,Map<String,List<String>> getMap,Map<String,Object> postMap)->friendlist(postMap));
		MyHttpServer.addHandler("/messenger/api/newmessage", "POST", (String uri,Map<String,List<String>> getMap,Map<String,Object> postMap)->sendMessage(postMap));
		MyHttpServer.addHandler("/messenger/api/getmessages", "POST", (String uri,Map<String,List<String>> getMap,Map<String,Object> postMap)->getMessages(postMap));
		MyHttpServer.addHandler("/messenger/api/history", "POST", (String uri,Map<String,List<String>> getMap,Map<String,Object> postMap)->history(postMap));
		MyHttpServer.addHandler("/messenger/api/searchcontact", "POST", (String uri,Map<String,List<String>> getMap,Map<String,Object> postMap)->searchContact(postMap));
		MyHttpServer.addHandler("/messenger/api/requestcontact", "POST", (String uri,Map<String,List<String>> getMap,Map<String,Object> postMap)->requestContact(postMap));
		MyHttpServer.addHandler("/messenger/api/acceptcontact", "POST", (String uri,Map<String,List<String>> getMap,Map<String,Object> postMap)->acceptContact(postMap));
		MyHttpServer.addHandler("/messenger/api/blockcontact", "POST", (String uri,Map<String,List<String>> getMap,Map<String,Object> postMap)->blockContact(postMap));
		MessengerServer curServer=new MessengerServer();
		curServer.dbObject=new MessengerDBInterface();
		
		
		int delay = 5000; //milliseconds
		TimeoutChecker timeoutChecker=curServer.new TimeoutChecker();
		  timeoutTimer=new Timer();
		timeoutTimer.schedule(timeoutChecker,5000,5000);
		
		
		
		MyHttpServer.startMyServer();
		
		
	}
	
	public static String login(Map<String,Object> postMap){
		Map auxMap=new HashMap<>();
		User auxUser=null;
		if((auxUser=dbObject.dbGetUserData((String)postMap.get("username")))!=null){
			userMap.put((String)postMap.get("username"),auxUser);
			if(auxUser.getPassword().equals(postMap.get("password"))){

				auxMap.put("state", "200");
				auxMap.put("session_data",auxUser.getUsername()+" "+new Date().toString());
				auxUser.setSession_data(auxMap.get("session_data").toString());
				auxUser.setOnline(true);
				auxUser.updateDate();
				dbObject.dbSetUserStatus(auxUser.getUsername(), true, auxUser.getSession_data());
				return(new JSONObject(auxMap).toString());
			}
		}
		auxMap.put("state", "401");
		return(new JSONObject(auxMap).toString());
	}
	/** checks if the caller and his credentials are legitimate and updates last access time
	 * 
	 * @param postMap
	 * @return the User object of the caller if valid call or null if invalid
	 */
	public static User checkCredentials(Map<String,Object> postMap){
		if(userMap.containsKey((String)postMap.get("username"))){
			User auxUser=userMap.get(postMap.get("username"));
			if(auxUser.getSession_data().equals(postMap.get("session_data"))){
				if(((new Date()).getTime()-auxUser.getDate().getTime())<timeout){
					auxUser.updateDate();
					return auxUser;
				}
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param postMap
	 * @return
	 */
	public static String logout(Map<String,Object> postMap){
		Map auxMap=new HashMap<>();
		User auxUser=null;
		if((auxUser=checkCredentials(postMap))!=null){
				auxMap.put("state", "200");
				auxUser.setSession_data("");
				dbObject.dbSetUserStatus(auxUser.getUsername(), false, "");
				auxUser.setOnline(false);
				System.out.println(auxUser.getUsername()+" has loged out!");
				return(new JSONObject(auxMap).toString());
			
		}
		auxMap.put("state", "401");
		return(new JSONObject(auxMap).toString());
	}
	
	
	public static String friendlist(Map<String,Object> postMap){
		Map auxMap=new HashMap<>();
		Map auxMap2=new HashMap();
		List contactList;
		User auxUser2=null;
		User requester;
		if((requester=checkCredentials(postMap))!=null){
			contactList=dbObject.dbGetContacts(requester.getUsername());
					
					auxMap.put("friendlist", contactList);
					auxMap.put("state", "200");
					System.out.println(auxMap2.toString());
					return(new JSONObject(auxMap).toString());
		}
		auxMap.put("state", "401");
		return(new JSONObject(auxMap).toString());
	}
	
	
	/** api call for searching the users for usernames like the one in the postMap
	 * 
	 * @param postMap
	 * @return
	 */
	public static String searchContact(Map<String,Object> postMap){
		
		Map auxMap=new HashMap();
		if((checkCredentials(postMap))!=null){
			try{
				String contact=(String)postMap.get("contact");
				List results=dbObject.dbSearchUsers(contact);
				auxMap.put("state", "200");
				auxMap.put("contact", contact);
				auxMap.put("userlist", results);
				return(new JSONObject(auxMap).toString());

			}
			catch(Exception e){
				System.out.println("Exception at searchContact");
				e.printStackTrace();

			}
		}	
		auxMap.put("state", "401");
		return(new JSONObject(auxMap).toString());
	}
	
	/** make a friend request
	 * 
	 * @param postMap
	 * @return
	 */
	public static String requestContact(Map<String,Object> postMap){
		User requester;
		Map auxMap=new HashMap();
		if((requester=checkCredentials(postMap))!=null){
			try{
				String contact=(String)postMap.get("contact");
				dbObject.dbSetContact(requester.getUsername(), contact, 1);
				auxMap.put("state", "200");
				auxMap.put("contact", contact);
				return(new JSONObject(auxMap).toString());

			}
			catch(Exception e){
				System.out.println("Exception at searchContact");
				e.printStackTrace();

			}
		}	
		auxMap.put("state", "401");
		return(new JSONObject(auxMap).toString());
	}
	
	/**accept a friend Request
	 * 
	 * @param postMap
	 * @return
	 */
	public static String acceptContact(Map<String,Object> postMap){
		User requester;
		Map auxMap=new HashMap();
		if((requester=checkCredentials(postMap))!=null){
			try{
				String contact=(String)postMap.get("contact");
				dbObject.dbSetContact(requester.getUsername(), contact, 1);
				auxMap.put("state", "200");
				auxMap.put("contact", contact);
				return(new JSONObject(auxMap).toString());

			}
			catch(Exception e){
				System.out.println("Exception at searchContact");
				e.printStackTrace();

			}
		}	
		auxMap.put("state", "401");
		return(new JSONObject(auxMap).toString());
	}
	
	/** block a contact
	 * 
	 * @param postMap
	 * @return
	 */
	public static String blockContact(Map<String,Object> postMap){
		User requester;
		Map auxMap=new HashMap();
		if((requester=checkCredentials(postMap))!=null){
			try{
				String contact=(String)postMap.get("contact");
				dbObject.dbSetContact(requester.getUsername(), contact, 2);
				auxMap.put("state", "200");
				auxMap.put("contact", contact);
				return(new JSONObject(auxMap).toString());

			}
			catch(Exception e){
				System.out.println("Exception at searchContact");
				e.printStackTrace();

			}
		}	
		auxMap.put("state", "401");
		return(new JSONObject(auxMap).toString());
	}
	
	/** loads the messages that the user has communicated with other users
	 * TODO check the read state for incoming
	 * @param postMap
	 * @return
	 */
	public static String history(Map<String,Object> postMap){
		Map auxMap=new HashMap<>();
		Map auxMap2=new HashMap();
		List<Message> historyList=null;
		if(userMap.containsKey((String)postMap.get("username"))){
			User auxUser=userMap.get(postMap.get("username"));
			String username=auxUser.getUsername();
			if(auxUser.getSession_data().equals(postMap.get("session_data"))){
				if((new Date()).getTime()-auxUser.getDate().getTime()<timeout){
					auxUser.updateDate();
					historyList=dbObject.dbGetHistory(username);
					if(historyList!=null){
						if(!historyList.isEmpty()){
							for(Message i:historyList){
								if(i.getSender().equals(username)){
									if(auxMap2.containsKey(i.getReceiver())){
										ClientMessage cMessage=new ClientMessage(i.getMessage(),true,i.getDate(),i.getReadState());
										((List)auxMap2.get(i.getReceiver())).add(cMessage);
									}
									else{
										auxMap2.put(i.getReceiver(), new ArrayList<>());
										ClientMessage cMessage=new ClientMessage(i.getMessage(),true,i.getDate(),i.getReadState());
										((List)auxMap2.get(i.getReceiver())).add(cMessage);
									}
								}
								else{
									if(auxMap2.containsKey(i.getSender())){
										ClientMessage cMessage=new ClientMessage(i.getMessage(),false,i.getDate(),true);
										((List)auxMap2.get(i.getSender())).add(cMessage);
									}
									else{
										auxMap2.put(i.getSender(), new ArrayList<>());
										ClientMessage cMessage=new ClientMessage(i.getMessage(),false,i.getDate(),true);
										((List)auxMap2.get(i.getSender())).add(cMessage);
									}
								}
							}
							auxMap.put("history", auxMap2);
							auxMap.put("state", "200");
							return(new JSONObject(auxMap).toString());
						}

					}					
					auxMap2=null;
					auxMap.put("history", auxMap2);
					auxMap.put("state", "404");
				}
			

			}	
		}
		auxMap.put("state", "401");
		return(new JSONObject(auxMap).toString());
	}

	/**getMessages handles the getmessages api call that asks for new incoming messages*/
	public static String getMessages(Map<String,Object> postMap){
		Map auxMap=new HashMap<>();
		Map auxMap2=new HashMap();
		List<Message> newMessagesList=null;
		if(userMap.containsKey((String)postMap.get("username"))){
			User auxUser=userMap.get(postMap.get("username"));
			String username=auxUser.getUsername();
			if(auxUser.getSession_data().equals(postMap.get("session_data"))){
				if((new Date()).getTime()-auxUser.getDate().getTime()<timeout){
					auxUser.updateDate();
					newMessagesList=dbObject.dbGetNewMessages(username);

					if(newMessagesList!=null){
						if(!newMessagesList.isEmpty()){
							for(Message i:newMessagesList){					
								if(auxMap2.containsKey(i.getSender())){
									ClientMessage cMessage=new ClientMessage(i.getMessage(),false,i.getDate(),true);
									((List)auxMap2.get(i.getSender())).add(cMessage);
								}
								else{
									auxMap2.put(i.getSender(), new ArrayList<>());
									ClientMessage cMessage=new ClientMessage(i.getMessage(),false,i.getDate(),true);
									((List)auxMap2.get(i.getSender())).add(cMessage);
								}
							}
							auxMap.put("newmessages", auxMap2);
							auxMap.put("state", "200");

						}
						else{

							auxMap.put("newmessages", null);
							auxMap.put("state", "404");
						}
					}
					else{
						auxMap.put("newmessages", null);
						auxMap.put("state", "404");
					}
					return(new JSONObject(auxMap).toString());
				}
		
			}			
		}
		auxMap.put("state", "401");
		return(new JSONObject(auxMap).toString());
	}
	

}
