package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Message.Message;

import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ListModel;

import java.awt.event.ActionListener;
import java.util.HashMap;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class BuyBook extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textBook;
    
	DefaultListModel<String> blist = new DefaultListModel<>();
	DefaultListModel<String> clist = new DefaultListModel<>();
    
	HashMap<String, Integer> bmap = new HashMap<>();  // book name->id map
	HashMap<String, Integer> cmap = new HashMap<>();  // class name->id map

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			BuyBook dialog = new BuyBook();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public BuyBook() {
		setModal(true);
		setTitle("\u6559\u6750\u7BA1\u7406\u7CFB\u7EDF-\u8D2D\u7F6E\u56FE\u4E66");
		setResizable(false);
		setBounds(100, 100, 655, 440);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel label = new JLabel("\u9009\u62E9\u56FE\u4E66:");
			label.setBounds(10, 10, 68, 15);
			contentPanel.add(label);
		}
		
		JButton btnAdd = new JButton("\u6DFB\u52A0");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                String book = textBook.getText();
                Message m = Global.send("addbook", book);
                if (m.name.equals("err")) {
                	Global.alert("添加图书失败");
                	return;
                }
                blist.addElement(book);
                // add to list
			}
		});
		btnAdd.setBounds(236, 6, 77, 23);
		contentPanel.add(btnAdd);
		
		textBook = new JTextField();
		textBook.setBounds(93, 7, 133, 21);
		contentPanel.add(textBook);
		textBook.setColumns(10);
		
		JLabel label = new JLabel("\u9009\u62E9\u73ED\u7EA7\uFF1A");
		label.setBounds(335, 10, 77, 15);
		contentPanel.add(label);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(345, 35, 294, 334);
		contentPanel.add(scrollPane);
		
		JList listClass = new JList();
		scrollPane.setViewportView(listClass);
		listClass.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listClass.setModel(clist);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(20, 35, 293, 334);
		contentPanel.add(scrollPane_1);
		
		JList listBook = new JList();
		scrollPane_1.setViewportView(listBook);
		listBook.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
        listBook.setModel(blist);
        {
        	JButton ok = new JButton("\u786E\u5B9A");
        	ok.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
                    String selBook = (String)listBook.getSelectedValue();
                    String selClass = (String)listClass.getSelectedValue();
                    if (selBook == null) {
                    	Global.alert("请选择一本书");
                        return;
                    }
                    if (selClass == null) {
                    	Global.alert("请选择一个班");
                        return;
                    }
                    Message m = Global.send("addbuy", selBook, selClass);
                    if (m.name.equals("err"))
                    	Global.alert("FAILURE");
                    BuyBook.this.dispose();
        		}
        	});
        	ok.setBounds(466, 379, 77, 23);
        	contentPanel.add(ok);
        	ok.setActionCommand("OK");
        	getRootPane().setDefaultButton(ok);
        }
        {
        	JButton cancel = new JButton("\u53D6\u6D88");
        	cancel.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
                    BuyBook.this.dispose();
        		}
        	});
        	cancel.setBounds(562, 379, 77, 23);
        	contentPanel.add(cancel);
        	cancel.setActionCommand("Cancel");
        }
		init();
	}
    
	void init() {
		getbooklist();
		getclasslist();
	}
	
	boolean getbooklist() {
		Message m = Global.send("booklist");
		for (int i = 0; i < m.args.length; i++) {
			String b = (String) m.args[i];
            blist.addElement(b);
		}
		return true;
	}
	boolean getclasslist() {
		Message m = Global.send("classlist");
		for (int i = 0; i < m.args.length; i++) {
			String b = (String) m.args[i];
            clist.addElement(b);
		}
		return true;
	}
}
