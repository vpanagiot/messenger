package messenger.server;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GenericMessage {
	protected Date date;
	protected String message;
	protected boolean read;
	static SimpleDateFormat ft =  new SimpleDateFormat ("yyyy.MM.dd,hh:mm:ss");
	
	public GenericMessage(String message){
		this.message=message;
		date=new Date();
		read=false;
	}
	
	public GenericMessage(String message,Date date,boolean read){
		this.message=message;
		this.date=date;
		this.read=read;
	}
	
	public GenericMessage(String message,String date,boolean read){
		this.message=message;
		try{
		this.date=ft.parse(date);
		}
		catch(Exception e){
			this.date=new Date();
			System.err.println("Exception on parsing date");
		}
		this.read=read;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public Date getDate() {
		return date;
	}

	public String getMessage() {
		return message;
	}
}
