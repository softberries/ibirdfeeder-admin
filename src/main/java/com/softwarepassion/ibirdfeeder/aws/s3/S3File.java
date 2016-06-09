package com.softwarepassion.ibirdfeeder.aws.s3;

import java.text.DecimalFormat;

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

    public String getName() {
        return name;
    }

    public String getSmallImgUrl() {
        return smallImgUrl;
    }

    public String getMediumImgUrl() {
        return mediumImgUrl;
    }

    public String getLargeImgUrl() {
        return largeImgUrl;
    }

    public long getSize() {
        return size;
    }
    
    public String readableFileSize() {
        if (getSize() <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
