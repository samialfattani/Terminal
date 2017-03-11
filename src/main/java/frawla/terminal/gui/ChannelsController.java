package frawla.terminal.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

import frawla.terminal.core.Channel;
import frawla.terminal.core.Util;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;


class Channels
{
	private FXMLLoader fxmlLoader;
	private ChannelsController myController;
	

	public Channels(){		
		try
		{
			fxmlLoader = new FXMLLoader(Util.getResource("Channels.fxml").toURL());			

			BorderPane root = (BorderPane) fxmlLoader.load();
			myController = (ChannelsController) fxmlLoader.getController();

			Stage window = new Stage( );
			window.setScene(new Scene(root, 700, 300));
			window.getIcons().add(new Image(Util.getResource("images/icon.png").toString() ));
			window.setTitle("Channels");
			window.setOnCloseRequest(event -> myController.close() );
			window.show();
		}
		catch (IOException e){
			Util.showError(e, e.getMessage());
		}            
	}

	public ChannelsController getMyController(){
		return myController;
	}
}

public class ChannelsController implements Initializable
{
	@FXML private TextArea txtConnectionString;
	@FXML private TextField txtChannelName;
	@FXML private TextField txtUserName;
	@FXML private TextField txtPassword;
	@FXML private Button btnRemove;
	@FXML private Button btnAdd;
	@FXML private Button btnSample;
	@FXML private ComboBox<String> cmbDBMS;
	@FXML private ListView<Channel> lstChannels;
	@FXML private Button btnUp ;
	@FXML private Button btnDown ;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		cmbDBMS.getItems().addAll(Channel.getListOfDBMSs()); //
		Image img ;
		img = new Image(Util.getResourceAsStream("images/add.png"));
		btnAdd.setGraphic(new ImageView(img));
		
		img = new Image(Util.getResourceAsStream("images/remove.png"));
		btnRemove.setGraphic(new ImageView(img));
		
		img = new Image(Util.getResourceAsStream("images/up.png"));
		btnUp.setGraphic(new ImageView(img));
		img = new Image(Util.getResourceAsStream("images/down.png"));
		btnDown.setGraphic(new ImageView(img));
		
		if (Util.CONNECTION_FILE.exists())
		{
			ArrayList<Channel> myList = (ArrayList<Channel>) Util.readFileAsObject(Util.CONNECTION_FILE); 
			lstChannels
				.getItems()
				.setAll( FXCollections.observableList(myList) );
		}
		
		lstChannels.setCellFactory(new Callback<ListView<Channel>, ListCell<Channel>>() {
            @Override
            public ListCell<Channel> call(ListView<Channel> param) {
                return new ListCell<Channel>(){
                	@Override
                	protected void updateItem(Channel item, boolean empty)
                	{
                		super.updateItem(item, empty);
                		setText( (item == null)?"":item.getName() );
                	}
                };
            }
        });
		
		
		lstChannels.getSelectionModel()
					.selectedItemProperty()
					.addListener(new ChangeListener<Channel>() {

		    @Override
		    public void changed(ObservableValue<? extends Channel> observable, Channel oldValue, Channel newValue) {
		        // Your action here
		    	if(oldValue != null){
		    		oldValue.nameProperty().unbind();
		    		oldValue.connectionStringProperty().unbind();
		    		oldValue.DBMSProperty().unbind();
		    		oldValue.userNameProperty().unbind();
		    		oldValue.passwordProperty().unbind();
		    	}
		    	
		    	txtChannelName.setText(newValue.getName());
		    	txtConnectionString.setText(newValue.getConnectionString());
		    	cmbDBMS.getSelectionModel().select(newValue.getDBMS());
		    	txtUserName.setText(newValue.getUserName());
		    	txtPassword.setText(newValue.getPassword());
		    	
		    	newValue.nameProperty().bind(txtChannelName.textProperty());		    	
		        newValue.connectionStringProperty().bind(txtConnectionString.textProperty());
		        newValue.DBMSProperty().bind(cmbDBMS.getSelectionModel().selectedItemProperty());
		        newValue.userNameProperty().bind(txtUserName.textProperty());
		        newValue.passwordProperty().bind(txtPassword.textProperty());
		        lstChannels.refresh();
		        //System.out.println("Selected item: " + newValue.getName() + " - " + newValue.getConnectionString());
		        resetUpAndDownButtons();
		    }
		});
		
		resetUpAndDownButtons();
		
	}//initialize 
	
	
	public void close()
	{
		ArrayList<Channel> myList = new ArrayList<Channel>(lstChannels.getItems()); 
		Util.Save(myList, Util.CONNECTION_FILE);
		
	}


	public void btnSample_click()
	{
		String dbms = cmbDBMS.getSelectionModel().getSelectedItem();
		txtConnectionString.appendText( Channel.getSampleUrl(dbms) );
	}
	
	public void btnAdd_click()
	{
		String n = String.format("%02d", lstChannels.getItems().size()+1);  
		lstChannels.getItems().add(new Channel("con-"+n));
		lstChannels.refresh();
	}

	public void btnRemove_click()
	{
		lstChannels.getItems().remove(lstChannels.getSelectionModel().getSelectedItem());
		lstChannels.refresh();
	}
	
	public void btnUp_click()
	{	
		int idx = lstChannels.getSelectionModel().getSelectedIndex();
		Collections.swap(lstChannels.getItems(), idx-1, idx);
		lstChannels.getSelectionModel().select(idx-1);
		resetUpAndDownButtons();
	}

	public void btnDown_click()
	{	
		int idx = lstChannels.getSelectionModel().getSelectedIndex();
		Collections.swap(lstChannels.getItems(), idx, idx+1);
		lstChannels.getSelectionModel().select(idx+1);
		resetUpAndDownButtons();
	}

	private void resetUpAndDownButtons()
	{
		btnUp.setDisable(false);
		btnDown.setDisable(false);

		int idx = lstChannels.getSelectionModel().getSelectedIndex();
		
		if(idx < 0 ){
			btnUp.setDisable(true);
			btnDown.setDisable(true);
		}
		
		if(idx == 0 )
			btnUp.setDisable(true);
		
		if(idx >= lstChannels.getItems().size()-1 )
			btnDown.setDisable(true);
	}

	
}
