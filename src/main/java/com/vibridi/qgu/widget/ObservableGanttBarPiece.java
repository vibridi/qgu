package com.vibridi.qgu.widget;

import com.vibridi.qgu.widget.GanttBarPiece.PieceType;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ObservableGanttBarPiece implements ObservableValue<GanttBarPiece> {

	private GanttBarPiece piece;
	
	public ObservableGanttBarPiece(PieceType type) {
		piece = new GanttBarPiece(type);
	}
	
	@Override
	public void addListener(InvalidationListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addListener(ChangeListener<? super GanttBarPiece> listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GanttBarPiece getValue() {
		return piece;
	}

	@Override
	public void removeListener(ChangeListener<? super GanttBarPiece> listener) {
		// TODO Auto-generated method stub
		
	}

}
