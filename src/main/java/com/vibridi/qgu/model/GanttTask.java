package com.vibridi.qgu.model;

import java.time.LocalDate;
import java.util.List;

public class GanttTask {
	
	private String name;
	private LocalDate startDate;
	private LocalDate endDate;
	private long daysWorked;
	//private boolean isOverdue;
	
	private List<GanttTask> children;
	
	public GanttTask() {
		this("Task");
	}
	
	public GanttTask(String name) {
		this(name, LocalDate.now(), LocalDate.now().plusDays(1));
	}
	
	public GanttTask(String name, LocalDate startDate, LocalDate endDate) {
		this.name = name;
		this.setStartDate(startDate);
		this.endDate = endDate;
		daysWorked = 0;
	}

	public List<GanttTask> getChildren() {
		return children;
	}
	
	public void addChild(GanttTask task, int... path) {
		GanttTask parent = this;
		for(int i = 0; i < path.length; i++) {
			parent = parent.getChildren().get(path[i]);	
		}
		parent.getChildren().add(task);
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
