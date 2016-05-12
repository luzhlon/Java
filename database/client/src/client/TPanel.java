package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import Message.Message;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class TPanel extends JFrame {

	private static final Object[] Object = null;
	private JPanel contentPane;
	private JTable table;
	
	static String[] column = {
			"ID", "书名", "班级", "人数", "库存", "可领取"
	};
	DefaultTableModel tabmod = new DefaultTableModel(column, 0);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TPanel frame = new TPanel();
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
	public TPanel() {
		setResizable(false);
		setTitle("\u6559\u6750\u7BA1\u7406\u7CFB\u7EDF-\u5BFC\u5458\u63A7\u5236\u53F0");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 579, 464);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel label = new JLabel("\u5F85\u8D2D\u56FE\u4E66\uFF1A");
		label.setBounds(10, 10, 76, 15);
		contentPane.add(label);
		
		JButton btnAdd = new JButton("\u6DFB\u52A0");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                BuyBook.main(null);
                refreshtable();
			}
		});
		btnAdd.setBounds(299, 6, 76, 23);
		contentPane.add(btnAdd);
		
		JButton btnDel = new JButton("\u79FB\u9664");
		btnDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                int i = getselindex();
                if (i < 0) return;
                Integer it = (Integer) tabmod.getValueAt(i, 0);
                Global.send("delbuy", it);
                refreshtable();
			}
		});
		btnDel.setBounds(385, 6, 76, 23);
		contentPane.add(btnDel);
		
		JButton btnPush = new JButton("\u5DF2\u53D1\u653E");
		btnPush.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                int i = getselindex();
                if (i < 0) return;
                Integer it = (Integer) tabmod.getValueAt(i, 0);
                Global.send("pushbuy", it);
                refreshtable();
			}
		});
		btnPush.setBounds(471, 6, 82, 23);
		contentPane.add(btnPush);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 35, 533, 381);
		contentPane.add(scrollPane);
		
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(table);
		
		init();
	}
	
    void init() {
        table.setModel(tabmod);
    	refreshtable();
    }
    int getselindex() {
        int i = table.getSelectedRow();
        if (i < 0)
        	Global.alert("请选择一个条目 ");
        return i;
    }
	void refreshtable() {
		Message m = Global.send("buylist");
//		table.removeAll();
        tabmod.setRowCount(0);
		int i = 0;
        Object[] o = new Object[column.length];
		while (i < m.args.length) {
			o[0] = m.args[i++];		// buy id
			o[1] = m.args[i++];		// book name
			o[2] = m.args[i++];		// class name
			o[3] = m.args[i++];		// class count
			o[4] = m.args[i++];		// book count
			o[5] = (Integer)o[3] < (Integer)o[4] ? "是" : "否";
            
			tabmod.addRow(o);
		}
	}

}
