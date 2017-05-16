package com.vibridi.qgu.storage;

import java.io.File;
import java.io.IOException;
import java.util.prefs.BackingStoreException;

import com.dedalus.xml.exception.XMLException;
import com.vibridi.qgu.model.GanttData;
import com.vibridi.qgu.util.AppContext;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public enum QGUStorageManager implements QGUStorageAgent {
	instance;

	private static final int MAX_RECENT = 5;
	private static final String RECENT = "recent";
	private static final String LAST_VIEWED = "last-viewed";

	private QGUFileSystemStorage storage;	
	private ObservableList<File> recent;

	private QGUStorageManager() {
		storage = new QGUFileSystemStorage();
		recent = loadRecentFiles();
	}

	/**
	 * Saves the current project and sets the file handle.
	 * 
	 * @param handle The file that will be written. It is responsibility of the caller to ensure that that this file exists and is writable.
	 * @param meta Gantt metadata
	 * @param root Gantt tree root task
	 * @throws IOException - In case of an error when writing the file
	 */
	@Override
	public void save(File handle, GanttData data) throws IOException {
		assert(handle != null && handle.exists());
		try {
			storage.save(handle, data);
		} catch (XMLException e) {
			throw new IOException(e);
		}
		addRecentFile(handle);
	}

	/**
	 * Loads a gantt project. 
	 * 
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	@Override
	public GanttData load(File file) throws IOException {
		assert(file.getName().endsWith(".gtt"));
		return storage.load(file);
	}

	public void addRecentFile(File file) {
		recent.add(file);
		AppContext.preferences.node(RECENT).put(file.getName(), file.getAbsolutePath());

		if(recent.size() >= MAX_RECENT) {
			file = recent.remove(0);
			AppContext.preferences.node(RECENT).remove(file.getName());	
		}
	}
	
	public void removeRecentFile(File file) {
		AppContext.preferences.node(RECENT).remove(file.getName());
	}

	public ObservableList<File> getRecentFiles() {
		return recent;
	}
	
	public void rememberLastViewed(File handle) {
		if(handle == null)
			return;
		AppContext.preferences.put(LAST_VIEWED, handle.getAbsolutePath());
	}
	
	public File loadLastViewed() throws IOException {
		return new File(AppContext.preferences.get(LAST_VIEWED, ""));
	}
	
	public void forgetLastViewed() {
		AppContext.preferences.put(LAST_VIEWED, "");
	}

	private ObservableList<File> loadRecentFiles() {
		try {
			ObservableList<File> obs = FXCollections.observableArrayList();
			for(String fname : AppContext.preferences.node(RECENT).keys())
				obs.add(new File(AppContext.preferences.node(RECENT).get(fname, "")));
			return obs;
		} catch(BackingStoreException e) {
			throw new RuntimeException();
		}
	}
}
