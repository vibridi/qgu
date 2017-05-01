package com.vibridi.qgu.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.vibridi.qgu.exception.UnreadableGanttFileException;
import com.vibridi.qgu.model.GanttTask;

public class QGUFileSystemStorage {
		
	public QGUFileSystemStorage() {

	}

	public void saveTree(File file, GanttTask root) throws IOException {
		assert(file != null);
		assert(file.exists());
		
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(root);
		oos.close();
	}

	/**
	 * 
	 * @param file
	 * @return the deserialized object
	 * @throws IOException
	 * @throws UnreadableGanttFileException 
	 */
	public GanttTask load(File file) throws UnreadableGanttFileException {
		assert(file != null);
		assert(file.exists());
		
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			GanttTask deserialized = (GanttTask) ois.readObject();
			ois.close();
			return deserialized;
		} catch(Throwable e) {
			throw new UnreadableGanttFileException("Cannot open gantt file", e);
		} 
	}

}
