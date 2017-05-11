package com.vibridi.qgu.widget.api;

import com.vibridi.qgu.model.GanttTask;

@FunctionalInterface
public interface TaskVisitor {
	public void processNode(GanttTask node);
}
