package com.espressoshock.drinkle.controllers.app.beverageBuilder;

public class Glassware {

    private String name;
    private Integer volume;
    private String imageUrl;

    Glassware(String name, Integer volume, String imageUrl) {
        this.name = name;
        this.volume = volume;
        this.imageUrl = imageUrl;
    }

    String getName() {
        return name;
    }

    public Integer getVolume() {
        return volume;
    }

//    String getImageUrl() {
//        return imageUrl;
//    }
}
