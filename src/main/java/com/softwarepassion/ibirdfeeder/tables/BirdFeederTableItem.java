package com.softwarepassion.ibirdfeeder.tables;

import com.softwarepassion.ibirdfeeder.aws.dynamodb.BirdFeederItem;
import javafx.beans.property.SimpleStringProperty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BirdFeederTableItem {

    private final SimpleStringProperty time;
    private final SimpleStringProperty topic;
    private final SimpleStringProperty payload;

    public BirdFeederTableItem(SimpleStringProperty time, SimpleStringProperty topic, SimpleStringProperty payload) {
        this.time = time;
        this.topic = topic;
        this.payload = payload;
    }

    public BirdFeederTableItem(BirdFeederItem item) {
        this.time = new SimpleStringProperty(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(item.getTimestamp()))));
        this.topic = new SimpleStringProperty(item.getTopic());
        this.payload = new SimpleStringProperty(item.getPayload().toString());
    }

    public String getTime() {
        return time.get();
    }

    public SimpleStringProperty timeProperty() {
        return time;
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public String getTopic() {
        return topic.get();
    }

    public SimpleStringProperty topicProperty() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic.set(topic);
    }

    public String getPayload() {
        return payload.get();
    }

    public SimpleStringProperty payloadProperty() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload.set(payload);
    }
}
