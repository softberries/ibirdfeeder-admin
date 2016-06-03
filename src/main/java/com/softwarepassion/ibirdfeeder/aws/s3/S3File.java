package com.softwarepassion.ibirdfeeder.aws.s3;

public class S3File {
    private String name;
    private String smallImgUrl;
    private String mediumImgUrl;
    private String largeImgUrl;
    private long size;

    public S3File(String name, String smallImgUrl, String mediumImgUrl, String largeImgUrl, long size) {
        this.name = name;
        this.smallImgUrl = smallImgUrl;
        this.mediumImgUrl = mediumImgUrl;
        this.largeImgUrl = largeImgUrl;
        this.size = size;
    }
}
