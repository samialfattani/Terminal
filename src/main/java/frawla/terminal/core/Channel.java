package frawla.terminal.core;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Channel implements Externalizable
{
	private static final long serialVersionUID = 286747378942995224L;
	public static final String DBMS_MYSQL = "MYSQL";
	public static final String DBMS_ORACLE = "ORACLE";
	public static final String DBMS_SQLITE = "SQLITE";
	public static final String DBMS_MSACCESS = "MSACCESS";
	public static final String DBMS_SQLSERVER = "SQLSERVER";
	public static final String DBMS_DB2 = "DB2";
	public static final String DBMS_CSV = "CSV";
	public static final String DBMS_POSTGRESQL = "POSTGRESQL";

	public static ObservableList<String> getListOfDBMSs()
	{
		ObservableList<String> DBMSs = FXCollections.observableList(new ArrayList<>());
		DBMSs.add(DBMS_MYSQL);
		DBMSs.add(DBMS_ORACLE);
		DBMSs.add(DBMS_SQLITE);
		DBMSs.add(DBMS_MSACCESS);
		DBMSs.add(DBMS_SQLSERVER);
		DBMSs.add(DBMS_DB2);
		DBMSs.add(DBMS_CSV);
		DBMSs.add(DBMS_POSTGRESQL);
		
		return DBMSs;
	}
	//---------------------------------

	transient private StringProperty name = new SimpleStringProperty(this, "", "");
	transient private StringProperty DBMS = new SimpleStringProperty(this, "", "");
	transient private StringProperty connectionString = new SimpleStringProperty(this, "", "");
	transient private StringProperty userName = new SimpleStringProperty(this, "", "");
	transient private StringProperty password = new SimpleStringProperty(this, "", "");

	public Channel(){
		
	}
	public Channel(String name, String dbms, String cs, String userName, String password)
	{
		this.name.set(name);
		this.DBMS.set(dbms);
		this.connectionString.set(cs);
		this.userName.set(userName);
		this.password.set(password);
	}

	public Channel(String name){this.name.set(name);}
	public Channel(String name, String dbms, String cs){
		this.name.set(name);
		this.DBMS.set(dbms);
		this.connectionString.set(cs);
	}

	public String getName(){return name.get();}
	public void setName(String name){this.name.set(name);}
	public StringProperty nameProperty(){return this.name;}

	public String getDBMS(){return DBMS.get();}
	public void setDBMS(String dbms){this.DBMS.set(dbms);}
	public StringProperty DBMSProperty(){return this.DBMS;}

	public String getConnectionString(){return connectionString.get();}
	public void setConnectionString(String cs){this.connectionString.set(cs);}
	public StringProperty connectionStringProperty(){return this.connectionString;}

	public String getUserName(){return userName.get();}
	public void setUserName(String userName){this.userName.set(userName);}
	public StringProperty userNameProperty(){return this.userName;}

	public String getPassword(){return password.get();}
	public void setPassword(String password){this.password.set(password);}
	public StringProperty passwordProperty(){return this.password;}

	public String getDriverClassName()
	{
		Map<String, String> drivers = new HashMap<>();
		drivers.put(DBMS_MYSQL, "com.mysql.jdbc.Driver");
		drivers.put(DBMS_ORACLE, "oracle.jdbc.driver.OracleDriver");
		drivers.put(DBMS_DB2, "com.mysql.jdbc.Driver");
		drivers.put(DBMS_SQLITE, "org.sqlite.JDBC");
		drivers.put(DBMS_MSACCESS, "sun.jdbc.odbc.JdbcOdbcDriver");
		drivers.put(DBMS_SQLSERVER, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
		drivers.put(DBMS_CSV, "jstels.jdbc.csv.CsvDriver2");
		drivers.put(DBMS_POSTGRESQL, "org.postgresql.Driver");
		

		return drivers.get(DBMS.get());
	}

	public static String getSampleUrl(String dbms)
	{
		Map<String, String> samples = new HashMap<>();
		samples.put(DBMS_MYSQL, "jdbc:mysql://192.168.1.20:3306/dbtest?&useUnicode=true&characterEncoding=UTF-8");
		samples.put(DBMS_ORACLE, "jdbc:oracle:thin:@oraxe:1521:XE");
		samples.put(DBMS_DB2, "jdbc:db2://192.168.11.6/PERSONNEL");
		samples.put(DBMS_SQLITE, "not available!");
		samples.put(DBMS_MSACCESS, "not available!");
		samples.put(DBMS_SQLSERVER, "jdbc:jtds:sqlserver://samipc:1433/db2");
		samples.put(DBMS_CSV, "not available!");
		samples.put(DBMS_POSTGRESQL, "jdbc:postgresql://localhost/postgres?user=postgres&password=123&ssl=true");

		return samples.get(dbms);
	}
	
//	private void writeObject(ObjectOutputStream out) throws IOException{
//		out.defaultWriteObject();
//	}
//	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
//		in.defaultReadObject();
//	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		name = new SimpleStringProperty(this, "", (String)in.readObject());
		DBMS = new SimpleStringProperty(this, "", (String)in.readObject());
		connectionString = new SimpleStringProperty(this, "", (String)in.readObject());
		userName = new SimpleStringProperty(this, "", (String)in.readObject());
		password = new SimpleStringProperty(this, "", (String)in.readObject());
		
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeObject(name.get());
		out.writeObject(DBMS.get());
		out.writeObject(connectionString.get());
		out.writeObject(userName.get());
		out.writeObject(password.get());
	}

	
}
