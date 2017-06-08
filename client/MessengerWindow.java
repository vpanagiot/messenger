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

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JLayeredPane;
import javax.swing.JDesktopPane;
import javax.swing.JEditorPane;
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
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.border.EtchedBorder;
import java.awt.Insets;
import java.awt.MenuItem;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ScrollPaneConstants;
import java.awt.event.MouseAdapter;

public class MessengerWindow extends JFrame {
	static final long serialVersionUID=1;
	private Map<String,List<ClientMessage>> messageMap;
	private Map<String,String> loginData=new HashMap<>();
	private Map<String,Boolean> friendMap=new HashMap();
	private List<Map> friendMapList=new ArrayList<>();
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
	private int historyCounter=0;
	private List<ClientMessage> curMessageList=new ArrayList<>();
	private JPopupMenu friendListPopup;
	

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
		
		JMenu mnNewMenu = new JMenu("File");
		mnNewMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmLogout = new JMenuItem("Sign Out");
		mntmLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(loginData.containsKey("session_data")){
					Map callData= new HashMap();
					callData.put("username", loginData.get("username"));
					callData.put("session_data", loginData.get("session_data"));
					AsyncHttp.asyncHttpCall("POST","http://127.0.0.1:8080","/messenger/api/logout",callData, 
						(state,result)->logout(state,result));
				}
				newTimer.stop();
				toggle=false;
				messageMap=null;
				friendMap.clear();
				friendMapList.clear();
				
