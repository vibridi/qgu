package com.vibridi.qgu.widget;

import java.time.LocalDate;
import java.util.Collections;

import com.vibridi.qgu.model.GanttTask;
import com.vibridi.qgu.util.TaskUtils;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class ObservableGanttTask implements Comparable<ObservableGanttTask> {

	private GanttTask task;
	private StringProperty id;
	private StringProperty name;
	private StringProperty startDate;
	private StringProperty endDate;
	private IntegerProperty level;
	
	public ObservableGanttTask(GanttTask task) {
		this.task = task;
		this.id = new SimpleStringProperty(indentation(task.getLevel()) + TaskUtils.pathToString(task.getPath()));
		this.name = new SimpleStringProperty(indentation(task.getLevel()) + task.getName());
		this.startDate = new SimpleStringProperty(task.getStartDate().toString()); // TODO configure date format
		this.endDate = new SimpleStringProperty(task.getEndDate().toString());
		this.level = new SimpleIntegerProperty(task.getLevel());
	}
	
	@Override 
	public boolean equals(Object o) {
		if(!(o instanceof ObservableGanttTask))
			return false;
		
		ObservableGanttTask that = (ObservableGanttTask) o;
		return this.task.comparePath(that.task.getPath()) == 0;
	}
	
	@Override
	public int compareTo(ObservableGanttTask that) {
		return this.task.comparePath(that.task.getPath());
	}
	
	public GanttTask getTask() {
		return task;
	}
	
	public int getTaskLevel() {
		return task.getLevel();
	}

	public void setTask(GanttTask task) {
		this.task = task;
	}

	/*******************************************
	 * 										   *
	 * JAVA FX GETTERS/SETTERS                 *
	 *                                         *
	 *******************************************/
	/**
	 * Name property getter
	 * @return name property
	 */
	public StringProperty nameProperty() {
		return this.name;
	}

	public String getName() {
		return this.nameProperty().get();
	}

	public void setName(final String name) {
		this.nameProperty().set(indentation(task.getLevel()) + name.trim());
		this.task.setName(name.trim());
	}

	public StringProperty startDateProperty() {
		return this.startDate;
	}

	public String getStartDate() {
		return this.startDateProperty().get();
	}

	public void setStartDate(final String startDate) {
		this.startDateProperty().set(startDate);
		this.task.setStartDate(LocalDate.parse(startDate));
	}

	public StringProperty endDateProperty() {
		return this.endDate;
	}

	public String getEndDate() {
		return this.endDateProperty().get();
	}

	public void setEndDate(final String endDate) {
		this.endDateProperty().set(endDate);
		this.task.setEndDate(LocalDate.parse(endDate)); // TODO make sure date is in ISO format or set DateTimeFormatter
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
	
	public void updateId() {
		this.idProperty().set(indentation(task.getLevel()) + TaskUtils.pathToString(task.getPath()));
	}
	
	private String indentation(int level) {
		return String.join("", Collections.nCopies(level-1, "  "));
	}

	public IntegerProperty levelProperty() {
		return this.level;
	}
	

	public int getLevel() {
		return this.levelProperty().get();
	}
	

	public void setLevel(final int level) {
		this.levelProperty().set(level);
	}
	
}
