package frawla.terminal.test;
import java.io.File;
import org.junit.Test;

import frawla.terminal.core.DataFile;

public class DataFileTest
{

	@Test
	public void test()
	{
		DataFile df = new DataFile(new File("data/mydata.dat" ));
		
		df.Make(10, 5);
		df.Print();
		df.WriteToDatabase("Hello");
	}

}
