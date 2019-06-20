package ChatVer4;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatServerThread extends Thread{
	private ChatServer server = null;
	private Socket socket = null;
	private DataInputStream strIn = null;
	private DataOutputStream strOut = null;
	private int ID = -1;
	
	public ChatServerThread(ChatServer _server, Socket _socket){
		super();
		server = _server;
		socket = _socket;
		ID = socket.getPort(); //becomes client's ID
		System.out.println("INFO: server= "+server+ " socket= "+socket+ " ID="+ID);
	}
	protected int getID(){
		return ID;
	}
	@Override
	public void run(){
		try{
			while(ID != -1){
				//getInput();
				//msg="pm_to:52341:Hi this is our secret"
				
				server.handle(ID, strIn.readUTF());//v4 get input and send out to clients using the server's handle
				//close();
			}
		}catch(IOException e){
			System.out.println("Exception running ChatServerThread "+e.getMessage());
		}
	}
	
	public void open() throws IOException{//step 3 open streams //strOut introduced in v4
		strIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		strOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		System.out.println("Opened Input & Outpu Streams Successfully");
	}

	public void send(String msg){//introduced in v4
		try {
			strOut.writeUTF(msg);
			strOut.flush();
		} catch (IOException e) {
			server.remove(ID); //on server call remove pass in my ID
			ID = -1; //set my ID to -1
		}
	}
	public void close() throws IOException{//step 5 close streams and sockets
		if(strIn!=null){
			strIn.close();
		}
		if(strOut != null){
			strOut.close();//introduced in v4
		}
		if(socket!=null){
				socket.close();
		}
	}
}
