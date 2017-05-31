package messenger.server;
import java.util.Map;
import java.util.List;
/**It is the interface called by the serve method of MyHttpServer to handle a specific URI call
 * 
 * @author vpanagiot
 *
 */
public interface MyHttpHandler {
	public String handle(String uri,Map<String,List<String>> getMap,Map<String,Object> postMap);
}
