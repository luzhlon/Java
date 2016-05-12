package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Message.Message;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField textID;
	private JPasswordField pswd;
    
	String id;
	String passwd;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Login() {
		setResizable(false);
		setTitle("\u6559\u6750\u7BA1\u7406\u7CFB\u7EDF-\u767B\u5F55");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 444, 283);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblId = new JLabel("ID\uFF1A");
		lblId.setBounds(75, 47, 54, 15);
		contentPane.add(lblId);
		
		JLabel lblPndc = new JLabel("\u5BC6\u7801\uFF1A");
		lblPndc.setBounds(75, 101, 54, 15);
		contentPane.add(lblPndc);
		
		textID = new JTextField();
		textID.setText("12345");
		textID.setBounds(147, 44, 193, 21);
		contentPane.add(textID);
		textID.setColumns(10);
		
		pswd = new JPasswordField();
		pswd.setBounds(147, 98, 193, 21);
		contentPane.add(pswd);
		
		JButton btnT = new JButton("\u767B\u5F55(\u5BFC\u5458)");
		btnT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                if (!check_connect()) return;
                getlogininfo();
                Integer iid;
                try {
                	iid = Integer.parseInt(id);
                } catch (NumberFormatException ex) {
                    Global.alert("´íÎóµÄID:%s", id);
                	return;
                }
                if (check_login(login(iid, passwd))) {
                	TPanel.main(null);
                	Login.this.setVisible(false);
                }
			}
		});
		btnT.setBounds(92, 190, 114, 23);
		contentPane.add(btnT);
		
		JButton btnA = new JButton("\u767B\u5F55(\u7BA1\u7406\u5458)");
		btnA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                if (!check_connect()) return;
                getlogininfo();
                if (check_login(login(id, passwd))) {
                	APanel.main(null);
                	Login.this.setVisible(false);
                }
			}
		});
		btnA.setBounds(226, 190, 114, 23);
		contentPane.add(btnA);
	}
    
	boolean check_connect() {
        if (Global.connect())
        	return true;
        Global.alert("Á¬½Ó·þÎñÆ÷Ê§°Ü");
		return false;
	}
	boolean check_login(Message m) {
        if (m == null || m.name.isEmpty()) {
        	Global.alert("µÇÂ¼Ê§°Ü£¬Î´Öª´íÎó");
            return false;
        }
        switch (m.name) {
        case "err_id":
        	Global.alert("µÇÂ¼Ê§°Ü£¬´íÎóµÄID");
            return false;
        case "err_passwd":
        	Global.alert("µÇÂ¼Ê§°Ü£¬´íÎóµÄÃÜÂë");
            return false;
        }
		return true;
	}
	Message login(Object ID, String ps) {
        return Global.send("login", ID, ps);
	}
    void getlogininfo() {
    	id = textID.getText();
    	passwd = new String(pswd.getPassword());
    }
}
