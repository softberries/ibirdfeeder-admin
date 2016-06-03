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
    private static final String BIRD_FEEDER_TEMP_BUCKET_NAME = "birdfeeder001";
    private static final String BIRD_FEEDER_ALBUM_BUCKET_NAME = "birdfeederAlbum001";
    private final AmazonS3 s3;

    public S3Manager() {
        s3 = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider("AWSCredentials.properties"));
    }

    public List<S3File> listTempBucketContent() {
        return listBucketContent(BIRD_FEEDER_TEMP_BUCKET_NAME);
    }

    public List<S3File> listAlbumBucketContent() {
        return listBucketContent(BIRD_FEEDER_ALBUM_BUCKET_NAME);
    }

    public List<S3File> listBucketContent(String buckeName) {

        ObjectListing listing = s3.listObjects(buckeName, "small");
        List<S3ObjectSummary> summaries = listing.getObjectSummaries();
        List<S3File> s3Files = new ArrayList<>();
        while (listing.isTruncated()) {
            listing = s3.listNextBatchOfObjects(listing);
            summaries.addAll(listing.getObjectSummaries());
        }
        for (S3ObjectSummary objectSummary : summaries) {
            if (objectSummary.getKey().startsWith("small_image_")) {
                S3File s3File = new S3File(objectSummary.getKey(),
                    S3_BASE_URL + objectSummary.getKey(),
                    S3_BASE_URL + objectSummary.getKey().replace("small", "medium"),
                    S3_BASE_URL + objectSummary.getKey().replace("small_", ""),
                    objectSummary.getSize());
                s3Files.add(s3File);
            }
        }
        log.info("Found " + s3Files.size() + " files.");
        return s3Files;
    }
}
