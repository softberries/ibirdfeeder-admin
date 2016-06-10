package com.softwarepassion.ibirdfeeder.aws.dynamodb;


import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DynamoDbManager {

    private static final Logger log = LoggerFactory.getLogger(DynamoDbManager.class);
    private static final String IMAGE_CREATED_TOPIC = "image_created";
    private static final String TEMPERATURE_TOPIC = "temperature";
    private static final String tableName = "birdfeeder";

    private AmazonDynamoDBClient dynamoDBClient;
    private DynamoDB dynamoDB;
    private boolean refresh;
    private List<BirdFeederItem> birdFeederItems;

    public DynamoDbManager() {
        dynamoDBClient = new AmazonDynamoDBClient(new ClasspathPropertiesFileCredentialsProvider("AWSCredentials.properties"));
        dynamoDBClient.withRegion(Regions.EU_WEST_1);
        dynamoDB = new DynamoDB(dynamoDBClient);
    }

    public List<TemperatureItem> getAllTemperatureItems() {
        refreshIfNeeded();
        return birdFeederItems.stream().filter(bfi -> bfi.getTopic().equals(TEMPERATURE_TOPIC)).map(
            bfi -> new TemperatureItem(Long.parseLong(bfi.getTimestamp()), Double.parseDouble(bfi.getPayload().get("temperature").getS()))).collect(Collectors.toList());
    }

    public List<TemperatureItem> getCurrentDayTemperatureItems() {
        LocalDateTime now = LocalDateTime.now();
        List<TemperatureItem> allItems = getAllTemperatureItems();
        Map<Integer, List<TemperatureItem>> tempItemsGrouped =
            allItems.stream().collect(Collectors.groupingBy(TemperatureItem::getMonth));
        List<TemperatureItem> currentMonthItems = tempItemsGrouped.get(now.getMonth().getValue());
        Map<Integer, List<TemperatureItem>> tempItemsByDayGrouped =
            currentMonthItems.stream().collect(Collectors.groupingBy(TemperatureItem::getDay));
        ComparatorChain chain = new ComparatorChain(Arrays.asList(
            new BeanComparator("hour"),
            new BeanComparator("minute")));
        List<TemperatureItem> result = tempItemsByDayGrouped.get(now.getDayOfMonth()) != null ?
            tempItemsByDayGrouped.get(now.getDayOfMonth()) : new ArrayList<>();
        Collections.sort(result, chain);
        return result;
    }

    public List<TemperatureItem> getCurrentMonthTemperatureItems() {
        LocalDateTime now = LocalDateTime.now();
        List<TemperatureItem> allItems = getAllTemperatureItems();
        Map<Integer, List<TemperatureItem>> tempItemsGrouped =
            allItems.stream().collect(Collectors.groupingBy(TemperatureItem::getMonth));
        List<TemperatureItem> currentMonthItems = tempItemsGrouped.get(now.getMonth().getValue());
        Map<Integer, List<TemperatureItem>> tempItemsByDayGrouped =
            currentMonthItems.stream().collect(Collectors.groupingBy(TemperatureItem::getDay));
        List<TemperatureItem> result = tempItemsByDayGrouped.entrySet().stream().map(
            e -> new TemperatureItem(e.getValue().get(0).getTimestamp(), getAverageTemperature(e.getValue()))).collect(Collectors.toList());
        ComparatorChain chain = new ComparatorChain(Arrays.asList(
            new BeanComparator("day")));
        Collections.sort(result, chain);
        return result;
    }

    private double getAverageTemperature(List<TemperatureItem> items) {
        return items.stream().mapToDouble(TemperatureItem::getTemperature).average().getAsDouble();
    }

    private void refreshIfNeeded() {
        if (birdFeederItems == null || birdFeederItems.isEmpty() || refresh) {
            birdFeederItems = fetchItems();
            this.refresh = false;
        }
    }

    private List<BirdFeederItem> fetchItems() {
        List<BirdFeederItem> birdFeederItems = new ArrayList<>();
        ScanResult result = null;
        do {
            ScanRequest req = new ScanRequest();
            req.setTableName(tableName);
            if (result != null) {
                req.setExclusiveStartKey(result.getLastEvaluatedKey());
            }
            result = dynamoDBClient.scan(req);
            List<Map<String, AttributeValue>> rows = result.getItems();
            for (Map<String, AttributeValue> map : rows) {
                try {
                    AttributeValue topic = map.get("topic");
                    AttributeValue payload = map.get("payload");
                    AttributeValue timestamp = map.get("timestamp");
                    String topicValue = topic.getS();
                    String timestampValue = timestamp.getS();
                    Map<String, AttributeValue> payloadValue = payload.getM();
                    birdFeederItems.add(new BirdFeederItem(payloadValue, topicValue, timestampValue));
                } catch (NumberFormatException e) {
                    log.info(e.getMessage());
                }
            }
        } while (result.getLastEvaluatedKey() != null);
        log.info("Result size: " + birdFeederItems.size());
        return birdFeederItems;
    }
}
