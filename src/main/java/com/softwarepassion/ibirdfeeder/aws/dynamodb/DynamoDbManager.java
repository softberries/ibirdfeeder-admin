package com.softwarepassion.ibirdfeeder.aws.dynamodb;


import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.TableDescription;

public class DynamoDbManager {

    private AmazonDynamoDBClient dynamoDBClient;
    private static final String tableName = "birdfeeder";

    public DynamoDbManager(){
        dynamoDBClient = new AmazonDynamoDBClient(new ClasspathPropertiesFileCredentialsProvider("AWSCredentials.properties"));
        dynamoDBClient.withRegion(Regions.EU_WEST_1);
    }

    public void getItems(){
        DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(tableName);
        TableDescription tableDescription = dynamoDBClient.describeTable(describeTableRequest).getTable();
        System.out.println("Table Description: " + tableDescription);
    }
}
