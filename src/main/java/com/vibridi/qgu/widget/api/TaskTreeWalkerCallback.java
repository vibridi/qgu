package com.vibridi.qgu.widget.api;

import com.vibridi.qgu.model.GanttTask;

@FunctionalInterface
public interface TaskTreeWalkerCallback {
	public void processNode(GanttTask task);
}
