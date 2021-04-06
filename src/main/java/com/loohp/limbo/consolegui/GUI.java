package com.loohp.limbo.consolegui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import com.loohp.limbo.Limbo;

@SuppressWarnings("serial")
public class GUI extends JFrame {

	public static JPanel contentPane;
	public static JTextField commandInput;
	public static JButton execCommand;
	public static JTextPane textOutput;
	public static JScrollPane scrollPane;
	public static JLabel consoleLabel;
	public static JLabel clientLabel;
	public static JTextPane clientText;
	public static JScrollPane scrollPane_client;
	public static JLabel sysLabel;
	public static JScrollPane scrollPane_sys;
	public static JTextPane sysText;
	
	public static List<String> history = new ArrayList<String>();
	public static int currenthistory = 0;
	
	public static boolean loadFinish = false;

	/**
	 * Launch the application.
	 */
	public static void main() {
		GUI frame = new GUI();
		frame.setVisible(true);
		
		Thread t1 = new Thread(new Runnable() {
		    @Override
		    public void run() {
		    	SystemInfo.printInfo();
		    }
		});  
		t1.start();
		
		loadFinish = true;
	}

	/**
	 * Create the frame.
	 */
	public GUI() {
		setTitle("Limbo Minecraft Server");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (Limbo.getInstance().isRunning()) {
					Limbo.getInstance().stopServer();
				} 
			}
		});	
		setBounds(100, 100, 1198, 686);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{243, 10, 36, 111, 0};
		gbl_contentPane.rowHeights = new int[]{0, 160, 0, 10, 33, 33, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		sysLabel = new JLabel("System Information");
		sysLabel.setFont(new Font("Arial", Font.BOLD, 11));
		GridBagConstraints gbc_sysLabel = new GridBagConstraints();
		gbc_sysLabel.fill = GridBagConstraints.BOTH;
		gbc_sysLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sysLabel.gridx = 0;
		gbc_sysLabel.gridy = 0;
		contentPane.add(sysLabel, gbc_sysLabel);
		
		consoleLabel = new JLabel("Console Output");
		consoleLabel.setFont(new Font("Arial", Font.BOLD, 11));
		GridBagConstraints gbc_consoleLabel = new GridBagConstraints();
		gbc_consoleLabel.anchor = GridBagConstraints.WEST;
		gbc_consoleLabel.insets = new Insets(0, 0, 5, 5);
		gbc_consoleLabel.gridx = 2;
		gbc_consoleLabel.gridy = 0;
		contentPane.add(consoleLabel, gbc_consoleLabel);
		
		commandInput = new JTextField();
		commandInput.setToolTipText("Input a command");
		commandInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 10) {					
					String cmd = commandInput.getText();
					if (!commandInput.getText().equals("")) {
						history.add(cmd);
						currenthistory = history.size();
					}
					Limbo.getInstance().dispatchCommand(Limbo.getInstance().getConsole(), cmd.trim().replaceAll(" +", " "));
					commandInput.setText("");
				} else if (e.getKeyCode() == 38) {
					currenthistory--;
					if (currenthistory >= 0) {
						commandInput.setText(history.get(currenthistory));
					} else {
						currenthistory++;
					}
				} else if (e.getKeyCode() == 40) {
					currenthistory++;
					if (currenthistory < history.size()) {
						commandInput.setText(history.get(currenthistory));
					} else {
						currenthistory--;
					}
				}
			}
		});
		
		scrollPane_sys = new JScrollPane();
		GridBagConstraints gbc_scrollPane_sys = new GridBagConstraints();
		gbc_scrollPane_sys.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane_sys.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_sys.gridx = 0;
		gbc_scrollPane_sys.gridy = 1;
		contentPane.add(scrollPane_sys, gbc_scrollPane_sys);
		
		sysText = new JTextPane();
		sysText.setFont(new Font("Consolas", Font.PLAIN, 12));
		sysText.setEditable(false);
		scrollPane_sys.setViewportView(sysText);
		
		clientLabel = new JLabel("Connected Clients");
		clientLabel.setFont(new Font("Arial", Font.BOLD, 11));
		GridBagConstraints gbc_clientLabel = new GridBagConstraints();
		gbc_clientLabel.anchor = GridBagConstraints.WEST;
		gbc_clientLabel.insets = new Insets(0, 0, 5, 5);
		gbc_clientLabel.gridx = 0;
		gbc_clientLabel.gridy = 3;
		contentPane.add(clientLabel, gbc_clientLabel);
		
		scrollPane_client = new JScrollPane();
		GridBagConstraints gbc_scrollPane_client = new GridBagConstraints();
		gbc_scrollPane_client.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_client.gridheight = 2;
		gbc_scrollPane_client.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPane_client.gridx = 0;
		gbc_scrollPane_client.gridy = 4;
		contentPane.add(scrollPane_client, gbc_scrollPane_client);
		
		clientText = new JTextPane();
		scrollPane_client.setViewportView(clientText);
		clientText.setFont(new Font("Consolas", Font.PLAIN, 12));
		clientText.setEditable(false);
		
		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 4;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 2;
		gbc_scrollPane.gridy = 1;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		textOutput = new JTextPane();
		scrollPane.setViewportView(textOutput);
		textOutput.setFont(new Font("Consolas", Font.PLAIN, 12));
		textOutput.setEditable(false);
		commandInput.setFont(new Font("Tahoma", Font.PLAIN, 19));
		GridBagConstraints gbc_commandInput = new GridBagConstraints();
		gbc_commandInput.insets = new Insets(0, 0, 0, 5);
		gbc_commandInput.fill = GridBagConstraints.BOTH;
		gbc_commandInput.gridx = 2;
		gbc_commandInput.gridy = 5;
		contentPane.add(commandInput, gbc_commandInput);
		commandInput.setColumns(10);
		
		execCommand = new JButton("RUN");
		execCommand.setToolTipText("Execute a command");
		execCommand.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				String cmd = commandInput.getText();
				if (!commandInput.getText().equals("")) {
					history.add(cmd);
					currenthistory = history.size();
				}
				Limbo.getInstance().dispatchCommand(Limbo.getInstance().getConsole(), cmd.trim().replaceAll(" +", " "));
				commandInput.setText("");
			}
		});
		execCommand.setFont(new Font("Tahoma", Font.PLAIN, 19));
		GridBagConstraints gbc_execCommand = new GridBagConstraints();
		gbc_execCommand.fill = GridBagConstraints.BOTH;
		gbc_execCommand.gridx = 3;
		gbc_execCommand.gridy = 5;
		contentPane.add(execCommand, gbc_execCommand);
	}

}
