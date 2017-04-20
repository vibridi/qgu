package com.vibridi.qgu.widget;

import com.vibridi.qgu.model.GanttTask;

import javafx.scene.control.TreeItem;

public class GanttTreeItem extends TreeItem<ObservableGanttTask> {

	private GanttTask task;
	
	// TODO consider whether it is a good idea to have this ctor
	public GanttTreeItem(GanttTask task) {
		this(new ObservableGanttTask(task));
	}
	
	public GanttTreeItem(ObservableGanttTask taskItem) {
		super(taskItem);
		this.task = taskItem.getTask();
		
		setExpanded(true);
	}
	
	public int size() {
		return getChildren().size();
	}
	
	public void addChild(GanttTreeItem child) {
		task.addChild(child.getValue().getTask());
		getChildren().add(child);
	}
	
	public void addChild(GanttTreeItem child, int... path) {
		GanttTreeItem parent = this;
		for(int i = 0; i < path.length; i++) {
			parent = (GanttTreeItem) parent.getChildren().get(path[i]);
			
		}
		parent.addChild(child);
	}

	public GanttTreeItem removeChild(int index) { // TODO remove last
		
		return (GanttTreeItem) getChildren().remove(index);
	}
	
	public GanttTreeItem remove
	
}
