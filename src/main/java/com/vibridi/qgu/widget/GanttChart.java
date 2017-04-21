package com.vibridi.qgu.widget;

import java.time.LocalDate;
import java.time.Month;

import com.vibridi.qgu.model.GanttTask;
import com.vibridi.qgu.util.TaskUtils;
import com.vibridi.qgu.widget.api.TaskListener;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;

public class GanttChart {

	private TaskTreeView taskView;
	private TableView<GanttTask> timelineView;
	
//	private LocalDate chartStartDate;
//	private LocalDate chartEndDate;
	
	public GanttChart() {
		timelineView = new TableView<GanttTask>();
		timelineView.getStyleClass().add("qgu-timeline");
		timelineView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		initTimeline(LocalDate.now(),LocalDate.now().plusDays(60));

		taskView = new TaskTreeView();
		initTaskView();
	}
	
	private void initTimeline(LocalDate start, LocalDate end) {
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
		
		// TODO make timeline right-left scrollable with wheel on windows 
//		progressView.setOnScroll(event -> {
//			progressView.scrollToColumnIndex(progressView.getVisibleLeafColumns().size() + 1);
//			
//		});
		
	}
	
	private void initTaskView() {
		taskView.addTaskListener(new TaskListener() { // TODO place in its own file?

			@Override
			public void taskAddedEvent(int taskAbsoluteRow, GanttTask task) {
				if(taskAbsoluteRow > timelineView.getItems().size())
					timelineView.getItems().add(task);
				else
					timelineView.getItems().add(taskAbsoluteRow, task);
			}

			@Override
			public void taskRemovedEvent(int taskAbsoluteRow, GanttTask task) {
				timelineView.getItems().remove(taskAbsoluteRow);
			}
		});
		
		taskView.addTaskTree(TaskUtils.readTaskTree("tasktree.txt"));
	}

	public TreeTableView<ObservableGanttTask> getTaskView() {
		return taskView;
	}
	
	public TableView<GanttTask> getTimelineView() {
		return timelineView;
	}
	
	public void addTask(GanttTask task) {
		taskView.addTask(task);
	}
	
	public void addTask(GanttTask task, int... path) {
		taskView.addTask(task, path);
	}
}
