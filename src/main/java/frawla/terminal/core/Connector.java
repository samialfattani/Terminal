package frawla.terminal.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Optional;

// import org.hsqldb.jdbcDriver;

public class Connector
{
    private Optional<Connection> con;
    private Optional<ResultSet> rs;
    private Channel channel ;
    private String QUERY = "SELECT * from IMVU_Chatt";

    public Connector(Channel ch)
    {
    	channel = ch;
        try
        {
        	Class.forName(channel.getDriverClassName());
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

    
    public Connection getConnection(){ return con.get(); }
    public Optional<ResultSet> getResultSet(){ return rs; }
    public String getQeury(){ return QUERY; }

	private PreparedStatement createPreparedStatement(String sql)
    {
    	PreparedStatement pstmt = null;
		try
		{
			 
        	if( isScrollableDBMS() )
        		pstmt = con.get().prepareStatement(
            			sql,  
    					ResultSet.TYPE_SCROLL_SENSITIVE, 
    					ResultSet.CONCUR_UPDATABLE ) ;
        	else
        		pstmt = con.get().prepareStatement(sql );
		}
		catch (SQLException e)
		{
			Util.showError(e, "--- SQL Exception: " + e.getMessage() + "\n" );
		}
    	return pstmt;
    }
    
    //===============================================================================


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

    public String getValue(int i)
    {
        if(!rs.isPresent())
        	return "";
        
    	String res = "";
        
        try{
            if (rs.get().getRow() == 0)//no current row
                res = "";
			
            switch( rs.get().getMetaData().getColumnType(i) ) 
			{
				//case Types.NUMERIC: res = rs.getDouble(i) + ""; break;
				case Types.CLOB:
				case Types.BLOB: 
					res = "X"; break;
				default: 
					res = rs.get().getString(i); break;
			}

        }
        catch (SQLException e){
        	Util.showError(e, "--- SQL Exception: " + e.getMessage() + "\n" );
        }
    		
    	return res;
    }

    public String getValue(String colName)
    {
    	try {
    		for (int j=1; j <= rs.get().getMetaData().getColumnCount() ; j++)
			{
				if( colName.equals(rs.get().getMetaData().getColumnName(j)) )
					return getValue(j);
			}
    		Util.showError("--- Sami-Terminal: Column's Name is not correct.\n" );
    	}
    	catch (SQLException e){
    		Util.showError(e, "--- SQL Exception: " + e.getMessage() + "\n" );
    	}
		return "";
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

    public int executeDDLorDML(String sql)
    {
    	QUERY = sql;
    	PreparedStatement pstmt = createPreparedStatement(sql);
    	
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

    public void refreshAllRecords()
    {
        refreshAllRecords( QUERY );
    } //overloading

    public void refreshAllRecords(String qry)
    {
    	QUERY = qry;
    	if(rs == null)
    		return;
    	
    	ResultSet rs = this.rs.get();
        int myRow = 0;
        try
        {
            myRow = rs.getRow();

    		//PreparedStatement pstmt  = createPreparedStatement(QUERY);

    		executeDDLorDML(QUERY); //this.rs is updated!!
            //this.rs = Optional.ofNullable( pstmt.executeQuery(QUERY) );
            rs = this.rs.get();
            
            if( !isScrollableDBMS() )
            	return; 
            
            if (myRow > 0 && myRow < getRecordCount() )
            	rs.absolute( myRow );
            else
            	rs.absolute( 0 );
            
        }		
        catch (SQLException e)
        {
        	Util.showError(e, "--- SQL Exception: " + e.getMessage() + "\n" );
        }
    }

    public int getRecordCount()
    {
        int count = 0;
        
        try
        {
        	//becuse this DBMS supports TYPE_FORWARD_ONLY
            if( isScrollableDBMS() )
            {
	            rs.get().last();
	            count = rs.get().getRow();
            }
            else
            {
            	String q = "Select count(*) from ( " + QUERY + " )";
            	ResultSet r = createPreparedStatement(q).executeQuery();
            	if(r.next())
            		count = r.getInt(1);
            }
        }
        catch (SQLException e)
        {
        	Util.showError(e, "--- SQL Exception: " + e.getMessage() + "\n" );
        }
        return count;
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




	public boolean isValid(int timeOut)
	{
		try
		{
			return con.get().isValid(timeOut);
		}
		catch (SQLException e){Util.showError(e, "--- SQL Exception: " + e.getMessage() + "\n" );}
		return false;
	}

	//if not then the DBMS is TYPE_FORWARD_ONLY
	private boolean isScrollableDBMS() throws SQLException
	{
		return con.get().getMetaData().supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE) ||
			   con.get().getMetaData().supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE);
	}

}// end of class Connector 
