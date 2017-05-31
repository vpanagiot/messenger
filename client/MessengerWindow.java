package messenger.client;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.FlowLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ListDataListener;
import javax.swing.JTextArea;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JLayeredPane;
import javax.swing.JDesktopPane;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.border.EtchedBorder;
import java.awt.Insets;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MessengerWindow extends JFrame {
	static final long serialVersionUID=1;
	private Map<String,List<ClientMessage>> messageMap;
	private Map<String,List<ClientMessage>> unreadMap=new HashMap<>();
	private Map<String,String> loginData=new HashMap<>();
	private Map<String,Boolean> friendMap=new HashMap();
	private Map<String,Boolean> orderedFriendMap=new LinkedHashMap<>();
	private DefaultListModel<String> friends;
	private List<String> friendOrderedList=new ArrayList<>();
	private DefaultListModel<String> messages;
	private String curFriend="";
	private JList<String> friendList,list_1;
	private Timer newTimer;
	private MessengerWindow mainWindow;
	private boolean toggle=false;
	private int friendPreviousSelectionFirst=0;
	private int friendPreviousSelectionLast=0;
	

	public MessengerWindow() throws HeadlessException {
		super();
		mainWindow=this;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setSize(new Dimension(0, 20));
		menuBar.setMinimumSize(new Dimension(10, 20));
		menuBar.setMaximumSize(new Dimension(32000, 20));
		menuBar.setPreferredSize(new Dimension(32000, 20));
		getContentPane().add(menuBar);
		
		JMenu mnNewMenu = new JMenu("New menu");
		mnNewMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmLogin = new JMenuItem("Login");
		mntmLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LoginDialog authDialog=new LoginDialog(true,loginData,mainWindow);
				authDialog.setVisible(true);
			}
		});
		mnNewMenu.add(mntmLogin);
		
		JMenuItem mntmSignup = new JMenuItem("Signup");
		mntmSignup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LoginDialog authDialog=new LoginDialog(false,loginData,mainWindow);
				authDialog.setVisible(true);
			}
		});
		mnNewMenu.add(mntmSignup);
		
		JPanel panel = new JPanel();
		panel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		getContentPane().add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(200, 3));
		scrollPane.setMinimumSize(new Dimension(100, 22));
		scrollPane.setMaximumSize(new Dimension(200, 32767));
		scrollPane.setBackground(Color.WHITE);
		scrollPane.setForeground(Color.WHITE);
		
		scrollPane.setAutoscrolls(true);
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(scrollPane);
		
		friendList = new JList<>();
		friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		friendList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				int selection=0;
				try{
					if(friendOrderedList.size()>arg0.getLastIndex()){
						
						try{
						
						JList fList=(JList)arg0.getSource();
						
						 //lsm=((JList)arg0.getSource()).getSelectionModel();
						
						if(fList.isSelectionEmpty()){
							if(curFriend.isEmpty()){
								fList.setSelectionInterval(0, 0);
							curFriend=friendOrderedList.get(0);
							populateMessagesList();
							}
							else{
								if(friendOrderedList.contains("curFriend")){
									selection=friendOrderedList.indexOf("curFriend");
									fList.setSelectionInterval(selection,selection);
									populateMessagesList();
									
								}
								else{
									curFriend=friendOrderedList.get(0);
									fList.setSelectionInterval(0,0);
									populateMessagesList();
								}
								
							}
							
						}
						else{
							curFriend=friendOrderedList.get(fList.getMaxSelectionIndex());
							populateMessagesList();
						}
						
						}catch(Exception e){
							System.out.println("Exception at friend choosing");
							e.printStackTrace();
						}
						
						
						
					}
				}
				catch(Exception e){
					System.out.println("At the listener");
				}
				populateMessagesList();
			}
		});
		
		friends=new DefaultListModel<String>();
		
		friendList.setModel(friends);
		
		scrollPane.setViewportView(friendList);
		
		
		messages=new DefaultListModel<>();
		
		
		JPanel panel_1 = new JPanel();
		panel_1.setAlignmentX(Component.RIGHT_ALIGNMENT);
		panel.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.PAGE_AXIS));
		
		list_1 = new JList<>();
		list_1.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				list_1.clearSelection();
			}
		});
		
		JScrollPane messagesPane=new JScrollPane();
		messagesPane.setBackground(Color.WHITE);
		messagesPane.setForeground(Color.WHITE);
		messagesPane.setAutoscrolls(true);
		list_1.setModel(messages);
		messagesPane.setPreferredSize(new Dimension(500, 250));
		messagesPane.setMinimumSize(new Dimension(50, 0));
		messagesPane.setMaximumSize(new Dimension(35000, 35000));
		messagesPane.setBorder(new LineBorder(Color.GRAY));
		
		
		panel_1.add(messagesPane);
		messagesPane.setViewportView(list_1);
		
		JTextArea textArea = new JTextArea();
		textArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()==KeyEvent.VK_ENTER){
					try{
						newMessage(textArea.getText());
						textArea.setText("");
					}
					catch(Exception e){
						System.out.println("Exception in new Message handler");
					}
				}
			}
		});
		textArea.setMaximumSize(new Dimension(2147483647, 400));
		textArea.setMargin(new Insets(5, 5, 5, 5));
		textArea.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		panel_1.add(textArea);
		JTextArea user1=new JTextArea();
		user1.setAlignmentY(0.5f);
		user1.setBorder(BorderFactory.createLineBorder(Color.black));
		JTextArea user2=new JTextArea();
		user2.setAlignmentY(0.5f);
		user2.setBorder(BorderFactory.createLineBorder(Color.black));
		JTextArea user3=new JTextArea();
		user3.setAlignmentY(0.5f);
		user3.setBorder(BorderFactory.createLineBorder(Color.black));
		
		
		ActionListener serverPolling=new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				if(loginData.containsKey("session_data")){
					Map callData= new HashMap();
					callData.put("username", loginData.get("username"));
					callData.put("session_data", loginData.get("session_data"));
					//if(!toggle){
					AsyncHttp.asyncHttpCall("POST","http://127.0.0.1:8080","/messenger/api/getfriends",callData, 
							(state,result)->friendreturn(state,result));
					
					//toggle=true;
					//}
					//else{
					if(!toggle){
					AsyncHttp.asyncHttpCall("POST","http://127.0.0.1:8080","/messenger/api/history",callData, 
							(state,result)->historyreturn(state,result));}
					else{
						AsyncHttp.asyncHttpCall("POST","http://127.0.0.1:8080","/messenger/api/getmessages",callData, 
								(state,result)->getNewMessages(state,result));
					}
					toggle=true;
					//}
				}
				
				
				//JOptionPane.showMessageDialog(null, "Timer Event");

			}
		};
		newTimer=new Timer(1000,serverPolling);
		newTimer.setInitialDelay(100);
		
		
		
		initUI();
		// TODO Auto-generated constructor stub
	}
	
	
	
	public void newMessage(String mes){
		Map callData= new HashMap();
		callData.put("username", loginData.get("username"));
		callData.put("session_data", loginData.get("session_data"));
		callData.put("message", mes);
		callData.put("receiver", curFriend);
		if(messageMap.containsKey(curFriend)){
		messageMap.get(curFriend).add(new ClientMessage(mes));
		}
		else{
			messageMap.put(curFriend, new ArrayList<ClientMessage>());
			messageMap.get(curFriend).add(new ClientMessage(mes));
		}
		populateMessagesList();
		AsyncHttp.asyncHttpCall("POST", "http://127.0.0.1:8080", "/messenger/api/newmessage", callData, (state,result)->newMessageReturn(state));
	}
	
	public void newMessageReturn(Integer String){
		//needs to know which newmessage call it is receiving from
	}
	
	public void friendreturn(Integer state,Map result){
		if(state==200){
			Map<String,Boolean> friendMapAux=(Map<String,Boolean>)(result.get("friendlist"));
			boolean changes=false;
			for(Map.Entry i:friendMapAux.entrySet()){
				String user=(String)i.getKey();
				if(friendMap!=null){
					if(friendMap.containsKey(user)){
						if(friendMap.get(user)!=i.getValue()){
							friendMap.put(user,(Boolean) i.getValue());
							friends.set(friendOrderedList.indexOf(user), ("<html><p style='margin-bottom:5px'>"+user+"<br>"+(((boolean)i.getValue())?"Online":"Offline")+"</p></html>"));
							
						}
					}
					else{
						friendMap.put(user,(Boolean)i.getValue());
						friendOrderedList.add(user);
						friendsAddElement(user,(Boolean)i.getValue());
					}
				}
			}
			for(Map.Entry i:friendMap.entrySet()){
				if(!friendMapAux.containsKey(i.getKey())){
					friendMap.remove(i.getKey());
					friends.remove(friendOrderedList.indexOf(i.getKey()));
					friendOrderedList.remove(friendOrderedList.indexOf(i.getKey()));
					if(!friendOrderedList.contains(curFriend)){
						if(friendList.getSelectedIndex()<friendOrderedList.size()){
						curFriend=friendOrderedList.get(friendList.getSelectedIndex());
						}
						else{
							System.out.println("Something went wrong");
							curFriend="";
						}
					}
				}
			}
			
		}
		else{
		}
	}
	
	public void historyreturn(Integer state,Map result){

		if(state==200){
		ClientMessage message;	
			Map aux2=new HashMap();
			Date date;
			
			Map<String,List<Map<String,String>>> aux=(Map<String,List<Map<String,String>>>) (result.get("history"));
			for(Map.Entry i:aux.entrySet()){
				List<Map<String,String>> auxlist=aux.get(i.getKey());
				List<ClientMessage> auxlist2=new ArrayList();
				aux2.put(i.getKey(), auxlist2);
				for(Map k:auxlist){
					SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", new Locale("us"));
					date=new Date();
					/*try {
						date=sdf.parse((String)k.get("sendDate"));
					} catch (ParseException e) {
						
						date=new Date();
					}*/
				message=new ClientMessage((String)k.get("message"),(Boolean)(k.get("sent")),date,(Boolean)(k.get("read")));
				auxlist2.add(message);	
				}
			}
			messageMap=aux2;
			System.out.println("History after parsing"+messageMap.toString());
			
			populateMessagesList();
			
			
		}
		else{
			messageMap=new HashMap();
			
		}
	}
	
	/** callback when new messages are received*/
	public void getNewMessages(Integer state,Map result){

		if(state==200){
		ClientMessage message;	
			Map aux2=new HashMap();
			Date date;
			if(result!=null){if(result.containsKey("newmessages")){
			Map<String,List<Map<String,String>>> aux=(Map<String,List<Map<String,String>>>) (result.get("newmessages"));
			for(Map.Entry i:aux.entrySet()){
				if(!messageMap.containsKey(i.getKey()))
				{
					messageMap.put((String) i.getKey(), new ArrayList<ClientMessage>());
				}
				List<Map<String,String>> auxlist=aux.get(i.getKey());
				List<ClientMessage> auxlist2=messageMap.get((String) i.getKey());
				
				aux2.put(i.getKey(), auxlist2);
				for(Map k:auxlist){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd,hh:mm:ss", new Locale("us"));
					//date=new Date();
					try {
						date=sdf.parse((String)k.get("date"));
					} catch (ParseException e) {
						
						date=new Date();
					}
				message=new ClientMessage((String)k.get("message"),false,date,false);
				
				auxlist2.add(message);	
				}
			}			
			populateMessagesList();
			
			
		}}}
		else{
			
		}
	}
	
	public void friendsAddElement(String userName,boolean online){
		try{
		friends.addElement(("<html><p style='margin-bottom:5px'>"+userName+"<br>"+((online)?"Online":"Offline")+"</p></html>"));	
		}
		catch(Exception e){
			System.out.println("At friendsAddElement");
			e.printStackTrace();
		}
	}
	
	public void messagesAddElement(String userName,boolean delivered,String message){
		messages.addElement(("<html><div style='margin-bottom:10px;font-style:Courier;"+((delivered)?"font-color:Grey":"font-color:Black")+"'>"
				+ "<h3>"+userName+":</h3><p >"+message+((delivered)?"(delivered)":"(undelivered)")+"</p></div></html>"));	
	}

	
	public static void main(String[] args) {
		MessengerWindow ex = new MessengerWindow();
		LoginDialog authDialog=new LoginDialog(true,ex.loginData,ex);
		authDialog.setVisible(true);
        ex.setVisible(true);
        
		// TODO Auto-generated method stub

	}
	
	public void startTimer(){
		newTimer.start();
	}
	
	/** populateMessagesList loads the appropriate messages to messages
	 * 
	 */
	public void populateMessagesList(){
		/*if(!unreadMap.isEmpty()){
			if(unreadMap.containsKey(curFriend)){
				List<ClientMessage> unreadList=unreadMap.get(curFriend);
				for(ClientMessage i:unreadList){
					i.setRead(true);
					unreadList.remove(i);
				}
			}
		}*/
		
		try{
		if(messageMap!=null){
		if(!messageMap.isEmpty()){
			if(messageMap.containsKey(curFriend)){
				List<ClientMessage> messageList=messageMap.get(curFriend);
				if(!messages.isEmpty()){
					messages.clear();
				}
				for(ClientMessage i:messageList){
					messagesAddElement((i.getSent())?loginData.get("username"):curFriend,true,i.getMessage());
				}
			}
			else{
				if(!messages.isEmpty()){
					messages.clear();
				}
			}
		}
		else{
			if(!messages.isEmpty()){
				messages.clear();
			}
		}
		}
		else{
			if(!messages.isEmpty()){
				messages.clear();
			}
		}
		}
		catch(Exception e){
			System.out.println("Exception in populateMessages");
			e.printStackTrace();
		}
	}
	
	public void populateFriendList(){
		try{
			if(friendMap!=null){
				if(friendMap.isEmpty()){
					friends.clear();
				}
				else{
					
					friends.clear();
					friendOrderedList.clear();
					
					for(Map.Entry<String,Boolean> friendintro :friendMap.entrySet()){
						try{
							friendOrderedList.add(friendintro.getKey());
						friendsAddElement(friendintro.getKey(),friendintro.getValue());	
						
						
						}
						catch(Exception e){
							System.out.println("Exception in upper inner case friendlist");
							e.printStackTrace();
						}
					}
					
					try{
						if(friendOrderedList.contains(curFriend)){
							friendList.setSelectedIndex(friendOrderedList.indexOf(curFriend));
						}
						else{
							if(!friendOrderedList.isEmpty()){
								friendList.setSelectedIndex(0);
								curFriend=friendOrderedList.get(0);
							}
							else{
								curFriend="";
							}
						}
					}catch(Exception e){
						System.out.println("Exception in inner case friendlist");
						e.printStackTrace();
					}

				}
			}
			else{
				curFriend="";
				friends.clear();
				friendOrderedList.clear();

			}
		}
		
		catch(Exception e){
			System.out.println("Exception in populate friendlist");
			e.printStackTrace();
		}

	}
	
	private void initUI() {
        
        setTitle("Messenger");
        setSize(600, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
