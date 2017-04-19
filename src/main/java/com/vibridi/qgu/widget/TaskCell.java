package com.vibridi.qgu.widget;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class TaskCell extends TreeCell<String> {

	private HBox hbox;
	private Label textLabel;
	private Pane spacer;
	private Button plusButton;
	
	public TaskCell() {
		super();
		initialize();
	}
	
	private void initialize() {
		hbox = new HBox();
		textLabel = new Label("");
		spacer = new Pane();
		plusButton = new Button("+");
		plusButton.setPrefSize(5.0, 5.0);
		plusButton.setVisible(false);
		
		setOnMouseEntered(event -> {
			plusButton.setVisible(true);
		});
		
		setOnMouseExited(event -> {
			plusButton.setVisible(false);
		});
		
		hbox.getChildren().addAll(textLabel, spacer, plusButton);
		HBox.setHgrow(spacer, Priority.ALWAYS);
	}
	
	@Override
	public void updateItem(String text, boolean empty) {
		super.updateItem(text, empty);
		
		if(empty) {
			setGraphic(null);
			
		} else {
			textLabel.setText(text);
			setGraphic(hbox);
		}
		
	}
	
	
}
