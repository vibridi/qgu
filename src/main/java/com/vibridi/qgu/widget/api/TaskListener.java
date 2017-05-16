package com.vibridi.qgu.widget.api;

import com.vibridi.qgu.model.GanttTask;

public interface TaskListener {
	public void taskAddedEvent(int taskRowIndex, GanttTask task);
	public void taskEditedEvent(int taskRowIndex, GanttTask task);
	public void taskRemovedEvent(int taskRowIndex);
}
