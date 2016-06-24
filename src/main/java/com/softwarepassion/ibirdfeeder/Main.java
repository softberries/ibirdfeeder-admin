package com.softwarepassion.ibirdfeeder;

import com.softwarepassion.ibirdfeeder.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.PrintStream;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource(
            "/fxml/main.fxml"));
        Parent root = (Parent) loader.load();
        MainController mainController = loader.getController();
        mainController.init();


        primaryStage.setTitle("iBirdFeeder Administration Tool");
        primaryStage.setScene(new Scene(root, 1224, 868));

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
