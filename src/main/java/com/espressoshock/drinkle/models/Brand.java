package com.espressoshock.drinkle.models;

public class Brand {
    private String name;
    private IngredientCategory category;

    public Brand(String name, IngredientCategory category) {
        this.name = name;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IngredientCategory getCategory() {
        return category;
    }

    public void setCategory(IngredientCategory category) {
        this.category = category;
    }
}
