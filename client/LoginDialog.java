package messenger.client;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Component;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LoginDialog extends JDialog {
	private JTextField textUsername;
	private JTextField textPassword;
	private JTextField textUsername2;
	private JTextField textPassword2;
	private Map<String,String> loginData;
	private LoginDialog loginDialog;
	private MessengerWindow caller;
	public LoginDialog(boolean login,Map<String,String> loginData,MessengerWindow caller) {
		this.loginData=loginData;
		loginDialog=this;
		this.caller=caller;
		loginData.put("state", "null");
		getContentPane().setMaximumSize(new Dimension(300, 100));
		setModal(true);
		setMaximumSize(new Dimension(300, 100));
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane);
		JPanel loginPanel=new JPanel();
		JPanel signupPanel=new JPanel();
		tabbedPane.addTab("Login", loginPanel);
		loginPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		loginPanel.add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.PAGE_AXIS));
		
		JLabel lblUsername = new JLabel("Username");
		panel_1.add(lblUsername);
		lblUsername.setLabelFor(textUsername);
		
		textUsername = new JTextField();
		textUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
		textUsername.setMaximumSize(new Dimension(200, 30));
		panel_1.add(textUsername);
		textUsername.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password");
		panel_1.add(lblPassword);
		lblPassword.setLabelFor(textPassword);
		
		textPassword = new JTextField();
		textPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
		textPassword.setMaximumSize(new Dimension(200, 30));
		panel_1.add(textPassword);
		textPassword.setColumns(10);
		
		JPanel panel = new JPanel();
		panel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		loginPanel.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loginData.put("username",textUsername.getText());
				loginData.put("password",textPassword.getText());
				AsyncHttp.asyncHttpCall("POST","http://127.0.0.1:8080","/messenger/api/login",loginData, 
					(state,result)->loginReturn(state,result));
			}
		});
		btnSubmit.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(btnSubmit);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loginData.put("state", "null");
				loginDialog.setVisible(false);
				System.exit(EXIT_ON_CLOSE);
			}
		});
		btnCancel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(btnCancel);
		tabbedPane.addTab("Signup", signupPanel);
		signupPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_2 = new JPanel();
		signupPanel.add(panel_2);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.PAGE_AXIS));
		
		JLabel lblusername2 = new JLabel("Username");
		panel_2.add(lblusername2);
		
		textUsername2 = new JTextField();
		textUsername2.setMaximumSize(new Dimension(200, 30));
		textUsername2.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_2.add(textUsername2);
		textUsername2.setColumns(10);
		
		JLabel lblPassword2 = new JLabel("Password");
		panel_2.add(lblPassword2);
		
		textPassword2 = new JTextField();
		textPassword2.setMaximumSize(new Dimension(200, 30));
		textPassword2.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_2.add(textPassword2);
		textPassword2.setColumns(10);
		
		JPanel panel_3 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_3.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		signupPanel.add(panel_3, BorderLayout.SOUTH);
		
		JButton btnSubmit2 = new JButton("Submit");
		panel_3.add(btnSubmit2);
		
		JButton btnCancel2 = new JButton("Cancel");
		panel_3.add(btnCancel2);
		
		tabbedPane.setSelectedIndex((login)?0:1);
		initUI();
		
		
		// TODO Auto-generated constructor stub
	}
	
	void loginReturn(int state,Map result){
		
		if(state==200){
			loginData.put("session_data",(String)result.get("session_data"));
			loginData.put("state", "loggedin");
			JOptionPane.showMessageDialog(null, "correct login");
			caller.startTimer();
			this.setVisible(false);
		}
		else{
			JOptionPane.showMessageDialog(null, "Incorrect login");
			loginData.remove("username");
			loginData.remove("password");
		}
	}
	
	private void initUI() {
        
        setTitle("Login Form");
        setSize(300, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}
