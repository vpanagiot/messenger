package messenger.server;
import java.util.*;
import org.json.JSONObject;
import org.nanohttpd.util.ServerRunner;

import messenger.client.ClientMessage;
public class MessengerServer {
	
	private static Map<String,Map<String,List<Message>>> messageMap=new HashMap<>();
	private static Map<String,User> userMap=new HashMap<>();
	
	
	public MessengerServer() {
		// TODO Auto-generated constructor stub
	}
	/** sendMessage handles an incoming send api request
	 * 
	 * @param postData
	 * @return 200 if success null if fail
	 */
	public static String sendMessage(Map<String,Object> postMap){
		Map auxMap=new HashMap<>();
		
		if(userMap.containsKey((String)postMap.get("username"))){
			User auxUser=userMap.get(postMap.get("username"));
			String sender=(String)postMap.get("username");
			String receiver=(String)postMap.get("receiver");
			if(auxUser.getSession_data().equals(postMap.get("session_data"))){
				if(!userMap.containsKey(receiver)){
					auxMap.put("state", "401");
					return(new JSONObject(auxMap).toString());
				}
				else{
				if(!messageMap.containsKey(sender)){
					messageMap.put(sender, new HashMap<String,List<Message>>());
				}
				if(!messageMap.get(sender).containsKey(receiver)){
					messageMap.get(sender).put(receiver, new ArrayList<Message>());
				}
				if(!messageMap.containsKey(receiver)){
					messageMap.put(receiver, new HashMap<String,List<Message>>());
				}
				if(!messageMap.get(receiver).containsKey(sender)){
					messageMap.get(receiver).put(sender, new ArrayList<Message>());
				}
				Message message=new Message((String)postMap.get("message"),receiver,sender);
				messageMap.get(sender).get(receiver).add(message);
				messageMap.get(receiver).get(sender).add(message);
				auxMap.put("state", "200");
				
				return(new JSONObject(auxMap).toString());
				}
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
		String user1="Vasilis1";
		String user2="Vasilis2";
		
		dummyData();
		
		MyHttpServer.startMyServer();
	}
	
	public static String login(Map<String,Object> postMap){
		Map auxMap=new HashMap<>();
		
		if(userMap.containsKey((String)postMap.get("username"))){
			User auxUser=userMap.get(postMap.get("username"));
			if(auxUser.getPassword().equals(postMap.get("password"))){
				
				auxMap.put("state", "200");
				auxMap.put("session_data",auxUser.getUsername()+" "+new Date().toString());
				auxUser.setSession_data(auxMap.get("session_data").toString());
				auxUser.setOnline(true);
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
	
	public static String history(Map<String,Object> postMap){
		Map auxMap=new HashMap<>();
		Map auxMap2=new HashMap();
		
		if(userMap.containsKey((String)postMap.get("username"))){
			User auxUser=userMap.get(postMap.get("username"));
			if(auxUser.getSession_data().equals(postMap.get("session_data"))){
				if(messageMap.containsKey(auxUser.getUsername())){
					String client=auxUser.getUsername();
					for(Map.Entry intro:messageMap.get(client).entrySet()){
						List auxList=new ArrayList();
						auxMap2.put(intro.getKey(), auxList);
						for (Object i:(List)intro.getValue()){
							ClientMessage cMessage=new ClientMessage(((Message)i).getMessage(),((Message)i).getSender().equals(client),((Message)i).getDate(),((Message)i).getReadState());
							auxList.add(cMessage);
							if(((Message)i).getReceiver().equals(auxUser.getUsername())){
								((Message)i).setReadState();
							}
						}
					}
					
					auxMap.put("history", auxMap2);
					auxMap.put("state", "200");
					
				}
				else{
					
					auxMap.put("history", null);
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
	
	/**getMessages handles the getmessages api call that asks for new incoming messages*/
	public static String getMessages(Map<String,Object> postMap){
		Map auxMap=new HashMap<>();
		Map auxMap2=new HashMap();
		
		if(userMap.containsKey((String)postMap.get("username"))){
			User auxUser=userMap.get(postMap.get("username"));
			if(auxUser.getSession_data().equals(postMap.get("session_data"))){
				if(messageMap.containsKey(auxUser.getUsername())){
					String client=auxUser.getUsername();
					for(Map.Entry intro:messageMap.get(client).entrySet()){
						List auxList=new ArrayList();
						auxMap2.put(intro.getKey(), auxList);
						for (Object i:(List)intro.getValue()){
							if(!((Message)i).getReadState()){
								if(((Message)i).getReceiver().equals(client)){
								Map message=new HashMap();
								message.put("message", ((Message)i).getMessage());
								message.put("date", ((Message)i).getDate());
								auxList.add(message);
								((Message)i).setReadState();
								}
							}
							
						}
					}
					System.out.println(auxMap2.toString());
					auxMap.put("newmessages", auxMap2);
					auxMap.put("state", "200");
					
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
