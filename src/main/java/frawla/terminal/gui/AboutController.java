package frawla.terminal.gui;

import java.io.IOException;

import frawla.terminal.core.Util;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

class About
{
	private FXMLLoader fxmlLoader;
	private AboutController myController;
	

	public About(){		
		try
		{
			fxmlLoader = new FXMLLoader(Util.getResource("About.fxml").toURL());			

			AnchorPane root = (AnchorPane) fxmlLoader.load();
			myController = (AboutController) fxmlLoader.getController();

			Stage window = new Stage( );
			window.setScene(new Scene(root, 600, 250));
			window.getIcons().add(new Image(Util.getResource("images/icon.png").toString() ));
			window.setTitle("About");
			//window.setOnCloseRequest(event -> myController.close() );
			window.setResizable(false);
			window.show();
		}
		catch (IOException e){
			Util.showError(e, e.getMessage());
		}            
	}

	public AboutController getMyController(){
		return myController;
	}
}

public class AboutController
{
	
}
