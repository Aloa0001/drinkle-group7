package com.espressoshock.drinkle.controllers.app.beverageBuilder;

import com.espressoshock.drinkle.controllers.print.Print;
import com.espressoshock.drinkle.databaseLayer.ConnectionLayer;
import com.espressoshock.drinkle.models.Beverage;
import com.espressoshock.drinkle.models.BrandsEnum;
import com.espressoshock.drinkle.models.Ingredient;
import com.espressoshock.drinkle.progressIndicator.RingProgressIndicator;
import com.espressoshock.drinkle.viewLoader.EventDispatcherAdapter;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.NodeOrientation;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;


public class BeverageBuilder extends EventDispatcherAdapter implements Initializable {
    //-----------------DB variables-----------------------------
    private Connection connection = null;
    private PreparedStatement prepStatement = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    //----------end of DB variables-----------------------------
    //-------------------------Fields---------------------------
    private Integer volumeSeparator = 0; // Value has been set to current slider value when ingredient is added
    private Double progressSeparator = 0.0; // Value has been set to current progress bar value when ingredient is added
    private double costSeparator = 0.0;  // Value has been set to current cost value when ingredient is added
    private String sliderDoubleValueAsString = null;
    private String cost = "0.0"; //initial cost label text
    private double sliderValueSet = 0.0;
    private Double volume = null;
    private Beverage bvg = null; // declared public static so that print can access it
    private int beverage_id = 0;
    private Glassware glass = null; // declared public static so that print can access it

    //------------------------------------------------------
    private ArrayList<Ingredient> choseIngredientsList2 = new ArrayList<>();
    private ArrayList<Ingredient> addedIngredientsList2 = new ArrayList<>();
    private ArrayList<Ingredient> searchList = new ArrayList<>();
    private ArrayList<Glassware> choseGlasswareList = new ArrayList<>();
    private Ingredient selected = null;// selected object of ingredient
    private RingProgressIndicator alcoholPercent = new RingProgressIndicator();// <-- to be accessed in print
    private ArrayList<String> brands = new ArrayList<>();
    //------------------ @FXML ------------------------------
    @FXML
    private Label lblChosenName, lblVolume, lblCost, lblTotalVolume, lblChosenGlass, lblChosenAlcohol;
    @FXML
    private AnchorPane alcoholPercentCircle;
    @FXML
    private VBox vBoxChosenIngredients, vBoxListOfIngredients;
    @FXML
    private Slider slider;
    @FXML
    private ProgressBar progressGlass;
    @FXML
    private Button btnAddIngredient, btnExport, btnSave;
    @FXML
    private TextField txtFieldBeverageName, searchField;
    @FXML
    private TextArea txtAreaNotes;
    @FXML
    private ImageView glassImage;
    @FXML
    private MenuButton brandsList;
    //---------------------- code -------------------------------


    private void addBrandsToSearch() {
        for (BrandsEnum a : BrandsEnum.values()) {
//            System.out.println(a.getBrandName()); // <--- test
            brands.add(a.getBrandName());

        }
        Collections.sort(brands);
        MenuItem m = new MenuItem("All");
        m.setOnAction(event -> brandsList.setText(m.getText()));
        brandsList.getItems().add(m);
        for (String s : brands) {
            MenuItem a = new MenuItem(s);
            a.setOnAction(event -> brandsList.setText(a.getText()));
            brandsList.getItems().add(a);
        }
    }

    private void search(String string) {
        searchList.clear();
        vBoxListOfIngredients.getChildren().removeAll();

        for (Ingredient i : choseIngredientsList2) {
            if (i.getName() != null && i.getName().toLowerCase().startsWith(string)) {
                searchList.add(i);
            }

            //something here
        }
        IngredientAddToList(searchList);
    }

    @FXML
    private void saveBeverageToDB() throws Exception {
        Beverage d = createBeverageObject();
        Thread.sleep(1000);
        createBeverage(d);
        Thread.sleep(1000);
        for (Ingredient a : d.getIngredients()) {
            createBeverageHasIngredients(a);
        }
    }

