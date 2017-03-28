package com.cyc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.TextArea;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;
public class Window {

	private JFrame frame;
	private JTextField file1;
	private TextArea content;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				Window window = new Window();
				window.frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the application.
	 * @wbp.parser.entryPoint
	 */
	public Window() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setFont(new Font("Dialog", Font.PLAIN, 16));
		frame.setTitle("相似度软件");
		frame.getContentPane().setBackground(new Color(153, 204, 204));
		frame.getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton("选择文件");
		btnNewButton.setForeground(new Color(0, 0, 0));
		btnNewButton.setBackground(new Color(204, 204, 204));
		btnNewButton.addActionListener(e -> {
			uploadFile(new JButton());
		});
		btnNewButton.setBounds(6, 36, 145, 30);
		btnNewButton.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		frame.getContentPane().add(btnNewButton, BorderLayout.NORTH);
		
		JLabel lblNewLabel = new JLabel("* 文章相似度检查");
		lblNewLabel.setBounds(6, 6, 716, 30);
		frame.getContentPane().add(lblNewLabel);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(6, 66, 773, 12);
		frame.getContentPane().add(separator);
		
	    content = new TextArea();
		content.setForeground(new Color(0, 0, 0));
		content.setBackground(new Color(255, 255, 255));
		content.setBounds(6, 79, 707, 371);
		frame.getContentPane().add(content);
		
		file1 = new JTextField();
		file1.setBounds(153, 37, 425, 28);
		frame.getContentPane().add(file1);
		file1.setColumns(10);
		
		JButton button = new JButton("开始匹配");
		button.setForeground(Color.BLACK);
		button.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		button.setBackground(new Color(204, 204, 204));
		button.setBounds(578, 36, 145, 30);
		frame.getContentPane().add(button);
		frame.setBackground(Color.WHITE);
		frame.setAutoRequestFocus(false);
		frame.setBounds(100, 100, 806, 482);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void uploadFile(Component obj) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileFilter(new FileNameExtensionFilter("txt", "doc", "docx"));
		int returnVal = fileChooser.showOpenDialog(obj);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (null == file) 
				return;
			file1.setText(file.getName());
			try {
				InputStream is = new FileInputStream(file);
				OPCPackage opcPackage = POIXMLDocument.openPackage(file.getAbsolutePath());
				POIXMLTextExtractor wx = new XWPFWordExtractor(opcPackage);
				System.out.println(wx.getText().length());
				content.setText(wx.getText());
			} catch (IOException | XmlException | OpenXML4JException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
