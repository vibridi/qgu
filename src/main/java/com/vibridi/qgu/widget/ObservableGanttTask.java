package com.vibridi.qgu.widget;

import com.vibridi.qgu.model.GanttTask;
import com.vibridi.qgu.util.TaskUtils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class ObservableGanttTask {

	private GanttTask task;
	private StringProperty id;
	private StringProperty name;
	private StringProperty startDate;
	private StringProperty endDate;
	
	
	public ObservableGanttTask(GanttTask task) {
		this.task = task;
		this.id = new SimpleStringProperty(TaskUtils.pathToString(task.getPath()));
		this.name = new SimpleStringProperty(task.getName());
		this.startDate = new SimpleStringProperty(task.getStartDate().toString()); // TODO configure date format
		this.endDate = new SimpleStringProperty(task.getEndDate().toString());
	}
	
	
	
	public GanttTask getTask() {
		return task;
	}



	public void setTask(GanttTask task) {
		this.task = task;
	}



	public StringProperty nameProperty() {
		return this.name;
	}
	


	public String getName() {
		return this.nameProperty().get();
	}
	


	public void setName(final String name) {
		this.nameProperty().set(name);
	}
	


	public StringProperty startDateProperty() {
		return this.startDate;
	}
	


	public String getStartDate() {
		return this.startDateProperty().get();
	}
	


	public void setStartDate(final String startDate) {
		this.startDateProperty().set(startDate);
	}
	


	public StringProperty endDateProperty() {
		return this.endDate;
	}
	


	public String getEndDate() {
		return this.endDateProperty().get();
	}
	


	public void setEndDate(final String endDate) {
		this.endDateProperty().set(endDate);
	}



	public StringProperty idProperty() {
		return this.id;
	}
	



	public String getId() {
		return this.idProperty().get();
	}
	



	public void setId(final String id) {
		this.idProperty().set(id);
	}
	
	
	
	
}