    private Beverage createBeverageObject() {
        Beverage a = new Beverage(txtFieldBeverageName.getText(), countPercentage(), Double.valueOf(cost), countVolume(), addedIngredientsList2);
        a.setNotes("Here you can read and add some notes" + "\n"
                + "-------------------------------------\n" + txtAreaNotes.getText() + "\n");
        bvg = a;
        return a;

    }

    @FXML
    private void createBeverage(Beverage beverage) throws SQLException {


        try {
            connection = ConnectionLayer.getConnection();
            statement = connection.createStatement();
            //resultSet = statement.executeQuery("SELECT ingredient.id,ingredient.name,ingredient.alcohol,ingredient.price_per_litre,ingredient.brand_id,brand.name " +
            //"FROM ingredient,brand WHERE ingredient.brand_id=brand.id");
            prepStatement = connection.prepareStatement("INSERT INTO beverage (name, alcohol, cost, notes) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            prepStatement.setString(1, beverage.getName());
            prepStatement.setInt(2, beverage.getAlcoholPercentage());
            prepStatement.setDouble(3, beverage.getCost());
            prepStatement.setString(4, "NOTES TEST");


            int rowAffected = prepStatement.executeUpdate();
            if (rowAffected == 1) {
                System.out.println("Beverage successfully added");
            }

            resultSet = prepStatement.getGeneratedKeys();
            if (resultSet.next()) {
                beverage_id = resultSet.getInt(1);
            }

        } catch (SQLException ex) {
            System.out.println("Exception: ");
            ex.printStackTrace();
        } finally {
            ConnectionLayer.cleanUp(statement, resultSet);
        }
        connection.close();
    }

    @FXML
    private void createBeverageHasIngredients(Ingredient ing) throws SQLException {
        try {
            connection = ConnectionLayer.getConnection();
            statement = connection.createStatement();
            //resultSet = statement.executeQuery("SELECT ingredient.id,ingredient.name,ingredient.alcohol,ingredient.price_per_litre,ingredient.brand_id,brand.name " +
            //"FROM ingredient,brand WHERE ingredient.brand_id=brand.id");
            prepStatement = connection.prepareStatement("INSERT INTO beverage_has_ingredient (beverage_id, ingredient_id, magnitude) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            prepStatement.setInt(1, beverage_id);
            prepStatement.setInt(2, ing.getId());
            prepStatement.setInt(3, ing.getMagnitude());


            int rowAffected = prepStatement.executeUpdate();
            if (rowAffected == 1) {
                System.out.println("Ingredient successfully added to beverage");
            }


        } catch (SQLException ex) {
            System.out.println("Exception: ");
            ex.printStackTrace();
        } finally {
            ConnectionLayer.cleanUp(statement, resultSet);
        }
        connection.close();
    }

    private void loadIngredientsFromDB() throws SQLException {
        int id_ing;
        String name;
        int alcohol;
        int price_per_litre;
        int brand_id;
        String brand;
        try {
            connection = ConnectionLayer.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT ingredient.id,ingredient.name,ingredient.alcohol,ingredient.price_per_litre,ingredient.brand_id,brand.name " +
                    "FROM ingredient,brand WHERE ingredient.brand_id=brand.id");


            while (resultSet.next()) {
                id_ing = resultSet.getInt(1);
                name = resultSet.getString(2);
                alcohol = resultSet.getInt(3);
                price_per_litre = resultSet.getInt(4);
                brand = resultSet.getString(6);

                BrandsEnum brandEnum = BrandsEnum.fromString(brand);

//            System.out.println(resultSet.getString("id") +
//                    resultSet.getString("name")+
//                    resultSet.getString("alcohol")+
//                    resultSet.getString("price_per_litre"));
                //System.out.printf("id: %d, name: %s, alcohol: %d, price: %d\n", id, name, alcohol, price_per_litre);
                Ingredient i = new Ingredient(name, alcohol, price_per_litre / 10, brandEnum, 0);
                i.setId(id_ing);
                //System.out.println(i);
                choseIngredientsList2.add(i);
            }
        } catch (SQLException ex) {
            System.out.println("Exception: ");
            ex.printStackTrace();
        } finally {
            ConnectionLayer.cleanUp(statement, resultSet);
        }
        connection.close();

    }

    private int countVolume() {
        int totalVolume = 0;
        for (Ingredient a : addedIngredientsList2) {
            totalVolume = totalVolume + a.getMagnitude();

        }
        return totalVolume;
    }

    private int countPercentage() {

        double preciseValueAlc;
        double preciseVolumeAlc = 0.0;
        int totalVolume = 0;

        int totalAlcohol;
        if (!addedIngredientsList2.isEmpty()) {
            for (Ingredient a : addedIngredientsList2) {
                totalVolume = totalVolume + a.getMagnitude();
                preciseVolumeAlc = preciseVolumeAlc + (a.getMagnitude() * a.getAlcoholPercentage() / 100.00);
            }
            preciseValueAlc = ((preciseVolumeAlc * 100) / totalVolume);
//            preciseValueAlc = Math.round(preciseValueAlc);
            if (preciseValueAlc - (int) preciseValueAlc > 0.5) {
                totalAlcohol = (int) preciseValueAlc + 1;
            } else
                totalAlcohol = (int) preciseValueAlc;

            return totalAlcohol;
        } else
            return 0;
    }

    @FXML
    private void openPrintView(Event e) throws Exception {
        createBeverageObject();
        Button b = (Button)e.getSource();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/app/print-layout.fxml"));
        Stage primaryStage = new Stage();
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.initOwner(b.getScene().getWindow()); //<-- Parent blocked until printView is closed
        Parent root = loader.load();
        //-------Passing data to another view-------------
        Print printController = loader.getController();
        printController.loadBeverage(bvg);
        printController.loadGlass(glass);
        printController.loadAlcoholRing(alcoholPercent);
        printController.loadIngredientList(addedIngredientsList2);
        //-------------------------------------------------
        Scene print = new Scene(root);
        primaryStage.setTitle("Drinkle-Print to PDF");
        primaryStage.setResizable(false);
        primaryStage.setScene(print);
        primaryStage.show();
    }

    private void findSelected() {
        Ingredient sel = null;
        for (Ingredient e : choseIngredientsList2) {
            if (e == selected) {
                sel = e;
            }

        }
        choseIngredientsList2.remove(sel);
    }

    private void sliderProgressChange() {
        if (selected == null) {
            slider.setDisable(true);

        } else {


            slider.valueProperty().addListener((arg0, arg1, arg2) -> {
                try {
                    slider.setValue(Math.round(arg2.doubleValue()));
                    sliderDoubleValueAsString = String.format("%.2f", slider.getValue() - sliderValueSet);
                    volume = Double.parseDouble(String.format("%.2f", slider.getValue()));
                    if (slider.getValue() >= slider.getMin()) {
                        if (selected != null) {
                            cost = String.format("%.2f", ((Double.parseDouble(sliderDoubleValueAsString) - slider.getMin())
                                    * selected.getPricePerLiter() / 1000.00) + costSeparator);
                        } else {
                            cost = "0.0";
                        }

                        progressGlass.setProgress(Double.parseDouble(String.format("%.2f", slider.getValue() / (glass.getVolume() / 100.00) / 100.00)));
                    } else {
                        progressGlass.setProgress(0.0);
                    }
                    lblVolume.setText(String.valueOf(volume.intValue()));
                    lblCost.setText(cost);
                    if (slider.getValue() == slider.getMin()) {
                        btnAddIngredient.setDisable(true);
                    } else {
                        btnAddIngredient.setDisable(false);
                    }
                } catch (NumberFormatException ex) {
                    //throws exception only on MacOS.
                    ex.printStackTrace();
                }
            });

        }
    }

    private void disableAdd() {
        slider.valueProperty().addListener((arg0, arg1, arg2) -> {
            if (slider.getValue() == slider.getMin()) {
                btnAddIngredient.setDisable(true);
            }
        });
        if (selected == null) {
            btnAddIngredient.setDisable(true);
        }
    }

    private void choseIngredient(Event event) {
        if (slider.getValue() > slider.getMin()) {
            slider.setValue(slider.getMin());
        }
        slider.setDisable(false);
        Label lbl = (Label) event.getSource();
        selected = (Ingredient) lbl.getUserData();
        lblChosenName.setText(lbl.getText());
        lblChosenAlcohol.setText(selected.getAlcoholPercentage() + "%");
        sliderProgressChange();
        disableAdd();
    }

    private void choseGlassware(Event event) {

        Label lbl = (Label) event.getSource();
        Glassware gls = (Glassware) lbl.getUserData();
        if (gls.getVolume() > slider.getMin()) {
            glass = (Glassware) lbl.getUserData();
            lblChosenGlass.setText(glass.getName());
            lblTotalVolume.setText(String.valueOf(glass.getVolume()));

            slider.setMax(glass.getVolume());


            Image image = new Image(glass.getImageUrl());
            glassImage.setImage(image);
            IngredientAddToList(choseIngredientsList2);
            searchField.setDisable(false);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Error");
            alert.setContentText("Glass that you are trying to chose has a smaller volume than added ingredients magnitude \n" +
                    "Please remove some ingredients!");
            alert.showAndWait();
        }
    }

    //------------Creating labels to be represented in the ingredient list------
    private void choseIngredientListElement(Ingredient object) {

        Label choseIngName = new Label();
        Label choseIngPrice = new Label();
        choseIngPrice.setTextAlignment(TextAlignment.JUSTIFY);
        choseIngPrice.setContentDisplay(ContentDisplay.RIGHT);
        choseIngPrice.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        choseIngPrice.setMnemonicParsing(true);
        Tooltip percentage = new Tooltip("Alcohol: " + object.getAlcoholPercentage() + "%");
        choseIngName.setText(object.getName());
        choseIngName.setOnMouseClicked((EventHandler<Event>) this::choseIngredient);
        choseIngName.setCursor(Cursor.HAND);
        choseIngName.setTooltip(percentage);
        choseIngPrice.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        choseIngPrice.setText(object.getPricePerLiter() + " \u20ac" + "/l");//<--- " \u20ac" is EUR symbol
        choseIngPrice.setLayoutX(205.0);

        Group choseIngredient = new Group();
        choseIngredient.getChildren().add(choseIngName);
        choseIngredient.getChildren().add(choseIngPrice);
        choseIngName.setUserData(object);
        vBoxListOfIngredients.getChildren().add(choseIngredient);
    }

    private void choseGlasswareListElement(Glassware object) {

        Label choseGlasswareName = new Label();
        Label choseGlasswareVolume = new Label();
        choseGlasswareVolume.setTextAlignment(TextAlignment.RIGHT);
        choseGlasswareName.setText(object.getName());
        choseGlasswareName.setOnMouseClicked((EventHandler<Event>) this::choseGlassware);
        choseGlasswareName.setCursor(Cursor.HAND);
        choseGlasswareVolume.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        choseGlasswareVolume.setText(String.valueOf(object.getVolume()));
        choseGlasswareVolume.setLayoutX(205.0);

        Image image = new Image(object.getImageUrl());
        ImageView imageView = new ImageView(image);
        Tooltip tooltip = new Tooltip();
        tooltip.setGraphic(imageView);
        tooltip.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7);");

        choseGlasswareName.setTooltip(tooltip);
        choseGlasswareName.getTooltip().setOnShowing(s -> {

            //Get button current bounds on computer screen
            Bounds bounds = choseGlasswareName.localToScreen(choseGlasswareName.getBoundsInLocal());
            choseGlasswareName.getTooltip().setX(bounds.getMaxX() + 100);
            choseGlasswareName.getTooltip().setY(bounds.getMinY() + -40);

        });

        Group choseIngredient = new Group();
        choseIngredient.getChildren().add(choseGlasswareName);
        choseIngredient.getChildren().add(choseGlasswareVolume);
        choseGlasswareName.setUserData(object);
        vBoxListOfIngredients.getChildren().add(choseIngredient);
    }

