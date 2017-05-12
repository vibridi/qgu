package com.vibridi.qgu.widget.api;

import com.vibridi.qgu.model.GanttTask;
import com.vibridi.qgu.widget.ObservableGanttTask;

import javafx.collections.ListChangeListener;

public interface TaskListener {
	public void taskAddedEvent(int taskRowIndex, GanttTask task);
	public void taskEditedEvent(int taskRowIndex, GanttTask task);
	public void taskRemovedEvent(int taskRowIndex);
}
