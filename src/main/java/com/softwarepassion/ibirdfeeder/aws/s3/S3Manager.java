package com.softwarepassion.ibirdfeeder.aws.s3;


import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
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
    private static final String BIRD_FEEDER_ALBUM_BUCKET_NAME = "birdfeederalbum001";
    private static final String BIRD_FEEDER_wEBSITE_BUCKET_NAME = "birdfeederwebsite001";
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

    public List<S3File> listWebsiteBucketContent() {
        return listBucketContent(BIRD_FEEDER_wEBSITE_BUCKET_NAME);
    }

    private List<S3File> listBucketContent(String buckeName) {

        ObjectListing listing = s3.listObjects(buckeName);
        List<S3ObjectSummary> summaries = listing.getObjectSummaries();
        List<S3File> s3Files = new ArrayList<>();
        while (listing.isTruncated()) {
            listing = s3.listNextBatchOfObjects(listing);
            summaries.addAll(listing.getObjectSummaries());
        }
        for (S3ObjectSummary objectSummary : summaries) {
            String key = objectSummary.getKey();
            if (key.startsWith("image")) {
                S3File s3File = new S3File(key,
                    S3_BASE_URL + key.replace("image_", "small_image_"),
                    S3_BASE_URL + key.replace("image_", "medium_image_"),
                    S3_BASE_URL + key,
                    objectSummary.getSize());
                s3Files.add(s3File);
            } else if (!key.startsWith("small_image") && !key.startsWith("medium_image")) {
                S3File s3File = new S3File(key, null, null, null, objectSummary.getSize());
                s3Files.add(s3File);
            }
        }
        log.info("Found " + s3Files.size() + " files.");
        return s3Files;
    }

    public void copyFileToAlbum(String fileName) {
        log.info("copying file: " + fileName);
        s3.copyObject(new CopyObjectRequest(BIRD_FEEDER_TEMP_BUCKET_NAME, fileName,
            BIRD_FEEDER_ALBUM_BUCKET_NAME, fileName));
        log.info("copied: " + fileName);
        log.info("copying file: small_" + fileName);
        s3.copyObject(new CopyObjectRequest(BIRD_FEEDER_TEMP_BUCKET_NAME, "small_" + fileName,
            BIRD_FEEDER_ALBUM_BUCKET_NAME, "small_" + fileName));
        log.info("copied: small_" + fileName);
        log.info("copying file: medium_" + fileName);
        s3.copyObject(new CopyObjectRequest(BIRD_FEEDER_TEMP_BUCKET_NAME, "medium_" + fileName,
            BIRD_FEEDER_ALBUM_BUCKET_NAME, "medium_" + fileName));
        log.info("copied: medium_" + fileName);
    }

    public void deleteFileFromTemp(String fileName) {
        s3.deleteObject(new DeleteObjectRequest(BIRD_FEEDER_TEMP_BUCKET_NAME, fileName));
        s3.deleteObject(new DeleteObjectRequest(BIRD_FEEDER_TEMP_BUCKET_NAME, "small_" + fileName));
        s3.deleteObject(new DeleteObjectRequest(BIRD_FEEDER_TEMP_BUCKET_NAME, "medium_" + fileName));
    }

}
