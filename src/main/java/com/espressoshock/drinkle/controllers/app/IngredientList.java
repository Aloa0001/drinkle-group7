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
    private ArrayList<BrandsEnum> brandsList = new ArrayList<>();
    private ArrayList<IngredientCategory> categoriesAlc = new ArrayList<>();
    private ArrayList<IngredientCategory> categoryNonAlc = new ArrayList<>();
    private ArrayList<String> accountBeverages = new ArrayList<>();
    private ArrayList<String> accountIngredients = new ArrayList<>();


    @FXML
    private VBox vBoxIngredients, vBoxSimilarIngredient,vBoxBevOrIng;
    @FXML
    private Button  btnSearch, btnAddIngredient, btnCloseList;
    @FXML
    private MenuButton menuBtnCategory, menuBtnBrand, menuBtnAlcoholOption,menuBtnAccount;
    @FXML
    private TextField txtSearchOption, txtSimilarWith, txtFieldMinAlc, txtFieldMaxAlc;
    @FXML
    private ProgressBar progressBarAlcohol, progressBarPrice;
    @FXML
    private Label lblSelectedIngredientName, lblIngredientCategory, lblAlcohol, lblPrice, lblIngredientBrand, lblPrivateAccountWarning, lblNoBeverages, lblNoIngredients;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        menuBtnAccount.setText(Current.environment.currentUser.getEmail());
        menuBtnBrand.setDisable(true);
        menuBtnCategory.setDisable(true);
        btnCloseList.setDisable(true);


        if(Current.environment.currentUser instanceof PrivateAccount){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Your Account does not have a collection of ingredients!");
            alert.setContentText("You can visualize the ingredients but you can not add them to a private list. Your" +
                    " account has a collection of beverages.");
            alert.showAndWait();
            lblPrivateAccountWarning.setText("Your account does not allow a collection of ingredients");
            btnAddIngredient.setDisable(true);
        }
        createCategoryList();
        populateNonAlcCategories();
        populateBrandsList();
        try {
            setAccountIngredients();
            setAccountBeverges();
            retrieveIngredientsFromDB();
            System.out.println("Ingredients list populated");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to populate the Ingredients list");
        }
    }

    @FXML
    private void selectBtnSearch() {
        txtFieldMaxAlc.clear();
        txtFieldMinAlc.clear();
        menuBtnAlcoholOption.setText("Alc Option");
        vBoxIngredients.getChildren().clear();
        String text = txtSearchOption.getText().toLowerCase();
        for (Ingredient x : ingredientsList) {
            if (text.length() != 0 && x.getName().toLowerCase().contains(text)) {
                Button button = new Button();
                button.setOnAction(this::selectVbButton);
                button.setMinWidth(400);
                button.setMinHeight(40);
                button.setText(x.getName());
                vBoxIngredients.getChildren().add(button);
            }
            if (x.getBrand().getProductType().getName().toLowerCase().contains(text)) {
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
        txtSearchOption.clear();
        btnSearch.setDisable(false);
    }
    @FXML
    private void selectAlcoholSelection(ActionEvent event) {
        menuBtnCategory.setDisable(false);
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

        menuBtnBrand.setDisable(false);

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
        txtFieldMaxAlc.clear();
        txtFieldMinAlc.clear();
        txtSearchOption.clear();
        menuBtnBrand.setText("Brand");
        menuBtnCategory.setText("Category");
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
            vBoxSimilarIngredient.getChildren().clear();
            for (Ingredient x : ingredientsList) {
                if (x.getName().equals(selection.getText())) {
                    createSimilarIngredientList(x);
                    lblSelectedIngredientName.setText(x.getName());
                    txtSimilarWith.setText(x.getName());
                    lblAlcohol.setText(Integer.toString(x.getAlcoholPercentage()));
                    progressBarAlcohol.setProgress(Double.valueOf(x.getAlcoholPercentage()) / 100);
                    lblPrice.setText(Integer.toString(x.getPricePerLiter()));
                    progressBarPrice.setProgress(Double.valueOf(x.getPricePerLiter()) / 1000);
                    lblIngredientBrand.setText(x.getBrand().getBrandName());
                    lblIngredientCategory.setText(x.getBrand().getProductType().getName());
                }
            }
            txtSearchOption.clear();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
    @FXML
    private void showIngredientsOrBeveragesList(ActionEvent e){
        MenuItem selectedList = (MenuItem) e.getSource();
        vBoxBevOrIng.getChildren().clear();
        btnCloseList.setDisable(false);
        if(selectedList.getText().equals("View your list of beverages")){
            Label beverage = new Label("Beverages  :");
            Label line = new Label("--------------");
            vBoxBevOrIng.getChildren().addAll(beverage,line);
            for(String beverages: accountBeverages) {
                Label label = new Label(beverages);
                label.setMinWidth(100);
                label.setMinHeight(20);
                vBoxBevOrIng.getChildren().add(label);
            }
        }else{
            Label ingred = new Label("Ingredients :");
            Label line = new Label("--------------");
            vBoxBevOrIng.getChildren().addAll(ingred,line);
            for(String ingredient: accountIngredients){
                Label label = new Label(ingredient);
                label.setMinWidth(100);
                label.setMinHeight(20);
                vBoxBevOrIng.getChildren().add(label);
            }
        }

    }
    @FXML
    private void selectCloseList(){
        vBoxBevOrIng.getChildren().clear();
        btnCloseList.setDisable(true);
    }
    @FXML
    private void searcByAlcohol(){
        txtSearchOption.clear();
        menuBtnBrand.setText("Brands");
        menuBtnCategory.setText("Category");
        vBoxIngredients.getChildren().clear();
        try{
            int min = Integer.parseInt(txtFieldMinAlc.getText());
            int max = Integer.parseInt(txtFieldMaxAlc.getText());
            for (Ingredient x : ingredientsList) {
                if (x.getAlcoholPercentage() < max && x.getAlcoholPercentage() > min) {
                    Button button = new Button();
                    button.setOnAction(this::selectVbButton);
                    button.setMinWidth(400);
                    button.setMinHeight(40);
                    button.setText(x.getName());
                    vBoxIngredients.getChildren().add(button);
                }
            }
        }catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please enter integers for alcohol options");
            alert.setHeaderText("Wrong input! ");
            alert.showAndWait();
        }finally {
            txtFieldMaxAlc.clear();
            txtFieldMinAlc.clear();
        }
    }

    private void createSimilarIngredientList(Ingredient selection){
        for (Ingredient x : ingredientsList) {
            if (selection.getBrand().getProductType().getName().equals(x.getBrand().getProductType().getName())&&! selection.getName().equals(x.getName())) {
                Button button = new Button();
                button.setOnAction(this::selectVbButton);
                button.setMinWidth(400);
                button.setMinHeight(40);
                button.setText(x.getName());
                vBoxSimilarIngredient.getChildren().add(button);
            }
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
        Collections.addAll(brandsList, BrandsEnum.values());
    }

    /**
     * DB connection
     */
    @FXML
    private void addIngredient() throws Exception {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        int ingredientID = 0;
        int accountID = 0/* Current.environment.currentUser.getId()*/;
        if(Current.environment.currentUser instanceof BusinessAccount) {
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
                pstmt.setInt(1, accountID);
                pstmt.setInt(2, ingredientID);
                int rowAffected = pstmt.executeUpdate();
                if (rowAffected == 1) {
                    alert.setHeaderText(lblSelectedIngredientName.getText() + " was added to your list!");
                    alert.setContentText("Ingredient " + lblSelectedIngredientName.getText() + " was added to your collection of ingredients.");
                    alert.showAndWait();
                }
            } catch (SQLException ex) {
                alert.setAlertType(Alert.AlertType.WARNING);
                alert.setHeaderText("You have already the ingredient " + lblSelectedIngredientName.getText() + " in your list!");
                alert.setContentText("Please chose another ingredient!");
                alert.showAndWait();
            } finally {
                ConnectionLayer.cleanUp(statement, resultSet);
            }
            connection.close();
        }else{
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setHeaderText("You don't have an ingredients list !");
            alert.setContentText("Your account does not have this option.");
            alert.showAndWait();
        }
        try{
        setAccountIngredients();
        setAccountBeverges();
        }catch(Exception e){
            e.printStackTrace();
        }
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

    private void setAccountBeverges()throws Exception{

        int beverages= 0;
        try {
            connection = ConnectionLayer.getConnection();
            statement = connection.createStatement();
            String query = String.format("select beverage.name from company_account_has_beverage, beverage, company_account where company_account_has_beverage.beverage_id = beverage.id\n" +
                    "and company_account_has_beverage.company_account_id = company_account.id and company_account.email = '%s';", Current.environment.currentUser.getEmail());
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                accountBeverages.add(resultSet.getString(1));
                beverages ++;
            }
        } catch (SQLException ex) {
            System.out.println("Exception: ");
            ex.printStackTrace();
        } finally {
            lblNoBeverages.setText(Integer.toString(beverages));
            ConnectionLayer.cleanUp(statement, resultSet);
        }
        connection.close();
    }

    private void setAccountIngredients()throws Exception{
        int ingredients = 0;
        try {
            connection = ConnectionLayer.getConnection();
            statement = connection.createStatement();
            String query = String.format("select ingredient.name from company_account_has_ingredient, ingredient, company_account where company_account_has_ingredient.ingredient_id = ingredient.id\n" +
                    "and company_account_has_ingredient.company_account_id = company_account.id and company_account.email = '%s';", Current.environment.currentUser.getEmail());
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                accountIngredients.add(resultSet.getString(1));
                ingredients ++;
            }
        } catch (SQLException ex) {
            System.out.println("Exception: ");
            ex.printStackTrace();
        } finally {
            lblNoIngredients.setText(Integer.toString(ingredients));
            ConnectionLayer.cleanUp(statement, resultSet);
        }
        connection.close();
    }
}