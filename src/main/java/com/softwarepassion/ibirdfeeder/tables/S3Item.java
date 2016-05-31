package com.softwarepassion.ibirdfeeder.tables;

import javafx.beans.property.SimpleStringProperty;

public class S3Item {

    private final SimpleStringProperty name;
    private final SimpleStringProperty size;
    private final SimpleStringProperty status;
    private final String url;

    public S3Item(String name, String url, String size, String status) {
        this.name = new SimpleStringProperty(name);
        this.url = url;
        this.size = new SimpleStringProperty(size);
        this.status = new SimpleStringProperty(status);
    }

    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public String getUrl() {
        return url;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getSize() {
        return size.get();
    }

    public SimpleStringProperty sizeProperty() {
        return size;
    }

    public void setSize(String size) {
        this.size.set(size);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        S3Item s3Item = (S3Item) o;

        if (!getName().equals(s3Item.getName())) return false;
        if (!getSize().equals(s3Item.getSize())) return false;
        if (!getStatus().equals(s3Item.getStatus())) return false;
        return getUrl().equals(s3Item.getUrl());

    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getSize().hashCode();
        result = 31 * result + getStatus().hashCode();
        result = 31 * result + getUrl().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "S3Item{" +
            "name=" + name +
            ", size=" + size +
            ", status=" + status +
            ", url='" + url + '\'' +
            '}';
    }
}
