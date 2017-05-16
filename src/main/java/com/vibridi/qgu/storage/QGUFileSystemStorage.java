package com.vibridi.qgu.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.dedalus.xml.XMLTools;
import com.dedalus.xml.exception.XMLException;
import com.vibridi.qgu.exception.UnreadableGanttFileException;
import com.vibridi.qgu.model.GanttData;
import com.vibridi.qgu.model.GanttTask;
import com.vibridi.qgu.util.TaskUtils;

public class QGUFileSystemStorage {
	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
	private static final QName xpath_string = XPathConstants.STRING;
	
	public QGUFileSystemStorage() {
	}

	public void save(File file, GanttData data) throws IOException, XMLException {
		assert(file != null);
		assert(file.exists());
		
		Document doc = XMLTools.emptyDocument("GanttChart");
		Element e1 = doc.createElement("Meta");
		e1.appendChild(elementWithText(doc, "ProjectName", data.getGanttName()));
		Element e1timeline = doc.createElement("Timeline");
		e1timeline.appendChild(elementWithText(doc, "StartDate", data.getChartStartDate().format(formatter)));
		e1timeline.appendChild(elementWithText(doc, "EndDate", data.getChartEndDate().format(formatter)));
		e1.appendChild(e1timeline);
		
		Element e2 = doc.createElement("TaskList");
		e2.appendChild(elementWithText(doc, "Value", Base64.encodeBase64String(serialize(data.getRoot()))));	
		Element e2list = doc.createElement("List");
		
		TaskUtils.walkDepthFirst(data.getRoot(), node -> {
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
	public GanttData load(File file) throws IOException {
		assert(file != null);
		assert(file.exists());
		
		try {
			Document doc = XMLTools.fileToDocument(file);
			
			String name = (String) XMLTools.applyXPath(doc, "/GanttChart/Meta/ProjectName", null, xpath_string);
			LocalDate start = LocalDate.parse((String) XMLTools.applyXPath(doc, "/GanttChart/Meta/Timeline/StartDate", null, xpath_string), formatter);
			LocalDate end = LocalDate.parse((String) XMLTools.applyXPath(doc, "/GanttChart/Meta/Timeline/EndDate", null, xpath_string), formatter);
			
			byte[] decoded = Base64.decodeBase64((String) XMLTools.applyXPath(doc, "/GanttChart/TaskList/Value", null, xpath_string));
			GanttTask deserialized = deserialize(decoded);
			
			return new GanttData(name, start, end, deserialized);
			
		} catch(Throwable e) {
			throw new IOException("Cannot open gantt file", e);
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
	
	private GanttTask deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
		GanttTask deserialized = (GanttTask) ois.readObject();
		ois.close();
		return deserialized;
	}
	
}
