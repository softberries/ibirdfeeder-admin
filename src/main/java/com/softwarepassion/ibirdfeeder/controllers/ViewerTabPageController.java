package com.softwarepassion.ibirdfeeder.controllers;


import com.softwarepassion.ibirdfeeder.images.S3ImageView;
import com.softwarepassion.ibirdfeeder.tables.S3Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

public class ViewerTabPageController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(ViewerTabPageController.class);

    @FXML
    public TableView itemsTable;
    @FXML
    public FlowPane mainGrid;
    @FXML
    public TableColumn nameColumn;
    @FXML
    public TableColumn sizeColumn;
    @FXML
    public TableColumn statusColumn;
    @FXML
    public ToolBar toolbar;

    private ObservableList<S3Item> selectedItems = FXCollections.observableArrayList();

    public void refreshAction(ActionEvent actionEvent) {
        log.info("refreshAction");
    }

    public void cleanUpAction(ActionEvent actionEvent) {
        log.info("cleanUpAction");
    }

    public void sendToS3Action(ActionEvent actionEvent) {
        log.info("sendToS3Action");
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

        for (int i = 0; i < 200; i++) {
            mainGrid.getChildren().add(createImageView("https://s3-eu-west-1.amazonaws.com/birdfeeder001/small_image_20160523-042030.jpg", i));
        }
    }

    private Parent createImageView(String url, int i) {
        S3ImageView imageView = new S3ImageView();
        Image image = new Image(url, true);
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
                    S3Item s3Item = new S3Item(url.substring(url.lastIndexOf("/") + 1), url.replace("small_", ""), "2.2" + i, "READY");
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
                        System.out.println("Tile pressed " + url);
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
