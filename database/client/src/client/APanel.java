package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import Message.Message;

import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class APanel extends JFrame {

	private JPanel contentPane;
	static String[] column = {
			"书名", "库存"
	};
	DefaultTableModel tabmod = new DefaultTableModel(column, 0);
	private JTable table;
	private JTextField textCount;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					APanel frame = new APanel();
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
	public APanel() {
		setResizable(false);
		setTitle("\u6559\u6750\u7BA1\u7406\u7CFB\u7EDF-\u7BA1\u7406\u5458\u63A7\u5236\u53F0");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 462, 455);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 35, 424, 372);
		contentPane.add(scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		
		JLabel label = new JLabel("\u56FE\u4E66\u5E93\u5B58\uFF1A");
		label.setBounds(10, 10, 73, 15);
		contentPane.add(label);
		
		textCount = new JTextField();
		textCount.setBounds(233, 7, 101, 21);
		contentPane.add(textCount);
		textCount.setColumns(10);
		
		JButton btnAdd = new JButton("\u6DFB\u52A0");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer count;
				int index = table.getSelectedRow();
				if (index < 0) {
					Global.alert("请选择一本图书");
                    return;
				}
				String book = (String) tabmod.getValueAt(index, 0);
                try {
                	count = Integer.parseInt(textCount.getText());
                    Global.send("addbookc", book, count);
                    refreshtable();
                } catch (NumberFormatException ex) {
                	Global.alert("错误的数字 :%d", textCount.getText());
                	return;
                }
			}
		});
		btnAdd.setBounds(341, 6, 93, 23);
		contentPane.add(btnAdd);
        
		init();
	}
    
	void init() {
		table.setModel(tabmod);
		refreshtable();
	}
    void refreshtable() {
    	Message m = Global.send("bookcount");
        tabmod.setRowCount(0);
        int i = 0;
        while (i < m.args.length) {
        	String bname = (String) m.args[i++];
        	Integer count = (Integer) m.args[i++];
            Object[] o = { bname, count };
            tabmod.addRow(o);
        }
    }

}
