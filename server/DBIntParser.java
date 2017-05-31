package messenger.server;

import java.sql.ResultSet;
/** DBIntParser implements DPParserInterface for SELECT return of a field with type int
 * 
 * @author vpanagiot
 *
 */
public class DBIntParser implements DBParserInterface {

	public DBIntParser() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String resultParse(ResultSet res, String field) {
		try{
			return (String.valueOf(res.getInt(field)));
		}
		catch(Exception e){
			return(null);
		}
	}

}
