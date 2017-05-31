package messenger.client;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;


public class ClientTest {

	public ClientTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stu
		String result="";
		Map getMap=new HashMap<>();
		getMap.put("username", "Vasilis");
		//getMap.put("field2", "value2");
		//HttpCommunication HttpCom= new HttpCommunication("http://127.0.0.1:8080");
		//result=HttpCom.doGetHttp("/api/friendlist/dfg",getMap);
		System.out.print(result);
		String test="{\"password\":\"Vasilis\",\"state\":\"null\",\"username\":\"Vasilis\"}";
		JSONObject myJSON=new JSONObject(test);
		System.out.println(test);
		getMap=myJSON.toMap();
		System.out.print(getMap.keySet().toString());
	}

}
