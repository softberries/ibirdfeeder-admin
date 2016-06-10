package com.softwarepassion.ibirdfeeder.controllers;

import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @FXML
    private ViewerTabPageController viewerTabPageController;

    @FXML
    private MainTabPageController mainTabPageController;

    public void init() throws MalformedURLException {
        log.debug("init method called");
        mainTabPageController.init();
        viewerTabPageController.init();
    }

}
