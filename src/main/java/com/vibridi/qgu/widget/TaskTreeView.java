package com.vibridi.qgu.widget;

import com.vibridi.fxu.dialog.FXDialog;
import com.vibridi.qgu.model.GanttTask;
import com.vibridi.qgu.util.TaskUtils;

import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;

public class TaskTreeView extends TreeTableView<ObservableGanttTask> {

	private final TreeItem<ObservableGanttTask> root;
	private final TreeTableColumn<ObservableGanttTask, String> title;
	private final TreeTableColumn<ObservableGanttTask, String> taskName;
	private final TreeTableColumn<ObservableGanttTask, String> startDate;
	private final TreeTableColumn<ObservableGanttTask, String> endDate;
	private final TreeTableColumn<ObservableGanttTask, String> plus;
	
	public TaskTreeView() {
		root = new TreeItem<ObservableGanttTask>(new ObservableGanttTask(new GanttTask())); 
		root.setExpanded(true);
		
		setRoot(root);
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
	
	public void addTaskTree(GanttTask root) {
		TaskUtils.traverseDepthFirst(root, task -> {
			if(task.getLevel() == 0)
				return null;
			
			addChild(new ObservableGanttTask(task));
			return null;
		});
	}
	
	public void addTask() {
		onAddChild(root.getChildren().size(), root.getValue());
	}
	
	private TaskToolbarCell createTaskCell(TreeTableColumn<ObservableGanttTask, String> parent) {
		TaskToolbarCell taskCell = new TaskToolbarCell();
		taskCell.setOnAddChild(this::onAddChild);
		taskCell.setOnDeleteSelf(this::onDeleteSelf);
		return taskCell;
	}
	
	private Boolean onAddChild(Integer absoluteIndex, ObservableGanttTask task) {
		addChild(task);

		// TODO fire event or call timeline method
		return true;
	}
	
	private Boolean onDeleteSelf(Integer absoluteIndex, ObservableGanttTask task) {
		if(task.getTask().getChildren().size() > 0) {
			ButtonType type = FXDialog.binaryChoiceAlert("This will delete also all sub-tasks. Proceed?").showAndWait().get();
			if(type == ButtonType.NO)
				return false;
		}
		
		TreeItem<ObservableGanttTask> parent = root;
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
	
	
	private void addChild(ObservableGanttTask parent) {
		GanttTask child = new GanttTask("New Task " + parent.getTask().getChildren().size());
		parent.getTask().addChild(child);
		addChild(parent, new ObservableGanttTask(child));
	}
	
	// TODO doesn't work with addTaskTree
	private void addChild(ObservableGanttTask parentTask, ObservableGanttTask childTask) {
		TreeItem<ObservableGanttTask> parent = root;
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