				messages.clear();
				friends.clear();
				friendOrderedList.clear();
				historyCounter=0;
				curFriend="";
				loginData.clear();
				LoginDialog authDialog=new LoginDialog(true,loginData,mainWindow);
				authDialog.setVisible(true);
			}
		});
		mnNewMenu.add(mntmLogout);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(loginData.containsKey("session_data")){
					Map callData= new HashMap();
					callData.put("username", loginData.get("username"));
					callData.put("session_data", loginData.get("session_data"));
					AsyncHttp.asyncHttpCall("POST","http://127.0.0.1:8080","/messenger/api/logout",callData, 
						(state,result)->logout(state,result));
				}
				
				System.exit(EXIT_ON_CLOSE);
			}
		});
		mnNewMenu.add(mntmExit);
		
		JMenu mnFriends = new JMenu("Friends");
		menuBar.add(mnFriends);
		
		JMenuItem mntmAddFriend = new JMenuItem("Add Friend");
		mntmAddFriend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FriendsManagement friendsManagement=new FriendsManagement(loginData);
				friendsManagement.setVisible(true);
			}
		});
		mnFriends.add(mntmAddFriend);
		
		JPanel panel = new JPanel();
		panel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		getContentPane().add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(200, 3));
		scrollPane.setMinimumSize(new Dimension(100, 22));
		scrollPane.setMaximumSize(new Dimension(200, 32767));
		scrollPane.setBackground(Color.WHITE);
		scrollPane.setForeground(Color.WHITE);
		
		scrollPane.setAutoscrolls(true);
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(scrollPane);
		
		friendList = new JList<>();
		friendList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				checkPopup(arg0);
			}
			
			@Override
			public void mouseReleased(MouseEvent arg0){
				checkPopup(arg0);
			}
		});
		friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		friendList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				int selection=0;
				try{
					
						
						try{
						
						JList fList=(JList)arg0.getSource();
						
						 //lsm=((JList)arg0.getSource()).getSelectionModel();
						
						if(fList.isSelectionEmpty()){
							if(curFriend.isEmpty()){
								fList.setSelectionInterval(0, 0);
							curFriend=friendOrderedList.get(0);
							System.out.println("Friend for Selection Empty: "+curFriend);
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
							System.out.println("Friend is: "+curFriend);
						}
						
						}catch(Exception e){
							System.out.println("Exception at friend choosing");
							e.printStackTrace();
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
		friendListPopup=new JPopupMenu();
		
		friendList.add(friendListPopup);
		
		messages=new DefaultListModel<>();
		
		
		JPanel panel_1 = new JPanel();
		panel_1.setAlignmentX(Component.RIGHT_ALIGNMENT);
		panel.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.PAGE_AXIS));
		
		list_1 = new JList<>();
		list_1.setMaximumSize(new Dimension(30000, 30000));
		list_1.setMinimumSize(new Dimension(100, 0));
		list_1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
		messagesPane.setPreferredSize(new Dimension(200, 250));
		messagesPane.setMinimumSize(new Dimension(50, 0));
		messagesPane.setMaximumSize(new Dimension(35000, 35000));
		messagesPane.setBorder(new LineBorder(Color.GRAY));
		
		
		
		panel_1.add(messagesPane);
		messagesPane.setViewportView(list_1);
		
		
		
		JTextArea textArea = new JTextArea();
		textArea.setLineWrap(true);
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
	
	public void checkPopup(MouseEvent arg0){
		if(arg0.isPopupTrigger()){
			int friendIndex=friendList.locationToIndex(arg0.getPoint());
			friendList.setSelectedIndex(friendIndex);
			curFriend=friendOrderedList.get(friendIndex);
			Map contact=friendMapList.get(friendIndex);
			JMenuItem friendRemove=new JMenuItem();
			friendRemove.setText("Remove");
			JMenuItem friendBlock=new JMenuItem();
			friendBlock.setText("Block");
			JMenuItem friendAccept=new JMenuItem();
			friendAccept.setText("Accept");
			//event handlers
			friendRemove.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(loginData.containsKey("session_data")){
						Map callData= new HashMap();
						callData.put("username", loginData.get("username"));
						callData.put("session_data", loginData.get("session_data"));
						callData.put("contact", curFriend);
						AsyncHttp.asyncHttpCall("POST","http://127.0.0.1:8080","/messenger/api/removecontact",callData, 
							(state,result)-> System.out.println("remove state"+state));
					}
				}
			});
			
			friendBlock.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(loginData.containsKey("session_data")){
						Map callData= new HashMap();
						callData.put("username", loginData.get("username"));
						callData.put("session_data", loginData.get("session_data"));
						callData.put("contact", curFriend);
						AsyncHttp.asyncHttpCall("POST","http://127.0.0.1:8080","/messenger/api/blockcontact",callData, 
							(state,result)-> System.out.println("block state"+state));
					}
				}
			});
				
			friendAccept.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(loginData.containsKey("session_data")){
						Map callData= new HashMap();
						callData.put("username", loginData.get("username"));
						callData.put("session_data", loginData.get("session_data"));
						callData.put("contact", curFriend);
						AsyncHttp.asyncHttpCall("POST","http://127.0.0.1:8080","/messenger/api/acceptcontact",callData, 
							(state,result)-> System.out.println("accept state"+state));
					}
				}
			});
			
			///////////////////
			switch((int)contact.get("friendship")){
			case 1:
				friendListPopup.removeAll();
				friendListPopup.add(friendRemove);
				friendListPopup.add(friendBlock);
				break;
			case 2:
				friendListPopup.removeAll();
				friendListPopup.add(friendRemove);
				break;
			case 3:
				friendListPopup.removeAll();
				friendListPopup.add(friendAccept);
				friendListPopup.add(friendBlock);
				break;
			case 4:
				friendListPopup.removeAll();
				friendListPopup.add(friendAccept);
				break;
			default:
				break;
			}
			friendListPopup.show(friendList, arg0.getX(), arg0.getY());
		}
			
	}
	
	public void logout(int state,Map result){
		
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
	
	private int searchFriendList(String username){
		for (Map i:friendMapList){
			if(i.get("username").equals(username)){
				return(friendMapList.indexOf(i));
			}
		}
		return -1;
	}
	
	private int searchMapList(String username,List mapList){
		for (Map i:(List<Map>)mapList){
			if(i.get("username").equals(username)){
				return(friendMapList.indexOf(i));
			}
		}
		return -1;
	}
	
	public void friendreturn(Integer state,Map result){
		if(state==200){
			List<Map> friendListAux=(List)(result.get("friendlist"));
			Map contact;
			boolean changes=false;
			int position;
			for(Map i:friendListAux){
				String user=(String)i.get("username");
				if(friendMapList!=null){
					if((position=searchFriendList(user))!=-1){
						contact=friendMapList.get(position);
						if(contact.get("online")!=i.get("online")||contact.get("friendship")!=i.get("friendship")){
							contact.put("online",(Boolean) i.get("online"));
							contact.put("friendship", i.get("friendship"));
							friends.set(friendOrderedList.indexOf(user), ("<html><p style='margin-bottom:5px'>"+user+"<br>"+(((boolean)contact.get("online"))?"Online ":"Offline ")+contact.get("friendship")+"</p></html>"));
							
						}
					}
					else{
						friendMapList.add(i);
						friendOrderedList.add(user);
						friendsAddElement(user,(boolean)i.get("online"),(int)i.get("friendship"));///////////////////////////////
					}
				}
			}
			for(Map i:friendMapList){
				if(searchMapList((String)i.get("username"),friendListAux)==-1){
					friendMapList.remove(i);
					friends.remove(friendOrderedList.indexOf(i.get("uername")));
					friendOrderedList.remove(friendOrderedList.indexOf(i.get("username")));
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
		System.out.println("Printing Friends");
		System.out.println(friendMap.toString());
		System.out.println("Printing FriendOrderedList: "+friendOrderedList.toString());
	}
	
	public void historyreturn(Integer state,Map result){
		
		if(state==200){
			if(!toggle){
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
			toggle=true;
			System.out.println("Printing History");
			System.out.println(messageMap.toString());
			populateMessagesList();
			}
			else{
				//ignore for now , have to check for unread messages and append them
			}
			
		}
		else{
			historyCounter++;
			if(historyCounter>5){
				toggle=true;
				messageMap=new HashMap();
			}
			
			
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
	
	public void friendsAddElement(String userName,boolean online,int friendship){
		try{
		friends.addElement(("<html><p style='margin-bottom:5px'>"+userName+"<br>"+((online)?"Online  ":"Offline  ")+friendship+"</p></html>"));	
		}
		catch(Exception e){
			System.out.println("At friendsAddElement");
			e.printStackTrace();
		}
	}
	
	public void messagesAddElement(String userName,boolean delivered,String message){
		
		messages.addElement(("<html><div style='margin-bottom:10px;font-style:Courier;width:100%;"+((userName.equals(curFriend))?"text-align:left;":"text-align:right;")+"'>"
				+ "<h3>"+userName+":</h3><p >"+message+((delivered)?"(delivered)":"(undelivered)")+"</p></div></html>"));
				
		
	}
	
	/** cell rendered to treat the users and contact messages differently*/
	 class MyCellRenderer extends JEditorPane implements ListCellRenderer<Object> {
	     public MyCellRenderer() {
	         setOpaque(true);
	     }

	     public Component getListCellRendererComponent(JList<?> list,
	                                                   Object value,
	                                                   int index,
	                                                   boolean isSelected,
	                                                   boolean cellHasFocus) {
	    	 this.setContentType("text/html");
	         setText(value.toString());
	         /**
	         if(curMessageList!=null){
	        	 if(curMessageList.get(index).getSent()){
	        		 this.setAlignmentX(SwingConstants.RIGHT);
	        	 }
	        	 else{
	        		 this.setAlignmentX(SwingConstants.LEFT);
	        	 }
	         }*/
	         //this.setEditable(false);
	         //this.setWrapStyleWord(true);
	         //this.setLineWrap(true);
	         
	         Color background;
	         Color foreground;

	         // check if this cell represents the current DnD drop location
	         JList.DropLocation dropLocation = list.getDropLocation();
	         if (dropLocation != null
	                 && !dropLocation.isInsert()
	                 && dropLocation.getIndex() == index) {

	             background = Color.BLUE;
	             foreground = Color.WHITE;

	         // check if this cell is selected
	         } else if (isSelected) {
	             background = Color.RED;
	             foreground = Color.WHITE;

	         // unselected, and not the DnD drop location
	         } else {
	             background = Color.WHITE;
	             foreground = Color.BLACK;
	         };

	         setBackground(background);
	         setForeground(foreground);

	         return this;
	     }
	 }
	 
	/* class MyCellRenderer extends JLabel implements ListCellRenderer<Object> {
	     public MyCellRenderer() {
	         setOpaque(true);
	     }

	     public Component getListCellRendererComponent(JList<?> list,
	                                                   Object value,
	                                                   int index,
	                                                   boolean isSelected,
	                                                   boolean cellHasFocus) {

	         setText(value.toString());
	         if(curMessageList!=null){
	        	 if(curMessageList.get(index).getSent()){
	        		 this.setAlignmentX(SwingConstants.RIGHT);
	        	 }
	        	 else{
	        		 this.setAlignmentX(SwingConstants.LEFT);
	        	 }
	         }
	         //this.setEditable(false);
	         //this.setWrapStyleWord(true);
	         //this.setLineWrap(true);
	         
	         Color background;
	         Color foreground;

	         // check if this cell represents the current DnD drop location
	         JList.DropLocation dropLocation = list.getDropLocation();
	         if (dropLocation != null
	                 && !dropLocation.isInsert()
	                 && dropLocation.getIndex() == index) {

	             background = Color.BLUE;
	             foreground = Color.WHITE;

	         // check if this cell is selected
	         } else if (isSelected) {
	             background = Color.RED;
	             foreground = Color.WHITE;

	         // unselected, and not the DnD drop location
	         } else {
	             background = Color.WHITE;
	             foreground = Color.BLACK;
	         };

	         setBackground(background);
	         setForeground(foreground);

	         return this;
	     }
	 }*/
	 
	

	
	public static void main(String[] args) {
		MessengerWindow ex = new MessengerWindow();
		LoginDialog authDialog=new LoginDialog(true,ex.loginData,ex);
		ex.list_1.setCellRenderer(ex.new MyCellRenderer());
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
		
		
		try{
		if(messageMap!=null){
		if(!messageMap.isEmpty()){
			if(messageMap.containsKey(curFriend)){
				List<ClientMessage> messageList=messageMap.get(curFriend);
				curMessageList=messageList;
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
	/*
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
	*/
	
	private void initUI() {
        
        setTitle("Messenger");
        setSize(600, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
