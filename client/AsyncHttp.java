package messenger.client;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

import javax.swing.SwingWorker;

import org.json.JSONObject;
/** AsyncHttp is a class that implements asynchronous restful api 
 * it extends SwingWorker
 * @author vpanagiot
 *
 */
public class AsyncHttp extends SwingWorker<Map, Object> {
	private String url;
	private String apiCall;
	private String method;
	private Map<String,String> parameterMap;
	//private AsyncHttp curAsyncHttp;

	private AsyncHttp(String method,String url,String apiCall,Map<String,String> parameterMap, BiConsumer<Integer,Map> callBack){
		this.url=url;
		this.apiCall=apiCall;
		this.method=method;
		this.parameterMap=parameterMap;
		//this.curAsyncHttp=this;
		this.addPropertyChangeListener(new PropertyChangeListener(){
			public  void propertyChange(PropertyChangeEvent evt) {
				
            if (("state".equals(evt.getPropertyName()))&&("DONE".equals(evt.getNewValue().toString()))) {
            	try{
            		Map retData=get();
                	callBack.accept((Integer)retData.get("state"),(Map)retData.get("data"));
            	}
            	catch(InterruptedException | ExecutionException e){
            		
            		callBack.accept(-1, null); 
            	
            		
            	}
            	
            	
            }
        }
			
		});
	}
	
	public static AsyncHttp asyncHttpCall(String method,String url,String apiCall,Map<String,String> parameterMap, BiConsumer<Integer,Map> callBack)
	{
		AsyncHttp newAsync=new AsyncHttp(method,url,apiCall,parameterMap,callBack);
		newAsync.execute();
		return(newAsync);
	}
	
	
	
	@Override
	public Map doInBackground(){
		HttpCommunication newHTTP=new HttpCommunication(url);
		Map retData=new HashMap();
		if (method.equals("GET")){
		    String recData=newHTTP.doGetHttp(apiCall, parameterMap);
		    if (recData==null){
		    	retData.put("state", new Integer(-1));
				retData.put("data", null);
		    }
		    else{
		    	JSONObject theData=new JSONObject(recData);
		    	Map auxMap=theData.toMap();
		    	retData.put("state",Integer.parseInt((String)auxMap.get("state")));
		    	retData.put("data", auxMap.get("data") );
		    }
		}
		else if(method.equals("POST")){
			String recData=newHTTP.doPostHttp(apiCall, parameterMap);
			
		    if (recData==null){
		    	retData.put("state", new Integer(-1));
				retData.put("data", null);
		    }
		    else{
		    	JSONObject theData=new JSONObject(recData.toString());
		    	Map auxMap=theData.toMap();
		    	retData.put("state",(int)Integer.parseInt((String)auxMap.get("state")));
		    	retData.put("data", auxMap);
		    	
		    	

		    }
		}
		else{
			retData.put("state", new Integer(-1));
			retData.put("data", null);
		}
		
		return retData;
		
	}

}
