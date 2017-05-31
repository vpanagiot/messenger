package messenger.client;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

/** This class implements client-side HTTP communication
 * 
 * @author vpanagiot
 *
 */
public class HttpCommunication {
	private String serverUrl;
	private String fullUrl; //defined for scope reasons used where needed
	
    
	public HttpCommunication(String str) {
		serverUrl=str;
	}
	
	private String UrlEncode(String inUrl, Map<String,String> extras) throws Exception{
		fullUrl =serverUrl+inUrl;
		if(extras!=null){
		fullUrl+="?";
			try{
				extras.forEach((String key,String value)->fullUrl+=key+"="+value+"&");
				fullUrl=fullUrl.substring(0, fullUrl.length()-1);
				return(fullUrl);
			}
			catch(Exception e){
				throw e;
			}
		}
		return(fullUrl);
		
	}
	
	public String doGetHttp(String url2, Map<String,String> extras){
		BufferedReader reader = null;
		String data="";
		try{
			  
			  URL conUrl=new URL(UrlEncode(url2,extras));
		      HttpURLConnection con = (HttpURLConnection) conUrl.openConnection();
		      con.setRequestMethod("GET");
		      
		      //connection.setDoOutput(true);
		      
		      // give it 15 seconds to respond
		      con.setReadTimeout(3*1000);
		      con.connect();

		      // read the output from the server
		      reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		      while(reader.ready()){
		    	  data+=reader.readLine();
		      }
		      reader.close();
		      return(data);
		}
		catch(Exception e){
			e.printStackTrace();
			return(null);
		}
	}
	
	public String doPostHttp(String url2, Map<String,String> extras){
		BufferedReader reader = null;
		String data="";
		String postData;
		try{
			  if(extras==null){
				  return(null);
			  }
			  JSONObject postDataO=new JSONObject(extras);
			  postData=postDataO.toString();
			  
			  URL conUrl=new URL(UrlEncode(url2,null));
		      HttpURLConnection con = (HttpURLConnection) conUrl.openConnection();
		      con.setRequestMethod("POST");
		      con.setDoOutput(true);
		      con.setRequestProperty("Content-Type", 
		              "application/json;charset=utf-8");
		      con.setRequestProperty("Content-Length", "" + 
		                  Integer.toString(postData.length()));
		      con.setRequestProperty("Content-Language", "en-US"); 	
		      con.setUseCaches (false);
		      con.setDoInput(true);
		      
		      // give it 15 seconds to respond
		      con.setReadTimeout(3*1000);
		      
		      OutputStreamWriter postStream=new OutputStreamWriter(con.getOutputStream());
		      postStream.write(postData);
		      postStream.flush();
		      postStream.close();
		      int responseCode = con.getResponseCode();
		      // read the output from the server
		      reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		      while(reader.ready()){
		    	  data+=reader.readLine();
		      }
		      reader.close();
		      
		      return(data);
		}
		catch(Exception e){
			return(null);
		}
	}
}
