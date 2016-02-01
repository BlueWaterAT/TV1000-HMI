package com.bwat.hmi.data;

import javax.swing.JButton;

public class BindedJButton extends JButton {
    BindedString data;

    public BindedJButton(String path) {
        data = new BindedString(path, new BindedStringListener() {
            @Override
            public void stringChanged(String content) {
                if (!content.equals(getText())) {
                    setData(content);
                }
            }
        });
        setData(data.getContent());
    }

    public void setData(String text) {
        super.setText(text);
        data.setContent(text);
    }

}
