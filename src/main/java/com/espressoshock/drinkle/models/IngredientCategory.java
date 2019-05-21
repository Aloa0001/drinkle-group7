package com.espressoshock.drinkle.models;


public enum IngredientCategory {
    GLASSWARE("Glassware"), GARNISH("Garnish"), ICE_TYPE("Ice Type"),
    WATER("Water"), DAIRY_PRODUCT("Dairy Product"), VODKA("Vodka"),
    WHISKEY("Whiskey"), VERMOUTH("Vermouth"), BITTER("Bitter"), GIN("Gin"),
    TEQUILA("Tequila"), RUM("Rum"), LIQUEUR("Liqueur"), BRANDY("Brandy"),
    CIDER("Cider"), WINE("Wine"), BEER("Beer"), JUICE("Juice"),
    SYRUP("Syrup"), FRUIT("Fruit"), WARM_DRINK("Warm Drink"), POWDER("Powder"),
    OTHER("Other Category");

    private final String displayName;

    IngredientCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return displayName;
    }
}
