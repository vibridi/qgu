package com.vibridi.qgu.storage;

import java.io.File;
import java.io.IOException;

import com.vibridi.qgu.model.GanttData;

public interface QGUStorageAgent {
	public void save(File file, GanttData data) throws IOException;
	public GanttData load(File file) throws IOException;
}
