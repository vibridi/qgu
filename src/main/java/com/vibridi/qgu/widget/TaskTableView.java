package com.vibridi.qgu.widget;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import com.vibridi.fxu.dialog.FXDialog;
import com.vibridi.fxu.keyboard.FXKeyboard;
import com.vibridi.qgu.model.GanttTask;
import com.vibridi.qgu.util.TaskUtils;
import com.vibridi.qgu.widget.api.TaskListener;
import com.vibridi.qgu.widget.api.TaskTreeWalkerCallback;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;

public class TaskTableView extends TableView<ObservableGanttTask> {
	
	private final GanttTask listRoot;
	private final TableColumn<ObservableGanttTask, String> title;
	private final TableColumn<ObservableGanttTask, String> taskId;
	private final TableColumn<ObservableGanttTask, String> taskName;
	private final TableColumn<ObservableGanttTask, String> startDate;
	private final TableColumn<ObservableGanttTask, String> endDate;
	
	private TreeSet<String> absoluteList; // TODO NEXT: this is not updated across objects. 
	private Map<String,GanttTask> taskMap;
	
	public TaskTableView() {		
		listRoot = new GanttTask("root");
		
		setEditable(true);
		
		title = new TableColumn<ObservableGanttTask,String>("Task List");
		
		taskId = new TableColumn<ObservableGanttTask,String>("Id");
		taskId.setCellValueFactory(cellData -> new SimpleStringProperty(TaskUtils.pathToString(cellData.getValue().getTask().getPath())));
		taskId.setCellFactory(TextFieldTableCell.<ObservableGanttTask>forTableColumn());
		taskId.setSortable(false);
		
		taskName = new TableColumn<ObservableGanttTask,String>("Task");
		taskName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		taskName.setCellFactory(TextFieldTableCell.<ObservableGanttTask>forTableColumn());
		taskName.setSortable(false);
		
		startDate = new TableColumn<ObservableGanttTask,String>("Start");
		startDate.setCellValueFactory(cellData -> cellData.getValue().startDateProperty());
		startDate.setCellFactory(TextFieldTableCell.<ObservableGanttTask>forTableColumn());
		startDate.setSortable(false);
		
		endDate = new TableColumn<ObservableGanttTask,String>("End");
		endDate.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());
		endDate.setCellFactory(TextFieldTableCell.<ObservableGanttTask>forTableColumn());
		endDate.setSortable(false);
		
		title.getColumns().add(taskId);
		title.getColumns().add(taskName);
		title.getColumns().add(startDate);
		title.getColumns().add(endDate);
		
		taskId.prefWidthProperty().bind(this.widthProperty().multiply(0.15));
		taskName.prefWidthProperty().bind(this.widthProperty().multiply(0.40));
		startDate.prefWidthProperty().bind(this.widthProperty().multiply(0.225));
		endDate.prefWidthProperty().bind(this.widthProperty().multiply(0.225));		
		title.prefWidthProperty().bind(this.widthProperty());
		
		getColumns().add(title);

		absoluteList = new TreeSet<>();
		taskMap = new HashMap<>();
		
		FXKeyboard.setKeyCombinationShortcut(this, "Ctrl+Shift+N", event -> addTask(new GanttTask("New Task")));
	}
	
	/**
	 * Adds a task to the root node
	 * 
	 * @param task
	 * @return The absolute index of the new task in the task list
	 */
	public int addTask(GanttTask task) {
		listRoot.addChild(task);
		getItems().add(new ObservableGanttTask(task));
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
		listRoot.addChild(task, path);		
		int i = makeInternalRepresentation(task);
		getItems().add(i, new ObservableGanttTask(task));
		return i;
	}
	
	public void removeTask(int... path) {
		removeInternalRepresentation();
//		String sPath = TaskUtils.pathToString(path);
//		if(!absoluteList.contains(sPath))
//			return null;
//		itemRoot.removeChild(path);	// since this path comes from an actual item in the list, it must always exist
	}
	
	public GanttTask getGanttRoot() {
		return listRoot;
	}
	
	public void walkDepthFirst(TaskTreeWalkerCallback callback) {
		TaskUtils.walkDepthFirst(listRoot, callback);
	}
	
	public void clearTaskTree() {
		listRoot.clear();
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

	
	private Boolean onDebug(Integer absoluteIndex, ObservableGanttTask task) {
		System.out.print(task.getTask().getName() + " path:\t");
		TaskUtils.printPath(task.getTask().getPath());
		return true;
	}
	
	public void addTaskListener(TaskListener lsnr) {
		
	}
}
