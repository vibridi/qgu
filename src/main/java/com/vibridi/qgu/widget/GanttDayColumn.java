package com.vibridi.qgu.widget;

import java.time.LocalDate;

import com.vibridi.qgu.model.GanttTask;
import com.vibridi.qgu.widget.GanttBarPiece.PieceType;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;

public class GanttDayColumn extends TableColumn<GanttTask,GanttBarPiece> {
	
	private LocalDate representedDate;
	
	public GanttDayColumn(int year, int month, int dayOfMonth) {
		this(LocalDate.of(year, month, dayOfMonth));
	}
	
	public GanttDayColumn(LocalDate representedDate) {
		super();
		this.representedDate = representedDate;
		this.setCellValueFactory(this::createPiece);
	}
	
	
	private ObservableValue<GanttBarPiece> createPiece(CellDataFeatures<GanttTask,GanttBarPiece> cdf) {
		LocalDate taskStartDate = cdf.getValue().getStartDate();
		LocalDate taskEndDate = cdf.getValue().getEndDate();
		
		if(representedDate.isBefore(taskStartDate) || representedDate.isAfter(taskEndDate))
			return null;
		
		if(representedDate.isEqual(taskStartDate))
			return new ObservableGanttBarPiece(PieceType.BEGINNING); 
			
		if(representedDate.isAfter(taskStartDate) && representedDate.isBefore(taskEndDate))
			return new ObservableGanttBarPiece(PieceType.CENTER); 
		
		if(representedDate.isEqual(taskEndDate))
			return new ObservableGanttBarPiece(PieceType.END); 
		
		return null;
	}
	
}
