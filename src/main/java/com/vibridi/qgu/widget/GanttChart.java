package com.vibridi.qgu.widget;

import java.time.LocalDate;
import java.util.Arrays;

import com.vibridi.fxu.input.EditableLabel;
import com.vibridi.fxu.input.FXInput;
import com.vibridi.qgu.model.GanttMetadata;
import com.vibridi.qgu.model.GanttTask;
import com.vibridi.qgu.util.TaskUtils;
import com.vibridi.qgu.widget.api.TaskListener;

import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;

public class GanttChart implements TaskListener {

	private static final double TOOLBAR_HEIGHT = 40.0;
	private static final String TASK_DATE_FORMAT = "yyyy-MM-dd"; // TODO put this in some global property
	private static final String GANTT_DEFAULT_NAME = "New Gantt";
	
	private TaskTableView taskView;
	private EditableLabel projectName;
	
	private TimelineView timelineView;
	private DatePicker dpStart;
	private DatePicker dpEnd;
	
	private ToolBar taskViewToolBar;
	private ToolBar timelineViewToolBar;
	
	private GanttMetadata metadata;

	public GanttChart(GanttTask root) {
		this();
		setGantt(root);
	}

	public GanttChart() {
		timelineView = new TimelineView(LocalDate.now(), LocalDate.now().plusDays(60));

		timelineViewToolBar = new ToolBar();
		initTimelineViewToolBar();
		
		taskView = new TaskTableView(TASK_DATE_FORMAT);
		taskView.addTaskListener(this);
		
		taskViewToolBar = new ToolBar();
		initTaskViewToolBar();
		
		metadata = new GanttMetadata();
		metadata.ganttNameProperty().bind(projectName.baseTextProperty());
		metadata.chartStartDateProperty().bind(dpStart.valueProperty());
		metadata.chartEndDateProperty().bind(dpEnd.valueProperty());
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

	/*********************************************
	 *                                           *
	 * API METHODS			                     *
	 *                                           *
	 *********************************************/
	public void setGantt(GanttTask root) {		
		taskView.clear();
		TaskUtils.walkDepthFirst(root, node -> {
			if(node.isRoot())
				return;
			taskView.addTask(node.clone(), Arrays.copyOf(node.getPath(), node.getPath().length - 1));
		});
		
	}
	
	public GanttTask getGanttRoot() {
		return taskView.getGanttRoot();
	}
	
	public GanttMetadata getMetadata() {
		return metadata;
	}

	public void clear() {
		timelineView.clear();
		taskView.clear();
		taskView.addTask(new GanttTask("New Task"));
		projectName.setBaseText(GANTT_DEFAULT_NAME);
	}

//	public void resetTimeline(LocalDate start, LocalDate end) {
//		timelineView.reset(start, end);
//	}

	public int addTask(GanttTask task) {
		return taskView.addTask(task);
	}

	public int addTask(GanttTask task, int... path) {
		return taskView.addTask(task, path);
	}
	
	/*********************************************
	 *                                           *
	 * TASK LISTENER		                     *
	 *                                           *
	 *********************************************/
	@Override
	public void taskAddedEvent(int taskRowIndex, GanttTask task) {
		timelineView.getItems().add(taskRowIndex, task);		
	}

	@Override
	public void taskEditedEvent(int taskRowIndex, GanttTask task) {
		timelineView.getItems().set(taskRowIndex, task);
	}

	@Override
	public void taskRemovedEvent(int taskRowIndex) {
		timelineView.getItems().remove(taskRowIndex);
	}
	
	/*********************************************
	 *                                           *
	 * PRIVATE METHODS		                     *
	 *                                           *
	 *********************************************/
	private void initTimelineViewToolBar() {
		timelineViewToolBar.setPrefHeight(TOOLBAR_HEIGHT);
		
		dpStart = new DatePicker(timelineView.getTimelineStart());
		FXInput.setDateFormat(dpStart, TASK_DATE_FORMAT);
		dpStart.setOnAction(event -> {
			timelineView.resetStart(dpStart.getValue());
		});
		
		dpEnd = new DatePicker(timelineView.getTimelineEnd());
		FXInput.setDateFormat(dpEnd, TASK_DATE_FORMAT);
		dpEnd.setOnAction(event -> {
			timelineView.resetEnd(dpEnd.getValue());
		});
		
		timelineViewToolBar.getItems().addAll(new Label("From: "), dpStart, new Label("To: "), dpEnd);
	}
	
	private void initTaskViewToolBar() {
		taskViewToolBar.setPrefHeight(TOOLBAR_HEIGHT);
		projectName = new EditableLabel();
		projectName.setBaseText(GANTT_DEFAULT_NAME);
		taskViewToolBar.getItems().add(projectName);
	}
	
}
