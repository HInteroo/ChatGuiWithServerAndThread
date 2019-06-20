package ChatVer4;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatGUI extends JFrame{

	public ChatGUI(String serverName, int serverPort){
		ChatPanel chatPanel = new ChatPanel(serverName, serverPort);
		add(chatPanel);
	}
	
	private class ChatPanel extends JPanel implements Runnable, ActionListener{
		private Socket socket = null; 				
		private DataInputStream streamIn = null;		
		private DataOutputStream streamOut = null;	
		boolean done = false;							
		private String _serverName;					
		private int _serverPort;						
		private JTextArea displayArea;				
		private JTextField tInput, t_priv_ID_Input;
		private String[] jbtnNames = {"SEND","SEND PRIV", "SEND PRIV ENCR", "CONNECT", "DISCONNECT"};
		private JButton [] jbtns = new JButton[jbtnNames.length];
		private final int BTN_INDEX_SEND = 0;
		private final int BTN_INDEX_SEND_PRIV = 1;
		private final int BTN_INDEX_SEND_PRIV_ENCR = 2;
		private final int BTN_INDEX_CONNECT = 3;
		private final int BTN_INDEX_DISCONNECT = 4;
		private final String ENC_INDICATOR = "encr-";
		private String 	decrMsg,eMsg,EnMsg;
		private OneTimePad otp = new OneTimePad();

		
		public ChatPanel(String serverName, int serverPort){
			JLabel MsgLabel = new JLabel("Message: ");
			JLabel PMsgLabel = new JLabel("Priv ID: ");

			_serverName = serverName;							
			_serverPort = serverPort;	
			tInput = new JTextField (10);
			t_priv_ID_Input = new JTextField (1);
			
			setLayout(new BorderLayout());
			displayArea = new JTextArea();
			add(displayArea, BorderLayout.CENTER);
			
			//fill this in
			
			JPanel bottomPanel = new JPanel();
			bottomPanel.setLayout(new GridLayout(2,1));
			JPanel btm1Panel = new JPanel();
			btm1Panel.setLayout(new GridLayout(1,3));
			btm1Panel.add(MsgLabel,BorderLayout.WEST);
			btm1Panel.add(tInput,BorderLayout.EAST);
			
			btm1Panel.add(PMsgLabel,BorderLayout.EAST);
			btm1Panel.add(t_priv_ID_Input,BorderLayout.EAST);
			//fill this in
			
			JPanel btn2Panel = new JPanel();
			btn2Panel.setLayout(new GridLayout(3,2));
			for(int i=0; i<jbtns.length; i++){
				jbtns[i] = new JButton(jbtnNames[i]);
				jbtns[i].addActionListener(this);
				jbtns[i].setEnabled(false);
				btn2Panel.add(jbtns[i]);
			}
			jbtns[BTN_INDEX_CONNECT].setEnabled(true);

			//fill this in
			bottomPanel.add(btm1Panel);
			bottomPanel.add(btn2Panel);
			add(bottomPanel, BorderLayout.SOUTH);

			//fill this in
			
		}
		@Override
		public void run() {	
			while(!done){
				try{
					String line = streamIn.readUTF();
					updateDisplay(line);
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		public void connect(){	
			String conStatus="Connected to server "+_serverName + " on port: "+ _serverPort+"\n";
			try {
				socket = new Socket(_serverName, _serverPort);
				open();
				enableButtons();
			} catch (UnknownHostException e) {
				conStatus = e.getMessage();
			} catch (IOException e) {
				conStatus = e.getMessage();   
			}
			finally{
				updateDisplay(conStatus);
			}
		}
		public void disconnect(){						
			disableButtons();
			send("bye");
			done=true;
		}
		public void open(){							
			try{
				streamOut = new DataOutputStream(socket.getOutputStream());
				streamIn = new DataInputStream(socket.getInputStream());
				new Thread(this).start();//to be able to listen in
			}catch(IOException e){
				e.printStackTrace();
			}
		}

		public void enableButtons(){
			for(int i=0; i<jbtns.length; i++){					
				jbtns[i].setEnabled(true);
			}
			jbtns[BTN_INDEX_CONNECT].setEnabled(false);

		}
		public void disableButtons(){
			for(int i=0; i<jbtns.length; i++){					
				jbtns[i].setEnabled(false);
			}
			jbtns[BTN_INDEX_CONNECT].setEnabled(true);
		}
		
		public void send(String msg){						
				try {
					streamOut.writeUTF(msg);
					streamOut.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		public void sendPrivate(String ID, String msg){	
			String s = "pm:"+ID+msg;
			send(s);
		}
		public void sendPrivateEncr(String ID, String msg){
			/*
			ENCRYPT THE MESSAGE BEFORE SENDING IT
			*/
			otp = new OneTimePad(msg);
			eMsg = otp.encrypt();
			EnMsg = eMsg;
			System.out.println("WILL SEND: User "+ID+" :" +ENC_INDICATOR  + EnMsg);
			send(EnMsg);
		}
		private String decrypt(String text){
			if(eMsg.length()>0) {
				System.out.println("msgEncAndKey= encryped message:"+eMsg+"\n\tKey:"+otp.getKey());
				decrMsg = otp.decrypt();
				return decrMsg;
			}
			return "";
		}
		public void updateDisplay(String text){
			if(text.contains(ENC_INDICATOR)){
				String plainMsg = decrypt(text);
				displayArea.append("\n"+text.substring(0, text.indexOf(ENC_INDICATOR))+plainMsg);
			}
			else{
				displayArea.append("\n"+text);//plain incoming message
			}
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			//fill this in
			JButton btnClicked = (JButton) e.getSource();
			if(btnClicked.equals(jbtns[BTN_INDEX_CONNECT])){
				connect();
			}
			else if(btnClicked.equals(jbtns[BTN_INDEX_DISCONNECT])){
				disconnect();
			}
			else if(btnClicked.equals(jbtns[BTN_INDEX_SEND])){
				String msg = tInput.getText();
				send(msg);
			}
			else if(btnClicked.equals(jbtns[BTN_INDEX_SEND_PRIV])){
				String msg = tInput.getText();
				String ID = t_priv_ID_Input.getText();
				sendPrivate(ID, msg);
			}
			else if(btnClicked.equals(jbtns[BTN_INDEX_SEND_PRIV_ENCR])){
				String msg = tInput.getText();
				String ID = t_priv_ID_Input.getText();
				sendPrivateEncr(ID, msg);
			}
			//fill this in
		}
	}
	public static void main(String[] args) {
		
		ChatGUI gui = new ChatGUI(args[0], Integer.parseInt(args[1]));
		gui.setSize(500, 500);
		gui.setDefaultCloseOperation(EXIT_ON_CLOSE);
		gui.setVisible(true);	
	}

}