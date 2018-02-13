package frawla.terminal.gui;

import java.net.URL;
import java.util.ResourceBundle;

import frawla.terminal.core.Util;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class AboutController implements Initializable
{
	@FXML private Pane PanRoot;
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		//to be executed after initialize()
		Platform.runLater(() -> {
			Stage window = new Stage( );
			Scene scene = new Scene(PanRoot, PanRoot.getPrefWidth(), PanRoot.getPrefHeight());
			window.setScene(scene);

			window.getIcons().add(new Image( Util.getResource("images/icon.png").toString() ));
			window.setTitle("About");
			//window.setOnCloseRequest(event -> System.exit(0) );
			window.setResizable(false);
			window.show();
			
		});        


	}
}
