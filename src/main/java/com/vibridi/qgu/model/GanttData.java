package com.vibridi.qgu.model;

import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GanttData {
	
	private GanttTask root;
	private StringProperty ganttName;
	private ObjectProperty<LocalDate> chartStartDate;
	private ObjectProperty<LocalDate> chartEndDate;
	
	public GanttData() {
		this.ganttName = new SimpleStringProperty("(No name)");
		this.chartStartDate = new SimpleObjectProperty<>();
		this.chartEndDate = new SimpleObjectProperty<>();
	}
	
	public GanttData(String name, LocalDate start, LocalDate end, GanttTask root) {
		this.ganttName = new SimpleStringProperty(name);
		this.chartStartDate = new SimpleObjectProperty<>(start);
		this.chartEndDate = new SimpleObjectProperty<>(end);
		this.root = root;
	}


	public GanttTask getRoot() {
		return root;
	}

	public void setRoot(GanttTask root) {
		this.root = root;
	}

	/*********************************************
	 *                                           *
	 * FX PROPERTIES		                     *
	 *                                           *
	 *********************************************/
	public StringProperty ganttNameProperty() {
		return this.ganttName;
	}

	public String getGanttName() {
		return this.ganttNameProperty().get();
	}

	public void setGanttName(final String ganttName) {
		this.ganttNameProperty().set(ganttName);
	}

	public ObjectProperty<LocalDate> chartStartDateProperty() {
		return this.chartStartDate;
	}

	public LocalDate getChartStartDate() {
		return this.chartStartDateProperty().get();
	}

	public void setChartStartDate(final LocalDate chartStartDate) {
		this.chartStartDateProperty().set(chartStartDate);
	}


	public ObjectProperty<LocalDate> chartEndDateProperty() {
		return this.chartEndDate;
	}

	public LocalDate getChartEndDate() {
		return this.chartEndDateProperty().get();
	}

	public void setChartEndDate(final LocalDate chartEndDate) {
		this.chartEndDateProperty().set(chartEndDate);
	}
	
	
}
