package com.example.bookandpostroom;

public class OptionItem {
    private final String name;
    private final int iconResId;

    public OptionItem(String name, int iconResId) {
        this.name = name;
        this.iconResId = iconResId;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }
}

