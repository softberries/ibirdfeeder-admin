package com.softwarepassion.ibirdfeeder.controllers;


import com.softwarepassion.ibirdfeeder.aws.dynamodb.DynamoDbManager;
import com.softwarepassion.ibirdfeeder.aws.dynamodb.TemperatureItem;
import com.softwarepassion.ibirdfeeder.aws.s3.S3File;
import com.softwarepassion.ibirdfeeder.aws.s3.S3Manager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class MainTabPageController {

    @FXML
    public Label tempSizeLbl;
    @FXML
    public Label websiteSizeLbl;
    @FXML
    public Label albumSizeLbl;
    @FXML
    public Label totalSizeLbl;

    @FXML
    private AreaChart dayTemperatureChart;

    @FXML
    private AreaChart monthTemperatureChart;

    @FXML
    private PieChart pieChart1;

    private DynamoDbManager dynamoDbManager;
    private S3Manager s3manager;

    public void init() {
        dynamoDbManager = new DynamoDbManager();
        s3manager = new S3Manager();
        initTemperatureChart();
        initStorageSection();
    }

    private void initStorageSection() {
        List<S3File> tempFiles = s3manager.listTempBucketContent();
        List<S3File> albumFiles = s3manager.listAlbumBucketContent();
        List<S3File> websiteFiles = s3manager.listWebsiteBucketContent();
        long tempSize = tempFiles.stream().mapToLong(S3File::getSize).sum();
        long albumSize = albumFiles.stream().mapToLong(S3File::getSize).sum();
        long websiteSize = websiteFiles.stream().mapToLong(S3File::getSize).sum();
        long totalSize = tempSize + albumSize + websiteSize;
        totalSizeLbl.setText(readableFileSize(totalSize));
        tempSizeLbl.setText(readableFileSize(tempSize));
        albumSizeLbl.setText(readableFileSize(albumSize));
        websiteSizeLbl.setText(readableFileSize(websiteSize));
        initChartsSection(100 * (float) tempSize / (float) totalSize,
            100 * (float) albumSize / (float) totalSize,
            100 * (float) websiteSize / (float) totalSize);
    }

    public String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private void initChartsSection(float temp, float album, float website) {
        ObservableList<PieChart.Data> pieChartData =
            FXCollections.observableArrayList(
                new PieChart.Data("Uprocessed", temp),
                new PieChart.Data("Album", album),
                new PieChart.Data("Website", website));
        pieChart1.setTitle("S3 Storage");
        pieChart1.setData(pieChartData);
    }

    private void initTemperatureChart() {
        XYChart.Series seriesDay = new XYChart.Series();
        seriesDay.setName("Today");
        List<TemperatureItem> items = dynamoDbManager.getCurrentDayTemperatureItems();
        for (TemperatureItem item : items) {
            seriesDay.getData().add(new XYChart.Data(Double.parseDouble(item.getHour() + "." + item.getMinute()), item.getTemperature()));
        }
        dayTemperatureChart.getData().add(seriesDay);

        XYChart.Series seriesMonth = new XYChart.Series();
        seriesMonth.setName("Current Month");
        List<TemperatureItem> itemsMonth = dynamoDbManager.getCurrentMonthTemperatureItems();
        for (TemperatureItem item : itemsMonth) {
            seriesMonth.getData().add(new XYChart.Data(item.getDay(), item.getTemperature()));
        }
        monthTemperatureChart.getData().add(seriesMonth);
    }


}
