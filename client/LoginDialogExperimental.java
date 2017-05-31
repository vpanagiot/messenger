package messenger.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import java.awt.Color;
import javax.swing.border.LineBorder;
import javax.swing.Box;

public class LoginDialogExperimental extends JDialog {
	private JTextField textUsername;
	private JTextField textPassword;
	private JTextField textUsername2;
	private JTextField textPassword2;

	public LoginDialogExperimental() {
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
		
		Box verticalBox = Box.createVerticalBox();
		panel_1.add(verticalBox);
		
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
		
		Component horizontalGlue = Box.createHorizontalGlue();
		panel.add(horizontalGlue);
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setBorder(new LineBorder(new Color(0, 0, 0), 5, true));
		btnSubmit.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(btnSubmit);
		
		JButton btnCancel = new JButton("Cancel");
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
		
		// TODO Auto-generated constructor stub
	}
}
