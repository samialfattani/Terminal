package frawla.terminal.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;


class Terminal
{
	private FXMLLoader fxmlLoader;
	private TerminalController myController;
	

	public Terminal(){		
		try
		{
			fxmlLoader = new FXMLLoader(Util.getResource("Terminal.fxml").toURL());			

			Parent root = (Parent) fxmlLoader.load();
			myController = (TerminalController) fxmlLoader.getController();

			Stage window = new Stage( );
			Scene scene = new Scene(root, 700,600);
			window.setScene(scene);
			window.getIcons().add(new Image(Util.getResource("images/icon.png").toString() ));
			window.setTitle("Terminal");
			window.setOnCloseRequest(event -> System.exit(0) );
			window.show();
			
			scene.setOnDragOver( event -> 
			{
                Dragboard db = event.getDragboard();
                if (db.hasFiles())
                    event.acceptTransferModes(TransferMode.COPY);
                else
                    event.consume();
	        });
	        
	        // Dropping over surface
	        scene.setOnDragDropped( event -> 
	        {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    success = true;
                    for (File file : db.getFiles()) {
                    	myController.OpenFile(file);
                        System.out.println(file.getAbsolutePath());
                    }
                }
                event.setDropCompleted(success);
                event.consume();
	        });
			
		}
		catch (IOException e){
			Util.showError(e, e.getMessage());
		}            
	}

	public TerminalController getMyController(){
		return myController;
	}
}

public class TerminalController implements Initializable
{
	@FXML private TextArea txtSQL;
	@FXML private TextArea txtLog;
	@FXML private Label lblFileName;
	@FXML private ComboBox<Channel> cmbConnections;
	@FXML private TableView<ObservableList<String>> tblResult;
	@FXML private Menu mnuRecent;
	
	private StringProperty myFileName = new SimpleStringProperty(this,"" ,"<null>");
	private Optional<Connector> connector = Optional.ofNullable(null);
	private LinkedHashSet<RecentFile> recentFiles = new LinkedHashSet<>();

	/* Listener as a private member so that it can be removed.*/
	private ChangeListener<String> myListener = new ChangeListener<String>()
	{
		@Override
		public void changed(ObservableValue<? extends String> ov, String oldv, String newv)
		{
			txtSQL_textChanged();
		}
	};

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		lblFileName.textProperty().bind( myFileName );

		loadRecentFilesIntoMenu();
		
		if (Util.CONNECTION_FILE.exists())
		{
			ArrayList<Channel> myList = (ArrayList<Channel>) Util.readFileAsObject(Util.CONNECTION_FILE); 
			cmbConnections
				.getItems()
				.setAll( FXCollections.observableList(myList) );
		}
		
