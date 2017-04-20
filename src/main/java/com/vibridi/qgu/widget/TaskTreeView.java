package com.vibridi.qgu.widget;

import com.vibridi.fxu.dialog.FXDialog;
import com.vibridi.qgu.model.GanttTask;

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
	private final TreeTableColumn<ObservableGanttTask, String> plus;
		
	public TaskTreeView() {
		itemRoot = new GanttTreeItem(new ObservableGanttTask(new GanttTask())); 
		
		setRoot(itemRoot);
		setShowRoot(false);
		setEditable(true);
		//setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
		
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
		
		plus = new TreeTableColumn<ObservableGanttTask,String>("");
		plus.setCellFactory(this::createTaskCell);
		plus.setSortable(false);
		plus.setResizable(false);
		plus.setPrefWidth(TaskToolbarCell.MIN_WIDTH + 0.19999);
		
		title.getColumns().add(taskName);
		title.getColumns().add(startDate);
		title.getColumns().add(endDate);
		title.getColumns().add(plus);
		
		taskName.prefWidthProperty().bind(this.widthProperty().subtract(TaskToolbarCell.MIN_WIDTH).multiply(0.33329));
		startDate.prefWidthProperty().bind(this.widthProperty().subtract(TaskToolbarCell.MIN_WIDTH).multiply(0.33329));
		endDate.prefWidthProperty().bind(this.widthProperty().subtract(TaskToolbarCell.MIN_WIDTH).multiply(0.33329));		
		title.prefWidthProperty().bind(this.widthProperty());
		
		getColumns().add(title);
		
		getStyleClass().add("qgu-task-list");
	}
	
	private TaskToolbarCell createTaskCell(TreeTableColumn<ObservableGanttTask, String> parent) {
		TaskToolbarCell taskCell = new TaskToolbarCell();
		taskCell.setOnAddChild(this::onAddChild);
		taskCell.setOnDeleteSelf(this::onDeleteSelf);
		return taskCell;
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
		itemRoot.addChild(new GanttTreeItem(new GanttTask("New Task " + itemRoot.size())), path);
	}
	
	/**
	 * Adds an existing task to the root node
	 * 
	 * @param task
	 */
	public void addTask(GanttTask task) {
		itemRoot.addChild(new GanttTreeItem(task));
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
		
		walkTree(root, null);
		
//		taskRoot = root;
//		
//		
//        
//        for(String line : lines) {
//        	int tab = line.lastIndexOf('\t');
//        	GanttTask parent = root;
//        	for(int i = 0; i < tab; i++)
//        		parent = parent.getChildren().get(parent.getChildren().size() - 1);
//        	parent.addChild(new GanttTask(line));
//        }
	}
	
	private GanttTreeItem walkTree(GanttTreeItem parent, GanttTreeItem child) {	
		if(child.size() == 0) 
			return child;
		
		
		
	}
	
	
	private Boolean onAddChild(Integer absoluteIndex, ObservableGanttTask observableTask) {
		addTask(observableTask.getTask().getPath());

		// TODO fire event or call timeline method
		// the absolute index is used to add the row in the gantt timeline
		return true;
	}
	
	private Boolean onDeleteSelf(Integer absoluteIndex, ObservableGanttTask task) {
		if(task.getTask().getChildren().size() > 0) {
			ButtonType type = FXDialog.binaryChoiceAlert("This will delete also all sub-tasks. Proceed?").showAndWait().get();
			if(type == ButtonType.NO)
				return false;
		}
		
		TreeItem<ObservableGanttTask> parent = itemRoot;
		int[] path = task.getTask().getPath();
		
		System.out.print("Removing " + task.getName() + " ");
		for(int i = 0; i < path.length; i++)
			System.out.print(path[i] + "\t");
		System.out.println("");
		
		for(int i = 0; i < path.length - 1; i++)
			parent = parent.getChildren().get(path[i]);
		
		parent.getChildren().remove(path[path.length - 1]);
		
		// TODO fire event or call timeline method
		
		return true;
	}
	
	// TODO doesn't work with addTaskTree
	private void addChild(ObservableGanttTask parentTask, ObservableGanttTask childTask) {
		TreeItem<ObservableGanttTask> parent = itemRoot;
		int[] path = childTask.getTask().getPath();
		
		for(int i = 0; i < path.length; i++)
			System.out.print(path[i] + "\t");
		System.out.println("");
		
		
		for(int i = 0; i < path.length - 1; i++)
			parent = parent.getChildren().get(path[i]);
		
		TreeItem<ObservableGanttTask> item = new TreeItem<>(childTask);
		item.setExpanded(true);
		parent.getChildren().add(item);
	}
	
}
