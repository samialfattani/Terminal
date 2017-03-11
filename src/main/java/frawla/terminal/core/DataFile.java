package frawla.terminal.core;

import java.io.*;

public class DataFile
{
	File myFile ;
	int RecordCount =0;
	int AttrCount =0;
	
	public DataFile(File f)
	{
		myFile = f;
	}
	
	public void Make(int tuples, int Attr)
	{
		RecordCount = tuples;
		AttrCount = Attr;
		
		try{
	
			//boolean exist = file.createNewFile();
	      	DataOutputStream out = new DataOutputStream(
	      								new FileOutputStream(myFile));
			

			out.writeInt(RecordCount);
			out.writeInt(AttrCount);
			for(int i=0; i<RecordCount; i++)
				for(int j=0; j<AttrCount; j++)
					out.writeInt(i);

			out.flush();
			out.close();
			System.out.println("File created successfully.");
			
		}catch(IOException e){
			System.out.println (e);
		}

	}//Make
	
	public void Print()
	{
	
	    try(FileInputStream fis  = new FileInputStream(myFile);
		    DataInputStream dis = new DataInputStream(fis);)
	    {
			
			// dis.available() returns 0 if the file does not have more lines.
			RecordCount = dis.readInt();
			AttrCount = dis.readInt();
			for(int i=0; i<RecordCount; i++){
				for(int j=0; j<AttrCount; j++){
					System.out.print(dis.readInt() + "\t");
				}
				System.out.print("\n");
			}
	      
	    }catch (FileNotFoundException e) {
	      e.printStackTrace();
	    }catch (IOException e) {
	      e.printStackTrace();
	    }		
		
	}//Print

	public void WriteToDatabase(String tableName)
	{
	
	    try(DataInputStream in = new DataInputStream(
	    							new FileInputStream(myFile));)
	    {
			RecordCount = in.readInt();
			AttrCount = in.readInt();
			
			//CREATE TABLE Marks (a1 integer, a2 integer, a3 integer);
			String CreateTable = "CREATE TABLE " + tableName + " (";
			for(int j=1; j<= AttrCount-1; j++){
				CreateTable += "a" + j + " integer, ";
			}
			CreateTable += "a" + AttrCount + " integer); ";
			
			String InsertInto = ""; 
			for(int i=1; i<=RecordCount; i++)
			{
				InsertInto += "INSERT INTO " + tableName + " VALUES(";
				for(int j=1; j<= AttrCount-1; j++){
					InsertInto += in.readInt() + ", ";
				}
				InsertInto += in.readInt() + "); \n";
			}
			
			System.out.println(CreateTable);
			System.out.println(InsertInto);

	    }catch (FileNotFoundException e) {
	      e.printStackTrace();
	    }catch (IOException e) {
	      e.printStackTrace();
	    }		
		
	}

}//end Class