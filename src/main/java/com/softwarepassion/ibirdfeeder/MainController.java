package com.softwarepassion.ibirdfeeder;

import com.softwarepassion.ibirdfeeder.aws.s3.S3Manager;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @FXML
    private FlowPane mainGrid;

    @FXML
    private FlowPane acceptedImagesGrid;

    @FXML
    private TextArea logTextArea;


    public void init() throws MalformedURLException {
        log.debug("init method called");
        mainGrid.prefWidthProperty().bind(mainGrid.getScene().widthProperty().subtract(20));

        List<String> urls = new S3Manager().listBucketContent();
        for (String urlStr : urls) {
            mainGrid.getChildren().add(createImageFromUrl(urlStr));
        }

        acceptedImagesGrid.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /* data is dragged over the target */
                System.out.println("onDragOver");

                /* accept it only if it is  not dragged from the same node
                 * and if it has a string data */
                if (event.getGestureSource() != acceptedImagesGrid &&
                    event.getDragboard().hasString()) {
                    /* allow for both copying and moving, whatever user chooses */
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }

                event.consume();
            }
        });

        acceptedImagesGrid.setOnDragEntered(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /* the drag-and-drop gesture entered the target */
                System.out.println("onDragEntered");
                /* show to the user that it is an actual gesture target */
                if (event.getGestureSource() != acceptedImagesGrid &&
                    event.getDragboard().hasString()) {
//                    target.setFill(Color.GREEN);
                }

                event.consume();
            }
        });

        acceptedImagesGrid.setOnDragExited(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /* mouse moved away, remove the graphical cues */
//                target.setFill(Color.BLACK);

                event.consume();
            }
        });

        acceptedImagesGrid.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /* data dropped */
                System.out.println("onDragDropped");
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    System.out.println("Received: " + db.getString());
                    acceptedImagesGrid.getChildren().add(createImageFromUrl(db.getString()));
                    success = true;
                }
                /* let the source know whether the string was successfully
                 * transferred and used */
                event.setDropCompleted(success);

                event.consume();
            }
        });


//        acceptedImagesGrid.getChildren().add(target);
    }

    private Parent createImageFromUrl(String url) {
        Image image = new Image(url, true);
        ImageView imageView = new ImageView();
        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                System.out.println("Tile pressed " + url);

                event.consume();
            }
        });

        imageView.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                System.out.println("dragging..." + url);
        /* drag was detected, start a drag-and-drop gesture*/
        /* allow any transfer mode */
                Dragboard db = imageView.startDragAndDrop(TransferMode.ANY);

        /* Put a string on a dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(url);
                db.setContent(content);

                event.consume();
            }
        });

        imageView.setOnDragDone(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /* the drag-and-drop gesture ended */
                System.out.println("onDragDone");
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    System.out.println("moved");
                }

                event.consume();
            }
        });

        Pane pane = new Pane(imageView);
        pane.setMaxWidth(115);
        imageView.fitWidthProperty().bind(pane.widthProperty());
        imageView.fitHeightProperty().bind(pane.heightProperty());
        imageView.setPreserveRatio(true);
        imageView.setImage(image);
        return pane;
    }

    public void refreshMainGrid(ActionEvent actionEvent) {
        log.info("refreshing grid...");
    }
}
