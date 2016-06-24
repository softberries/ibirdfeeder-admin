package com.softwarepassion.ibirdfeeder.controllers;


import com.softwarepassion.ibirdfeeder.aws.s3.S3File;
import com.softwarepassion.ibirdfeeder.aws.s3.S3Manager;
import com.softwarepassion.ibirdfeeder.images.S3ImageView;
import com.softwarepassion.ibirdfeeder.tables.S3Item;
import com.softwarepassion.ibirdfeeder.tables.S3ItemStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ViewerTabPageController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(ViewerTabPageController.class);

    private static final int MAX_NUMBER_OF_IMAGES_TO_LOAD = 1000;

    @FXML
    private TableView itemsTable;
    @FXML
    private FlowPane mainGrid;
    @FXML
    private TableColumn nameColumn;
    @FXML
    private TableColumn sizeColumn;
    @FXML
    private TableColumn statusColumn;
    @FXML
    private ToolBar toolbar;
    @FXML
    private ProgressIndicator copyProgressBar;
    @FXML
    private ProgressIndicator cleanProgressBar;

    private ObservableList<S3Item> selectedItems = FXCollections.observableArrayList();
    private List<S3File> currentItems = new ArrayList<>();
    private S3Manager s3Manager = new S3Manager();

    public void refreshAction(ActionEvent actionEvent) {
        log.info("refreshAction");
        populateMainGrid();
    }

    public void cleanUpAction(ActionEvent actionEvent) {
        log.info("cleanUpAction");
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                final int max = currentItems.size();
                for (int i = 0; i < max; i++) {
                    if (isCancelled()) {
                        break;
                    }
                    log.info("{} marked to delete", currentItems.get(i));
                    s3Manager.deleteFileFromTemp(currentItems.get(i).getName());
                    updateProgress(i, max);
                }
                return null;
            }
        };
        cleanProgressBar.setVisible(true);
        cleanProgressBar.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                cleanProgressBar.setVisible(false);
                currentItems.clear();
                selectedItems.clear();
            }
        });
        new Thread(task).start();
    }

    public void sendToS3Action(ActionEvent actionEvent) {
        log.info("sendToS3Action");
        log.info("Currently displaying {} files.", currentItems.size());
        log.info("Selected to copy: {}", selectedItems.size());
        log.info("Removing:");
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                final int max = selectedItems.size();
                for (int i = 0; i < max; i++) {
                    if (isCancelled()) {
                        break;
                    }
                    log.info("{} marked to copy", selectedItems.get(i));
                    s3Manager.copyFileToAlbum(selectedItems.get(i).getName());
                    selectedItems.get(i).setStatus(S3ItemStatus.PROCESSED.name());
                    updateProgress(i, max);
                }
                return null;
            }
        };
        copyProgressBar.setVisible(true);
        copyProgressBar.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                copyProgressBar.setVisible(false);
            }
        });
        new Thread(task).start();
    }


    @Override
    public void init() {
        mainGrid.prefWidthProperty().bind(toolbar.widthProperty().subtract(10));
        itemsTable.prefWidthProperty().bind(toolbar.widthProperty());

        nameColumn.setCellValueFactory(
            new PropertyValueFactory<S3Item, String>("Name")
        );
        sizeColumn.setCellValueFactory(
            new PropertyValueFactory<S3Item, String>("Size")
        );
        statusColumn.setCellValueFactory(
            new PropertyValueFactory<S3Item, String>("Status")
        );
        itemsTable.setItems(selectedItems);
        populateMainGrid();
    }

    private void populateMainGrid() {
        currentItems.clear();
        selectedItems.clear();
        mainGrid.getChildren().clear();
        List<S3File> allFiles = s3Manager.listTempBucketContent().stream().filter(f -> f.getName().startsWith("image")).collect(Collectors.toList());
        List<S3File> files = allFiles.subList(0, allFiles.size() > MAX_NUMBER_OF_IMAGES_TO_LOAD ? MAX_NUMBER_OF_IMAGES_TO_LOAD : allFiles.size());
        for (S3File f : files) {
            mainGrid.getChildren().add(createImageView(f));
            currentItems.add(f);
        }
    }

    private Parent createImageView(S3File file) {
        S3ImageView imageView = new S3ImageView();
        Image image = new Image(file.getSmallImgUrl(), true);
        Pane pane = new Pane(imageView);
        pane.setMaxWidth(142);
        imageView.fitWidthProperty().bind(pane.widthProperty());
        imageView.fitHeightProperty().bind(pane.heightProperty());
        imageView.setPreserveRatio(true);
        imageView.setEffect(new DropShadow(10, Color.BLACK));
        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    S3Item s3Item = new S3Item(file.getName(), file.getSmallImgUrl(), file.readableFileSize(), S3ItemStatus.READY.name());
                    if (event.getClickCount() == 2) {
                        System.out.println("Double clicked");
                        Stage stage = new Stage();
                        Parent root = null;
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/imageModal.fxml"));
                        try {
                            root = loader.load();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        stage.setResizable(false);
                        ImageModalController imageModalController = loader.getController();
                        imageModalController.init(s3Item);
                        stage.setScene(new Scene(root));
                        stage.setTitle(s3Item.getName());
                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.initOwner(
                            ((Node) event.getSource()).getScene().getWindow());
                        stage.show();
                    } else {
                        System.out.println("Tile pressed " + file.getName());
                        imageView.setSelected(!imageView.isSelected());
                        if (imageView.isSelected()) {
                            imageView.setEffect(new DropShadow(20, Color.BLUE));
                            selectedItems.add(s3Item);
                        } else {
                            imageView.setEffect(new DropShadow(10, Color.BLACK));
                            selectedItems.remove(s3Item);
                        }
                    }
                    event.consume();
                }
            }
        });
        imageView.setImage(image);
        return pane;
    }
}
