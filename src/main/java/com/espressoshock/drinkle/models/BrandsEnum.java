package com.espressoshock.drinkle.models;

public enum BrandsEnum {
    ABSOLUTE("Absolute",  IngredientCategory.VODKA),GREY_GOOSE("Grey Goose",  IngredientCategory.VODKA),
    BLACK_COW("Black Cow", IngredientCategory.VODKA),SMIRNOFF("Smirnoff",  IngredientCategory.VODKA),
    STOLICHNAYA("Stolichnaya",  IngredientCategory.VODKA),TITOS("Tito's",  IngredientCategory.VODKA),
    JEAN_MARC("Jean-Marc", IngredientCategory.VODKA),JACK_DANIELS("Jack Daniel's",  IngredientCategory.WHISKEY),
    JOHNNIE_WALKER("Johnnie Walker",  IngredientCategory.WHISKEY),JIM_BEAM("Jim Beam",  IngredientCategory.WHISKEY),
    GLENFARCLASS("Glenfarclass",  IngredientCategory.WHISKEY),JAMESON("Jameson",  IngredientCategory.WHISKEY),
    GLENFIDDICH("Glenfiddich", IngredientCategory.WHISKEY),MARTINI("Martini", IngredientCategory.VERMOUTH),
    CINZANO("Cinzano",  IngredientCategory.VERMOUTH),CONTRATTO("Contratto",  IngredientCategory.VERMOUTH),
    COCCHI("Cocchi",  IngredientCategory.VERMOUTH),BEEFEATER("Beefeater",  IngredientCategory.GIN),
    GORDONS("Gordon's",  IngredientCategory.GIN),FORTALEZA("Fortaleza",  IngredientCategory.TEQUILA),
    JOSE_CUERVO("Jose Cuervo",  IngredientCategory.TEQUILA),HERANCIA_DE_PLATA("Herencia de Plata",  IngredientCategory.TEQUILA),
    BACARDI("Bacardi",  IngredientCategory.RUM),HAVANA_CLUB("Havana Club", IngredientCategory.RUM),
    SAINT_JAMES("Saint James",  IngredientCategory.RUM),GREEN_ISLAND("Green Island", IngredientCategory.RUM),
    COGNAC("Cognac", IngredientCategory.BRANDY),ARMAGNAC( "Armagnac", IngredientCategory.BRANDY),
    AMARETTO("Amaretto",  IngredientCategory.LIQUEUR),BAILEYS("Baileys",  IngredientCategory.LIQUEUR),
    COINTREAU("Cointreau",  IngredientCategory.LIQUEUR),KAHLUA("Kahlua",  IngredientCategory.LIQUEUR),
    SAMBUCA("Sambuca", IngredientCategory.LIQUEUR),SHERIDANS("Sheridan's",  IngredientCategory.LIQUEUR),
    TIA_MARIA("Tia Maria", IngredientCategory.LIQUEUR),MIDNIGHT_ESPRESSO("Midnight Espresso",  IngredientCategory.LIQUEUR),
    AMARULA("Amarula",  IngredientCategory.LIQUEUR),CAROLANS("Carolans", IngredientCategory.LIQUEUR),
    CRUZAN("Cruzan",  IngredientCategory.LIQUEUR),CREME_DE_CASSIS("Creme de cassis", IngredientCategory.LIQUEUR),
    GUAVABERRY("Guavaberry", IngredientCategory.LIQUEUR),CURACAO("Curacao", IngredientCategory.LIQUEUR),
    ROSOLIO("Rosolio",  IngredientCategory.LIQUEUR),TRIPLE_SEC("Triple Sec",  IngredientCategory.LIQUEUR),
    VISINATA("Visinata",  IngredientCategory.LIQUEUR),ANISETTE("Anisette",  IngredientCategory.LIQUEUR),
    CREME_DE_MENTHE("Creme de menthe", IngredientCategory.LIQUEUR),FERNET("Fernet",  IngredientCategory.LIQUEUR),
    GALLIANO("Galliano",  IngredientCategory.LIQUEUR),MASATICA("Masatica",  IngredientCategory.LIQUEUR),
    JAGERMEISTER("Jagermeister",  IngredientCategory.LIQUEUR),UNICUM("Unicum", IngredientCategory.LIQUEUR),
    DRAMBUIE("Drambuie",  IngredientCategory.LIQUEUR),DISARONNO("Disaronno", IngredientCategory.LIQUEUR),
    NOCELLO("Nocello", IngredientCategory.LIQUEUR),ADVOCAAT("Advocaat", IngredientCategory.LIQUEUR),
    ROCK_RYE("Rock&Rye",  IngredientCategory.LIQUEUR),BANANAS( "Bananas", IngredientCategory.LIQUEUR),
    CAMPARI("Campari", IngredientCategory.LIQUEUR),GRAND_MARNIER("Grand Marnier",  IngredientCategory.LIQUEUR),
    LIMONCELLO("Limoncello",  IngredientCategory.LIQUEUR),MARASCHINO("Maraschino", IngredientCategory.LIQUEUR),
    MIDORI("Midori",  IngredientCategory.LIQUEUR),MANZANA_VERDE("Manzana Verde", IngredientCategory.LIQUEUR),
    NOIAU_DE_POISSY("Noiau de Poissy", IngredientCategory.LIQUEUR),PAMA("Pama", IngredientCategory.LIQUEUR),
    SAUVIGNON_BLANC("Sauvignon Blanc", IngredientCategory.WINE),CHARDONNAY("Chardonnay", IngredientCategory.WINE),
    PINOT_GRIS("Pinot Gris", IngredientCategory.WINE),CABERNET_SAUVIGNON("Cabernet Sauvignon", IngredientCategory.WINE),
    MERLOT("Merlot", IngredientCategory.WINE),SHIRAZ("Shiraz", IngredientCategory.WINE),
    PINOT_NOIR("Pinot Noir", IngredientCategory.WINE),GRENACHE("Grenache", IngredientCategory.WINE),
    DOM_PERIGNON("Dom Perignon",IngredientCategory.WINE),MOET_CHANDON("Moet&Chandon", IngredientCategory.WINE),
    CRISTAL("Cristal", IngredientCategory.WINE),PERRIER_JOUET("Perrier Jouet", IngredientCategory.WINE),
    PROSECCO("Prosecco",  IngredientCategory.WINE),LOUIS_BOUILOT("Louis Bouilot",  IngredientCategory.WINE),
    DEPREVILLE("Depreville", IngredientCategory.WINE),MAGNERS("Magners", IngredientCategory.CIDER),
    SOMERSBY("Somersby", IngredientCategory.CIDER),POMAGNE("Pomagne", IngredientCategory.CIDER),
    BULMERS("Bulmers", IngredientCategory.CIDER),FIZZ("Fizz",  IngredientCategory.CIDER),
    HEINEKEN("Heineken",  IngredientCategory.BEER),CARLSBERG("Carlsberg",  IngredientCategory.BEER),
    BUDWEISER("Budweiser",  IngredientCategory.BEER),CORONA("Corona",  IngredientCategory.BEER),
    PAULANER("Paulaner",  IngredientCategory.BEER),STELLA_ARTIOS("Stella Artios",  IngredientCategory.BEER),
    BIRRA_MORETTI("Birra Moretti",  IngredientCategory.BEER),SKOL("Skol",  IngredientCategory.BEER),
    OTHER_BRAND("Other Brand",IngredientCategory.OTHER);

    private final String brandName;
    private final IngredientCategory productType;

    BrandsEnum(String brandNAme, IngredientCategory productType) {
        this.brandName = brandNAme;
        this.productType = productType;
    }

    public String getBrandName() {
        return brandName;
    }

    public IngredientCategory getProductType() {
        return productType;
    }

    public static BrandsEnum fromString(String name) {
        for (BrandsEnum b : BrandsEnum.values()) {
            if (b.brandName.equalsIgnoreCase(name)) {
                return b;
            }
        }
        return null;
    }
}
