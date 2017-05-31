package messenger.server;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import org.nanohttpd.protocols.http.response.*;
import org.json.JSONObject;
import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.request.Method;
import org.nanohttpd.util.ServerRunner;
//import java.lang.reflect.Method;

public class MyHttpServer extends NanoHTTPD {
	private static final Logger LOG = Logger.getLogger(MyHttpServer.class.getName());
	private static Map<String,MyHttpHandler> getMap=new HashMap<>();
	private static Map<String,MyHttpHandler> postMap=new HashMap<>();
	public MyHttpServer() {
		super(8080);

// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args){
		ServerRunner.run(MyHttpServer.class);
	}
	/** used when starting the server from outside
	 * 
	 */
	public static void startMyServer(){
		ServerRunner.run(MyHttpServer.class);
	}
	
	/** addHandler add a handler to the appropriate callback HashMap for a specific URI
	 * 
	 * @param uri
	 * @param method
	 * @param handler
	 * @return true upon success
	 */
	public static boolean addHandler(String uri,String method,MyHttpHandler handler){
		if(method.equals("GET")){
			if(uri.contains("?") ||uri.contains("&")){
				return(false);
			}
			else{
				getMap.put(uri, handler);
				return(true);
			}
		}
		else if(method.equals("POST")){
			if(uri.contains("?") ||uri.contains("&")){
				return(false);
			}
			else{
				postMap.put(uri, handler);
				return(true);
			}
		}
		else{
			return(false);
		}
	}
	
	
	
	/**serve method receives each call and call the appropriate handler callback
	 * 
	 */
	@Override
	public Response serve(IHTTPSession session){
		Method method = session.getMethod();
        String uri = session.getUri();
        MyHttpServer.LOG.info(method + " '" + uri + "' ");

        //String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, List<String>> parms = session.getParameters();
        Map files = new HashMap<>();
        String result=null;
        if (Method.POST.equals(method)) {
                try {
                    session.parseBody(files);
                } catch (IOException ioe) {
                    return (Response.newFixedLengthResponse("Error"));
                } catch (ResponseException re) {
                    return Response.newFixedLengthResponse("Error");
                }
            
 
           try{
        	   JSONObject postJSON;
        	   //System.out.println("Before calling handler");
        	   
   
        	   
        	   postJSON=new JSONObject(files.get("postData").toString());
    
        	   result=postMap.get(uri).handle(uri, parms,new JSONObject(files.get("postData").toString()).toMap());
           }
           catch(Exception e){
        	   return(Response.newFixedLengthResponse("Not Found"));
           }
        }
        else if(Method.GET.equals(method)){
        	try{
         	   result=getMap.get(uri).handle(uri, parms, files);
            }
            catch(Exception e){
         	   return(Response.newFixedLengthResponse("Not Found"));
            }
        
        }
        else{
        	return(Response.newFixedLengthResponse(Status.NOT_FOUND, "None", "Not existent API or page"));
        }
        
        
        return Response.newFixedLengthResponse(result);
	}
}