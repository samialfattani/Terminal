package frawla.terminal.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.ResourceBundle;

import com.sun.javafx.scene.control.skin.TextAreaSkin;

import frawla.terminal.core.Channel;
import frawla.terminal.core.Connector;
import frawla.terminal.core.RecentFile;
import frawla.terminal.core.Util;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class TerminalController implements Initializable
{
	@FXML private Pane PanRoot;
	@FXML private TextArea txtSQL;
	@FXML private TextArea txtLog;
	@FXML private Label lblFileName;
	@FXML private ComboBox<Channel> cmbConnections;
	@FXML private TableView<ObservableList<String>> tblResult;
	@FXML private Menu mnuRecent;
	
	private StringProperty myFileName = new SimpleStringProperty(this,"" ,"<null>");
	private Connector connector = null;
	private LinkedHashSet<RecentFile> recentFiles = new LinkedHashSet<>();

	/* Listener as a private member so that it can be removed.*/
	private ChangeListener<String> txtSQLOnChange = (ov, oldv, newv) ->
	{
		txtSQL_textChanged();
	};

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		//to be executed after initialize()
		Platform.runLater(() -> {
			Stage window = new Stage( );
			Scene scene = new Scene(PanRoot, PanRoot.getPrefWidth(), PanRoot.getPrefHeight());
			window.setScene(scene);

			window.getIcons().add(new Image( Util.getResource("images/icon.png").toString() ));
			window.setTitle("Terminal");
			window.setOnCloseRequest(event -> System.exit(0) );
			window.show();
			
			scene.setOnDragOver( event -> scene_OnDragOver(event) );
	        scene.setOnDragDropped( event -> scene_OnDragDropped(event) );
		});        

		lblFileName.textProperty().bind( myFileName );

		loadRecentFilesIntoMenu();
		
		ArrayList<Channel> myList = loadConnectionList();
		cmbConnections
			.getItems()
			.setAll( FXCollections.observableList( myList) );
		
		cmbConnectionSetup();
		
		
		appendRunItemToTheOriginalPopupMenuOfTxtSQL();
        txtSQL.textProperty().addListener(txtSQLOnChange);
        
        //--------------------
//        Automated ********
        OpenFile(new File("data/sql.sql"));
        cmbConnections.getSelectionModel().select(2);
        cmbConnections_itemSelected();
	}//initialize

	private void scene_OnDragDropped(DragEvent event)
	{
		Dragboard db = event.getDragboard();
		boolean success = false;
		if (db.hasFiles()) {
		    success = true;
		    for (File file : db.getFiles()) {
		    	OpenFile(file);
		        System.out.println(file.getAbsolutePath());
		    }
		}
		event.setDropCompleted(success);
		event.consume();
	}

	// Dropping over surface
	private void scene_OnDragOver(DragEvent event)
	{
		Dragboard db = event.getDragboard();
		if (db.hasFiles())
		    event.acceptTransferModes(TransferMode.COPY);
		else
		    event.consume();
	}

	public void mnuOpen_click()
	{
		
		Util.getFileChooserForOpen()
			.ifPresent((f) -> 
			{				
				txtSQL.textProperty().removeListener(txtSQLOnChange);
				OpenFile(f);
				
				recentFiles.add(new RecentFile(f.getAbsolutePath(), recentFiles.size()+1 ));
				Util.Save(recentFiles, Util.RECENT_FILES_LIST_FILE);
				loadRecentFilesIntoMenu();
				txtSQL.textProperty().addListener(txtSQLOnChange);
			});
	}

	public void OpenFile(File f)
	{
		String s = Util.readFileAsString(f);
		txtSQL.setText(s);	
		myFileName.set(f.getAbsolutePath());
	}
	
	
	@SuppressWarnings("unchecked")
	private void loadRecentFilesIntoMenu()
	{
		if (Util.RECENT_FILES_LIST_FILE.exists())
		{
			mnuRecent.getItems().clear();
			recentFiles = (LinkedHashSet<RecentFile>) Util.readFileAsObject(Util.RECENT_FILES_LIST_FILE);
			recentFiles			
				.stream()
				.forEach(f -> 
				{
					MenuItem mi = new MenuItem(f.getAbsolutePath());
	                mi.setOnAction( (e) -> OpenFile(f) );
	                mnuRecent.getItems().add(0, mi);
	            });
		}
	}//loadRecentFilesIntoMenu

	//**********************************************
	
	public void cmbConnections_itemSelected()
	{
		connector = new Connector( cmbConnections.getValue() );
		if (connector.isValid(3)){
			log("Connected Sucessfully :) to:\n\t" + 
				cmbConnections.getSelectionModel().getSelectedItem().getConnectionString() );
		}
		
	}
	
	
	public void cmbConnections_mouseClick()
	{
		ArrayList<Channel> myList = loadConnectionList();		
		cmbConnections
			.getItems()
			.setAll( FXCollections.observableList(myList) );
	}
	
	public void btnEdit_click()
	{
		try
		{
			FXMLLoader fxmlLoader = new FXMLLoader(Util.getResourceAsURL("Channels.fxml"));			
			fxmlLoader.load();
		}
		catch (IOException e)
		{
			Util.showError(e, e.getMessage());
		}
	}
	
	public void txtSQL_textChanged(){
		File f = new File (myFileName.get());
		if( f.exists() )
			lblFileName.setStyle("-fx-border-color:red; -fx-background-color: pink;");
	}

	//-------------------------------------------- 
	
	public void mnuSave_click()
	{
		File f = new File(myFileName.get()) ;
		if(f.exists())
			Util.Save(txtSQL.getText(), f);
		else
			mnuSaveAs_click();
		lblFileName.setStyle("-fx-border-color:green; -fx-background-color: lightgreen;");
	}
	
	public void mnuSaveAs_click()
	{
		//using Otional in case if the user didn't choose any file.
		Util.getFileChooserForSave().ifPresent(f -> {
			Util.Save(txtSQL.getText(), f);
			myFileName.set(f.getAbsolutePath());
		});		
	}
	public void mnuClose_click(){
		mnuSave_click();
		System.exit(0);
	}

	public void mntmRunCurr_click(){
		mntmRun_click();
	}

	public void mntmRunAll_click()
	{
		String[] SQLs = txtSQL.getText().split(";");
		
		for(int i=0; i<SQLs.length; i++)
		{
			boolean res = buildData( SQLs[i] );
			if(!res)
				break;
		}
	}

	protected void mntmRun_click()
	{
		int crt = txtSQL.getCaretPosition()+1;
		String sql = getCommandTahtArroundTheCoursor(crt);
		runSQL(sql);
	}

	//------------- CORE METHODS -----------------
	
	private ArrayList<Channel> loadConnectionList()
	{
		@SuppressWarnings("unchecked")
		ArrayList<Channel> myList = (ArrayList<Channel>) Util.readFileAsObject(Util.CONNECTION_FILE);
		myList = Optional.ofNullable(myList)
						 .orElse(new ArrayList<Channel>());
		return myList;
	}

	private void runSQL(final String sql)
	{
		selectThisInTheTxtSQL(sql);
		
		if (connector == null)
			return;
		
		System.out.println("Running: -"+sql+"-");
		buildData(sql);

	}

	public boolean buildData(String sql)
	{
		if (connector == null)
		{
			Util.showError("Database is not connected");
			return false;
		}
		
		
		int c = connector.executeDDLorDML(sql);
		log( c + " Rows is selected.");
		
		tblResult.getColumns().clear();
		ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

		try
		{
			ResultSet rs = connector.getResultSet().get();

			/**********************************
			 * ADD COLUMNS DYNAMICALLY 			
			 **********************************/
			for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++)
			{
				//We are using non property style for making dynamic table
				final int j = i;                
				String colName = rs.getMetaData().getColumnName(i+1);
				TableColumn<ObservableList<String>, String> col = new TableColumn<>(colName);
				
				col.setCellValueFactory( myData -> 
				{
					//register the each row-index in the col.
					String v = Optional.ofNullable( myData.getValue().get(j) )
									   .orElse("");
					return new SimpleStringProperty(v);                        
				});

				tblResult.getColumns().add(col); 
			}

			/********************************
			 * Data added to ObservableList *
			 ********************************/
			//rs.beforeFirst(); //remove this if RS is Foraward Only.
			connector.refreshAllRecords(sql);
			rs = connector.getResultSet().get();

			while(rs.next() )
			{
				//Iterate Row
				ObservableList<String> row = FXCollections.observableArrayList();
				for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++)
				{
					row.add( connector.getValue(i) );
					//System.out.print(i + ", ");
				}
				
				//System.out.println("Row [1] added "+row );
				data.add(row);
			}

			//FINALLY ADDED TO TableView
			tblResult.setItems(data);
		}catch(Exception e){
			Util.showError(e, e.getMessage());
			return false;
		}
		return true;
	}
	
	public void mnuAbout_click()
	{
		try
		{
			FXMLLoader fxmlLoader = new FXMLLoader(Util.getResourceAsURL("About.fxml"));			
			fxmlLoader.load();
		}
		catch (IOException e)
		{
			Util.showError(e, e.getMessage());
		}
	}


	//----------------- OTHERS -----------------------------
	
	private void log(String str)
	{
		String fDate = Util.MY_TIME_FORMAT.format(new Date());
		txtLog.appendText(  fDate + ": " + str + "\n");	
	}

	private String getCommandTahtArroundTheCoursor(int crsrPos)
	{
		String[] SQLs = txtSQL.getText().split(";");
		int total = 0;
		int i=0;
		//int count = StringUtils.countMatches(txtSQL.getText(), ";");
		for(; i<SQLs.length; i++)
		{
			total += SQLs[i].length()+1; 
			if(total >= crsrPos)
				break;
		}
		
		String sql = SQLs[i].trim();
		return sql;
	}

	private void selectThisInTheTxtSQL(final String sql)
	{
		int indexOfSql = txtSQL.getText().indexOf(sql);
		txtSQL.selectRange(indexOfSql, indexOfSql + sql.length());
	}
	

	private void cmbConnectionSetup()
	{
		cmbConnections.setCellFactory(p -> {
            return new ListCell<Channel>()
            {
            	@Override
            	protected void updateItem(Channel ch, boolean empty)
            	{
            		super.updateItem(ch, empty);
            		ch = Optional.ofNullable(ch).orElse(new Channel());
            		setText( ch.getName() );
            	}
            };
         });
		
		/* Convert Object to String and String to Object. */
		cmbConnections.setConverter(new StringConverter<Channel>()
		{
			@Override
			public String toString(Channel ch)
			{
				if(ch == null) 
					return null;
				return ch.getName();
			}
			
			//this is useful in case if ComboBox is Editable.
			@Override
			public Channel fromString(String str)
			{
				//looking for an object by String.
			    return null;
			}
		});
	
	}//cmbConnectionSetup

	private void appendRunItemToTheOriginalPopupMenuOfTxtSQL()
	{
		/*Append to the popup menu*/
        TextAreaSkin customContextSkin = new TextAreaSkin(txtSQL) {
            @Override
            public void populateContextMenu(ContextMenu contextMenu) {
                super.populateContextMenu(contextMenu);
                MenuItem mntmRun = new MenuItem("Run");
                mntmRun.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
                mntmRun.setOnAction( (e) -> mntmRun_click() );
                contextMenu.getItems().add(0, mntmRun);
                contextMenu.getItems().add(1, new SeparatorMenuItem());
            }
        };
        txtSQL.setSkin(customContextSkin);
	}

}//end class 
