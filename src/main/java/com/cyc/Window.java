package com.cyc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.TextArea;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Window {

	private JFrame frame;
	private JTextField file1;
	private TextArea content;
	private InfiniteProgressPanel glassPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					Window window = new Window();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
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
		frame.setTitle("网络查重软件");
		frame.getContentPane().setBackground(new Color(153, 204, 204));
		frame.getContentPane().setLayout(null);

		JButton btnNewButton = new JButton("选择文件");
		btnNewButton.setForeground(new Color(0, 0, 0));
		btnNewButton.setBackground(new Color(204, 204, 204));
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				uploadFile(new JButton());
			}
		});
		btnNewButton.setBounds(6, 36, 145, 30);
		btnNewButton.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		frame.getContentPane().add(btnNewButton, BorderLayout.NORTH);

		JLabel lblNewLabel = new JLabel("* 文本网络相似度检查");
		lblNewLabel.setBounds(6, 6, 716, 30);
		frame.getContentPane().add(lblNewLabel);

		JSeparator separator = new JSeparator();
		separator.setBounds(6, 71, 716, 2);
		frame.getContentPane().add(separator);

		content = new TextArea();
		content.setForeground(Color.BLACK);
		content.setBackground(Color.WHITE);
		//content.setBounds(6, 79, 716, 354);
		//content.setEnabled(false);
		frame.getContentPane().add(content);

		file1 = new JTextField();
		file1.setBounds(153, 37, 425, 28);
		frame.getContentPane().add(file1);
		file1.setColumns(10);

		JButton button = new JButton("开始匹配");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!validateFileInfo()) 
					return;
				calculate();
			}
		});
		button.setForeground(Color.BLACK);
		button.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		button.setBackground(new Color(204, 204, 204));
		button.setBounds(578, 36, 145, 30);
		frame.getContentPane().add(button);
		frame.setBackground(Color.WHITE);
		frame.setAutoRequestFocus(false);
		frame.setBounds(100, 100, 754, 486);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		glassPane = new InfiniteProgressPanel();
		glassPane.setBounds(100, 100, 700, 450);
		frame.setGlassPane(glassPane);
	}

	@SuppressWarnings("resource")
	private void uploadFile(Component obj) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileFilter(new FileNameExtensionFilter("txt", "doc",
				"docx"));
		int returnVal = fileChooser.showOpenDialog(obj);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (null == file)
				return;
			file1.setText(file.getName());
			try {
				OPCPackage opcPackage = POIXMLDocument.openPackage(file
						.getAbsolutePath());
				POIXMLTextExtractor wx = new XWPFWordExtractor(opcPackage);
				String text = wx.getText();
				content.setText(text);
			} catch (IOException | XmlException | OpenXML4JException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private boolean validateFileInfo() {
		String text = content.getText().toString();
		String file = file1.getText().toString();
		if (null == file || "".equals(file)) {
			JOptionPane.showMessageDialog(null, "请先选择文件!");
			return false;
		}
		if (null == text || "".equals(text)) {
			JOptionPane.showMessageDialog(null, "文件内容为空!");
			return false;
		}
		return true;
	}

	private void calculate() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("开始匹配...");
				String text = content.getText().toString();
				// ...
				glassPane.setText("正在匹配，请稍等...");
				glassPane.start();// 开始动画加载效果
				List<List<ResultData>> pages = null;
				try {
					pages = new Search().search(text);
				} catch (HeadlessException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "网络查重出现问题，请稍后再试!");
					return;
				} finally {
					glassPane.stop();
				}
				String name = file1.getText().toString();
				ResultPane pane = new ResultPane(name.substring(0, name.lastIndexOf(".")), pages);
			}
		}).start();
		
	}
}
