package com.vibridi.qgu.widget;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

import com.vibridi.fxu.input.EditableLabel;
import com.vibridi.fxu.input.FXInput;
import com.vibridi.qgu.model.GanttData;
import com.vibridi.qgu.model.GanttTask;
import com.vibridi.qgu.storage.QGUStorageAgent;
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
	
	private GanttData metadata;
	private QGUStorageAgent storageAgent;
	private boolean saved;

	public GanttChart(GanttTask root) {
		this();
		setGantt(new GanttData("New Gantt", LocalDate.now(), LocalDate.now().plusDays(60), root));
	}

	public GanttChart() {
		timelineView = new TimelineView();

		timelineViewToolBar = new ToolBar();
		initTimelineViewToolBar();
		
		taskView = new TaskTableView(TASK_DATE_FORMAT);
		taskView.addTaskListener(this);
		
		taskViewToolBar = new ToolBar();
		initTaskViewToolBar();
		
		metadata = new GanttData();
		metadata.ganttNameProperty().bind(projectName.baseTextProperty());
		metadata.chartStartDateProperty().bind(dpStart.valueProperty());
		metadata.chartEndDateProperty().bind(dpEnd.valueProperty());
		metadata.setRoot(getGanttRoot());
		
		saved = false;
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
	/**
	 * 
	 * @param listener
	 */
	public void setStorageAgent(QGUStorageAgent storageAgent) {
		this.storageAgent = storageAgent;
	}
	
	public void loadGantt(File file) throws IOException {
		if(storageAgent == null)
			throw new IllegalStateException("Storage agent hasn't been set. Call setStorageAgent(...) before invoking load/save operations");
		setGantt(storageAgent.load(file));
	}
	
	public void saveGantt(File file) throws IOException {
		if(storageAgent == null)
			throw new IllegalStateException("Storage agent hasn't been set. Call setStorageAgent(...) before invoking load/save operations");
		storageAgent.save(file, metadata);
		saved = true;
	}
	
	public boolean isSaved() {
		return saved;
	}
	
	public GanttTask getGanttRoot() {
		return taskView.getGanttRoot();
	}
	
	public GanttData getMetadata() {
		return metadata;
	}

	/**
	 * Clears all tasks and timeline pieces and adds a default task as first element.
	 */
	public void clear() {
		timelineView.clear();
		taskView.clear();
		taskView.addTask(new GanttTask("New Task"));
		projectName.setBaseText(GANTT_DEFAULT_NAME);
		saved = false;
	}

//	public void resetTimeline(LocalDate start, LocalDate end) {
//		timelineView.reset(start, end);
//	}

	public int addTask(GanttTask task) {
		try {
			return taskView.addTask(task);
		} finally {
			saved = false;
		}
	}

	public int addTask(GanttTask task, int... path) {
		try {
			return taskView.addTask(task, path);
		} finally {
			saved = false;
		}
	}
	
	/*********************************************
	 *                                           *
	 * TASK LISTENER		                     *
	 *                                           *
	 *********************************************/
	@Override
	public void taskAddedEvent(int taskRowIndex, GanttTask task) {
		timelineView.getItems().add(taskRowIndex, task);
		registerChange();
	}

	@Override
	public void taskEditedEvent(int taskRowIndex, GanttTask task) {
		timelineView.getItems().set(taskRowIndex, task);
		registerChange();
	}

	@Override
	public void taskRemovedEvent(int taskRowIndex) {
		timelineView.getItems().remove(taskRowIndex);
		registerChange();
	}
	
	/*********************************************
	 *                                           *
	 * PRIVATE METHODS		                     *
	 *                                           *
	 *********************************************/
	/**
	 * Resets the gantt view and model to this gantt object.
	 * 
	 * @param data
	 */
	private void setGantt(GanttData data) {
		projectName.setBaseText(data.getGanttName());
		dpStart.setValue(data.getChartStartDate());
		dpEnd.setValue(data.getChartEndDate());
		timelineView.reset(dpStart.getValue(), dpEnd.getValue());
		
		taskView.clear();
		TaskUtils.walkDepthFirst(data.getRoot(), node -> {
			if(node.isRoot())
				return;
			taskView.addTask(node.clone(), Arrays.copyOf(node.getPath(), node.getPath().length - 1));
		});
		
	}
	
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
	
	private void registerChange() {
		saved = false;
	}
	
}
