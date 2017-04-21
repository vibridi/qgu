package com.vibridi.qgu.controller;

import com.vibridi.fxu.controller.BaseController;
import com.vibridi.qgu.Main;
import com.vibridi.qgu.model.GanttTask;
import com.vibridi.qgu.widget.GanttChart;
import com.vibridi.qgu.widget.ObservableGanttTask;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;

public class QGUViewController extends BaseController {

	@FXML private AnchorPane taskPane;
	@FXML private AnchorPane ganttPane;
	
	private GanttChart gantt;
	private TreeTableView<ObservableGanttTask> taskView;
	private TableView<GanttTask> timelineView;
	
	public QGUViewController() {
		gantt = new GanttChart();
		taskView = gantt.getTaskView();
		timelineView = gantt.getTimelineView();
	}
	
	@FXML
	public void initialize() {
		AnchorPane.setTopAnchor(taskView, 0.0);
		AnchorPane.setBottomAnchor(taskView, 0.0);
		AnchorPane.setRightAnchor(taskView, 0.0);
		AnchorPane.setLeftAnchor(taskView, 0.0);
		
		AnchorPane.setTopAnchor(timelineView, 0.0);
		AnchorPane.setBottomAnchor(timelineView, 0.0);
		AnchorPane.setRightAnchor(timelineView, 0.0);
		AnchorPane.setLeftAnchor(timelineView, 0.0);
		
		taskPane.getChildren().add(taskView);
		ganttPane.getChildren().add(gantt.getTimelineView());
	}
	
	@Override
	public void setup() {
		stage.getScene().getStylesheets().add(Main.class.getResource("css/qgu.css").toString());
		stage.setOnShown(event -> {
			Node n1 = taskView.lookup(".scroll-bar");
			Node n2 = timelineView.lookup(".scroll-bar");
			if(n1 instanceof ScrollBar && n2 instanceof ScrollBar)
				((ScrollBar) n1).valueProperty().bindBidirectional(((ScrollBar) n2).valueProperty());
		});
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
