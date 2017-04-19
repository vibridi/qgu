package com.vibridi.qgu;

import com.vibridi.fxmlutils.FXUtils;
import com.vibridi.qgu.util.AppContext;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    
    public static void main( String[] args ) {
        launch();
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXUtils.newView(this.getClass(), "view/mainview.fxml")
			.makeStage("QGU " + AppContext.VERSION_NUMBER)
			.build()
			.show();
	}
	
}
