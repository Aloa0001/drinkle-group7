package com.espressoshock.drinkle.controllers.app;

import com.espressoshock.drinkle.progressIndicator.RingProgressIndicator;
import com.espressoshock.drinkle.viewLoader.EventDispatcherAdapter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;


public class BeverageBuilder extends EventDispatcherAdapter implements Initializable {
    //-------------------------Test variables only!!!----------------

    Integer volumeSeparator = 0; // Value has been set to current slider value when ingredient is added
    Double progressSeparator = 0.0; // Value has been set to current progress bar value when ingredient is added
    String sliderDoubleValueAsString = null;

    String cost = "0.0"; //initial cost label text
    // double costIncrease = 0.0; <-- not sure what i used this for
    double slidervalueset = 0.0;
    Double volume = null;
    int index = 0;
    Random rand = new Random();
    DecimalFormat df = new DecimalFormat("#.##");
    //------------------------End of Test variables------------------
    /* -> to be implemented when ingridient list is ready
    ArrayList<Ingredient> choseIngredientsList = new ArrayList<>();
    ArrayList<Ingredient> addedIngredients = new ArrayList<>();
    */
    ArrayList<Ing> choseIngredientsList = new ArrayList<>();
    ObservableList<Ing> observableListChoseIngredient = FXCollections.observableArrayList(choseIngredientsList);
    Ing selected = null;// selected object of ingredient
    RingProgressIndicator alcoholPercent = new RingProgressIndicator();
    RingProgressIndicator pourCostPercent = new RingProgressIndicator();
    @FXML
    Label lblChosenName, lblVolume, lblCost, lblTotalVolume;
    @FXML
    AnchorPane alcoholPercentCircle, pourCostCircle;
    @FXML
    VBox vBoxChosenIngredients, vBoxListOfIngredients;
    @FXML
    Slider slider;
    @FXML
    ProgressBar progressGlass;


    private void sliderProgressChange() {
        slider.valueProperty().addListener((arg0, arg1, arg2) -> {
            try {
                sliderDoubleValueAsString = String.format("%.2f", slider.getValue() - slidervalueset);
                volume = Double.parseDouble(String.format("%.2f", slider.getValue())) * 100;
                cost = String.format("%.2f", Double.parseDouble(sliderDoubleValueAsString) * selected.getPrice() / 10);
                progressGlass.setProgress(Double.parseDouble(String.format("%.2f", slider.getValue())));
                lblVolume.setText(String.valueOf(volume.intValue()));
                lblCost.setText(cost);
            } catch (NumberFormatException ex) {
                //throws exception only on MacOS.
                System.out.println(ex);
            }
        });


    }

    public void choseIngredient(Event event) {
        Label lbl = (Label) event.getSource();
        selected = (Ing) lbl.getUserData();
        lblChosenName.setText(lbl.getText());
    }

    public void choseIngredientListElement(Ing object) {
        Ing obj = object;

        Label choseIngredientName = new Label();
        Label choseIngredientPrice = new Label();
        choseIngredientName.setText(obj.getName());
        choseIngredientName.setOnMouseClicked((Event event) -> {
            choseIngredient(event);
        });
        choseIngredientName.setCursor(Cursor.HAND);
        choseIngredientPrice.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        choseIngredientPrice.setText(String.format("%.2f", obj.getPrice()) + " \u20ac" + "/l");
        choseIngredientPrice.setLayoutX(205.0);

        Group choseIngredient = new Group();
        choseIngredient.getChildren().add(choseIngredientName);
        choseIngredient.getChildren().add(choseIngredientPrice);
        choseIngredientName.setUserData(obj);
        vBoxListOfIngredients.getChildren().add(choseIngredient);
    }

    public void dummyIngredientAddToList() {
        dummyIngredientCreate();
        for (Ing a : choseIngredientsList) {
            choseIngredientListElement(a);
        }
    }

