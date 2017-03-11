package frawla.terminal.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

// import org.hsqldb.jdbcDriver;

public class Connector
{
    private Optional<Connection> con;
    private Optional<ResultSet> rs;
    private Channel channel ;
    private final String QUERY = "SELECT * from IMVU_Chatt";

    public Connector(Channel ch)
    {
    	channel = ch;
        try
        {
        	Class.forName(ch.getDriverClassName());
        	con = Optional.ofNullable(DriverManager.getConnection(
					        			ch.getConnectionString(), 
					        			ch.getUserName(),
					        			ch.getPassword())
        		   );
        }
        catch (SQLException e){
        	Util.showError(e, "--- SQL Exception: " + e.getMessage() + "\n" );
        }
        catch (ClassNotFoundException e){
        	Util.showError(e, e.getMessage());
            close();
        }
        catch (Exception e){
        	Util.showError(e, e.getMessage());
        }
    }//end of constructor

    
    public PreparedStatement createPreparedStatement(String sqlStatement)
    {
    	PreparedStatement pstmt = null;
		try
		{
			 
        	if(channel.getDBMS().equals(Channel.DBMS_SQLITE))
        		pstmt = con.get().prepareStatement(sqlStatement );
        	else
        		pstmt = con.get().prepareStatement(
        			sqlStatement,  
					ResultSet.TYPE_SCROLL_SENSITIVE, 
					ResultSet.CONCUR_UPDATABLE ) ;
		}
		catch (SQLException e)
		{
			Util.showError(e, "--- SQL Exception: " + e.getMessage() + "\n" );
		}
    	return pstmt;
    }
    
    //===============================================================================

    public Connection getConnection()
    {
        return con.get();
    }

    public void goNext()
    {
    	rs.ifPresent( rs -> 
    	{
            try{
                if (!rs.isLast())
                    rs.next();        		
            }
            catch (SQLException e){
            	Util.showError(e, "--- SQL Exception: " + e.getMessage() + "\n" );
            }
    	});

    }

    public void goPrevious()
    {
    	rs.ifPresent( rs -> 
    	{
	        try{
	            if (!rs.isFirst())
	                rs.previous();
	        }
	        catch (SQLException e){
	        	Util.showError(e, "--- SQL Exception: " + e.getMessage() + "\n" );
	        }
    	});
    }

    public String getValue(String fieldName)
    {
        String s = "";
        try{
            if (rs.get().getRow() == 0)//no current row
                return "";
            s = rs.get().getString( fieldName );
        }
        catch (SQLException e){
        	Util.showError(e, "--- SQL Exception: " + e.getMessage() + "\n" );
        }
        return s;
    }

    public int executeDDLorDML(PreparedStatement pstmt)
    {
    	int c = 0;
        try
        {
        	if(pstmt.execute()){
        		rs = Optional.ofNullable( pstmt.getResultSet() );
        		c = getRecordCount();
        	}else{
        		c = pstmt.getUpdateCount();
        	}
        	
        }
        catch (SQLException e){
        	Util.showError(e, "--- SQL Exception: " + e.getMessage() + "\n" );
        }
        return c;
    }

    public void Delete()
    {
    	rs.ifPresent(rs -> 
    	{
            try{
                rs.deleteRow();
            }
            catch (SQLException e){
            	Util.showError(e, "--- SQL Exception: " + e.getMessage() + "\n" );
            }
    		
    	});
    }

    public void refreshAllRecords()
    {
        refreshAllRecords( QUERY );
    } //overloading

    public void refreshAllRecords(String qry)
    {
    	
        rs.ifPresent(rs -> 
    	{
            int myRow = 0;
            try
            {
            	Statement stmt ;
            	if(channel.getDBMS().equals(Channel.DBMS_SQLITE))
            		stmt = con.get().createStatement() ;
            	else
				stmt = con.get().createStatement( 
						ResultSet.TYPE_SCROLL_SENSITIVE, 
						ResultSet.CONCUR_UPDATABLE ) ;

                myRow = rs.getRow();
        		myRow = (myRow == 0)? 1 : myRow;
        		
                rs = stmt.executeQuery( qry );

                if (myRow <= getRecordCount() )
                	rs.absolute( myRow );
                else if (getRecordCount() >= 1)
                	rs.absolute( 1 );
                
                stmt.close();
            }		
            catch (SQLException e)
            {
            	Util.showError(e, "--- SQL Exception: " + e.getMessage() + "\n" );
            }
    	});
    }

    public int getRecordCount()
    {
        int c = 0;
        if(channel.getDBMS().equals(Channel.DBMS_SQLITE))
        {
        	return 0; //becuse this DBMS supports only TYPE_FORWARD_ONLY
        }
        try
        {
            rs.get().last();
            c = rs.get().getRow();
        }
        catch (SQLException e)
        {
        	Util.showError(e, "--- SQL Exception: " + e.getMessage() + "\n" );
        }
        return c;
    }

    public void close()
    {
    	rs.ifPresent( rs -> {
    		try{
    			rs.close();
    		}catch (SQLException e){Util.showError(e, "--- SQL Exception: " + e.getMessage() + "\n" );}
    	});
    	
    	con.ifPresent( con -> {
    		try{
    			con.close();
    		}catch (SQLException e){Util.showError(e, "--- SQL Exception: " + e.getMessage() + "\n" );}
    	});
    }


	public Optional<ResultSet> getResultSet()
	{
		return rs;
	}


	public boolean isValid(int timeOut)
	{
		try
		{
			return con.get().isValid(timeOut);
		}
		catch (SQLException e){Util.showError(e, "--- SQL Exception: " + e.getMessage() + "\n" );}
		return false;
	}

}// end of class Connector 
