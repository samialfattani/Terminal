package frawla.terminal.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import frawla.terminal.core.Util;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.JFXPanel;

public class GeneralTest
{

	@Before 
	public void before(){
		new JFXPanel();
	}

	@Test
	public void BidirectionalPropertyTest()
	{
		StringProperty p1 = new SimpleStringProperty("");
		StringProperty p2 = new SimpleStringProperty("");

		p2.bindBidirectional(p1);

		assertEquals(false, p1.isBound());
		assertEquals(false, p2.isBound());

		p1.set("asdf");
		assertEquals("asdf", p1.get());
		assertEquals("asdf", p2.get());

		p2.set("koko");
		assertEquals("koko", p1.get());
		assertEquals("koko", p2.get());

		p1.unbindBidirectional(p2);
		//or you can use: 
		//p2.unbindBidirectional(p1);

		p1.set("111"); p2.set("222");
		assertEquals("111", p1.get());
		assertEquals("222", p2.get());
	
	}
	
	@Test
	public void CrateFullPathIfTheFileIsNotExistsTest()
	{
		File f = new File("data/dir1/dir2/file2.txt");

		Util.Save(Arrays.asList("koko", "meme"), f);
		
		assertTrue(f.exists());
		assertTrue(f.getParentFile().exists());
		
		
		f.delete();
		f.getParentFile().delete();
		f.getParentFile().getParentFile().delete();
	}	
	
	@Test
	public void OptionalTest()
	{
		StringBuilder s = new StringBuilder("Hello");
		
		Optional<StringBuilder> os = Optional.ofNullable(s);		
		os.get().append(" Sami");
		
		StringBuilder temp = os.get();
		temp.append(" end");
		
		assertEquals("Hello Sami end", s.toString());
		assertEquals("Hello Sami end", temp.toString());
	}	

}
