package com.vibridi.qgu.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.vibridi.fxu.dialog.FXDialog;
import com.vibridi.qgu.model.GanttTask;
import com.vibridi.qgu.util.TaskUtils;
import com.vibridi.qgu.widget.api.TaskListener;
import com.vibridi.qgu.widget.api.TaskTreeWalkerCallback;

import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;

public class TaskTreeView extends TreeTableView<ObservableGanttTask> {

	private final GanttTreeItem itemRoot;
	private final TreeTableColumn<ObservableGanttTask, String> title;
	private final TreeTableColumn<ObservableGanttTask, String> taskName;
	private final TreeTableColumn<ObservableGanttTask, String> startDate;
	private final TreeTableColumn<ObservableGanttTask, String> endDate;
	
	private List<TaskListener> taskLsnr; // TODO possibly not needed
	private TreeSet<String> absoluteList; // TODO NEXT: this is not updated across objects. 
	private Map<String,GanttTask> taskMap;
		
	public TaskTreeView() {
		itemRoot = new GanttTreeItem(new ObservableGanttTask(new GanttTask("root"))); 
		
		setRoot(itemRoot);
		setShowRoot(false);
		setEditable(true);
		
		title = new TreeTableColumn<ObservableGanttTask,String>("Task List");
		
		taskName = new TreeTableColumn<ObservableGanttTask,String>("Task");
		taskName.setCellValueFactory(cellData -> cellData.getValue().getValue().nameProperty());
		taskName.setCellFactory(TextFieldTreeTableCell.<ObservableGanttTask>forTreeTableColumn());
		taskName.setSortable(false);
		
		startDate = new TreeTableColumn<ObservableGanttTask,String>("Start");
		startDate.setCellValueFactory(cellData -> cellData.getValue().getValue().startDateProperty());
		startDate.setCellFactory(TextFieldTreeTableCell.<ObservableGanttTask>forTreeTableColumn());
		startDate.setSortable(false);
		
		endDate = new TreeTableColumn<ObservableGanttTask,String>("End");
		endDate.setCellValueFactory(cellData -> cellData.getValue().getValue().endDateProperty());
		endDate.setCellFactory(TextFieldTreeTableCell.<ObservableGanttTask>forTreeTableColumn());
		endDate.setSortable(false);
		
//		plus = new TreeTableColumn<ObservableGanttTask,String>("");
//		plus.setCellFactory(this::createTaskCell);
//		plus.setSortable(false);
//		plus.setResizable(false);
//		plus.setPrefWidth(TaskToolbarCell.MIN_WIDTH + 0.19999);
		
		title.getColumns().add(taskName);
		title.getColumns().add(startDate);
		title.getColumns().add(endDate);
		//title.getColumns().add(plus);
		
		taskName.prefWidthProperty().bind(this.widthProperty().multiply(0.5));
		startDate.prefWidthProperty().bind(this.widthProperty().multiply(0.25));
		endDate.prefWidthProperty().bind(this.widthProperty().multiply(0.25));		
		title.prefWidthProperty().bind(this.widthProperty());
		
		getColumns().add(title);
		
		taskLsnr = new ArrayList<>();
		absoluteList = new TreeSet<>();
		taskMap = new HashMap<>();
	}
	
	
	// TODO possibly not needed any more
	private TaskToolbarCell createTaskCell(TreeTableColumn<ObservableGanttTask, String> parent) {
		TaskToolbarCell taskCell = new TaskToolbarCell();
		taskCell.setOnAddChild(this::onAddChild);
		taskCell.setOnDeleteSelf(this::onDeleteSelf);
		taskCell.setOnDebug(this::onDebug);
		return taskCell;
	}
	
	// TODO not needed any more
	public void addTaskListener(TaskListener lsnr) {
		taskLsnr.add(lsnr);
	}
	
