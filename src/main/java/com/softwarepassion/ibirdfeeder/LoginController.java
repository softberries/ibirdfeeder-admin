package com.softwarepassion.ibirdfeeder;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private Text actiontarget;

    @FXML protected void handleSubmitButtonAction(ActionEvent event) throws IOException {
        actiontarget.setText("Sign in button pressed");

//        Stage stageTheEventSourceNodeBelongs = (Stage) ((Node)event.getSource()).getScene().getWindow();
        // OR
        Stage stageTheLabelBelongs = (Stage) actiontarget.getScene().getWindow();
        // these two of them return the same stage
        // Swap screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent main = loader.load();
        stageTheLabelBelongs.setScene(new Scene(main, 1024, 768));
        MainController controller = loader.getController();
        controller.init();
    }

}
