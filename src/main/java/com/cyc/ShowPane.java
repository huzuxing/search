package com.cyc;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

public class ShowPane {

	private JFrame frame;
	private JTable table;

	public ShowPane(List<Double> rates, List<String> simTexts, List<String> urls) {
		initialize(rates, simTexts, urls);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(List<Double> rates, List<String> simTexts, List<String> urls) {
		frame = new JFrame();
		frame.setBounds(100, 100, 819, 478);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		String[] headers = {"相似度", "相似内容", "来源"};
		int size = rates.size();
		Object[][] objs = new Object[size][3];
		table = new JTable(objs, headers);
		table.setBackground(new Color(153, 204, 153));
		frame.getContentPane().add(table, BorderLayout.CENTER);
		frame.setVisible(true);
	}
	
}
