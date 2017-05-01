package com.vibridi.qgu.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.vibridi.fxu.dialog.FXDialog;
import com.vibridi.qgu.model.GanttTask;
import com.vibridi.qgu.util.TaskUtils;
import com.vibridi.qgu.widget.api.TaskListener;
import com.vibridi.qgu.widget.api.TaskTreeWalkerCallback;

import javafx.collections.ListChangeListener;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
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
	
	//private Map<String,GanttTask> TODO add something for computing the absolute index of the task
		
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
	}
	
	private TaskToolbarCell createTaskCell(TreeTableColumn<ObservableGanttTask, String> parent) {
		TaskToolbarCell taskCell = new TaskToolbarCell();
		taskCell.setOnAddChild(this::onAddChild);
		taskCell.setOnDeleteSelf(this::onDeleteSelf);
		taskCell.setOnDebug(this::onDebug);
		return taskCell;
	}
	
	public void addTaskListener(TaskListener lsnr) {
		taskLsnr.add(lsnr);
	}
	
	/**
	 * Adds an empty task to the root node
	 * 
	 */
	public void addTask() {		
		itemRoot.addChild(new GanttTreeItem(new GanttTask("New Task " + itemRoot.size())));
	}
	
	/**
	 * Adds an empty task as child of a specific item in the task tree
	 * 
	 * @param path
	 */
	public void addTask(int... path) {
		itemRoot.addChild(new GanttTreeItem(new GanttTask("New Task")), path);
	}
	
	/**
	 * Adds an existing task to the root node
	 * 
	 * @param task
	 */
	public void addTask(GanttTask task) {
		GanttTreeItem item = new GanttTreeItem(task);
		itemRoot.addChild(item);
		//this.getRow(item);
	}
	
	/**
	 * Adds an existing task as child of a specific item in the task tree
	 * 
	 * @param task
	 * @param path
	 */
	public void addTask(GanttTask task, int... path) {
		itemRoot.addChild(new GanttTreeItem(task), path);
	}
	
	
	public void addTaskTree(GanttTask root) {
		itemRoot.clear();
		AtomicInteger index = new AtomicInteger(0);
		TaskUtils.walkDepthFirst(root, node -> {
			if(node.getPath().length == 0)
				return;
			GanttTask task = node.clone();
			itemRoot.addChild(new GanttTreeItem(task), Arrays.copyOf(node.getPath(), node.getPath().length - 1));
			
			if(!task.isRoot())
				fireAddEvent(index.getAndIncrement(), task);
		});
	}
	
	public GanttTreeItem removeTask(int... path) {
		return itemRoot.removeChild(path);
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
