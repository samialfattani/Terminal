package frawla.terminal.test;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/*
 This class demonstrates how to write CSV files by using JDBC API.
*/

public class testCSV {
  public static void main(String[] args) {
    try {
      // load the driver into memory
      Class.forName("jstels.jdbc.csv.CsvDriver2");

      // create a connection with a path to the current CSV directory.
      Connection conn = DriverManager.getConnection("jdbc:jstels:csv:." );

      // create a Statement object to execute the query with
      Statement stmt = conn.createStatement();

	  //delete CSV files	
      try{
	  stmt.execute("DROP TABLE products");
      stmt.execute("DROP TABLE regions");
      stmt.execute("DROP TABLE prices");
	  }
	  catch(Exception ex){}	
	  
      // create the data tables
      stmt.execute("CREATE TABLE products(prodid INTEGER, description STRING(30))");
      stmt.execute("CREATE TABLE regions(id INTEGER, regionname STRING(30))");
      stmt.execute("CREATE TABLE prices(prodid INTEGER, regionid INTEGER, stdprice FLOAT, minprice FLOAT, startdate TIMESTAMP, enddate TIMESTAMP)");

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
      stmt.execute("INSERT INTO prices(prodid, regionid, stdprice, minprice, startdate, enddate) VALUES(100860,101,130,124,PARSEDATETIME('01-01-2002','dd-MM-yyyy'),PARSEDATETIME('21-01-2002','dd-MM-yyyy'))");
      stmt.execute("INSERT INTO prices(prodid, regionid, stdprice, minprice, startdate, enddate) VALUES(100860,102,132,125.6,PARSEDATETIME('01-01-2002','dd-MM-yyyy'),PARSEDATETIME('21-01-2002','dd-MM-yyyy'))");
      stmt.execute("INSERT INTO prices(prodid, regionid, stdprice, minprice, startdate, enddate) VALUES(100860,103,135,128,PARSEDATETIME('01-06-2003','dd-MM-yyyy'),PARSEDATETIME('01-07-2003','dd-MM-yyyy'))");
      stmt.execute("INSERT INTO prices(prodid, regionid, stdprice, minprice, startdate, enddate) VALUES(100861,105,239,231.2,PARSEDATETIME('01-01-2002','dd-MM-yyyy'),PARSEDATETIME('10-01-2002','dd-MM-yyyy'))");
      stmt.execute("INSERT INTO prices(prodid, regionid, stdprice, minprice, startdate, enddate) VALUES(100861,107,242,233.6,PARSEDATETIME('01-05-2003','dd-MM-yyyy'),PARSEDATETIME('01-06-2003','dd-MM-yyyy'))");
      stmt.execute("INSERT INTO prices(prodid, regionid, stdprice, minprice, startdate, enddate) VALUES(100861,106,245,236,PARSEDATETIME('12-01-2002','dd-MM-yyyy'),PARSEDATETIME('12-03-2002','dd-MM-yyyy'))");
      stmt.execute("INSERT INTO prices(prodid, regionid, stdprice, minprice, startdate, enddate) VALUES(100871,101,154,153.2,PARSEDATETIME('01-08-2002','dd-MM-yyyy'),PARSEDATETIME('01-09-2002','dd-MM-yyyy'))");
      stmt.execute("INSERT INTO prices(prodid, regionid, stdprice, minprice, startdate, enddate) VALUES(100890,108,445,440.5,PARSEDATETIME('01-09-2002','dd-MM-yyyy'),PARSEDATETIME('01-10-2002','dd-MM-yyyy'))");
      stmt.execute("INSERT INTO prices(prodid, regionid, stdprice, minprice, startdate, enddate) VALUES(100890,105,449.7,446.4,PARSEDATETIME('03-11-2002','dd-MM-yyyy'),PARSEDATETIME('03-12-2002','dd-MM-yyyy'))");
      stmt.execute("INSERT INTO prices(prodid, regionid, stdprice, minprice, startdate, enddate) VALUES(101863,102,98.0,99.1,PARSEDATETIME('01-12-2002','dd-MM-yyyy'),PARSEDATETIME('11-01-2003','dd-MM-yyyy'))");
      stmt.execute("INSERT INTO prices(prodid, regionid, stdprice, minprice, startdate, enddate) VALUES(102130,103,178.9,182.5,PARSEDATETIME('01-04-2002','dd-MM-yyyy'),PARSEDATETIME('01-05-2002','dd-MM-yyyy'))");

      // test inserted data by using an SQL query
      ResultSet rs = stmt.executeQuery(
          "SELECT prod.description, " +
	      "regs.regionname, " +
          "minprice, "+
          "stdprice, "+
          "FORMATDATETIME(startdate, 'dd MMMMM yyyy' ) AS \"Start Date\", "+
          "FORMATDATETIME(enddate, 'dd MMMMM yyyy') AS \"End Date\" FROM prices ps "+
          "JOIN regions regs ON ps.regionid = regs.id JOIN products prod "+
          "ON prod.prodid = ps.prodid ORDER BY prod.description");

      // read the data and put it to the console
      for (int j = 1; j <= rs.getMetaData().getColumnCount(); j++) {
	    System.out.print(rs.getMetaData().getColumnName(j) + "\t");
      }
      System.out.println();

      while (rs.next()) {
	    for (int j = 1; j <= rs.getMetaData().getColumnCount(); j++) {
	    System.out.print(rs.getObject(j) + "\t");
	  }
	  System.out.println();
      }

      // close the objects
      rs.close();
      stmt.close();
      conn.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
