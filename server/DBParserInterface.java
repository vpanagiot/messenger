package messenger.server;
import java.sql.*;
/** DBParserInterface implements functions that are used for parsing every data types in databases
 *  
 * @author vpanagiot
 *
 */
public interface DBParserInterface {
	public String resultParse(ResultSet res ,String field);

}
