package messenger.client;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JList;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class FriendsManagement extends JDialog {
	private JTextField textField;
	private JList list;
	DefaultListModel<String>	users;
	List<Map> friendListAux;
	FriendsManagement friendsManagement;

	public FriendsManagement(Map loginData) {
		friendsManagement=this;
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel AddFriends = new JPanel();
		tabbedPane.addTab("Add Friends", null, AddFriends, null);
		AddFriends.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		AddFriends.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		textField = new JTextField();
		panel.add(textField);
		textField.setColumns(10);
		
		JButton btnSearch = new JButton("Search");
		
		/* Search button pressed*/
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String sUsername=textField.getText();
				if(loginData.containsKey("session_data")){
					Map callData= new HashMap();
					callData.put("username", loginData.get("username"));
					callData.put("session_data", loginData.get("session_data"));
					callData.put("contact", sUsername);
					AsyncHttp.asyncHttpCall("POST","http://127.0.0.1:8080","/messenger/api/searchcontact",callData, 
						(state,result)-> searchContactReturn(state,result));
				}
			}
		});
		panel.add(btnSearch);
		
		JScrollPane scrollPane = new JScrollPane();
		AddFriends.add(scrollPane, BorderLayout.CENTER);
		
		list = new JList();
		
		scrollPane.setViewportView(list);
		users=new DefaultListModel<String>();
		
		list.setModel(users);
		
		JPanel panel_2 = new JPanel();
		AddFriends.add(panel_2, BorderLayout.SOUTH);
		
		JButton btnSendRequest = new JButton("Send Request");
		btnSendRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!list.isSelectionEmpty()){
					int sel=list.getSelectedIndex();
					String contact=(String)friendListAux.get(sel).get("username");
					if(loginData.containsKey("session_data")){
						Map callData= new HashMap();
						callData.put("username", loginData.get("username"));
						callData.put("session_data", loginData.get("session_data"));
						callData.put("contact", contact);
						AsyncHttp.asyncHttpCall("POST","http://127.0.0.1:8080","/messenger/api/requestcontact",callData, 
							(state,result)-> System.out.println("Contact Requested"));
					}
				}
			}
		});
		panel_2.add(btnSendRequest);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				friendsManagement.setVisible(false);
			}
		});
		panel_2.add(btnCancel);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("New tab", null, panel_1, null);
		// TODO Auto-generated constructor stub
		initUI();
	}
	
	public void searchContactReturn(int state,Map result){
		users.clear();
	
		if(state==200){
			 friendListAux=(List)(result.get("userlist")); 
			for(Map i:friendListAux){
				users.addElement((String)i.get("username"));
			}
		}
	}
	
private void initUI() {
        
        setTitle("Friends Management");
        setSize(300, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

	

}
