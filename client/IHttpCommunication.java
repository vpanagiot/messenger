package messenger.client;

import java.util.Map;

public interface IHttpCommunication {
	public String doGetHttp(String url2, Map<String,String> extras);
	public String doPostHttp(String url2, Map<String,String> extras);
}
