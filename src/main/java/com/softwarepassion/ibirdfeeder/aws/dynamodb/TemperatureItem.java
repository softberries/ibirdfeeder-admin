package com.softwarepassion.ibirdfeeder.aws.dynamodb;

public class TemperatureItem extends DynamoDBTimedEntry {
    private double temperature;


    public TemperatureItem(long timestamp, double temperature) {
        super(timestamp);
        this.temperature = temperature;
    }

    public double getTemperature() {
        return temperature;
    }
}
