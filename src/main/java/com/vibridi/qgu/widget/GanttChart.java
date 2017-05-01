package com.vibridi.qgu.widget;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import com.vibridi.fxu.dialog.FXDialog;
import com.vibridi.fxu.input.FormattableDatePicker;
import com.vibridi.fxu.keyboard.FXKeyboard;
import com.vibridi.qgu.exception.NestingLevelException;
import com.vibridi.qgu.model.GanttTask;
import com.vibridi.qgu.util.TaskUtils;
import com.vibridi.qgu.widget.api.TaskListener;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;

public class GanttChart {

	private static final double TASK_ID_VIEW_WIDTH = 50.0;
	private static final double TOOLBAR_HEIGHT = 40.0;
	private static final String TASK_DATE_FORMAT = "yyyy-MM-dd";
	
	private TableView<String> taskIdView;
	private TaskTreeView taskView;
	private TableView<GanttTask> timelineView;

	private ToolBar taskViewToolBar;
	private TextField taskIdField;
	private TextField taskNameField;
	private DatePicker startPicker;
	private DatePicker endPicker;
	private Button plus;
	
	private ToolBar timelineViewToolBar;
	
	private LocalDate chartStartDate;
	private LocalDate chartEndDate;

	public GanttChart(GanttTask root) {
		this();
		setGantt(root);
	}

	public GanttChart() {
		timelineView = new TableView<GanttTask>();
		timelineView.getStyleClass().add("qgu-timeline");
		timelineView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		initTimeline(LocalDate.now(),LocalDate.now().plusDays(60));
		
		timelineViewToolBar = new ToolBar();
		initTimelineViewToolBar();
		
		taskIdView = new TableView<String>();
		taskIdView.getStyleClass().add("qgu-task-id");
		taskIdView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		initTaskIdView();
		
		taskView = new TaskTreeView();
		taskView.getStyleClass().add("qgu-task-list");
		initTaskView();
		
		taskViewToolBar = new ToolBar();
		initTaskViewToolBar();
	}

	public void setTaskViewParent(AnchorPane parent) {
		AnchorPane.setTopAnchor(taskViewToolBar, 0.0);
		AnchorPane.setRightAnchor(taskViewToolBar, 0.0);
		AnchorPane.setLeftAnchor(taskViewToolBar, 0.0);
		
		AnchorPane.setTopAnchor(taskIdView, TOOLBAR_HEIGHT);
		AnchorPane.setBottomAnchor(taskIdView, 0.0);
		AnchorPane.setLeftAnchor(taskIdView, 0.0);
		
		AnchorPane.setTopAnchor(taskView, TOOLBAR_HEIGHT);
		AnchorPane.setBottomAnchor(taskView, 0.0);
		AnchorPane.setRightAnchor(taskView, 0.0);
		AnchorPane.setLeftAnchor(taskView, TASK_ID_VIEW_WIDTH);

		parent.getChildren().addAll(taskViewToolBar, taskIdView, taskView);
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
		
		FXKeyboard.setKeyCombinationShortcut(taskView.getScene().getRoot(), "Ctrl+Shift+N", event -> addTask());
	}

	public void setGantt(GanttTask root) {		
		taskView.clearTaskTree();
		AtomicInteger index = new AtomicInteger(0);
		TaskUtils.walkDepthFirst(root, node -> {
			if(node.getPath().length == 0)
				return;
			GanttTask task = node.clone();
			taskView.addTask(task, Arrays.copyOf(node.getPath(), node.getPath().length - 1));
			
			if(!task.isRoot())
				propagateTask(index.getAndIncrement(), task);
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
		timelineView.getColumns().clear();
		initTimeline(start,end);
	}

	public void addTask(GanttTask task) {
		taskView.addTask(task);
	}

	public void addTask(GanttTask task, int... path) {
		taskView.addTask(task, path);
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
				System.out.println("Task removed. Fired from TaskTreeView"); // TODO this is not used anymore
				//timelineView.getItems().remove(taskAbsoluteRow);
			}
		});
		
