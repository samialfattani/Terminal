package frawla.terminal.core;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Optional;

import javax.swing.JOptionPane;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;


public class Util
{

	public static SimpleDateFormat MY_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
	public static SimpleDateFormat MY_TIME_FORMAT = new SimpleDateFormat("hh:mm:ss");
	public static final Image CONNECTED_IMAGE = new Image(getResource("images/connect.jpg").toString());
	public static final Image DISCONNECTED_IMAGE = new Image(getResource("images/disconnect.jpg").toString());
	public static final File CONNECTION_FILE = new File("data/ConnectionList.dat") ;
	public static final File RECENT_FILES_LIST_FILE = new File("data/RecentFiles.dat") ;

	public Util()
	{
	}

  public static void RunApplication(File f) 
    {
        try {
            Desktop.getDesktop().open(f);
        }catch (IOException e) {
            Util.showError(e, e.getMessage());
        }
    }
  
	public static URI getResource(String s)
	{
		try{
			URI uri = Util.class.getClassLoader().getResource(s).toURI();
			return uri;
		}catch (URISyntaxException e){
			Util.showError(e , e.getMessage());
		}
		return null ;
	}

	public static URL getResourceAsURL(String s)
	{
		URL url = Util.class.getClassLoader().getResource(s);
		return url;
	}

	public static InputStream getResourceAsStream(String s)
	{		
		return Util.class.getClassLoader().getResourceAsStream(s);
	}

	public  static void cleanDirectory(File dir) 
	{
		if(!dir.exists())
			return;

		for (File file: dir.listFiles()) 
		{
			if (file.isDirectory()) 
				cleanDirectory(file);
			file.delete();
		}
	}	

	/** Converts a string into something you can safely insert into a URL. */
	public static String encodeURI(String s)
	{
		StringBuilder o = new StringBuilder();
		for (char ch : s.toCharArray()) {
			if (isUnsafe(ch)) {
				o.append('%');
				o.append(toHex(ch / 16));
				o.append(toHex(ch % 16));
			}
			else 
				o.append(ch);
		}
		return o.toString();
	}
	private static char toHex(int ch){
		return (char)(ch < 10 ? '0' + ch : 'A' + ch - 10);
	}
	private static boolean isUnsafe(char ch){
		if (ch < 0 || ch > 128) //not between 0~128
			return true;	    
		return " %$&+,/:;=?@<>#%\r\n\t".indexOf(ch) >= 0;
	}

	public static String getTempDir(){
		return System.getProperty("java.io.tmpdir");
	}
	public static File getTempFile() 
	{
		File f = null;
		try
		{
			File dir = new File(System.getProperty("java.io.tmpdir"));
			f = File.createTempFile( "Samitemp", null, dir );
		}
		catch (IOException e)
		{
			Util.showError(e, e.getMessage());
		}
		return f;		
	}

	public static String getCurrentDir()
	{		
		return new File("").getAbsolutePath();
	}

	public static Optional<File> getFileChooserForOpen()
	{
		FileChooser fc = new FileChooser();
		fc.setTitle("Open File");
		fc.getExtensionFilters().addAll(
				new ExtensionFilter("SQL Files", "*.sql"),
				new ExtensionFilter("Text Files", "*.txt"),
				new ExtensionFilter("All Files", "*.*"));
		fc.setTitle("Open SQL files");
		fc.setInitialDirectory(new File("."));
		File selectedFile = fc.showOpenDialog( null );
		return Optional.ofNullable(selectedFile);
	}

	public static Optional<File> getFileChooserForSave()
	{
		FileChooser fc = new FileChooser();
		fc.setTitle("Save File");
		fc.getExtensionFilters().addAll(
				new ExtensionFilter("SQL Files", "*.sql"),
				new ExtensionFilter("Text Files", "*.txt"),
				new ExtensionFilter("All Files", "*.*"));
		fc.setTitle("Save SQL files");
		fc.setInitialDirectory(new File("."));
		File selectedFile = fc.showSaveDialog( null );
		return Optional.ofNullable(selectedFile);
	}


	public static String formatTime(Duration d)
	{
		if (d.lessThan(Duration.ZERO)) {
			return "Unknown";
		}

		int seconds = (int) Math.floor(d.toSeconds());
		int h = seconds / 3600;
		int m = (seconds - h * 3600) / 60;
		int s = seconds - h * 3600 - m * 60;
		//int mi = ((int)(d.toMillis()) - seconds*1000)/100;

		if (h <= 0)
			return String.format("%02d:%02d", m, s );
		else
			return String.format("%d:%02d:%02d", h, m, s);
	}
	
