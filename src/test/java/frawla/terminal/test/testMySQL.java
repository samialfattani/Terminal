package frawla.terminal.test;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * This class demonstrates how to write mySQL files by using JDBC API.
 */

public class testMySQL
{
	public static void main(String[] args) throws SQLException, ClassNotFoundException
	{
		// load the driver into memory
		Class.forName("com.mysql.jdbc.Driver");

		Connection conn = DriverManager.getConnection(
				"jdbc:mysql://192.168.1.20:3306/dbtest?" //user=james&password=jojo
				+ "&useUnicode=true&characterEncoding=UTF-8"
				//+ "&autoReconnect=true&failOverReadOnly=false&maxReconnects=10" 
				, "james", "jojo");
		
//		Connection conn = DriverManager.getConnection(
//				"jdbc:mysql://sql7.freemysqlhosting.net:3306/sql7132035", 
//				"sql7132035", "BRJrUS7Drg");

		Statement stmt = conn.createStatement();

		try{
			stmt.execute("DROP TABLE products");
			stmt.execute("DROP TABLE regions");
			stmt.execute("DROP TABLE prices");
		}catch (Exception ex){}

		// create the data tables
		stmt.execute("CREATE TABLE products(prodid int, description varchar(30))");
		stmt.execute("CREATE TABLE regions(id int, regionname varchar(30))");
		stmt.execute("CREATE TABLE prices(prodid int, regionid int, stdprice FLOAT, minprice FLOAT, startdate TIMESTAMP, enddate TIMESTAMP)");

		// insert the records into the tables
		stmt.execute("INSERT INTO products(prodid, description) VALUES(100860,'Motherboard')");
		stmt.execute("INSERT INTO products(prodid, description) VALUES(100861,'Flat Monitor')");
		stmt.execute("INSERT INTO products(prodid, description) VALUES(100870,'Processor 5 GHZ')");
		stmt.execute("INSERT INTO products(prodid, description) VALUES(100871,'Printer')");
		stmt.execute("INSERT INTO products(prodid, description) VALUES(100890,'Digital Camera')");
		stmt.execute("INSERT INTO products(prodid, description) VALUES(101860,'Memory Card 1GB')");
		stmt.execute("INSERT INTO products(prodid, description) VALUES(101863,'Video Accelerator')");
		stmt.execute("INSERT INTO products(prodid, description) VALUES(102130,'Scanner')");
		stmt.execute("INSERT INTO products(prodid, description) VALUES(200376,'Network Card')");
		stmt.execute("INSERT INTO products(prodid, description) VALUES(200380,'Flash Card')");
		stmt.execute("INSERT INTO products(prodid, description) VALUES(300001,'LCD Monitor')");
		stmt.execute("INSERT INTO regions(id, regionname) VALUES(100,'Europe')");
		stmt.execute("INSERT INTO regions(id, regionname) VALUES(101,'North America')");
		stmt.execute("INSERT INTO regions(id, regionname) VALUES(102,'South America')");
		stmt.execute("INSERT INTO regions(id, regionname) VALUES(103,'Africa')");
		stmt.execute("INSERT INTO regions(id, regionname) VALUES(104,'Asia')");
		stmt.execute("INSERT INTO regions(id, regionname) VALUES(105,'Australia')");
		stmt.execute("INSERT INTO regions(id, regionname) VALUES(106,'Grenlandia')");
		stmt.execute("INSERT INTO regions(id, regionname) VALUES(107,'Arctica')");
		stmt.execute("INSERT INTO regions(id, regionname) VALUES(108,'Antarctida')");
		stmt.execute("INSERT INTO prices(prodid, regionid, stdprice, minprice, startdate, enddate) VALUES(100860,101,130,124, DATE('2002-01-01'), DATE('2002-01-21'))");
		stmt.execute("INSERT INTO prices(prodid, regionid, stdprice, minprice, startdate, enddate) VALUES(100860,102,132,125.6,DATE('2002-01-01'),DATE('2002-01-21'))");
		stmt.execute("INSERT INTO prices(prodid, regionid, stdprice, minprice, startdate, enddate) VALUES(100860,103,135,128,DATE('2003-06-01'),DATE('2003-07-01'))");
		stmt.execute("INSERT INTO prices(prodid, regionid, stdprice, minprice, startdate, enddate) VALUES(100861,105,239,231.2,DATE('2002-01-01'),DATE('2002-01-10'))");
		stmt.execute("INSERT INTO prices(prodid, regionid, stdprice, minprice, startdate, enddate) VALUES(100861,107,242,233.6,DATE('2003-05-01'),DATE('2003-06-01'))");
		stmt.execute("INSERT INTO prices(prodid, regionid, stdprice, minprice, startdate, enddate) VALUES(100861,106,245,236,DATE('2002-01-12'),DATE('2002-03-12'))");
		stmt.execute("INSERT INTO prices(prodid, regionid, stdprice, minprice, startdate, enddate) VALUES(100871,101,154,153.2,DATE('2002-08-01'),DATE('2002-09-01'))");
		stmt.execute("INSERT INTO prices(prodid, regionid, stdprice, minprice, startdate, enddate) VALUES(100890,108,445,440.5,DATE('2002-09-01'),DATE('2002-10-01'))");
		stmt.execute("INSERT INTO prices(prodid, regionid, stdprice, minprice, startdate, enddate) VALUES(100890,105,449.7,446.4,DATE('2002-11-03'),DATE('2002-12-03'))");
		stmt.execute("INSERT INTO prices(prodid, regionid, stdprice, minprice, startdate, enddate) VALUES(101863,102,98.0,99.1,DATE('2002-12-01'),DATE('2003-01-11'))");
		stmt.execute("INSERT INTO prices(prodid, regionid, stdprice, minprice, startdate, enddate) VALUES(102130,103,178.9,182.5,DATE('2002-04-01'),DATE('2002-05-01'))");

		// test inserted data by using an SQL query
		ResultSet rs = stmt.executeQuery(
			"SELECT prod.description, regs.regionname, minprice, stdprice, "
	        + "DATE_FORMAT(startdate, '%d-%m-%Y') AS \"Start Date\", " 
			+ "DATE_FORMAT(enddate, '%d-%m-%Y') AS \"End Date\" FROM prices ps "
	        + "JOIN regions regs ON ps.regionid = regs.id JOIN products prod " 
			+ "ON prod.prodid = ps.prodid ORDER BY prod.description");

		// read the data and put it to the console
		for (int j = 1; j <= rs.getMetaData().getColumnCount(); j++){
			System.out.print(rs.getMetaData().getColumnName(j) + "\t");
		}
		System.out.println();

		while (rs.next())
		{
			for (int j = 1; j <= rs.getMetaData().getColumnCount(); j++)
			{
				System.out.print(rs.getObject(j) + "\t");
			}
			System.out.println();
		}

		// close the objects
		rs.close();
		stmt.close();
		conn.close();
	}
}