		cmbConnections.setCellFactory(p -> {
            return new ListCell<Channel>()
            {
            	@Override
            	protected void updateItem(Channel item, boolean empty)
            	{
            		super.updateItem(item, empty);
            		setText( (item == null)?"":item.getName() );
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
		
		
		/*Append to the popup menu*/
        TextAreaSkin customContextSkin = new TextAreaSkin(txtSQL) {
            @Override
            public void populateContextMenu(ContextMenu contextMenu) {
                super.populateContextMenu(contextMenu);
                contextMenu.getItems().add(0, new SeparatorMenuItem());
                MenuItem mntmRun = new MenuItem("Run");
                mntmRun.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
                mntmRun.setOnAction( (e) -> mntmRun_click() );
                contextMenu.getItems().add(0, mntmRun);
            }
        };
        txtSQL.setSkin(customContextSkin);
        txtSQL.textProperty().addListener(myListener);
        

	}//initialize

	public void mnuOpen_click()
	{
		Util.getFileChooserForOpen()
			.ifPresent((f) -> 
			{				
				txtSQL.textProperty().removeListener(myListener);
				OpenFile(f);
				
				recentFiles.add(new RecentFile(f.getAbsolutePath(), recentFiles.size()+1 ));
				Util.Save(recentFiles, Util.RECENT_FILES_LIST_FILE);
				loadRecentFilesIntoMenu();
				txtSQL.textProperty().addListener(myListener);
			});
	}

	public void OpenFile(File f)
	{
		String s = Util.readFileAsString(f);
		txtSQL.setText(s);	
		myFileName.set(f.getAbsolutePath());
	}
	
	
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

	public void btnEdit_click()
	{
		new Channels();
	}
	
	public void txtSQL_textChanged(){
		File f = new File (myFileName.get());
		if( f.exists() )
			lblFileName.setStyle("-fx-border-color:red; -fx-background-color: pink;");
	}
	
	//**********************************************
	
	public void cmbConnections_itemSelected()
	{
		Connector cn = new Connector( cmbConnections.getValue() );
		if (cn.isValid(3)){
			log("Connected Sucessfully :) to:\n" + 
				cmbConnections.getSelectionModel().getSelectedItem().getConnectionString() );
		}
		
		connector = Optional.ofNullable(cn);
	}
	
	
	public void cmbConnections_mouseClick()
	{
		if (Util.CONNECTION_FILE.exists())
		{
			ArrayList<Channel> myList = (ArrayList<Channel>) Util.readFileAsObject(Util.CONNECTION_FILE); 
			cmbConnections
				.getItems()
				.setAll( FXCollections.observableList(myList) );
		}
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
		String[] SQLs = txtSQL.getText().split(";");
		int total = 0;
		int i=0;
		//int count = StringUtils.countMatches(txtSQL.getText(), ";");
		for(; i<SQLs.length; i++)
		{
			total += SQLs[i].length()+1; 
			if(total >= crt)
				break;
		}
		
		String sql = SQLs[i].trim();
		runSQL(sql);
	}

	private void runSQL(final String sql)
	{
		int indexOfSql = txtSQL.getText().indexOf(sql);
		txtSQL.selectRange(indexOfSql, indexOfSql + sql.length());
		
		connector.ifPresent( connector -> 
		{
			System.out.println("Running: -"+sql+"-");
			buildData(sql);
		});
		
		
	}

	private void log(String str)
	{
		String fDate = Util.MY_TIME_FORMAT.format(new Date());
		txtLog.appendText(  fDate + ": " + str + "\n");	
	}
	
	public boolean buildData(String sql)
	{
		if(!connector.isPresent())
		{
			Util.showError("Database is not connected");
			return false;
		}
		
		PreparedStatement pstmt = connector.get().createPreparedStatement(sql);
		int c = connector.get().executeDDLorDML(pstmt);
		log( c + " Rows is selected.");
		
		tblResult.getColumns().clear();
		ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

		try
		{
			ResultSet rs = connector.get().getResultSet().get();

			/**********************************
			 * TABLE COLUMN ADDED DYNAMICALLY *
			 **********************************/
			for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++)
			{
				//We are using non property style for making dynamic table
				final int j = i;                
				String colName = rs.getMetaData().getColumnName(i+1);
				TableColumn<ObservableList<String>, String> col = new TableColumn<>(colName);
				
				col.setCellValueFactory( p -> 
				{
					return new SimpleStringProperty(p.getValue().get(j).toString());                        
				});

				tblResult.getColumns().addAll(col); 
				//System.out.println("Column ["+i+"] ");
			}

			/********************************
			 * Data added to ObservableList *
			 ********************************/
			//rs.beforeFirst(); //remove this if RS is Foraward Only.
			connector.get().refreshAllRecords(sql);
			rs = connector.get().getResultSet().get();
			while(rs.next())
			{
				//Iterate Row
				ObservableList<String> row = FXCollections.observableArrayList();
				for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++)
				{
					row.add(rs.getString(i));
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
	
	public void mnuAbout_click(){
		new About();
	}

}
