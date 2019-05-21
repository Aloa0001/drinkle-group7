package com.espressoshock.drinkle.controllers.app;

import com.espressoshock.drinkle.appState.Current;
import com.espressoshock.drinkle.models.*;
import com.espressoshock.drinkle.viewLoader.EventDispatcherAdapter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.net.URL;
import com.espressoshock.drinkle.databaseLayer.ConnectionLayer;
import java.sql.*;
import java.util.*;

public class IngredientList extends EventDispatcherAdapter implements Initializable {

    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;

    private ArrayList<Ingredient> ingredientsList = new ArrayList<>();
    private ArrayList<IngredientCategory> categoriesAlc = new ArrayList<>();
    private ArrayList<BrandsEnum> brandsList = new ArrayList<>();
    private ArrayList<IngredientCategory> categoryNonAlc = new ArrayList<>();


    @FXML
    private VBox vBoxIngredients;
    @FXML
    private Button btnSimilarProduct,btnSearch;
    @FXML
    private MenuButton menuBtnCategory, menuBtnBrand, menuBtnAlcoholOption;
    @FXML
    private TextField txtSearchOption, txtSimilarWith;
    @FXML
    private ProgressBar progressBarAlcohol, progressBarPrice;
    @FXML
    private Label lblSelectedIngredientName,lblIngredientCategory, lblAlcohol, lblPrice, lblIngredientBrand;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        createCategoryList();
        populateNonAlcCategories();
        populateBrandsList();
        try {
            retrieveIngredientsFromDB();
            System.out.println("Ingredients list populated");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to populate the Ingredients list");
        }

    }

    @FXML
    private void selectBtnSearch() {
        vBoxIngredients.getChildren().clear();
        String text = txtSearchOption.getText().toLowerCase();
        for (Ingredient x : ingredientsList) {
            if (text.length() != 0&&x.getName().toLowerCase().contains(text)) {
                    Button button = new Button();
                    button.setOnAction(this::selectVbButton);
                    button.setMinWidth(400);
                    button.setMinHeight(40);
                    button.setText(x.getName());
                    vBoxIngredients.getChildren().add(button);
            }
            if(x.getBrand().getProductType().getName().toLowerCase().contains(text)){
                Button button = new Button();
                button.setOnAction(this::selectVbButton);
                button.setMinWidth(400);
                button.setMinHeight(40);
                button.setText(x.getName());
                vBoxIngredients.getChildren().add(button);
            }
        }
        menuBtnBrand.setText("Brand");
        menuBtnCategory.setText("Category");
        btnSearch.setDisable(false);
    }

    @FXML
    private void selectAlcoholSelection(ActionEvent event) {
        vBoxIngredients.getChildren().clear();
        MenuItem selection = (MenuItem) event.getSource();
        if (selection.getText().equals("Alcoholic")) {
            menuBtnAlcoholOption.setText("Alcohol");
            populateCategoryMenu(categoriesAlc);
            menuBtnCategory.setText("Categories");
            menuBtnBrand.setText("Brand");
        } else {
            menuBtnAlcoholOption.setText("Non Alcoholic");
            populateCategoryMenu((categoryNonAlc));
            menuBtnCategory.setText("Categories");
            menuBtnBrand.setText("Brand");
            menuBtnBrand.getItems().clear();
            vBoxIngredients.getChildren().clear();
        }
    }

    @FXML
    private void selectCategory(ActionEvent e) {

        vBoxIngredients.getChildren().clear();
        menuBtnBrand.setText("Brands");
        MenuItem selection = (MenuItem) e.getSource();
        menuBtnCategory.setText(selection.getText());
        menuBtnBrand.getItems().clear();
        for (BrandsEnum brandsEnum : brandsList) {
            if (selection.getText().equals(brandsEnum.getProductType().getName())) {
                MenuItem button = new MenuItem();
                button.setText(brandsEnum.getBrandName());
                button.setOnAction(this::selectBrand);
                menuBtnBrand.getItems().add(button);
            }
        }

    }

    @FXML
    private void selectBrand(ActionEvent e) {
        MenuItem selection = (MenuItem) e.getSource();
        menuBtnBrand.setText(selection.getText());
        vBoxIngredients.getChildren().clear();
        for (Ingredient x : ingredientsList) {
            if (selection.getText().equals(x.getBrand().getBrandName())) {
                Button button = new Button();
                button.setOnAction(this::selectVbButton);
                button.setMinWidth(400);
                button.setMinHeight(40);
                button.setText(x.getName());
                vBoxIngredients.getChildren().add(button);
            }
        }
    }

    @FXML
    private void selectVbButton(ActionEvent e) {
        try {
            Button selection = (Button) e.getSource();
            for (Ingredient x : ingredientsList) {
                if (x.getName().equals(selection.getText())) {
                    lblSelectedIngredientName.setText(x.getName());
                    txtSimilarWith.setText(x.getName());
                    lblAlcohol.setText(Integer.toString(x.getAlcoholPercentage()));
                    progressBarAlcohol.setProgress(Double.valueOf(x.getAlcoholPercentage()) / 100);
                    lblPrice.setText(Integer.toString(x.getPricePerLiter()));
                    progressBarPrice.setProgress(Double.valueOf(x.getPricePerLiter()) / 1000);
                    lblIngredientBrand.setText(x.getBrand().getBrandName());
                    lblIngredientCategory.setText(x.getBrand().getProductType().getName());
                    for (Ingredient y : ingredientsList) {
                        if (x.getBrand().getProductType().getName().equals(y.getBrand().getProductType().getName())&&!x.getName().equals(y.getName())) {
                                btnSimilarProduct.setText(y.getName());
                        }
                    }
                }
            }
            txtSearchOption.setText(" ");
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private void createCategoryList() {
        IngredientCategory[] category = {IngredientCategory.WHISKEY, IngredientCategory.VODKA, IngredientCategory.VERMOUTH, IngredientCategory.BITTER, IngredientCategory.TEQUILA, IngredientCategory.GIN, IngredientCategory.RUM, IngredientCategory.LIQUEUR,
                IngredientCategory.BRANDY, IngredientCategory.CIDER, IngredientCategory.WINE, IngredientCategory.BEER, IngredientCategory.OTHER};
        Collections.addAll(categoriesAlc, category);
    }
    private void populateCategoryMenu(ArrayList<IngredientCategory> categoriesData) {
        menuBtnCategory.getItems().clear();
        for (IngredientCategory x : categoriesData) {
            MenuItem category = new MenuItem(x.getName());
            category.setOnAction(this::selectCategory);
            menuBtnCategory.getItems().add(category);
        }
    }
    private void populateNonAlcCategories() {
        IngredientCategory[] nonAlcCategories = {IngredientCategory.GARNISH, IngredientCategory.ICE_TYPE, IngredientCategory.WATER, IngredientCategory.POWDER, IngredientCategory.OTHER,
                IngredientCategory.DAIRY_PRODUCT, IngredientCategory.JUICE, IngredientCategory.SYRUP, IngredientCategory.FRUIT, IngredientCategory.WARM_DRINK};
        Collections.addAll(categoryNonAlc, nonAlcCategories);
    }
    private void populateBrandsList() {
        BrandsEnum[] brands = {BrandsEnum.DOM_PERIGNON, BrandsEnum.GRENACHE, BrandsEnum.PINOT_NOIR, BrandsEnum.SHIRAZ, BrandsEnum.MERLOT, BrandsEnum.CABERNET_SAUVIGNON, BrandsEnum.PINOT_GRIS, BrandsEnum.CHARDONNAY, BrandsEnum.SAUVIGNON_BLANC, BrandsEnum.PAMA, BrandsEnum.MARASCHINO,
                BrandsEnum.LIMONCELLO, BrandsEnum.GRAND_MARNIER, BrandsEnum.CAMPARI, BrandsEnum.BANANAS, BrandsEnum.ROCK_RYE, BrandsEnum.ADVOCAAT, BrandsEnum.NOCELLO, BrandsEnum.DISARONNO, BrandsEnum.DRAMBUIE, BrandsEnum.UNICUM, BrandsEnum.JAGERMEISTER, BrandsEnum.MASATICA, BrandsEnum.COCCHI,
                BrandsEnum.CONTRATTO, BrandsEnum.ANISETTE, BrandsEnum.VISINATA, BrandsEnum.TRIPLE_SEC, BrandsEnum.ROSOLIO, BrandsEnum.CURACAO, BrandsEnum.GUAVABERRY, BrandsEnum.CREME_DE_CASSIS, BrandsEnum.CRUZAN, BrandsEnum.CAROLANS, BrandsEnum.AMARULA, BrandsEnum.MIDNIGHT_ESPRESSO, BrandsEnum.TIA_MARIA,
                BrandsEnum.BAILEYS, BrandsEnum.AMARETTO, BrandsEnum.ARMAGNAC, BrandsEnum.COGNAC, BrandsEnum.GREEN_ISLAND, BrandsEnum.SAINT_JAMES, BrandsEnum.HAVANA_CLUB, BrandsEnum.BACARDI, BrandsEnum.HERANCIA_DE_PLATA, BrandsEnum.JOSE_CUERVO, BrandsEnum.FORTALEZA, BrandsEnum.GORDONS, BrandsEnum.BEEFEATER,
                BrandsEnum.MARTINI, BrandsEnum.GLENFIDDICH, BrandsEnum.JAMESON, BrandsEnum.GLENFARCLASS, BrandsEnum.JIM_BEAM, BrandsEnum.JOHNNIE_WALKER, BrandsEnum.JACK_DANIELS, BrandsEnum.JEAN_MARC, BrandsEnum.TITOS, BrandsEnum.STOLICHNAYA, BrandsEnum.SMIRNOFF, BrandsEnum.BLACK_COW, BrandsEnum.GREY_GOOSE,
                BrandsEnum.OTHER_BRAND, BrandsEnum.SHERIDANS, BrandsEnum.SAMBUCA, BrandsEnum.KAHLUA, BrandsEnum.CREME_DE_MENTHE, BrandsEnum.FERNET, BrandsEnum.GALLIANO, BrandsEnum.SKOL, BrandsEnum.BIRRA_MORETTI, BrandsEnum.STELLA_ARTIOS, BrandsEnum.PAULANER, BrandsEnum.CORONA, BrandsEnum.BUDWEISER,
                BrandsEnum.CARLSBERG, BrandsEnum.HEINEKEN, BrandsEnum.COINTREAU, BrandsEnum.MIDORI, BrandsEnum.NOIAU_DE_POISSY, BrandsEnum.MANZANA_VERDE, BrandsEnum.FIZZ, BrandsEnum.BULMERS, BrandsEnum.POMAGNE, BrandsEnum.SOMERSBY, BrandsEnum.MAGNERS, BrandsEnum.DEPREVILLE,
                BrandsEnum.PROSECCO, BrandsEnum.PERRIER_JOUET, BrandsEnum.CINZANO, BrandsEnum.CRISTAL, BrandsEnum.MOET_CHANDON, BrandsEnum.ABSOLUTE, BrandsEnum.COCA_COLA,BrandsEnum.SANTAL,BrandsEnum.PEPSI,BrandsEnum.FRESH_JUICE,BrandsEnum.UNKNOWN,BrandsEnum.NATURAL_SYRUP,BrandsEnum.COCKTAIL_SYRUP,
                BrandsEnum.FRESH_FRUITS,BrandsEnum.CAN_FRUITS,BrandsEnum.CONFIT_FRUITS,BrandsEnum.COFFEE_ESPRESSO,BrandsEnum.HOT_CHOCOLATE,BrandsEnum.TEA,BrandsEnum.BOILD_WINE,BrandsEnum.POWDER_FOR_COCKTAIL,BrandsEnum.DIFFERENT_GARNISHES,BrandsEnum.FROZEN_FRUITS,BrandsEnum.ICE,BrandsEnum.PERRIER,
                BrandsEnum.NORDIC,BrandsEnum.EVIAN,BrandsEnum.EGG_BASED,BrandsEnum.MILK_BASED};
        Collections.addAll(brandsList, brands);
    }

    /**
     * DB connection
     */
    @FXML
    private void addIngredient() throws Exception {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        int ingredientID = 0;
        int accountID = 0;
        try {
            ingredientID = retrieveIngredientIdFromDB(lblSelectedIngredientName.getText());
            accountID = retrieveUserIdFromDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            connection = ConnectionLayer.getConnection();
            statement = connection.createStatement();
            String sql = "INSERT INTO company_account_has_ingredient(company_account_id, ingredient_id) VALUES(?,?)";
            PreparedStatement pstmt = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1,accountID);
            pstmt.setInt(2,ingredientID);
            int rowAffected = pstmt.executeUpdate();
            if(rowAffected == 1) {
                alert.setHeaderText(lblSelectedIngredientName.getText()+" was added to your list!" );
                alert.setContentText("Ingredient "+lblSelectedIngredientName.getText()+" was added to your collection of ingredients.");
                alert.showAndWait();
            }
        } catch (SQLException ex) {
            alert.setAlertType(Alert.AlertType.WARNING);
            alert.setHeaderText("You have already the ingredient "+lblSelectedIngredientName.getText()+" in your list!" );
            alert.setContentText("Please chose another ingredient!");
            alert.showAndWait();
        } finally {
            ConnectionLayer.cleanUp(statement, resultSet);
        }
        connection.close();
    }
    private void retrieveIngredientsFromDB() throws Exception {
        String ingredient_name;
        int ingredient_alcohol;
        int ingredient_price_per_litre;
        String ingredient_brand;
        try {
            connection = ConnectionLayer.getConnection();
            statement = connection.createStatement();
            String query = "select ingredient.name,brand.name,ingredient.price_per_litre,ingredient.alcohol from ingredient,brand where ingredient.brand_id=brand.id";
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                ingredient_name = resultSet.getString(1);
                ingredient_alcohol = resultSet.getInt(4);
                ingredient_price_per_litre = resultSet.getInt(3);
                ingredient_brand = resultSet.getString(2);
                for (BrandsEnum brand : brandsList) {
                    if (ingredient_brand.equals(brand.getBrandName())) {
                        ingredientsList.add(new Ingredient(ingredient_name, ingredient_alcohol, ingredient_price_per_litre, brand, 0));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            ConnectionLayer.cleanUp(statement, resultSet);
        }
        connection.close();
    }
    private int retrieveIngredientIdFromDB(String ingredientName) throws Exception {
        int ingredient_id = 0;
        try {
            connection = ConnectionLayer.getConnection();
            statement = connection.createStatement();
            String query = String.format("select ingredient.id from ingredient where ingredient.name like '%s';", ingredientName);
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                ingredient_id = resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println("Exception: ");
            ex.printStackTrace();
        } finally {
            ConnectionLayer.cleanUp(statement, resultSet);
        }
        connection.close();
        return ingredient_id;
    }
    private int retrieveUserIdFromDB() throws Exception {
        int user_id = 0;
        String user_email = Current.environment.currentUser.getEmail();
        try {
            connection = ConnectionLayer.getConnection();
            statement = connection.createStatement();
            String query = String.format("select company_account.id from company_account where company_account.email like '%s';", user_email);
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                user_id = resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println("Exception: ");
            ex.printStackTrace();
        } finally {
            ConnectionLayer.cleanUp(statement, resultSet);
        }
        connection.close();
        return user_id;
    }
}