package com.vibridi.qgu.storage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.vibridi.qgu.exception.UnreadableGanttFileException;
import com.vibridi.qgu.model.GanttTask;

public enum QGUStorageManager {
	instance;
	
	private QGUFileSystemStorage storage;
	private File workDir; // possibly not needed
	private File handle;
	private boolean saved;
	private Map<String,GanttTask> delta;
	
	private QGUStorageManager() {
		storage = new QGUFileSystemStorage();
		delta = new HashMap<>();
		handle = null;
		saved = false;
	}
	
	public void trackChange(GanttTask task) {
		//delta.put(task.getUid(), task);
		saved = false;
	}

	public void saveGantt(GanttTask root) throws IOException {
		// TODO consider adding delta-saving capability
		assert(handle != null);
		saveGantt(handle, root);
	}
	
	public void saveGantt(File handle, GanttTask root) throws IOException {
		storage.saveTree(handle, root);
		saved = true;
	}
	
	public GanttTask loadGantt(File file) throws UnreadableGanttFileException {
		assert(file.getName().endsWith(".gtt"));
		GanttTask root = storage.load(file);
		handle = file;
		return root;
	}
	
	public boolean hasHandle() {
		return handle != null;
	}
	
	public void setHandle(File handle) {
		this.handle = handle;
	}
	
	public boolean isSaved() {
		return saved;
	}
}
