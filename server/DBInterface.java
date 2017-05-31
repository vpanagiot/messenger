package messenger.server;
import java.sql.*;
import java.util.*;

public class DBInterface {
/** class dbInterface is the class used to communicate with the database
 * 
 */
	//member variables follow/////////////////////////////////////////////////
	private String dbType=null; 
	/* dbtype indicates the database type (for now "sqlite" only)*/
	private String dbFile=null;
	/* dbfile stores the filename of the database */
	private Map<String,DBTable> tableMap=new HashMap<>();
	//////////////////////////////////////////////////////////////////////////
	public DBInterface(String type,String dbName) {
		if(type.equals("sqlite")){
			this.dbType="sqlite";
			Connection c = null;
		    Statement stmt = null;
		    try {
		      Class.forName("org.sqlite.JDBC");
		      c = DriverManager.getConnection("jdbc:sqlite:"+dbName); //checking of dbname validity may be needed
		      this.dbFile=dbName;
		      c.close();
		    } catch ( Exception e ) {
		      System.exit(0);
		    }
		  
		}
	}
	
	/** dbGetType gets a string that describes column type and checks if it is valid and returns the right
	 * representation for the dbType of the instance
	 * @param columnType
	 * @return
	 */
	private String dbGetType(String columnType){
		if(dbType.equals("sqlite")){
			switch(columnType){
			case "int":return("INT");
			case "float":return("REAL");
			case "key":return("INT PRIMARY KEY");
			case "text":return("TEXT");
			default:return(null);
			}
		}
		return(null);
	}
	
	/** dbCheckString checks if the string contains illegal characters
	 * 
	 * @param str
	 * @return :true if string is valid
	 */
	private boolean dbCheckString(String str){
		return(!str.matches("(.)*(\W)+(.)*"));
	}
	
	/** dBCreateTable creates a table in the database
	 * 
	 * @param tableName   : is the name of the table to be created
	 * @param columnNames : is the array of the column names
	 * @param columnTypes :is the array of the column types
	 * @return            :true upon succesful creation
	 */
	public boolean dbCreateTable(String tableName,String[] columnNames, String[] columnTypes){
		Connection c=null;
		Statement stmt=null;
		String primaryKeyName=null;
		//validity of input checking block
		String[] finalColumnTypes=new String[columnTypes.length];
		if(!dbCheckString(tableName)){
			return(false);
		}
		for(String i:columnNames){
			if(!dbCheckString(i)){
				return(false);
			}
		}
		int counter=0;
		String strAux;
		for(String i:columnTypes){
			if(!dbCheckString(i)){
				return(false);
			}
			else if((strAux=dbGetType(i))!=null){
				finalColumnTypes[counter]=strAux;
				if(strAux.equals("INT PRIMARY KEY")){
					primaryKeyName=columnNames[counter];
				}
				counter++;
				
			}
			else{
				return(false);
			}
		}
		if(columnNames.length!=columnTypes.length){
			return(false);
		}
		if(primaryKeyName==null){
			return(false);
		}
		/////////////////////////////////
		try {
			Class.forName("org.sqlite.JDBC");
			c=DriverManager.getConnection("jdbc:sqlite:"+dbFile);
			stmt=c.createStatement();
			
			String tCreationStmt="CREATE TABLE"+ tableName+  "(";
			for(int i=0;i<columnNames.length-1;i++){
				tCreationStmt+=columnNames[i]+" "+finalColumnTypes[i]+", ";
			}
			tCreationStmt+=columnNames[columnNames.length-1]+" "+finalColumnTypes[columnNames.length-1]+")";
			stmt.executeUpdate(tCreationStmt);
		    stmt.close();
		    c.close();
		    tableMap.put(tableName,new DBTable(tableName,primaryKeyName,0,columnNames.length,columnNames,finalColumnTypes));
		    return(true);
		}
		catch(Exception e){
			return(false);
		}
	}
	