    //    private void choseGarnishListElement(Garnish object) {
//
//        Label choseGarnishName = new Label();
//        choseGarnishName.setText(object.getName());
////        choseGarnishName.setOnMouseClicked((Event event) -> {
////
////        });
////        choseGarnishName.setCursor(Cursor.HAND);
////        Group choseIngredient = new Group();
////        choseIngredient.getChildren().add(choseGarnishName);
////        choseGarnishName.setUserData(object);
////        vBoxListOfIngredients.getChildren().add(choseIngredient);
//    }
//
//    private void choseIceTypeListElement(IceType object) {
//        Label choseIceTypeName = new Label();
//        choseIceTypeName.setText(object.getName());
////        choseIceTypeName.setOnMouseClicked((Event event) -> {
////
////        });
////        choseIceTypeName.setCursor(Cursor.HAND);
////        Group choseIngredient = new Group();
////        choseIngredient.getChildren().add(choseIceTypeName);
////        choseIceTypeName.setUserData(object);
////        vBoxListOfIngredients.getChildren().add(choseIngredient);
//    }
    //------------Adding from array list to vBox (display)------

    //    private void choseGarnishListElement(Garnish object) {
//
//        Label choseGarnishName = new Label();
//        choseGarnishName.setText(object.getName());
////        choseGarnishName.setOnMouseClicked((Event event) -> {
////
////        });
////        choseGarnishName.setCursor(Cursor.HAND);
////        Group choseIngredient = new Group();
////        choseIngredient.getChildren().add(choseGarnishName);
////        choseGarnishName.setUserData(object);
////        vBoxListOfIngredients.getChildren().add(choseIngredient);
//    }
//
//    private void choseIceTypeListElement(IceType object) {
//        Label choseIceTypeName = new Label();
//        choseIceTypeName.setText(object.getName());
////        choseIceTypeName.setOnMouseClicked((Event event) -> {
////
////        });
////        choseIceTypeName.setCursor(Cursor.HAND);
////        Group choseIngredient = new Group();
////        choseIngredient.getChildren().add(choseIceTypeName);
////        choseIceTypeName.setUserData(object);
////        vBoxListOfIngredients.getChildren().add(choseIngredient);
//    }
    //------------Adding from array list to vBox (display)------
    @FXML
    private void IngredientAddToList(ArrayList<Ingredient> list) {
        vBoxListOfIngredients.getChildren().clear();
        list.sort(Ingredient.IngredientNameComparator);
        for (Ingredient a : list) {
            choseIngredientListElement(a);
        }
    }

