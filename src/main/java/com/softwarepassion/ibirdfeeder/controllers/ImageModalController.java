package com.softwarepassion.ibirdfeeder.controllers;


import com.softwarepassion.ibirdfeeder.tables.S3Item;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageModalController {

    private static final Logger log = LoggerFactory.getLogger(ImageModalController.class);

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private ImageView imagePlaceholder;

    @FXML
    private Button sizeBtn;

    private S3Item item;
    private boolean fullSizeViewEnabled;
    private Image image;

    public void init(S3Item item) {
        log.info("Loading modal image window with: " + item);
        this.item = item;
        progressIndicator.setProgress(0.0);
        progressIndicator.setVisible(true);
        this.image = new Image(item.getUrl(), true);
        progressIndicator.progressProperty().bind(image.progressProperty());
        imagePlaceholder.setFitWidth(1024.0);
        imagePlaceholder.setFitHeight(708.0);

        imagePlaceholder.preserveRatioProperty();
        imagePlaceholder.setImage(image);
    }

    public void sizeBtnAction(ActionEvent actionEvent) {
        log.info("size action called..");
        this.fullSizeViewEnabled = !this.fullSizeViewEnabled;
        if (this.fullSizeViewEnabled) {
            sizeBtn.setText("Fit Size");
            imagePlaceholder.setFitHeight(image.getWidth());
            imagePlaceholder.setFitHeight(image.getHeight());

            imagePlaceholder.preserveRatioProperty();
            imagePlaceholder.setImage(image);
        } else {
            sizeBtn.setText("Full Size");
            imagePlaceholder.setFitWidth(1024.0);
            imagePlaceholder.setFitHeight(708.0);
        }
    }
}
