package com.vibridi.qgu.widget;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;

import com.vibridi.qgu.model.GanttTask;
import com.vibridi.qgu.util.TaskUtils;
import com.vibridi.qgu.widget.api.TaskListener;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;

public class GanttChart implements TaskListener {

	private static final double TOOLBAR_HEIGHT = 40.0;
	private static final String TASK_DATE_FORMAT = "yyyy-MM-dd"; // TODO put this in some global property
	
	private TaskTableView taskView;
	private TableView<GanttTask> timelineView;

	private ToolBar taskViewToolBar;
	
	private ToolBar timelineViewToolBar;
	
	private LocalDate chartStartDate;
	private LocalDate chartEndDate;

	public GanttChart(GanttTask root) {
		this();
		setGantt(root);
	}

	public GanttChart() {
		chartStartDate = LocalDate.now();
		chartEndDate = LocalDate.now().plusDays(60);
		
		timelineView = new TableView<GanttTask>();
		timelineView.getStyleClass().add("qgu-timeline");
		timelineView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		initTimeline(chartStartDate, chartEndDate);
		
		timelineViewToolBar = new ToolBar();
		initTimelineViewToolBar();
		
		//taskView = new TaskTreeView();
		taskView = new TaskTableView(TASK_DATE_FORMAT);
		taskView.getStyleClass().add("qgu-task-list");
		initTaskView();
		
		taskViewToolBar = new ToolBar();
		initTaskViewToolBar();
	}

	public void setTaskViewParent(AnchorPane parent) {
		AnchorPane.setTopAnchor(taskViewToolBar, 0.0);
		AnchorPane.setRightAnchor(taskViewToolBar, 0.0);
		AnchorPane.setLeftAnchor(taskViewToolBar, 0.0);
		
		AnchorPane.setTopAnchor(taskView, TOOLBAR_HEIGHT);
		AnchorPane.setBottomAnchor(taskView, 0.0);
		AnchorPane.setRightAnchor(taskView, 0.0);
		AnchorPane.setLeftAnchor(taskView, 0.0);

		parent.getChildren().addAll(taskViewToolBar, taskView);
	}

	public void setTimelineViewParent(AnchorPane parent) {
		AnchorPane.setTopAnchor(timelineViewToolBar, 0.0);
		AnchorPane.setRightAnchor(timelineViewToolBar, 0.0);
		AnchorPane.setLeftAnchor(timelineViewToolBar, 0.0);
		
		AnchorPane.setTopAnchor(timelineView, TOOLBAR_HEIGHT);
		AnchorPane.setBottomAnchor(timelineView, 0.0);
		AnchorPane.setRightAnchor(timelineView, 0.0);
		AnchorPane.setLeftAnchor(timelineView, 0.0);

		parent.getChildren().addAll(timelineViewToolBar, timelineView);
	}

	public void onShown() {
		Node n1 = taskView.lookup(".scroll-bar");
		Node n2 = timelineView.lookup(".scroll-bar");
		if(n1 instanceof ScrollBar && n2 instanceof ScrollBar)
			((ScrollBar) n1).valueProperty().bindBidirectional(((ScrollBar) n2).valueProperty());
		
		
	}

	public void setGantt(GanttTask root) {		
		taskView.clearTaskTree();
		TaskUtils.walkDepthFirst(root, node -> {
			if(node.isRoot())
				return;
			GanttTask task = node.clone();
			int index = taskView.addTask(task, Arrays.copyOf(node.getPath(), node.getPath().length - 1));
			//taskAddedEvent(index, task);
		});
		
	}
	
	public GanttTask getGanttRoot() {
		return taskView.getGanttRoot();
	}

	public void clear() {
		taskView.clearTaskTree();
		timelineView.getItems().clear();
	}

	public void resetTimeline(LocalDate start, LocalDate end) {
		chartStartDate = start;
		chartEndDate = end;
		timelineView.getColumns().clear();
		initTimeline(start,end);
	}

	public int addTask(GanttTask task) {
		return taskView.addTask(task);
	}

	public int addTask(GanttTask task, int... path) {
		return taskView.addTask(task, path);
	}

	
	/* ******************************************************** */
	/* 	PRIVATE METHOD											*/
	/* ******************************************************** */
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
	}

	private void initTaskView() {
		taskView.addTaskListener(this);
	}
	
	private void initTimelineViewToolBar() {
		timelineViewToolBar.setPrefHeight(TOOLBAR_HEIGHT);
	}
	
	private void initTaskViewToolBar() {
		taskViewToolBar.setPrefHeight(TOOLBAR_HEIGHT);
	}
	
	@Override
	public void taskAddedEvent(int taskRowIndex, GanttTask task) {
		timelineView.getItems().add(taskRowIndex, task);		
	}

	@Override
	public void taskEditedEvent(int taskRowIndex, GanttTask task) {
		
		System.out.println(task.toString());
		// TODO Auto-generated method stub
		//timelineView.getItems().
	}

	@Override
	public void taskRemovedEvent(int taskRowIndex) {
		timelineView.getItems().remove(taskRowIndex);
	}
}
