package com.vibridi.qgu.widget;

import java.time.LocalDate;
import java.time.Month;

import com.vibridi.qgu.model.GanttTask;
import com.vibridi.qgu.util.TaskUtils;

import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;

public class GanttChart {

	private TaskTreeView taskView;
	private TableView<GanttTask> timelineView;
	
	private LocalDate chartStartDate;
	private LocalDate chartEndDate;
	
	public GanttChart() {
		taskView = new TaskTreeView();
		
		GanttTask root = TaskUtils.readTaskTree();
		
		taskView.addTaskTree(root);
		
		//taskView.addTask();
		//taskView.addTask();
		
//		taskView.getRoot().getChildren().add(new TreeItem<ObservableGanttTask>(new ObservableGanttTask(task)));
//		taskView.getRoot().getChildren().add(new TreeItem<ObservableGanttTask>(new ObservableGanttTask(task2)));
//		taskView.getRoot().getChildren().get(0).getChildren().add(new TreeItem<ObservableGanttTask>(new ObservableGanttTask(task)));
//		
		
		timelineView = new TableView<GanttTask>();
		timelineView.getStyleClass().add("qgu-timeline");
		timelineView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		
		
		chartStartDate = LocalDate.now();
		chartEndDate = LocalDate.now().plusDays(60);
		
		initChart(chartStartDate,chartEndDate);

	}
	
	private void initChart(LocalDate start, LocalDate end) {
		for(int m = start.getMonthValue(); m <= end.getMonthValue(); m++) {
			TableColumn<GanttTask,GanttBarPiece> monthCol = new TableColumn<GanttTask,GanttBarPiece>();
			Label month = new Label(Month.of(m).toString());
			monthCol.setGraphic(month);
			
			LocalDate chartStart = null;
			LocalDate tmp = LocalDate.of(start.getYear(), m, 1);
			
			chartStart = start.compareTo(tmp) > 0 ? start : tmp;
			
			for(int d = chartStart.getDayOfMonth(); d <= chartStart.lengthOfMonth(); d++) {
				GanttDayColumn c = new GanttDayColumn(chartStart.getYear(), m, d);
				c.setPrefWidth(20);
				Label day = new Label(Integer.toString(d));
				c.setGraphic(day);
				monthCol.getColumns().add(c);
			}
		
			timelineView.getColumns().add(monthCol);
		}
		
		timelineView.setItems(FXCollections.observableArrayList());
		
		GanttTask testTask = new GanttTask(); //TODO 
		testTask.setStartDate(LocalDate.now().plusDays(5));
		testTask.setEndDate(LocalDate.now().plusDays(10));
		
		timelineView.getItems().add(testTask);
		
//		progressView.setOnScroll(event -> {
//			progressView.scrollToColumnIndex(progressView.getVisibleLeafColumns().size() + 1);
//			
//		});
		
	}

	public TreeTableView<ObservableGanttTask> getTaskView() {
		return taskView;
	}
	
	public TableView<GanttTask> getTimelineView() {
		return timelineView;
	}
	
//	public void setRootText(String text) {
//		taskView.getRoot().setValue(text);
//	}
	
	public void addTask(GanttTask task) {
		timelineView.getItems().add(task);
	}
	
	public void addTask(int index, GanttTask task) {
		timelineView.getItems().add(index, task);
	}
}
