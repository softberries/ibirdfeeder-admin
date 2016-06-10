package com.softwarepassion.ibirdfeeder.controllers;

import javafx.event.Event;
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

    private boolean tabSelection = false;

    public void init() throws MalformedURLException {
        log.debug("init method called");
        mainTabPageController.init();
    }

    public void onMainTabSelectionChanged(Event event) {
        if (checkTab())
            log.info("main tab selection changed " + event.getSource().toString());
    }

    public void onViewerTabSelectionChanged(Event event) {
        if (checkTab()) {
            log.info("viewer tab selection changed " + event);
            viewerTabPageController.init();
        }
    }

    public void onTemperatureTabSelectionChanged(Event event) {
        if (checkTab())
            log.info("temperature tab selection changed " + event);
    }

    public void onAlbumTabSelectionChanged(Event event) {
        if (checkTab())
            log.info("album tab selection changed " + event);
    }

    private boolean checkTab() {
        tabSelection = !tabSelection;
        return tabSelection;
    }
}