    @FXML
    private void changeGlassware() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Changing the glass will reset current progress and " +
                "you will have to start over.");
        alert.setContentText("Are you sure you want to proceed?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            selected = null;
            glass = null;
            alcoholPercent.setProgress(0);
            vBoxChosenIngredients.getChildren().clear();
            addedIngredientsList2.clear();
            slider.setMin(0.0);
            lblCost.setText("0.0");
            lblVolume.setText("0");
            progressGlass.setProgress(0.0);
            progressSeparator = 0.0;
            volumeSeparator = 0;
            costSeparator = 0;
            lblChosenGlass.setText("");
            lblTotalVolume.setText("");
            glassImage.setImage(null);
            GlasswareAddToList();
        } else {
            alert.close();
        }
    }

    @FXML
    private void GlasswareAddToList() {

        searchField.setDisable(true);
        vBoxListOfIngredients.getChildren().clear();
        selected = null;
        for (Glassware a : choseGlasswareList) {
            choseGlasswareListElement(a);
        }
    }

//    private void dummyGarnishAddToList() {
//        vBoxListOfIngredients.getChildren().clear();
//        for (Garnish a : choseGarnishList) {
//            choseGarnishListElement(a);
//        }
//    }
//
//    private void dummyIceTypeAddToList() {
//        vBoxListOfIngredients.getChildren().clear();
//        for (IceType a : choseIceTypeList) {
//            choseIceTypeListElement(a);
//        }
//    }

    //-----------Creating objects-------------------------------


    private void GlasswareCreate() {//for Test Purpose Only!!!!
        // To be replaced with observable list of Glassware objects

        Glassware shot = new Glassware("Shot", 57, "images/app/beverage-builder/glasses/shot-34.png");
        Glassware highball = new Glassware("Highball", 350, "images/app/beverage-builder/glasses/highball-350.png");
        Glassware margarita = new Glassware("Margarita", 340, "images/app/beverage-builder/glasses/margarita-340.png");
        Glassware martini = new Glassware("Martini", 260, "images/app/beverage-builder/glasses/martini-260.png");
        Glassware brandy = new Glassware("Brandy", 395, "images/app/beverage-builder/glasses/brandy-395.png");
        Glassware cordial = new Glassware("Cordial", 115, "images/app/beverage-builder/glasses/cordial-115.png");
        Glassware hurricane = new Glassware("Hurricane", 440, "images/app/beverage-builder/glasses/hurricane-440.png");
        Glassware oldFashioned = new Glassware("Old fashioned", 200, "images/app/beverage-builder/glasses/old-fashioned-200.png");
        Glassware redWine = new Glassware("Red wine", 400, "images/app/beverage-builder/glasses/red-wine-400.png");
        Glassware whiteWine = new Glassware("White wine", 360, "images/app/beverage-builder/glasses/white-wine-360.png");


        choseGlasswareList.add(shot);
        choseGlasswareList.add(highball);
        choseGlasswareList.add(margarita);
        choseGlasswareList.add(martini);
        choseGlasswareList.add(brandy);
        choseGlasswareList.add(cordial);
        choseGlasswareList.add(hurricane);
        choseGlasswareList.add(oldFashioned);
        choseGlasswareList.add(redWine);
        choseGlasswareList.add(whiteWine);

    }

