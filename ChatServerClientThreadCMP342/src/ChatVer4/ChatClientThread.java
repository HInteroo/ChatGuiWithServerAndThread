package ChatVer4;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClientThread extends Thread{

	private Socket socket = null;
	private ChatClient client = null;
	private DataInputStream strIn = null;
	private boolean done = true;
	
	public ChatClientThread(ChatClient _client, Socket _socket){
		client = _client;
		socket = _socket;
		open();
		start();//start this thread so it can call run
	}
	
	public void open(){
		try {
			strIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		} catch (IOException e) {
			System.out.println("Inside ChatClientThread Problem opening Stream In "+e.getMessage());
		}
	}
	
	public void run(){
		done = false;
			while(!done){
			try{
				client.handle(strIn.readUTF());
			}
			catch(IOException e){
				close();
				client.stop();
				System.out.println("Err in ChatClientThread "
						+ "couldn't handle Input off of strIn");
				done = true;
			}
		}
	}
	public void close(){
			try {
				if(strIn !=null){
					strIn.close();
				}
				if(socket !=null){
					socket.close();
				}
			}catch (IOException e) {
				System.out.println("In ChatClientThread closing streams problem " );
			}
		}
	
	
	
	
}
