package ChatVer4;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatClient implements Runnable{
	private Socket socket = null;
	private BufferedReader console= null;
	private DataOutputStream  strOut= null;
	private ChatClientThread client = null;
	private Thread thread = null;
	private String line = "";
	
	public ChatClient(String serverName, int serverPort){
		try {
			socket = new Socket(serverName, serverPort);//step 1 connect to server using Socket
			start();//step 2 open streams
			communicate();//step 3 communicate

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() throws IOException{//step 2 open streams
		console = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		strOut = new DataOutputStream(socket.getOutputStream());
		if(thread == null){
			client = new ChatClientThread(this, socket);
			thread = new Thread(this);
			thread.start();
		}
	}
	public void run(){
		while( (thread !=null) ){
			try{
				communicate();
			}catch(IOException e){
				System.out.println("Chat Client IO problem "
						+ "running thread to"
						+ " read line and send it");
			}
		}
	}
	public void communicate() throws IOException{
		do{
			line = console.readLine();
			strOut.writeUTF(line);
			strOut.flush();
		}while(!line.equalsIgnoreCase("bye"));
	}
	public void handle(String msg){
		if(msg.equalsIgnoreCase("bye")){
			line="bye";
			stop();
		}
		else{
			System.out.println(msg);
		}
		
	}
	public void stop() {
		try{
			if(console !=null){
				console.close();
			}
			if(strOut !=null){
				strOut.close();
			}
			if(socket !=null){
				socket.close();
			}
		}
		catch(IOException e){
			System.out.println("problem inside stop of "
					+ "ChatClient " + e.getMessage());
		}
	}
	
	public static void main(String [] args){
		if(args.length != 2){
			System.out.println("You need a hostname and a port number to connect your client to a server");
		}
		else{
			String serverName = args[0];
			int port = Integer.parseInt(args[1]);
			ChatClient client = new ChatClient(serverName, port);
		}
	}
	
	private class OneTimePad {

		private String plainMessage = "";
		private String encrMessage = "";
		private String keyMessage = "";
		

		public OneTimePad(){
			
		}
		public OneTimePad(String msg){
			plainMessage = msg;
			keyMessage = getKey();
			encrMessage = encrypt();
		}
		
		protected String getKey(){
			String key = "";
			for(int i=0; i<plainMessage.length(); i++){
				char randomChar = Character.toChars( 7 + (int)(Math.random() * 50))[0];
				key += randomChar;
			}
			return key;
		}
		
		protected String encrypt(){
			String encryptedMessage = "";
			for(int i=0; i<plainMessage.length(); i++){
				encryptedMessage += 
						Character.toChars((keyMessage.charAt(i) + plainMessage.charAt(i)))[0];
				//System.out.println("heheeh encrypted message is "+encryptedMessage);
			}
			return encryptedMessage;
		}
		protected String decrypt(){
			String decryptedMessage = "";
			for(int i=0; i<encrMessage.length(); i++){
				decryptedMessage += 
						Character.toChars((encrMessage.charAt(i)  -  keyMessage.charAt(i)))[0];
				//System.out.println("heheeh decrypted message is "+decryptedMessage);
			}
			return decryptedMessage;
		}
		
		
		
//		public static void main(String [] args){
//			OneTimePad otp = new OneTimePad("abcdefghijklmnopqrstuvwxyz");
//			System.out.println("THE ENC MESSAGE IS "+ otp.encrMessage);
//			System.out.println("THE KEY  IS "+ otp.keyMessage);
//			System.out.println("THE DEC MESSAGE IS "+ otp.plainMessage);
//			
			
			
		}
		
		
		
		
		
		
		
		
		
	}