    public void dummyIngredientCreate() {//for Test Purpose Only!!!!
        // To be replaced with observable list of ingredient objects

        String[] names = new String[]{"Juice", "Vodka", "Whiskey", "Gin", "Beer", "Brandy", "Some other ingredient", "Water"};
        for (int i = 0; i < 60; i++) {
            DecimalFormat df2 = new DecimalFormat("#.##");
            double rangeMin = 10.0;
            double rangeMax = 100.0;
            double randomValue = rangeMin + (rangeMax - rangeMin) * rand.nextDouble();
            Ing ingred = new Ing(names[rand.nextInt(8)], rand.nextInt(100), Double.valueOf(df2.format(randomValue)));
            choseIngredientsList.add(ingred);
        }
    }


    public void addIngredientWidget() {
        // Test purposes only
        //-------------------Setting new min values----------------
        double diff = slider.getValue() - slider.getMin(); // slider value difference Current value - min value
        Integer setVolume = volume.intValue() - volumeSeparator; // available volume after adding previous ingredient
        Double setProgress = progressGlass.getProgress() - progressSeparator; // Progress bar in a glass/available volume
        //-------------------Creating components----------------
        Label ingredientName = new Label();
        Label ingredientVolume = new Label();
        ProgressBar addedIngredientPercentBar = new ProgressBar();
        Button overlay = new Button();
        ContextMenu removeMenu = new ContextMenu();
        MenuItem removeItem = new MenuItem("Remove Ingredient");
        //----------Adding functionality to components-----------
        overlay.setText("");
        overlay.setStyle("-fx-background-color: transparent");
        overlay.setLayoutX(14.0);
        overlay.setPrefHeight(36.0);
        overlay.setPrefWidth(204.0);
        removeMenu.getItems().add(removeItem);
        //-------------------------------
        ingredientName.setText(lblChosenName.getText());
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
        addedIng added = new addedIng(lblChosenName.getText(), setVolume, setProgress);
        overlay.setContextMenu(removeMenu);
        removeItem.setOnAction(new EventHandler<ActionEvent>() {// removig a widget
            @Override
            public void handle(ActionEvent event) {

                slider.setMin(diff);// adding back to volume removed ingredient value
                slider.setValue(slider.getMin() - setProgress);// adding back to slider removed ingredient value
                volumeSeparator = volumeSeparator - added.getVolume(); // adding to volume and progress separator
                progressSeparator = progressSeparator - added.getProgressBar();
                vBoxChosenIngredients.getChildren().remove(ingredient);
            }
        });
        vBoxChosenIngredients.getChildren().add(ingredient);
        volumeSeparator = volume.intValue();
        progressSeparator = progressGlass.getProgress();
        slider.setMin(slider.getValue());
        //cost = String.format("%.2f", Double.parseDouble(cost)+selected.getPrice()/10);
        index = +1;


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        alcoholPercent.setProgress(17);  //for visual presentation only
        pourCostPercent.setProgress(36);
        alcoholPercentCircle.getChildren().add(alcoholPercent);
        pourCostCircle.getChildren().add(pourCostPercent);
        dummyIngredientAddToList();// create generate and add to list mock ingredients
        sliderProgressChange();
        lblTotalVolume.setText("100");
    }
}

////////////////////simplified classes to test view -> to be deleted when Ingredients are available from database
class Ing {
    private String name;
    private Integer alcoholPercentage;
    private Double price;

    public Ing(String name, Integer alcoholPercentage, Double price) {
        this.name = name;
        this.alcoholPercentage = alcoholPercentage;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Integer getAlcoholPercentage() {
        return alcoholPercentage;
    }

    public Double getPrice() {
        return price;
    }
}

class addedIng {
    private String name;
    private Integer Volume;
    private Double progressBar;

    public addedIng(String name, Integer volume, Double progressBar) {
        this.name = name;
        Volume = volume;
        this.progressBar = progressBar;
    }

    public String getName() {
        return name;
    }

    public Integer getVolume() {
        return Volume;
    }

    public Double getProgressBar() {
        return progressBar;
    }
}
