package ChatVer4;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*ChatServer Version 4
 * Accept a client just like in server version 0,1
 * and print what the client said
 * new in version 1
 * repeat as long as the client does not say "bye"
 * new in version 2
 * prepare to accept another client ... use a new thread
 * new in version 3
 * handle multiple clients simultaneously
 * new in version 4
 * sent output from each client to ALL connected clients
 */
/**
 * FOR HOMEWORK
 * 1 - instead of sending the same message back to the client who sent it
 * identify the client and send him/her "You said: " +msg
 * 
 * 2 - change that... don't send the initiator of the message anything
 * 
 * For 1 & 2 send the message as it was including "User "+ID+ " said " + msg
 *
 *Play around ... send the message to every other user only
 *
 */
public class ChatServer implements Runnable{
	private ServerSocket server = null;
	private Thread thread = null;
	private ChatServerThread [] clients = new ChatServerThread[50];
	private int clientCount = 0;
	
	public ChatServer(int port){//same as previous version
		try {
			System.out.println("Will start server on port "+port);
			server = new ServerSocket(port);//step 1 create a Server Socket remember to pass in the port
			start();
		} catch (IOException e) {
			System.err.println("My Server does not work "+ e.getMessage());
		}
	}
	public void start(){//same as previous version
		if(thread == null){
			thread = new Thread(this);
			thread.start();
		}
	}
	@Override
	public void run() {//same as previous version
		while(thread!=null){
			try{
				System.out.println("Will wait for a client to connect");
				//add a thread and accept a client on it... ChatServerThread
				addThread(server.accept());
				
			}catch (IOException e) {
				System.err.println("YIKES!!! "+ e.getMessage());
			}
		}
	}

	public synchronized void handle(int ID, String msg){//introduced in v4
		//msg="pm:52341:Hi this is our secret"
		//if private send only to the recipient... example 52341
		//private encr send only to the recipient... with the encr marker
		
		//if you want to spy you can steal the encr algorithm/key and although
		//you send the encr.. you can print the decrypted for example
		String privatemsg = "";
		String UserN = "User: "+ID+" said: ";
		if (msg.length()>8) {
			privatemsg = msg.substring(0, 8);
		}
		for(int i=0; i<clientCount; i++){
			if(clients[i].getID() == ID){
				if(msg.equalsIgnoreCase("bye")){
					remove(ID);//disconnect the client.. remove from the array
				}
				else if(privatemsg.contains("pm:")){
					
					clients[i].send("YOU PM'd to "+msg.substring(3, 8)+": "+msg.substring(8));
					clients[findClient(Integer.parseInt(msg.substring(3, 8)))].send("User "+clients[i].getID()+" PM'd you: "+msg.substring(8));
				}
				else{
					clients[i].send("YOU SAID: "+msg);
				}
			}
			else if(!privatemsg.contains("pm:")){
				System.out.println("will send to "+ clients[i].getID() + " with msg= "+msg);
				clients[i].send(UserN+ msg);
			}
		}
	}
	
	public synchronized void remove(int ID){//v4
		int loc = findClient(ID);//0
		if(loc >= 0 && loc < clientCount){
			ChatServerThread tempToClose = clients[loc];//clients[loc].close();
			for(int i = loc+1; (i <= clientCount && i < clients.length); i++){
				clients[i-1] = clients[i];//shift all from right to left
			}
			if(loc == clients.length-1){//special circumstance last index loc
				clients[loc] = null;
			}
			clientCount--;
			System.out.println("removed "+ID+" from index location "+loc);
			try {
				tempToClose.close();//end connections ... io streams and ChatServerThread
				System.out.println("closed streams on "+tempToClose.getId() );
			} catch (IOException e) {
				System.out.println("Problem removing client "+e.getMessage());
			}
		}
	}
	private synchronized int findClient(int ID){
		for(int i=0; i<clientCount; i++){
			if(clients[i].getID() == ID){
				return i;//location of the client 
			}
		}
		return -1; //client not in the array
	}
	
	public synchronized void addThread(Socket socket){//similar but using the array in v4
		if( clientCount < clients.length ){
			clients[clientCount] = new ChatServerThread(this, socket);
			try{
				clients[clientCount].open();//open stream for ChatServerThread to handle input
				clients[clientCount].start();//start running ChatServerThread to handle the client
				clientCount++; //increment count
			}catch (IOException e) {
				System.err.println("Exception in addThread of ChatServer"+ e.getMessage());
			}
		}
		else{
			System.out.println("Client refused max num of clients is "+clients.length);
		}
	}

	public static void main(String [] args){
		if(args.length != 1){
			System.out.println("You need a port number to run your server");
		}
		else{
			int port = Integer.parseInt(args[0]);
			ChatServer myServer = new ChatServer(port);
		}
	}
	
}