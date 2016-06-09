package com.softwarepassion.ibirdfeeder.aws.dynamodb;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.Map;

public class BirdFeederItem {

    private Map<String, AttributeValue> payload;
    private String topic;
    private String timestamp;

    public BirdFeederItem(Map<String, AttributeValue> payload, String topic, String timestamp) {
        this.payload = payload;
        this.topic = topic;
        this.timestamp = timestamp;
    }

    public Map<String, AttributeValue> getPayload() {
        return payload;
    }

    public String getTopic() {
        return topic;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
