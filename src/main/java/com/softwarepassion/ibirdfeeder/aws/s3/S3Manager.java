package com.softwarepassion.ibirdfeeder.aws.s3;


import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class S3Manager {

    private static final Logger log = LoggerFactory.getLogger(S3Manager.class);

    private static final String S3_BASE_URL = "https://s3-eu-west-1.amazonaws.com/birdfeeder001/";
    private static final String BIRD_FEEDER_BUCKET_NAME = "birdfeeder001";
    private static final int MAX_SINGLE_LOAD_SIZE = 1000;
    private final AmazonS3 s3;

    public S3Manager() {
        s3 = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider("AWSCredentials.properties"));
    }

    public List<String> listBucketContent() {

        ObjectListing listing = s3.listObjects(BIRD_FEEDER_BUCKET_NAME, "small");
        List<S3ObjectSummary> summaries = listing.getObjectSummaries();
        List<String> urls = new ArrayList<>();
//        while (listing.isTruncated()) {
//            listing = s3.listNextBatchOfObjects(listing);
//            summaries.addAll(listing.getObjectSummaries());
//            if(summaries.size() >= MAX_SINGLE_LOAD_SIZE){
//                break;
//            }
//        }
        for (S3ObjectSummary objectSummary : summaries) {
            if (objectSummary.getKey().startsWith("small_image_")) {
                urls.add(S3_BASE_URL + objectSummary.getKey());
            }
        }
        log.info("Found " + urls.size() + " files.");
        return urls;
    }
}
