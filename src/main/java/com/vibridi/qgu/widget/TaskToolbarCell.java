package com.vibridi.qgu.widget;

import java.util.function.BiFunction;

import com.vibridi.fxu.FXUtils;
import com.vibridi.fxu.keyboard.FXKeyboard;

import javafx.scene.control.Button;
import javafx.scene.control.TreeTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class TaskToolbarCell extends TreeTableCell<ObservableGanttTask,String> {

	private final static double BUTTON_SIZE;
	private final static double SPACING;
	
	public final static double MIN_WIDTH;
	
	static {
		BUTTON_SIZE = 3.0;
		SPACING = 2.0;
		MIN_WIDTH = 0.0;
	}
	
	private int absoluteIndex;
	private ObservableGanttTask item;
	
	private HBox hbox;
	private Button debugButton;
	private Button addChild;
	private Button deleteSelf;
	private Pane spacer;
	
	public TaskToolbarCell() {
		super();
		initialize();
	}
	
	private void initialize() {
		hbox = new HBox();
		
		addChild = createButton("+");
		deleteSelf = createButton("x");
		debugButton = createButton("D");
		
		FXUtils.setTooltip(addChild, "Add a new sub-task");
		FXUtils.setTooltip(deleteSelf, "Delete this task");
		
		setOnMouseEntered(event -> {
			if(item == null)
				return;
			deleteSelf.setVisible(true);
			debugButton.setVisible(true);
			if(item.getTask().getLevel() <= 2)
				addChild.setVisible(true);
			
		});
		
		setOnMouseExited(event -> {
			addChild.setVisible(false);
			deleteSelf.setVisible(false);
			debugButton.setVisible(false);
		});

		spacer = new Pane();
		
		hbox.getChildren().addAll(addChild, deleteSelf, debugButton, spacer);
		hbox.setSpacing(SPACING);
		hbox.setVisible(true);
		addChild.setVisible(false);
		deleteSelf.setVisible(false);
		debugButton.setVisible(false);
		HBox.setHgrow(spacer, Priority.ALWAYS);
	}
	
	@Override
	public void updateItem(String text, boolean empty) {
		super.updateItem(text, empty);
		
		
		
		if(empty) {
			setGraphic(null);
			
		} else {
			absoluteIndex = getTreeTableRow().getIndex();
			item = getTreeTableRow().getItem();
			
			setGraphic(hbox);
		}
		
	}
	
	public void setOnAddChild(BiFunction<Integer, ObservableGanttTask, Boolean> func) {
		addChild.setOnAction(event -> func.apply(absoluteIndex, item));
		// TODO it appears this doesn't work
		//FXKeyboard.setKeyCombinationShortcut(addChild, "Ctrl+Shift+N", event -> func.apply(absoluteIndex, item));
	}
	
	public void setOnDeleteSelf(BiFunction<Integer, ObservableGanttTask, Boolean> func) {
		deleteSelf.setOnAction(event -> func.apply(absoluteIndex, item));
	}
	
	public void setOnDebug(BiFunction<Integer, ObservableGanttTask, Boolean> func) {
		debugButton.setOnAction(event -> func.apply(absoluteIndex, item));
	}
	
	private Button createButton(String text) {
		Button btn = new Button(text);
		btn.setPrefSize(BUTTON_SIZE, BUTTON_SIZE);
		btn.getStyleClass().add("task-list-button");		
		return btn;
	}
	
}