	public static void copyFile(File source, File dest) 
	{
		try
		{
			Files.copy(source.toPath(), dest.toPath());
		}catch (FileAlreadyExistsException  e){
			Util.showError("File is already exists. Choose another file name.");
		}
		catch (IOException e){
			Util.showError(e, e.getMessage());
		}
	}
	

	public static void sleep(long mills)
	{
		try
		{
			Thread.sleep(mills);
		}
		catch (InterruptedException e)
		{
			showError(e, e.getMessage());
		}

	}


	/**
	 * @param tFile
	 *            : a text file
	 * @return: the file content as a String.
	 * @throws IOException
	 */
	public static String readFileAsString(final File tFile)
	{
		return readFileAsString(tFile, System.lineSeparator());
	}

	public static String readFileAsString(final File f, final String LineSeperator)
	{
		//final close all open objects
		try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));)
		{
			//read the extraxcted file normally.
			String line = null;
			StringBuilder str = new StringBuilder();

			if(!f.exists()){
				JOptionPane.showMessageDialog(null, "Subtitle File is not found !!", "File Not Found", JOptionPane.ERROR_MESSAGE);
				return "";
			}

			//first line only
			if ((line = in.readLine()) != null)
			{
				line = line.replace("\n", "").replace("\r", "");
				str.append(line);
			}

			while ((line = in.readLine()) != null)
			{
				str.append(LineSeperator + line);
			}
			return str.toString();
		}
		catch (IOException e)
		{
			showError(e, e.getMessage());
		}

		return "";
	}

	public static void Save(String str, File f){
		
		f.getParentFile().mkdirs();
		try(PrintWriter out = new PrintWriter(f);)
		{
			out.print( str );
			out.flush();
			out.close();
		}
		catch (FileNotFoundException e)
		{
			Util.showError(e, e.getMessage());
		}
	}

	public static void Save(Object obj, File f)
	{
		f.getParentFile().mkdirs();
		try
		{
			FileOutputStream fileOut = new FileOutputStream(f);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(obj);
			out.close();
			fileOut.close();
		}catch(IOException e){
			showError(e, e.getMessage());
		}    	
	}

	public static Object readFileAsObject(File f)
	{
		Object obj = null;
		try
		{
			if(!f.exists())
				return null;
			
			FileInputStream fileIn = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fileIn);
			obj = ois.readObject();
			ois.close();
			fileIn.close();
		}catch(Exception e){
			showError(e, e.getMessage());
		}
		return obj;
	}

	public static byte[] fileToByteArray(File file) throws FileNotFoundException, IOException
	{
		FileInputStream fin = new FileInputStream(file );
        
        int fileSize = (int) file.length();
        byte[] fileData = new byte[fileSize];

		fin.read(fileData, 0, fileSize); //fill the imgData        
        fin.close();
		return fileData;
	}

	public static void showError(String string){
		Platform.runLater( ()->{
			Alert alert = new Alert(AlertType.ERROR);
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(new Image(getResource("images/error.png").toString()));
			alert.setTitle("Error - " + System.getProperty("program.name"));
			alert.setHeaderText(null);
			alert.setContentText(string);
			alert.showAndWait();
		});    	
	}
	public static void showError(Exception ex, String string){
		Platform.runLater( ()->{
			showErrorInAppThread(ex, string);
		});    	
	}
	
	private static void showErrorInAppThread(Exception ex, String string)
	{
		new JFXPanel(); //initialize graphics
		Alert alert = new Alert(AlertType.ERROR);
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(getResource("images/error.png").toString()));
		alert.setTitle("Exception - " + System.getProperty("program.name"));
		alert.setHeaderText("Suddenly a problem has been occured, Please Contact the customer support");
		alert.setContentText(string);

		if(ex != null)
		{
			// Create expandable Exception.
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			String exceptionText = sw.toString();

			Label label = new Label("The exception stacktrace was:");

			TextArea textArea = new TextArea(exceptionText);
			textArea.setEditable(false);
			textArea.setWrapText(false);
			textArea.setFont(new Font("Consolas", 16));
			textArea.setStyle("-fx-font-weight: bold");

			textArea.setMaxWidth(Double.MAX_VALUE);
			textArea.setMaxHeight(Double.MAX_VALUE);
			GridPane.setVgrow(textArea, Priority.ALWAYS);
			GridPane.setHgrow(textArea, Priority.ALWAYS);

			GridPane expContent = new GridPane();
			expContent.setMaxWidth(Double.MAX_VALUE);
			expContent.add(label, 0, 0);
			expContent.add(textArea, 0, 1);

			// Set expandable Exception into the dialog pane.
			alert.getDialogPane().setExpandableContent(expContent);

			ex.printStackTrace();
		}

		alert.showAndWait();    	

	}//showErrorInAppThread


	public static boolean isNumeric(String s) {  
	    return s.matches("[-+]?\\d*\\.?\\d+");  
	}  
}
