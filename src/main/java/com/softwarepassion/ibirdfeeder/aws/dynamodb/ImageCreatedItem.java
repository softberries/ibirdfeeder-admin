package com.softwarepassion.ibirdfeeder.aws.dynamodb;


public class ImageCreatedItem {
    private long timestamp;
    private String name;

    public ImageCreatedItem(long timestamp, String name) {
        this.timestamp = timestamp;
        this.name = name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getName() {
        return name;
    }
}
