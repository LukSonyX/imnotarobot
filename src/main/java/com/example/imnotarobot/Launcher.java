package com.example.imnotarobot;

import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        ApplicationController controller = new ApplicationController();
        controller.start(stage);
    }
}

