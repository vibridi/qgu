package com.vibridi.qgu.widget;

import java.time.LocalDate;
import java.time.Month;

import com.vibridi.qgu.model.GanttTask;

import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TimelineView extends TableView<GanttTask> {

	private LocalDate timelineStart;
	private LocalDate timelineEnd;
	
	public TimelineView() {
		this.getStyleClass().add("qgu-timeline");
		this.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		this.setItems(FXCollections.observableArrayList());
		reset(LocalDate.now(), LocalDate.now().plusDays(40));
	}
	
	public void clear() {
		getItems().clear();
	}
	
	public void resetStart(LocalDate start) {
		reset(start, timelineEnd);
	}
	
	public void resetEnd(LocalDate end) {
		reset(timelineStart, end);
	}
	
	public void reset(LocalDate start, LocalDate end) {
		this.getColumns().clear();
		initTimeline(start, end);
	}
	
	public LocalDate getTimelineStart() {
		return timelineStart;
	}

	public LocalDate getTimelineEnd() {
		return timelineEnd;
	}

	/* ******************************************************** */
	/* 	PRIVATE METHODS											*/
	/* ******************************************************** */
	private void initTimeline(LocalDate start, LocalDate end) {
		this.timelineStart = start;
		this.timelineEnd = end;
		
		for(int m = start.getMonthValue(); m <= end.getMonthValue(); m++) {
			TableColumn<GanttTask,GanttBarPiece> monthCol = new TableColumn<GanttTask,GanttBarPiece>();
			Label month = new Label(Month.of(m).toString());
			monthCol.setGraphic(month);
			monthCol.setResizable(false);

			LocalDate tmp = LocalDate.of(start.getYear(), m, 1);
			LocalDate chartStart = start.compareTo(tmp) > 0 ? start : tmp;

			for(int d = chartStart.getDayOfMonth(); d <= chartStart.lengthOfMonth(); d++) {
				GanttDayColumn c = new GanttDayColumn(chartStart.getYear(), m, d);
				c.setPrefWidth(20);
				Label day = new Label(Integer.toString(d));
				c.setGraphic(day);
				monthCol.getColumns().add(c);
			}

			this.getColumns().add(monthCol);
		}
		// TODO make timeline right-left scrollable with wheel on windows 
	}
}
