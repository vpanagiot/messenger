package messenger.client;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import messenger.server.*;
public class ClientMessage extends GenericMessage implements Serializable {
	private boolean sent; //True if sent, false if received
	static SimpleDateFormat ft =  new SimpleDateFormat ("yyyy.MM.dd,HH:mm:ss");
	
	/**This is invoked for incoming messages or for outgoing that are loaded from history call
	 * 
	 * @param message the message that is tranferred
	 * @param sent    true if sent, false if received 
	 * @param sendDate the date that the message was sent
	 */
	public ClientMessage(String message,boolean sent,Date sendDate) {
		super(message,sendDate,true); //check here later
		this.sent=sent;				// TODO Auto-generated constructor stub
	}
	
	public ClientMessage(String message,boolean sent,Date sendDate,boolean read) {
		super(message,sendDate,read); //check here later
		this.sent=sent;				// TODO Auto-generated constructor stub
	}
	
	public ClientMessage(String message,boolean sent,String sendDate,boolean read) {	
		super(message,sendDate,read); //check here later
		this.sent=sent;				// TODO Auto-generated constructor stub
	}
	/**This is invoked only in the case of outgoing message
	 * 
	 */
	public ClientMessage(String message){
		super(message);
		this.sent=true;
	}
	
	/** returns if the message is sent or received
	 * 
	 * @return true if sent
	 */
	public boolean getSent(){
		return(sent);
	}

}
