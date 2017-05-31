package messenger.server;

import java.sql.ResultSet;

public class DBDoubleParser implements DBParserInterface {

	public DBDoubleParser() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String resultParse(ResultSet res, String field) {
		try{
			return (String.valueOf(res.getDouble(field)));
		}
		catch(Exception e){
			return(null);
		}
	}

}
