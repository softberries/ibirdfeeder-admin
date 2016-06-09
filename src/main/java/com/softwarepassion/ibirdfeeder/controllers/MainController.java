package com.softwarepassion.ibirdfeeder.controllers;

import com.softwarepassion.ibirdfeeder.Console;
import com.softwarepassion.ibirdfeeder.aws.dynamodb.BirdFeederItem;
import com.softwarepassion.ibirdfeeder.aws.dynamodb.DynamoDbManager;
import com.softwarepassion.ibirdfeeder.aws.dynamodb.TemperatureItem;
import com.softwarepassion.ibirdfeeder.aws.s3.S3File;
import com.softwarepassion.ibirdfeeder.aws.s3.S3Manager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @FXML
    private ViewerTabPageController viewerTabPageController;

    @FXML
    private MainTabPageController mainTabPageController;

    private Set<String> selectedImages = new HashSet<>();
    private Set<String> allImages = new HashSet<>();
    private Console console;


    public void init() throws MalformedURLException {
        log.debug("init method called");
        List<TemperatureItem> items = new DynamoDbManager().getCurrentDayTemperatureItems();
        mainTabPageController.init();
        viewerTabPageController.init();
    }

}
