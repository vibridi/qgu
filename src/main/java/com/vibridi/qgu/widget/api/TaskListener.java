package com.vibridi.qgu.widget.api;

import com.vibridi.qgu.model.GanttTask;

public interface TaskListener {
	public void taskAddedEvent(int taskAbsoluteRow, GanttTask task);
	public void taskRemovedEvent(int taskAbsoluteRow);
}
