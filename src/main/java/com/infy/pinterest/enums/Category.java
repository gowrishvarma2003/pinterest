package com.infy.pinterest.enums;

public enum Category {
    TRAVEL("Travel"),
    FOOD("Food"),
    FITNESS("Fitness"),
    TECHNOLOGY("Technology"),
    FASHION("Fashion"),
    PHOTOGRAPHY("Photography"),
    DIY("DIY"),
    MOTIVATION("Motivation"),
    ART("Art"),
    MUSIC("Music"),
    GAMING("Gaming"),
    BEAUTY("Beauty"),
    HOME_DECOR("Home Decor"),
    NATURE("Nature"),
    SPORTS("Sports"),
    EDUCATION("Education"),
    LIFESTYLE("Lifestyle"),
    BUSINESS("Business");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
