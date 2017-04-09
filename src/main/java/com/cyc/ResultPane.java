package com.cyc;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.Component;
import java.awt.Window.Type;
import java.awt.Dialog.ModalExclusionType;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.List;
import java.util.Vector;

public class ResultPane {

	private JFrame frame;
	private JTable table;

	/**
	 * Launch the application.
	 */
	// public static void main(String[] args) {
	// EventQueue.invokeLater(new Runnable() {
	// public void run() {
	// try {
	// ResultPane window = new ResultPane();
	// window.frame.setVisible(true);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// });
	// }

	/**
	 * Create the application.
	 */
	public ResultPane(String fileName,List<List<ResultData>> pages) {
		initialize(fileName,pages);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(String fileName,List<List<ResultData>> pages) {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(153, 204, 153));
		frame.setTitle(fileName + "的匹配结果, 相似内容以;*分隔");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		Object[] headers = { "编号", "源内容", "相似内容", "相似度" };
		int size = pages.size();
		Object[][] objs = new Object[size][4];
		for (int i = 0; i < size; i++) {
			// 编号
			objs[i][0] = i + 1;
			List<ResultData> result = pages.get(i);
			// 源内容
			objs[i][1] = result.get(0).getOrigWord();
			// 相似内容
			StringBuilder simWord = new StringBuilder();
			for(ResultData o : result) {
				simWord.append(o.getSimWord()).append(";*");
			}
			objs[i][2] = simWord.toString().substring(0, simWord.toString().lastIndexOf(";"));
			// 相似度
			objs[i][3] = result.get(0).getRate();
		}
		table = 
				new JTable(objs, headers);
		table.getTableHeader().setVisible(true);
		
		table.setBackground(new Color(153, 204, 204));
		table.setEditingColumn(2);
		TableCellTextAreaRenderer render = new TableCellTextAreaRenderer();
		table.setDefaultRenderer(Object.class, render);
//		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();// 设置table内容居中
//		  tcr.setHorizontalAlignment(JLabel.CENTER);
//		  table.setDefaultRenderer(Object.class, tcr);
		frame.setBounds(100, 100, 754, 486);
		JScrollPane scroll = new JScrollPane(table);
		scroll.setBackground(new Color(153, 204, 204));
		frame.getContentPane().add(scroll, BorderLayout.CENTER);
		frame.setVisible(true);
	}
}

class TableCellTextAreaRenderer extends JTextArea implements TableCellRenderer {
	public TableCellTextAreaRenderer() {
		setLineWrap(true);
		setWrapStyleWord(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		// 计算当下行的最佳高度
		int maxPreferredHeight = 0;
		for (int i = 0; i < table.getColumnCount(); i++) {
			setText("" + table.getValueAt(row, i));
			setSize(table.getColumnModel().getColumn(column).getWidth(), 0);
			maxPreferredHeight = Math.max(maxPreferredHeight,
					getPreferredSize().height);
		}

		if (table.getRowHeight(row) != maxPreferredHeight) // 少了这行则处理器瞎忙
			table.setRowHeight(row, maxPreferredHeight);

		setText(value == null ? "" : value.toString());
		return this;
	}
}
