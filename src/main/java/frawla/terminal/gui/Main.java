
package frawla.terminal.gui;
import java.io.IOException;

import frawla.terminal.core.Util;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;


public class Main extends Application
{
	public static void main (String[] args)
	{
		launch(args);	
	}

	@Override
	public void start(Stage window) throws IOException 
	{
		FXMLLoader fxmlLoader = new FXMLLoader(Util.getResourceAsURL("Terminal.fxml"));			
		
		//TerminalController myController = (TerminalController) fxmlLoader.getController();			
		fxmlLoader.load(); //run initailize()
	}
	
}
