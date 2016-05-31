package com.softwarepassion.ibirdfeeder;

import javafx.scene.control.TextArea;

public class Console {

    private TextArea output;

    public Console(TextArea ta) {
        this.output = ta;
    }

    public void write(String str) {
        output.appendText(str + "\n");
    }
}