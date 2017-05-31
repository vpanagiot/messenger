package messenger.server;

import java.util.*;

/**
 * DBTable class instances contain information about tables of SQL databases
 * @author vpanagiot
 *
 */
public class DBTable {
	//Variables//////////////////////////////////////////////////////////////////////////
	private String tableName;
	private String primaryKeyName;
	private int maxPrimaryKey;
	private int columnsNumber;
	private String[] columnNames;
	private String[] columnTypes;
	private Map<String,DBParserInterface> parsers=new HashMap<>();
	/////////////////////////////////////////////////////////////////////////////////////
	private void createMap(){
		for(int i=0;i<columnNames.length;i++){
			switch(columnTypes[i]){
			case "INT":parsers.put(columnNames[i], new DBIntParser());
						break;
			case "TEXT":parsers.put(columnNames[i], new DBTextParser());
						break;
			case "REAL":parsers.put(columnNames[i], new DBDoubleParser());
						break;
			case "INT PRIMARY KEY":parsers.put(columnNames[i], new DBIntParser());
						break;
			default:parsers.put(columnNames[i], null);
					break;
			}
		}
	}
	
	public DBTable(String tableName,String primaryKeyName,int maxPrimaryKey,int columnsNumber,String[] columnNames,String[] columnTypes) {
		this.tableName=tableName;
		this.primaryKeyName=primaryKeyName;
		this.maxPrimaryKey=maxPrimaryKey;
		this.columnsNumber=columnsNumber;
		this.columnNames=columnNames;
		this.columnTypes=columnTypes;
		createMap();
	}
	
	/**getMaxKey returns the maxPrimaryKey
	 * 
	 * @return
	 */
	public int getMaxKey(){
		return(maxPrimaryKey);
	}
	
	public String getColumnNamesString(){
		String str="";
		for(int i=0;i<columnNames.length-1;i++){
			str+=columnNames[i]+", ";
		}
		str+=columnNames[columnNames.length-1];
		return(str);
	}
	
	public String[] getColumnTypes(){
		return(columnTypes);
	}
	
	public String[] getColumnNames(){
		return(columnNames);
	}
	
	public Map<String,DBParserInterface> getColumnsMap(){
		return(parsers);
	}
	

}
