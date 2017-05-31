package messenger.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
		private String sender;
		private String receiver;
		private Date date;
		private String message;
		private boolean read;
		
		public Message(String message,String receiver,String sender){
			this.receiver=receiver;
			this.sender=sender;
			date=new Date();
			this.message=message;
			read=false;
		}
		
		public Message(String message,String receiver,String sender, String senddate,boolean readStatus){
			this.receiver=receiver;
			this.sender=sender;
			SimpleDateFormat ft = 
				      new SimpleDateFormat ("yyyy.MM.dd,hh:mm:ss");
			try{
			date=ft.parse(senddate);
			}
			catch(ParseException e){
				date=new Date();
			}
			this.message=message;
			read=readStatus;
		}
		public String getSender() {
			return sender;
		}

		public String getReceiver() {
			return receiver;
		}

		public Message(String message,Date date){
			this.date=date;
			this.message=message;
			read=false;
		}
		
		public String getDate(){
			SimpleDateFormat ft = 
				      new SimpleDateFormat ("yyyy.MM.dd,hh:mm:ss");
			return(ft.format(date));
		}
		public String getMessage(){
			return(message);
		}
		
		public boolean getReadState(){
			return(read);
		}
		
		public void setReadState(){
			read=true;
		}
		
		public String toString(){
			String data="MESSAGE    :\n"+
						"Sender     :"+sender+"\n"+
						"Receiver   :"+receiver+"\n"+
						"message    :"+message+"\n"+
						"Send Date  :"+getDate()+"\n"+
						"Read Status:"+((read)?"Read \n":"Unread \n");
			return data;
		}
}


