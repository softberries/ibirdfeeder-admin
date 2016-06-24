package com.softwarepassion.ibirdfeeder.aws.dynamodb;


public class ImageCreatedStat {
    private int count;
    private String key;

    public ImageCreatedStat(int count, String key) {
        this.count = count;
        this.key = key;
    }

    public int getCount() {
        return count;
    }

    public String getKey() {
        return key;
    }
}
