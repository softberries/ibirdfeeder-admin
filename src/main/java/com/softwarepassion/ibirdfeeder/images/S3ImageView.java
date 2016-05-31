package com.softwarepassion.ibirdfeeder.images;

import javafx.scene.image.ImageView;

public class S3ImageView extends ImageView {

    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
