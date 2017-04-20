package com.vibridi.qgu.widget;

import javafx.scene.layout.Pane;

public class GanttBarPiece extends Pane {
	
	public enum PieceType {
		BEGINNING,
		CENTER,
		END
	}
	
	private PieceType type;
	private boolean isOverdue;
	
	public GanttBarPiece(PieceType type) {
		this.type = type;
		
		switch(type) {
		case BEGINNING:
			getStyleClass().add("gantt-piece-beginning");
			break;
		case CENTER:
			getStyleClass().add("gantt-piece-center");
			break;
		case END:
			getStyleClass().add("gantt-piece-end");
			break;
		}
	}
	
	public PieceType getPieceType() {
		return type;
	}
	
}
