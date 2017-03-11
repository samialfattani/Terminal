package frawla.terminal.test;
import static org.junit.Assert.assertEquals;

import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.junit.Before;
import org.junit.Test;

import frawla.terminal.core.Util;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.embed.swing.JFXPanel;

public class ExternlizableTest implements Externalizable /* ,Serializable */
{
	
	public String Name="";
	public String Password="";
	public IntegerProperty Age = new SimpleIntegerProperty(23) ;
	
	@Before public void before(){
		new JFXPanel();
	}


	@Test
	public void test()
	{
		ExternlizableTest obj = new ExternlizableTest();
		obj.Name="Sami";
		obj.Password = "123";
		obj.Age.set(34);
		
		File f = Util.getTempFile();
		Util.Save(obj, f );
		
		
		//ExternlizableTest myobj  = (ExternlizableTest)Util.readFileAsObject(f);
		obj  = (ExternlizableTest)Util.readFileAsObject(f);
		
		assertEquals("Sami", obj.Name);
		assertEquals("123", obj.Password);
		assertEquals(34, obj.Age.get());
	}

//	private void writeObject(ObjectOutputStream out) throws IOException{
//		out.defaultWriteObject();
//	}
//	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
//		in.defaultReadObject();
//	}
	
	
	//---------------------------------------------------
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		
		Name = (String)in.readObject();
		Password = (String)in.readObject();
		Age = new SimpleIntegerProperty((int)in.readObject());
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeObject(Name);
		out.writeObject(Password);
		out.writeObject(Age.get());
	}

}
