package com.erdlof.neutron.client;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.erdlof.neutron.util.Request;
import javax.swing.JMenuBar;
import javax.swing.JButton;
import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.CardLayout;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import java.awt.event.KeyEvent;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

public class Main extends JFrame implements ClientListener, ActionListener, KeyListener, WindowListener {
	private static final long serialVersionUID = 527099896996818525L;
	
	private Connection connection;
	private Toolkit toolkit;
	private volatile List<Partner> partners;
	
	private JTextField messageInput;
	private JTextField serverAdress;
	private JTextField clientName;
	private JPanel panel;
	private JButton btnConnect;
	private JLabel lblStatus;
	private JTextPane messageProvideBox;
	private KeyPairGenerator generator;
	private JLabel lblErrorDisplay;
	private JPanel messageContainerPanel;
	private JScrollPane scrollPane;
	private StyledDocument mainMessages;
	private SimpleAttributeSet style;
	private KeyPair keyPair;
	private JPanel clientListContainer;
	private JList<String> clientList;
	private DefaultListModel<String> lm;
	
	//ONLY FOR TESTING, I WILL MAKE IT MORE BEAUTIFUL
	public static void main(String[] args) {
		new Main();
	}
	
	public Main() { //set up the window
		style = new SimpleAttributeSet();
		lm = new DefaultListModel<String>();
		partners = new ArrayList<Partner>();
		
		try {
			generator = KeyPairGenerator.getInstance("RSA");
		} catch (Exception e) {
		}
		generator.initialize(2048);
		
		keyPair = generator.generateKeyPair(); //TODO additional config for custom key
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //make it look beautiful.
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		toolkit = Toolkit.getDefaultToolkit();
		setTitle("Neutron v0.1");
		setSize(901, 501);
		
		int x = (int) (toolkit.getScreenSize().width - this.getWidth()) / 2; //we want the window in the middle of our screen
		int y = (int) (toolkit.getScreenSize().height - this.getHeight()) / 2;
		
		setLocation(x, y);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //we do this on our own
		this.setMinimumSize(new Dimension(700, 400));
	
		this.initializeComponents();
		addWindowListener(this);
		
		setVisible(true);
    }
	
	private void initializeComponents() { //this is made by WindowBuilder as I'm too bad to design GUIs.
		JMenuBar mainMenuBar = new JMenuBar();
		setJMenuBar(mainMenuBar);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new CardLayout(5, 5));
		
		messageInput = new JTextField();
		messageInput.addKeyListener(this);
		panel.add(messageInput, "name_6666103840325");
		messageInput.setColumns(10);
		
		JPanel loginContainer = new JPanel();
		getContentPane().add(loginContainer, BorderLayout.EAST);
		loginContainer.setLayout(new MigLayout("", "[200px,grow,left]", "[19px][19px][][][grow][][][][][][][][][]"));
		
		serverAdress = new JTextField();
		loginContainer.add(serverAdress, "cell 0 0,alignx left,aligny top");
		serverAdress.setColumns(20);
		
		clientName = new JTextField();
		loginContainer.add(clientName, "cell 0 1,growx,aligny bottom");
		clientName.setColumns(20);
		
		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(this);
		loginContainer.add(btnConnect, "flowx,cell 0 2");
		
		lblStatus = new JLabel("");
		loginContainer.add(lblStatus, "cell 0 2");
		
		lblErrorDisplay = new JLabel("");
		loginContainer.add(lblErrorDisplay, "cell 0 3");
		
