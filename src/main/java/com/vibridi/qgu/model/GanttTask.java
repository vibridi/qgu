package com.vibridi.qgu.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GanttTask {
	
	private int level;
	private int[] path;
	private String name;
	private LocalDate startDate;
	private LocalDate endDate;
	//private long daysWorked;
	//private boolean isOverdue;
	
	//private GanttTask parent;
	private List<GanttTask> children;
	
	public GanttTask() {
		this("Task");
	}
	
	public GanttTask(String name) {
		this(name, LocalDate.now(), LocalDate.now().plusDays(1));
	}
	
	public GanttTask(String name, LocalDate startDate, LocalDate endDate) {
		this.level = 0;
		this.path = new int[0];
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		//this.parent = null;
		this.children = new ArrayList<GanttTask>();
	}
	
	@Override 
	public boolean equals(Object o) {
		if(!(o instanceof GanttTask))
			return false;
		
		GanttTask that = (GanttTask) o;
		
		if(!this.name.trim().equals(that.name.trim()))
			return false;
		
		if(!this.startDate.isEqual(that.startDate))
			return false;
		
		if(!this.endDate.isEqual(that.endDate))
			return false;
		
		return true;
		
	}
	
	public int size() {
		return children.size();
	}

	public List<GanttTask> getChildren() {
		return children;
	}
	
	public int[] getPath() {
		return path;
	}
	
	// appends to the end of the list
	public void addChild(GanttTask task) {
		task.level = level + 1;
		task.path = Arrays.copyOf(path, level + 1);
		task.path[task.level-1] = children.size();
		children.add(task);
	}
	
	public void removeChild(GanttTask task) {
		GanttTask rm = children.get(children.indexOf(task));
		
		
	}
	
	public GanttTask getChild(int... path) {
		GanttTask task = this;
		for(int i = 0; i < path.length; i++)
			task = task.getChildren().get(path[i]);
		return task;
	}
	
	public boolean isDueToday() {
		if(endDate.compareTo(LocalDate.now()) == 0)
			return true;
		return false;
	}
	
	public boolean isOverdue() {
		if(endDate.compareTo(LocalDate.now()) < 0)
			return true;
		return false;
	}
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	
}