	/**
	 * Adds an empty task to the root node
	 * @return The absolute index of the new task in the task list
	 */
	public int addTask() {
		GanttTask task = new GanttTask("New Task " + itemRoot.size());
		itemRoot.addChild(new GanttTreeItem(task));
		return makeInternalRepresentation(task);
	}
	
	/**
	 * Adds an empty task as child of a specific item in the task tree
	 * 
	 * @param path
	 * @return The absolute index of the new task in the task list
	 */
	public int addTask(int... path) {
		GanttTask task = new GanttTask("New Task");
		itemRoot.addChild(new GanttTreeItem(task), path);
		return makeInternalRepresentation(task);
	}
	
	/**
	 * Adds an existing task to the root node
	 * 
	 * @param task
	 * @return The absolute index of the new task in the task list
	 */
	public int addTask(GanttTask task) {
		itemRoot.addChild(new GanttTreeItem(task));
		return makeInternalRepresentation(task);
	}
	
	/**
	 * Adds an existing task as child of a specific item in the task tree
	 * 
	 * @param task
	 * @param path
	 * @return The absolute index of the new task in the task list
	 */
	public int addTask(GanttTask task, int... path) {
		itemRoot.addChild(new GanttTreeItem(task), path);
		return makeInternalRepresentation(task);
	}
	
	public void removeTask(int... path) {
		removeInternalRepresentation();
//		String sPath = TaskUtils.pathToString(path);
//		if(!absoluteList.contains(sPath))
//			return null;
		itemRoot.removeChild(path);	// since this path comes from an actual item in the list, it must always exist
	}
	
	public GanttTask getGanttRoot() {
		return itemRoot.getValue().getTask();
	}
	
	public void walkDepthFirst(TaskTreeWalkerCallback callback) {
		TaskUtils.walkDepthFirst(itemRoot.getValue().getTask(), callback);
	}
	
	public void clearTaskTree() {
		itemRoot.clear();
	}
	
	/* ******************
	 * PRIVATE METHODS
	 * ******************/
	private int makeInternalRepresentation(GanttTask task) {
		String sPath = TaskUtils.pathToString(task.getPath());
		absoluteList.add(sPath);
		taskMap.put(sPath, task);
		return absoluteIndex(sPath);
	}
	
	private void removeInternalRepresentation() {
		
	}
	
	private int absoluteIndex(GanttTask task) {
		return absoluteIndex(TaskUtils.pathToString(task.getPath()));
		
	}
	
	private int absoluteIndex(int[] path) {
		return absoluteIndex(TaskUtils.pathToString(path));
	}
	
	private int absoluteIndex(String sPath) {
		if(taskMap.get(sPath) == null)
			return -1;
		return absoluteList.headSet(sPath).size();
	}
	
	private void fireAddEvent(int absoluteIndex, GanttTask task) {
		taskLsnr.forEach(lsnr -> lsnr.taskAddedEvent(absoluteIndex, task));
	}
	
	private Boolean onAddChild(Integer absoluteIndex, ObservableGanttTask observableTask) {
		addTask(observableTask.getTask().getPath());
		taskLsnr.forEach(lsnr -> lsnr.taskAddedEvent(absoluteIndex, observableTask.getTask()));
		return true;
	}
	
	private Boolean onDeleteSelf(Integer absoluteIndex, ObservableGanttTask observableTask) {
		if(observableTask.getTask().size() > 0) {
			ButtonType type = FXDialog.binaryChoiceAlert("This will delete also all sub-tasks. Proceed?").showAndWait().get();
			if(type == ButtonType.NO)
				return false;
		}
				
		removeTask(observableTask.getTask().getPath());
		taskLsnr.forEach(lsnr -> lsnr.taskRemovedEvent(absoluteIndex, observableTask.getTask()));
		return true;
	}
	
	private Boolean onDebug(Integer absoluteIndex, ObservableGanttTask task) {
		System.out.print(task.getTask().getName() + " path:\t");
		TaskUtils.printPath(task.getTask().getPath());
		return true;
	}	
	
	
}
