package com.vibridi.qgu.controller;

import com.vibridi.fxmlutils.controller.BaseController;
import com.vibridi.qgu.Main;
import com.vibridi.qgu.model.GanttTask;
import com.vibridi.qgu.widget.GanttChart;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;

public class QGUViewController extends BaseController {

	@FXML private AnchorPane taskPane;
	@FXML private AnchorPane ganttPane;
	
	private GanttChart gantt;
	private TreeView<String> taskList;
	private TableView<GanttTask> progressView;
	
	public QGUViewController() {
		gantt = new GanttChart();
		taskList = gantt.getTaskView();
		progressView = gantt.getTimelineView();
	}
	
	@FXML
	public void initialize() {
		AnchorPane.setTopAnchor(taskList, 0.0);
		AnchorPane.setBottomAnchor(taskList, 0.0);
		AnchorPane.setRightAnchor(taskList, 0.0);
		AnchorPane.setLeftAnchor(taskList, 0.0);
		
		AnchorPane.setTopAnchor(progressView, 0.0);
		AnchorPane.setBottomAnchor(progressView, 0.0);
		AnchorPane.setRightAnchor(progressView, 0.0);
		AnchorPane.setLeftAnchor(progressView, 0.0);
		
		taskPane.getChildren().add(taskList);
		ganttPane.getChildren().add(gantt.getTimelineView());
	}
	
	@Override
	public void setup() {
		stage.getScene().getStylesheets().add(Main.class.getResource("css/qgu.css").toString());
	}

	@FXML
	public void newGantt() {
		
	}
	
	@FXML
	public void exportGantt() {
		
	}
	
	@Override
	protected void handleCloseRequest(WindowEvent event) {
		// TODO Auto-generated method stub
		
	}

}