		FXKeyboard.setKeyCombinationShortcut(taskView, "Ctrl+Backspace", event -> removeTask());
	}
	
	private void initTaskIdView() {
		taskIdView.setPrefWidth(TASK_ID_VIEW_WIDTH);
		taskIdView.setEditable(false);
		
		TableColumn<String, String> dummy = new TableColumn<>("");
		
		TableColumn<String, String> taskIdCol = new TableColumn<>("Id");
		taskIdCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
		taskIdCol.setSortable(false);
		
		dummy.getColumns().add(taskIdCol);
		taskIdView.getColumns().add(dummy);
		
	}
	
	private void initTimelineViewToolBar() {
		timelineViewToolBar.setPrefHeight(TOOLBAR_HEIGHT);
	}
	
	private void initTaskViewToolBar() {
		taskViewToolBar.setPrefHeight(TOOLBAR_HEIGHT);
		
		taskIdField = new TextField();
		taskIdField.setPromptText("Id");
		taskIdField.setPrefWidth(TASK_ID_VIEW_WIDTH - 5.0);
		
		taskNameField = new TextField();
		taskNameField.setPromptText("Task name");
		taskNameField.prefWidthProperty().bind(taskView.widthProperty().multiply(0.27));
		
		startPicker = new FormattableDatePicker(TASK_DATE_FORMAT); // TODO make this an option
		startPicker.setPromptText("Start date");
		startPicker.setShowWeekNumbers(false); // TODO make this option
		startPicker.prefWidthProperty().bind(taskView.widthProperty().multiply(0.3));
		
		endPicker = new FormattableDatePicker(TASK_DATE_FORMAT);
		endPicker.setPromptText("End date");
		endPicker.setShowWeekNumbers(false);
		endPicker.prefWidthProperty().bind(taskView.widthProperty().multiply(0.3));
		
		plus = new Button("+");
		plus.setOnAction(event -> addTask());
				
		taskViewToolBar.getItems().addAll(taskIdField, taskNameField, startPicker, endPicker, plus);
	}
	
	private void propagateTask(int absoluteIndex, GanttTask task) {
		if(absoluteIndex > timelineView.getItems().size()) {
			timelineView.getItems().add(task);
			taskIdView.getItems().add(TaskUtils.pathToString(task.getPath()));
		} else {
			timelineView.getItems().add(absoluteIndex, task);
			taskIdView.getItems().add(absoluteIndex, TaskUtils.pathToString(task.getPath()));
		}
	}
	
	private void removeTask() {	
		int absoluteIndex = taskView.getSelectionModel().getSelectedIndex();
		GanttTask removedTask = taskView.getSelectionModel().getSelectedItem().getValue().getTask();
		
		if(removedTask.size() > 0) {
			ButtonType type = FXDialog.binaryChoiceAlert("This will delete also all sub-tasks. Proceed?").showAndWait().get();
			if(type == ButtonType.NO)
				return;
		}
		
		taskView.removeTask(removedTask.getPath());
		taskIdView.getItems().remove(absoluteIndex);
		timelineView.getItems().remove(absoluteIndex);
	}
	
	private void addTask() {
		if(taskNameField.getText().isEmpty())
			return;
		
		LocalDate startDate = chartStartDate;
		if(startPicker.getValue() != null)
			startDate = startPicker.getValue();
		
		LocalDate endDate = chartStartDate.plusDays(5);
		if(endPicker.getValue() != null)
			endDate = endPicker.getValue();
		
		GanttTask task = new GanttTask(taskNameField.getText(), startDate, endDate);
		
		try {
			if(taskIdField.getText().isEmpty()) {
				addTask(task);
			} else {
				int[] path = TaskUtils.stringToPath(taskIdField.getText());
				if(path.length > 3)
					throw new NestingLevelException("Tasks cannot be nested deeper than the 3rd level");
				addTask(task, Arrays.copyOf(path, path.length - 1));
			}
			
		} catch(NumberFormatException e) {
			FXDialog.errorAlert("Task ID is invalid", e).showAndWait();
			return;

		} catch(IndexOutOfBoundsException e) {
			FXDialog.errorAlert(e.getMessage(), e).showAndWait();
			return;

		} catch(NestingLevelException e) { 
			FXDialog.errorAlert(e.getMessage());
		} 
		
		// propagate
		// clear fields
		taskIdField.requestFocus();
		
	}
	
}
