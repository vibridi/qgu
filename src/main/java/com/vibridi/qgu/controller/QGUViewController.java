package com.vibridi.qgu.controller;

import java.io.File;
import java.io.IOException;

import com.vibridi.fxu.controller.BaseController;
import com.vibridi.fxu.dialog.FXDialog;
import com.vibridi.fxu.keyboard.FXKeyboard;
import com.vibridi.qgu.Main;
import com.vibridi.qgu.exception.UnreadableGanttFileException;
import com.vibridi.qgu.storage.QGUStorageManager;
import com.vibridi.qgu.widget.GanttChart;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;

public class QGUViewController extends BaseController {

	@FXML private AnchorPane taskPane;
	@FXML private AnchorPane ganttPane;
	@FXML private Menu openRecentItem;
	
	private GanttChart gantt;
	private File currentFile;
	private QGUStorageManager storageManager;
	
	public QGUViewController() {
		storageManager = QGUStorageManager.instance;
		gantt = new GanttChart();
		gantt.setStorageAgent(storageManager);
		
		try {
			File last = storageManager.loadLastViewed();
			if(last != null && last.exists())
				gantt.loadGantt(last);
			else
				emptyGantt();
		} catch (IOException e) {
			storageManager.forgetLastViewed();
		}
	}
	
	@FXML
	public void initialize() {		
		gantt.setTaskViewParent(taskPane);
		gantt.setTimelineViewParent(ganttPane);
		
		// initialize recent files menu and sync's it to the storageManager list
		storageManager.getRecentFiles().forEach(f -> openRecentItem.getItems().add(recentFileItem(f)));
		storageManager.getRecentFiles().addListener((ListChangeListener.Change<? extends File> c) -> {
			while(c.next()) {
				if(c.wasAdded()) {
					for(File addFile : c.getAddedSubList())
						openRecentItem.getItems().add(recentFileItem(addFile));
				}
				if(c.wasRemoved()) {
					for(File rmFile : c.getRemoved()) {
						openRecentItem.getItems().filtered(item -> { 
							return ((File)item.getUserData()).getName().equals(rmFile.getName()); 
						}).clear();
					}
				}
			} // end while
		}); // end lambda
	}
	
	@Override
	public void setup() {
		stage.getScene().getStylesheets().add(Main.class.getResource("css/qgu.css").toString());
		stage.setOnShown(event -> gantt.onShown());
		
		FXKeyboard.setKeyCombinationShortcut(stage.getScene().getRoot(), "Ctrl+N", event -> newGantt());
		FXKeyboard.setKeyCombinationShortcut(stage.getScene().getRoot(), "Ctrl+O", event -> openGantt());
		FXKeyboard.setKeyCombinationShortcut(stage.getScene().getRoot(), "Ctrl+E", event -> exportGantt());
		FXKeyboard.setKeyCombinationShortcut(stage.getScene().getRoot(), "Ctrl+S", event -> saveGantt());
		FXKeyboard.setKeyCombinationShortcut(stage.getScene().getRoot(), "Ctrl+Shift+S", event -> saveGanttAs());
	}

	@FXML
	public void newGantt() {
		if(!gantt.isSaved()) {
			ButtonType bt = FXDialog.binaryChoiceAlert("The current project has not been saved. Proceed?").showAndWait().get();
			if(bt == ButtonType.YES) {
				emptyGantt();
			}
		}		
	}
	
	@FXML
	public void openGantt() {
		try {
			File f = FXDialog.openFile(stage, "gtt");
			if(f != null)
				openFile(f);
		} catch (Throwable e) {
			FXDialog.errorAlert(e.getMessage(), e).showAndWait(); // TODO make error msg conditional based on debug flag
		}
	}
	
	@FXML
	public void exportGantt() {
		System.out.println("Menu choice: export");
	}
	
	@FXML
	public void saveGantt() {
		try {
			if(currentFile == null) {
				currentFile = FXDialog.saveFile(stage, "gtt");
				if(currentFile == null || !currentFile.exists())
					return;
			}
			gantt.saveGantt(currentFile);		
			storageManager.rememberLastViewed(currentFile);
		} catch(Exception e) {
			FXDialog.errorAlert("Cannot save file", e).showAndWait();
		}
	}
	
	@FXML 
	public void saveGanttAs() {
		try {
			currentFile = FXDialog.saveFile(stage, "gtt");
			if(currentFile == null || !currentFile.exists())
				return;
			gantt.saveGantt(currentFile);
			storageManager.rememberLastViewed(currentFile);
		} catch(Exception e) {
			FXDialog.errorAlert("Cannot save file", e).showAndWait();
		}
	}
	
	@Override
	protected void handleCloseRequest(WindowEvent event) {
		if(!gantt.isSaved()) { // TODO add cancel button
			ButtonType bt = FXDialog.binaryChoiceAlert("There are unsaved changes in this project. Save and close?").showAndWait().get();
			if(bt == ButtonType.YES) {
				saveGantt();
			} else {
				event.consume();
				return;
			}
		}	
		
		storageManager.rememberLastViewed(currentFile);
	}
	
	@FXML
	public void toolbarNewItem() { // TODO change
		System.out.println("Menu choice: toolbarNewItem");
	}
	
	@FXML
	public void toolbarRemoveItem() {
		System.out.println("Menu choice: toolbarRemoveItem");
	}

	@FXML
	public void toolbarDebug() {
		System.out.println("Menu choice: toolbarDebug");
	}
	
	/*********************************************
	 *                                           *
	 * PRIVATE METHODS		                     *
	 *                                           *
	 *********************************************/
	/**
	 * Initializes the current views to an empty gantt chart
	 */
	private void emptyGantt() {
		gantt.clear();
		currentFile = null;
	}
	
	/**
	 * 
	 * @param f
	 * @throws UnreadableGanttFileException
	 */
	private void openRecent(File f) {
		if(!f.exists()) { // user might have moved it
			FXDialog.warningAlert("This file doesn't exist anymore").showAndWait();
			storageManager.removeRecentFile(f);
			return;
		}
		
		try {
			openFile(f);
		} catch(IOException e) {
			FXDialog.errorAlert(e.getMessage(), e).showAndWait(); // TODO make error msg conditional based on debug flag
		}
	}
	
	private void openFile(File f) throws IOException {
		currentFile = f;
		gantt.loadGantt(f);
	}
	
	private MenuItem recentFileItem(File f) {
		MenuItem item = new MenuItem(f.getName() + "\t[" + f.getAbsolutePath() + "]");
		item.setUserData(f);
		item.setOnAction(event -> openRecent(f));
		return item;
	}
	
}
