package cdst.application;

import java.io.File;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;

/**
 * A class to provide utility functions to the main application.
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class AppController {
	public static void configureFolderChooser(DirectoryChooser dirChooser) {
		dirChooser.setTitle("Select directory to export OCL constraints");
		dirChooser.setInitialDirectory(new File(System.getProperty("user.home")));
	}
	public static GridPane createExceptionArea(String expMsg) {
		Label label = new Label("The exception stacktrace is:");

		TextArea textArea = new TextArea(expMsg);
		textArea.setEditable(false);
		textArea.setWrapText(false);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);
		return expContent;
	}
	
	public static int convertToSeconds(String tUnit, int time) {
		if(tUnit.equals("Minutes")) {
			time *= 60;
		}else if(tUnit.equals("Hours")) {
			time *= 3600;
		}
		return time;
	}
	
	public static String getTimeUnit(ComboBox<?> timeCB) {
		String tUnit = null;
		if(timeCB.getValue() == null) {
			if(!timeCB.getItems().isEmpty()){
				tUnit = timeCB.getItems().get(0).toString();
			}else {
				tUnit = "Seconds"; //default unit
			}
		}
		else
			tUnit = timeCB.getValue().toString();
		return tUnit;
	}
	
}
