package com.vibridi.qgu.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.vibridi.qgu.util.TaskUtils;

public class GanttTask implements Cloneable {
	
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
	
	public GanttTask(GanttTask that) {
		this.level = 0;
		this.path = new int[0];
		this.name = that.name;
		this.startDate = LocalDate.from(that.startDate);
		this.endDate = LocalDate.from(that.endDate);
		this.children = new ArrayList<GanttTask>();
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
	
	/**
	 * Returns a task with the same name, start date and end date as this one. <br>
	 * Important: the children list is NOT copied. 
	 * 
	 */
	@Override
	public GanttTask clone() {
		return new GanttTask(name, LocalDate.from(startDate), LocalDate.from(endDate));
	}
	
	public boolean isRoot() {
		return level == 0;
	}
	
	public int size() {
		return children.size();
	}
	
	public int subtreeSize() {
		AtomicInteger acc = new AtomicInteger(0);
		TaskUtils.walkDepthFirst(this, node -> {
			acc.incrementAndGet();
		});
		return acc.get();
	}
	
	public void clear() {
		children.clear();
	}
	
	public int[] getPath() {
		return path;
	}
	
	public boolean comparePathTo(int[] that) {
		return Arrays.equals(this.path, that);
	}
	
	/**
	 * Returns a list of tasks. The list is obtained by searching the tree 
	 * rooted in this node in depth-first order.
	 * 
	 * @return
	 */
	public List<GanttTask> toFlatList() {
		List<GanttTask> list = new ArrayList<>();
		TaskUtils.walkDepthFirst(this, node -> {
			list.add(node);
		});
		return list;
	}
	
	/**
	 * Appends a task to the end of the children list.
	 * 
	 * @param task
	 */
	public void addChild(GanttTask task) {
		task.level = level + 1;
		task.path = Arrays.copyOf(path, level + 1);
		task.path[task.level-1] = children.size();
		children.add(task);
	}
	
	public GanttTask getChild(int... path) {
		GanttTask task = this;
		for(int i = 0; i < path.length; i++)
			task = task.children.get(path[i]);
		return task;
	}
	
	public GanttTask removeChild(GanttTask task) {
		return removeChild(children.indexOf(task));
	}
	
	/**
	 * Removes the child task at the given path starting from this node. 
	 * 
	 * @param path
	 * @return
	 */
	public GanttTask removeChild(int... path) {
		GanttTask task = this;
		for(int i = 0; i < path.length - 1; i++)
			task = task.getChild(path[i]);
		
		AtomicInteger taskLevel = new AtomicInteger(task.level);				// store level of current node
		for(int i = path[path.length - 1] + 1; i < task.children.size(); i++) { // cycle over siblings starting from the next one
			TaskUtils.walkDepthFirst(task.children.get(i), node -> {			// walk subtrees starting from siblings
				node.path[taskLevel.get()]--;
			});
		}
		
		return task.children.remove(path[path.length - 1]);
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