		clientListContainer = new JPanel();
		clientListContainer.setBorder(new TitledBorder(null, "Users", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		loginContainer.add(clientListContainer, "cell 0 4 1 10,grow");
		clientListContainer.setLayout(new CardLayout(0, 0));
		
		clientList = new JList<String>(lm);
		clientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		clientListContainer.add(clientList, "name_11791777654180");
		
		messageContainerPanel = new JPanel();
		getContentPane().add(messageContainerPanel, BorderLayout.CENTER);
		messageContainerPanel.setLayout(new CardLayout(5, 5));
		
		messageProvideBox = new JTextPane();
		messageProvideBox.setEditable(false);
		
		scrollPane = new JScrollPane(messageProvideBox);
		messageContainerPanel.add(scrollPane, "name_21017317536325");
		
		mainMessages = messageProvideBox.getStyledDocument();
	}
	//----
	private JLabel getlblStatus() {
		synchronized (lblStatus) {
			return lblStatus;
		}
	}
	
	private JLabel getlblErrorDisplay() {
		synchronized (lblErrorDisplay) {
			return lblErrorDisplay;
		}
	}
	
	private JTextPane getMessageProvideBox() {
		synchronized (messageProvideBox) {
			return messageProvideBox;
		}
	}
	
	private JList<String> getClientList() {
		synchronized (clientList) {
			return clientList;
		}
	}
	
	private JButton getbtnConnect() {
		synchronized (btnConnect) {
			return btnConnect;
		}
	}
	
	private void setPartners(List<Partner> partners) {
		synchronized (this.partners) {
			this.partners = partners;
		}
	}
	
	private List<Partner> getPartners() {
		synchronized (this.partners) {
			return this.partners;
		}
	}
	
	private JTextField getMessageInput() {
		synchronized (messageInput) {
			return messageInput;
		}
	}
	
	private JTextField getClientName() {
		synchronized (clientName) {
			return clientName;
		}
	}
	
	private JTextField getServerAdress() {
		synchronized (serverAdress) {
			return serverAdress;
		}
	}
	
	@Override
	public void connectionEstablished(Partner[] partners) { //is called when the connection has been successfully established
		setPartners(new ArrayList<Partner>(Arrays.asList(partners)));
		getlblStatus().setText("Connected.");
		getMessageProvideBox().setText("");
	}
	
	@Override
	public void connectionFailed() { //yeah
		getlblErrorDisplay().setText("Connection failed.");
		getbtnConnect().setEnabled(true);
	}
	
	@Override
	public void disconnected() { //called EVERY TIME on thread exit
		getlblStatus().setText("Disconnected");
		getbtnConnect().setText("Connect");
		getClientList().removeAll();
		connection = null;
	}
	
	@Override
	public void setRequest(int request, long senderID, byte[] data) {
		switch (request) { //TODO implement the notifications
			case Request.SEND_TEXT:
				appendText("["  + getPartnerByID(senderID).getName() + "] " + new String(data), Color.BLACK);
				break;
			case Request.CLIENT_CONNECT_NOTIFICATION:
				appendText(new String(data) + " just logged in.", Color.RED);
				getPartners().add(new Partner(senderID, new String(data)));
				renderClients();
				break;
			case Request.CLIENT_DISCONNECT_NOTIFICATION:
				appendText(getPartnerByID(senderID).getName() + " just logged out.", Color.RED);
				getPartners().remove(getPartnerByID(senderID));
				renderClients();
				break;
		}
	}
	
	private void renderClients() {
		synchronized (clientList) {
			lm.removeAllElements();
		
			for (Partner partner : getPartners()) {
				lm.addElement(partner.getName());
			}
		}
	}
	
	private void appendText(String text, Color textColor) {
		synchronized (messageProvideBox) {
			StyleConstants.setForeground(style, textColor);
			try {
				mainMessages.insertString(mainMessages.getLength(), text + "\n", style);
				messageProvideBox.select(mainMessages.getLength(), mainMessages.getLength());
			} catch (BadLocationException e) {}
		}
	}
	
	private Partner getPartnerByID(long ID) {
		for (Partner partner : getPartners()) {
			if (partner.getID() == ID) return partner;
		}
		return null;
	}
	
	@Override
	public void setDisconnectRequest(int request) {
		String reason = "";
		
		switch (request) {
			case Request.UNEXPECTED_ERROR:
				reason = "Unexpected error.";
				break;
			case Request.SERVER_SHUTDOWN:
				reason = "Server shutdown.";
				break;
			case Request.KICKED_FROM_SERVER:
				reason = "You were kicked.";
				break;
			case Request.ILLEGAL_REQUEST:
				reason = "Illegal request (that is very bad)!";
				break;
			case Request.ILLEGAL_NAME:
				reason = "That nickname is not allowed.";
				break;
			default:
				break;
		}
		getlblErrorDisplay().setText(reason);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == getbtnConnect()) {
			if (connection == null) {
				connection = new Connection(keyPair, getClientName().getText(), getServerAdress().getText(), this);
				connection.start();
				getlblErrorDisplay().setText("");
				
				getbtnConnect().setText("Disconnect");
				getlblStatus().setText("Connecting...");
			} else {
				connection.disconnect();
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (connection != null && e.getKeyCode() == 10 && getMessageInput().getText().length() > 0){
			connection.sendData(Request.SEND_TEXT, getMessageInput().getText().getBytes());
			getMessageInput().setText("");
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowClosing(WindowEvent arg0) {
		if (connection != null) connection.disconnect();
		dispose();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}
}
