package ChatVer4;

public class OneTimePad {

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
	
	
	
	public static void main(String [] args){
		OneTimePad otp = new OneTimePad("abcdefghijklmnopqrstuvwxyz");
		System.out.println("THE ENC MESSAGE IS "+ otp.encrMessage);
		System.out.println("THE KEY  IS "+ otp.keyMessage);
		System.out.println("THE DEC MESSAGE IS "+ otp.plainMessage);
		
		
		
	}
	
	
	
	
	
	
	
	
	
}
