package com.vibridi.qgu.storage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.dedalus.xml.XMLTools;
import com.dedalus.xml.exception.XMLException;
import com.vibridi.qgu.exception.UnreadableGanttFileException;
import com.vibridi.qgu.model.GanttMetadata;
import com.vibridi.qgu.model.GanttTask;
import com.vibridi.qgu.util.TaskUtils;

public class QGUFileSystemStorage {
	
	
	public QGUFileSystemStorage() {
	}

	public void saveTree(File file, GanttMetadata meta, GanttTask root) throws IOException, XMLException {
		assert(file != null);
		assert(file.exists());
		
		Document doc = XMLTools.emptyDocument("GanttChart");
		Element e1 = doc.createElement("Meta");
		e1.appendChild(elementWithText(doc, "ProjectName", meta.getGanttName()));
		Element e1timeline = doc.createElement("Timeline");
		e1timeline.appendChild(elementWithText(doc, "StartDate", meta.getChartStartDate().toString()));
		e1timeline.appendChild(elementWithText(doc, "EndDate", meta.getChartEndDate().toString()));
		e1.appendChild(e1timeline);
		
		Element e2 = doc.createElement("TaskList");
		e2.appendChild(elementWithText(doc, "Value", Base64.encodeBase64String(serialize(root))));	
		Element e2list = doc.createElement("List");
		
		TaskUtils.walkDepthFirst(root, node -> {
			if(node.isRoot())
				return;
			Element e2node = doc.createElement("Task");
			e2node.setAttribute("id", TaskUtils.pathToString(node.getPath()));
			e2node.setAttribute("name", node.getName());
			e2node.setAttribute("start", node.getStartDate().toString());
			e2node.setAttribute("end", node.getEndDate().toString());
			e2node.setAttribute("level", Integer.toString(node.getLevel()));
			e2list.appendChild(e2node);
		});
		
		e2.appendChild(e2list);
		
		XMLTools.appendChild(doc, "/GanttChart", null, e1);
		XMLTools.appendChild(doc, "/GanttChart", null, e2);
		XMLTools.documentToFile(doc, file, "UTF-8");
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

	
	private Element elementWithText(Document doc, String tagName, String text) {
		Element e = doc.createElement(tagName);
		e.appendChild(doc.createTextNode(text));
		return e;
	}

	private byte[] serialize(GanttTask root) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(root);
		oos.close();
		return bos.toByteArray();
	}
	
}
