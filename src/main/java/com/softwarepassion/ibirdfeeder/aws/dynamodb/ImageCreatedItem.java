package com.softwarepassion.ibirdfeeder.aws.dynamodb;


public class ImageCreatedItem extends DynamoDBTimedEntry {

    private String name;

    public ImageCreatedItem(long timestamp, String name) {
        super(timestamp);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
