package com.vibridi.qgu.controller;

import java.io.File;
import java.io.IOException;

import com.vibridi.fxu.controller.BaseController;
import com.vibridi.fxu.dialog.FXDialog;
import com.vibridi.fxu.keyboard.FXKeyboard;
import com.vibridi.qgu.Main;
import com.vibridi.qgu.exception.UnreadableGanttFileException;
import com.vibridi.qgu.storage.QGUStorageManager;
import com.vibridi.qgu.util.TaskUtils;
import com.vibridi.qgu.widget.GanttChart;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;

public class QGUViewController extends BaseController {

	@FXML private AnchorPane taskPane;
	@FXML private AnchorPane ganttPane;
	
	private GanttChart gantt;
	
	public QGUViewController() {
		// TODO initialize menu recent files
		
		gantt = new GanttChart(TaskUtils.readTaskTree("tasktree.txt")); // TODO initialize this by loading the last gantt project worked on OR a blank one
	}
	
	@FXML
	public void initialize() {		
		gantt.setTaskViewParent(taskPane);
		gantt.setTimelineViewParent(ganttPane);
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
		
		//FXKeyboard.setKeyCombinationShortcut(stage.getScene().getRoot(), "Ctrl+T", event -> taskView.requestFocus());
		
	}

	@FXML
	public void newGantt() {
		// TODO check if there is an open gantt and warn user
		gantt.clear();
		QGUStorageManager.instance.setHandle(null);
	}
	
	@FXML
	public void openGantt() {
		try {
			File f = FXDialog.openFile(stage, "gtt");
			if(f != null)
				gantt.setGantt(QGUStorageManager.instance.loadGantt(f));
			
		} catch (UnreadableGanttFileException e) {
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
			if(!QGUStorageManager.instance.hasHandle()) {
				File f = FXDialog.saveFile(stage, "gtt");
				QGUStorageManager.instance.setHandle(f);
			}
			QGUStorageManager.instance.saveGantt(gantt.getGanttRoot());
			
		} catch(IOException e) {
			FXDialog.errorAlert("Cannot save file", e).showAndWait();
		}
	}
	
	@FXML 
	public void saveGanttAs() {
		try {
			File f = FXDialog.saveFile(stage, "gtt");
			QGUStorageManager.instance.saveGantt(f, gantt.getGanttRoot());
			
		} catch(IOException e) {
			FXDialog.errorAlert("Cannot save file", e).showAndWait();
		}
	}
	
	@Override
	protected void handleCloseRequest(WindowEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	@FXML
	public void toolbarNewItem() {
		System.out.println("Menu choice: toolbarNewItem");
		//taskView.addTask(TaskUtils.randomTask(LocalDate.of(2017, 5, 10), LocalDate.of(2017, 5, 31)));
	}
	
	@FXML
	public void toolbarRemoveItem() {
		System.out.println("Menu choice: toolbarRemoveItem");
	}

	@FXML
	public void toolbarDebug() {
		System.out.println("Menu choice: toolbarDebug");
	}
}
