package messenger.server;

import java.sql.ResultSet;

public class DBTextParser implements DBParserInterface {

	public DBTextParser() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String resultParse(ResultSet res, String field) {
		try{
			return ((res.getString(field)));
		}
		catch(Exception e){
			return(null);
		}
	}

}
