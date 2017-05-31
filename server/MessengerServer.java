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
	private static SimpleDateFormat ft =  new SimpleDateFormat ("yyyy.MM.dd,hh:mm:ss");
	
	
	public MessengerServer() {
		// TODO Auto-generated constructor stub
	}
	/** sendMessage handles an incoming send api request
	 * 
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
				if(dbObject.dbNewMessage(sender,receiver,(String) postMap.get("message"),ft.format(new Date()),false)){
					auxMap.put("state", "200");
				}
				else{
					auxMap.put("state", "401");
				}
				
				return(new JSONObject(auxMap).toString());
			}
			else{
				auxMap.put("state", "401");
				return(new JSONObject(auxMap).toString());
			}
		}
		else{
			auxMap.put("state", "401");
			return(new JSONObject(auxMap).toString());
		}		
	}
	
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//MyHttpServer myserver=new MyHttpServer(8080);
		MyHttpServer.addHandler("/messenger/api/login", "POST", ((String uri,Map<String,List<String>> getMap,Map<String,Object> postMap)->login(postMap)));
		MyHttpServer.addHandler("/messenger/api/getfriends", "POST", (String uri,Map<String,List<String>> getMap,Map<String,Object> postMap)->friendlist(postMap));
		MyHttpServer.addHandler("/messenger/api/newmessage", "POST", (String uri,Map<String,List<String>> getMap,Map<String,Object> postMap)->sendMessage(postMap));
		MyHttpServer.addHandler("/messenger/api/getmessages", "POST", (String uri,Map<String,List<String>> getMap,Map<String,Object> postMap)->getMessages(postMap));
		MyHttpServer.addHandler("/messenger/api/history", "POST", (String uri,Map<String,List<String>> getMap,Map<String,Object> postMap)->history(postMap));
		MessengerServer curServer=new MessengerServer();
		curServer.dbObject=new MessengerDBInterface();
		
		
		dummyData();
		
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
				auxUser.setFriendList(dbObject.dbGetContacts(auxUser.getUsername()));
				dbObject.dbSetUserStatus(auxUser.getUsername(), true, auxUser.getSession_data());
				return(new JSONObject(auxMap).toString());
			}
			else{
				auxMap.put("state", "401");
				return(new JSONObject(auxMap).toString());
			}
		}
		else{
			auxMap.put("state", "401");
			return(new JSONObject(auxMap).toString());
		}		
		
	}
	
	
	public static String friendlist(Map<String,Object> postMap){
		Map auxMap=new HashMap<>();
		Map auxMap2=new HashMap();

		if(userMap.containsKey((String)postMap.get("username"))){
			User auxUser=userMap.get(postMap.get("username"));
			if(auxUser.getSession_data().equals(postMap.get("session_data"))){
				for(String i:auxUser.getFriendList()){
					auxMap2.put(i, userMap.get(i).isOnline());
				}

				auxMap.put("friendlist", auxMap2);
				auxMap.put("state", "200");


				return(new JSONObject(auxMap).toString());
			}
			else{
				auxMap.put("state", "401");
				return(new JSONObject(auxMap).toString());
			}
		}
		else{
			auxMap.put("state", "401");
			return(new JSONObject(auxMap).toString());
		}		

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
					}
					else{
						auxMap2=null;
						auxMap.put("history", auxMap2);
						auxMap.put("state", "404");
					}
				}
				else{
					auxMap2=null;
					auxMap.put("history", auxMap2);
					auxMap.put("state", "404");
				}

				

			}
			else{

				auxMap.put("history", null);
				auxMap.put("state", "404");
			}
			System.out.println(auxMap.toString());
			return(new JSONObject(auxMap).toString());
		}
		else{
			auxMap.put("state", "401");
			return(new JSONObject(auxMap).toString());
		}


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
			else{
				auxMap.put("state", "401");
				return(new JSONObject(auxMap).toString());
			}
		}
		else{
			auxMap.put("state", "401");
			return(new JSONObject(auxMap).toString());
		}		

	}
	
	private static void dummyData(){
		String[] users={"Vasilis","Michael","Veronica","Sarah", "Helen","George","Jenny","Jimmy"};
		User curUser;
		List<String> friends;
		for(String i:users){
			curUser=new User(i,i);
			userMap.put(i, curUser);
			friends=new ArrayList<>();
			for(String k:users){
				if(!k.equals(i)){
					friends.add(k);
				}
			}
			curUser.setFriendList(friends);
		}
		Map<String,List<Message>> aux1;
		List<Message> dummyList;
		Message dummyMessage;
		for(String i:users){

			if(messageMap.containsKey(i)){
				aux1=messageMap.get(i);

			}
			else{
				aux1=new HashMap<>();
				messageMap.put(i, aux1);
			}

			for(String k:userMap.get(i).getFriendList()){

				if(!messageMap.get(i).containsKey(k)){

					dummyList=new ArrayList<>();

					dummyMessage=new Message("I'm fine. You?",i,k);
					dummyList.add(dummyMessage);
					dummyMessage=new Message("Quite fine",k,i);
					dummyList.add(dummyMessage);
					dummyMessage=new Message("So what about today?",i,k);
					dummyList.add(dummyMessage);


					dummyList=new ArrayList<>();
					dummyMessage=new Message("Are you in the mood for movie tonight?",k,i);
					dummyList.add(dummyMessage);
					dummyMessage=new Message("Yes why not",i,k);
					dummyList.add(dummyMessage);
					dummyMessage=new Message("Nice, what would you like?",k,i);
					dummyList.add(dummyMessage);
					dummyMessage=new Message("A comedy perhaps",i,k);
					dummyList.add(dummyMessage);


					aux1.put(k, dummyList);


				}
				else{
					aux1.put(k, messageMap.get(k).get(i));
				}

			}
		}

		for(Map.Entry i:messageMap.entrySet()){
			for(Map.Entry k:messageMap.get(i.getKey()).entrySet()){
				for(Message j:messageMap.get(i.getKey()).get(k.getKey())){
					j.setReadState();
				}
			}
		}

	}

}