//    private void addIngredientTOLIST() {
//        for (Ing a : addedIngredientsList) {
//
//        }
//    }

    @FXML
    private void addIngredientWidget() {
        if (selected != null) {
            costSeparator = Double.valueOf(cost);
            // Test purposes only
            btnAddIngredient.setDisable(true);
            //-------------------Setting new min values----------------
//            double diff = slider.getValue() - slider.getMin(); // slider value difference Current value - min value
            int setVolume = volume.intValue() - volumeSeparator; // available volume after adding previous ingredient
            double setProgress = progressGlass.getProgress() - progressSeparator; // Progress bar in a glass/available volume
//            double setCost = Double.valueOf(cost) - costSeparator;
            //-------------------Creating components----------------
            Label ingredientName = new Label();
            Label ingredientVolume = new Label();
            ProgressBar addedIngredientPercentBar = new ProgressBar();
            Button overlay = new Button();
            ContextMenu removeMenu = new ContextMenu();
            MenuItem removeItem = new MenuItem("Remove Ingredient");
            //----------Adding functionality to components-----------
            overlay.setText("");
            //overlay.setStyle("-fx-background-color: transparent");
            overlay.setLayoutX(12.0);
            overlay.setPrefHeight(36.0);
            overlay.setPrefWidth(210.0);
            removeMenu.getItems().add(removeItem);
            //-------------------------------
            ingredientName.setText(selected.getName());
            ingredientName.setLayoutX(16.0);
            ingredientVolume.setText(setVolume + "ml");
            ingredientVolume.setLayoutX(195.0);
            addedIngredientPercentBar.setLayoutX(14.0);
            addedIngredientPercentBar.setLayoutY(29.0);
            addedIngredientPercentBar.setProgress(setProgress);
            addedIngredientPercentBar.setPrefWidth(204);
            addedIngredientPercentBar.setPrefHeight(8);
            //--------------------------------
            Group ingredient = new Group();
            ingredient.getChildren().addAll(ingredientName, ingredientVolume, addedIngredientPercentBar, overlay);
            //addedIng added = new addedIng(lblChosenName.getText(), setVolume, setProgress);
            selected.setMagnitude(setVolume);
            addedIngredientsList2.add(selected);
            btnExport.setDisable(disableExport());
            overlay.setContextMenu(removeMenu);
            overlay.setUserData(selected);
            overlay.setOnMouseClicked(event -> {
                selected = (Ingredient) overlay.getUserData();
                lblChosenName.setText(selected.getName());
                lblChosenAlcohol.setText((selected.getAlcoholPercentage() + "%"));
            });

            slider.setDisable(true);
            removeItem.setOnAction(event -> {// removig a widget

                vBoxChosenIngredients.getChildren().remove(ingredient);
                addedIngredientsList2.remove(selected);
                slider.setMin(countVolume());// adding back to volume removed ingredient value
                slider.setValue(slider.getMin() - setProgress);// adding back to slider removed ingredient value
                volumeSeparator = volumeSeparator - selected.getMagnitude(); // adding to volume and progress separator
                double a = selected.getMagnitude();
                double b = glass.getVolume().doubleValue();
                //System.out.println(selected.getMagnitude()+"/"+glass.getVolume()+"="+a/b);
                progressSeparator -= a / b;
                costSeparator = costSeparator - (selected.getPricePerLiter() * selected.getMagnitude() / 1000.00);
                choseIngredientsList2.add(selected);
                alcoholPercent.setProgress(countPercentage());
                IngredientAddToList(choseIngredientsList2);
                btnExport.setDisable(disableExport());
                if (addedIngredientsList2.isEmpty()) {
                    slider.setMin(0);
                    volumeSeparator = 0;
                    progressSeparator = 0.0;
                    costSeparator = 0;
                }
                selected = null;
                lblChosenName.setText("None");
                lblChosenAlcohol.setText("0%");
                lblCost.setText(String.format("%.2f", costSeparator));


            });
            vBoxChosenIngredients.getChildren().add(ingredient);
            volumeSeparator = volume.intValue();
            progressSeparator = progressGlass.getProgress();
            slider.setMin(countVolume());
            alcoholPercent.setProgress(countPercentage());
            findSelected();
            IngredientAddToList(choseIngredientsList2);
            selected = null;

            lblChosenName.setText("None");
            lblChosenAlcohol.setText("0%");
            searchField.setText("");
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please chose an ingredient");
            alert.showAndWait();
        }

    }

    private static void limitText(TextInputControl textInput, int maxLength) {
        textInput.textProperty().addListener((ov, oldValue, newValue) -> {
            if (textInput.getText().length() > maxLength) {
                String s = textInput.getText().substring(0, maxLength);
                textInput.setText(s);
            }
        });
    }

    @FXML
    private void checkSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> search(newValue));
    }

    @FXML
    private void checkText() {
        txtFieldBeverageName.textProperty().addListener((observable, oldValue, newValue) -> {
            btnExport.setDisable(disableExport());
            btnSave.setDisable(disableExport());
        });
    }

    private boolean disableExport() {
        return addedIngredientsList2.isEmpty() || txtFieldBeverageName.getText() == null || txtFieldBeverageName.getText().trim().isEmpty();
    }

    private void toLowerCase() {
        searchField.textProperty().addListener((ov, oldValue, newValue) -> searchField.setText(newValue.toLowerCase()));
    }
    @FXML
    private void helpDialog(){

        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/app/beverage-builder/user-manual.fxml"));
        final Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Scene manual = new Scene(root);
        stage.setTitle("Drinkle-User Manual");
        stage.setResizable(false);
        stage.setScene(manual);
        stage.setAlwaysOnTop(true);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        brandsList.setDisable(true);
        searchField.setDisable(true);
        toLowerCase();
        checkSearch();
        checkText();
        addBrandsToSearch();
        int totalAlcoholPercentage = 0;
        alcoholPercent.setProgress(totalAlcoholPercentage);  //for visual presentation only
        alcoholPercentCircle.getChildren().add(alcoholPercent);
        GlasswareCreate();
        sliderProgressChange();
        slider.setBlockIncrement(1);
        GlasswareAddToList();
        btnExport.setDisable(disableExport());
        btnSave.setDisable(disableExport());
        limitText(txtFieldBeverageName, 45);// <--- limit name to varchar(45)
        limitText(txtAreaNotes, 400);// <--- limit notes to 400 characters
        disableAdd();
        try {
            loadIngredientsFromDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        helpDialog();
    }


}