	private boolean statementTypeParser(String str,int i, String columnType,PreparedStatement stmt){
		try{
			switch(columnType){
			case "TEXT": stmt.setString(i, str);
						 return(true);
			case "INT":  stmt.setInt(i, Integer.parseInt(str));
						 return(true);
			case "REAL": stmt.setDouble(i, Double.parseDouble(str));
			  			 return(true);
			case "INT PRIMARY KEY":stmt.setInt(i, Integer.parseInt(str));
			 						return(true);
			default: return(false);
			}
		}
		catch(Exception e){
			return(false);
		}
		
	}
	
	/**dbNewRecord inserts a new record in a table
	 * 
	 * @param tableName  :the name of the table to be appended
	 * @param fieldValues : the list of values that are constitute the new row
	 * @return true if succeded
	 */
	public boolean dbNewRecord(String tableName,String[] fieldValues){
		Connection c=null;
		PreparedStatement stmt=null;
		//validity of input checking block
		if(!dbCheckString(tableName)){
			return(false);
		}
		
		/////////////////////////////////
		try {
			Class.forName("org.sqlite.JDBC");
			c=DriverManager.getConnection("jdbc:sqlite:"+dbFile);
			DBTable curTable=tableMap.get(tableName);
			
			String tCreationStmt="INSERT INTO"+ tableName+  "("+ tableMap.get(tableName).getColumnNamesString()+") VALUES (";
			String[] columnTypes=tableMap.get(tableName).getColumnTypes();
			if(columnTypes.length!=fieldValues.length-1){
				c.close();
				return(false);
			}
			tCreationStmt+=curTable.getMaxKey()+1+", ";
			for(int i=0;i<columnTypes.length-2;i++){
				tCreationStmt+="?,";
			}
			tCreationStmt+="?)";
			stmt=c.prepareStatement(tCreationStmt);
			for(int i=1;i<columnTypes.length;i++){
				if(!statementTypeParser(fieldValues[i-1],i+1,columnTypes[i],stmt)){
					return(false);
				}
				
			}
			stmt.executeUpdate(tCreationStmt);
		    stmt.close();
		    c.close();
		    return(true);
		}
		catch(Exception e){
			return(false);
		}
	}
	
	/** dbSelectEqual queries the table for the up to  limit last records with Value(fieldName)=fieldValue
	 * 
	 * @param tableName
	 * @param fields
	 * @param fieldName
	 * @param fieldValue :potentially unsafe has to go through parser
	 * @param limit
	 */
	public List dbSelectEqual(String tableName,String[] fields,String fieldName,String fieldValue,int limit){
		Connection c=null;
		PreparedStatement stmt=null;
		//validity of input checking block
		if(!dbCheckString(tableName)){
			return(null);
		}		
		/////////////////////////////////
		try{
			c=DriverManager.getConnection("jdbc:sqlite:"+dbFile);
			String creationStmt="";
			if(fields==null){
				creationStmt+="SELECT * FROM" +tableName;
			}
			else{
				creationStmt+="SELECT (";
				for(int i=0;i<fields.length-1;i++){
					creationStmt+=fields[i]+", ";
				}
				creationStmt+=fields[fields.length]+")";
			}
			if(fieldName!=null){
				creationStmt+="WHERE "+fieldName+"=? LIMIT="+limit;
			}
			stmt=c.prepareStatement(creationStmt);
			DBTable curTable=tableMap.get(tableName);
			String[] tCNames=curTable.getColumnNames();
			String curType=null;
			int i=0;
			while(i<tCNames.length){
				if(tCNames[i].equals(fieldValue)){
					curType=curTable.getColumnTypes()[i];
					break;
				}
				i++;
			}
			if(curType==null){
				return(null);
			}
			if(!statementTypeParser(fieldValue,1,curType,stmt)){
				return(null);
			}
			ResultSet res=stmt.executeQuery();
			List<Map> resList=new ArrayList<>();
			Map<String,DBParserInterface> columnMap=curTable.getColumnsMap();
			Map<String,String> curMap=new HashMap<>();
			while(res.next()){
				for(String curfield:fields){
					curMap.put(curfield,columnMap.get(curfield).resultParse(res, curfield));
				}
				resList.add(curMap);
			}
			stmt.close();
			res.close();
			c.close();
			return(resList);
		}
		catch(Exception e){
			return(null);
		}
		
	}
	
   

}
