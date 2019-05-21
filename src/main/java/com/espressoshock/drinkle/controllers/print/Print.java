package com.espressoshock.drinkle.controllers.print;


import com.espressoshock.drinkle.controllers.app.beverageBuilder.Glassware;
import com.espressoshock.drinkle.models.Beverage;
import com.espressoshock.drinkle.models.Ingredient;
import com.espressoshock.drinkle.progressIndicator.RingProgressIndicator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.effect.Bloom;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Print implements Initializable {
    @FXML
    AnchorPane printView, alcPercentageCircle;
    @FXML
    VBox ingredientsList;
    @FXML
    TextArea notesTextArea;
    @FXML
    Label costLabel, bvgName, lblGlassName, lblGlassVolume, lblBvgMagnitude;
    @FXML
    ImageView glassImagePrint;
    private Beverage printBeverage;
    private Glassware printGlass;
    private RingProgressIndicator aclPercent = new RingProgressIndicator();
    private ArrayList<Ingredient> ingredients = new ArrayList<>();

    public void loadBeverage(Beverage beverage) {
        printBeverage = beverage;
        notesTextArea.setText(printBeverage.getNotes());
        costLabel.setText(printBeverage.getCost() + "\u20ac"); //<--- "\u20ac" is EUR symbol
        bvgName.setText(printBeverage.getName());
    }

    public void loadGlass(Glassware glass) {
        printGlass = glass;
        Image img = new Image(printGlass.getImageUrl());
        Bloom bloom = new Bloom();
        bloom.setThreshold(0.2);
        glassImagePrint.setImage(img);
        glassImagePrint.setEffect(bloom);
        lblGlassName.setText(printGlass.getName());
        lblGlassVolume.setText(printGlass.getVolume() + " ml");
    }

    public void loadAlcoholRing(RingProgressIndicator alcoholRing) {
        aclPercent.setProgress(alcoholRing.getProgress());
        aclPercent.setLayoutX(40);
        aclPercent.setLayoutY(45);
        alcPercentageCircle.getChildren().add(aclPercent);
    }

    public void loadIngredientList(ArrayList<Ingredient> list) {
        ingredients = list;
        lblBvgMagnitude.setText(countMagnitude() + "ml");
        populateVbox();
    }

    private void addIngredientWidget2(Ingredient selected) {
        Label ingredientName = new Label();
        Label ingredientVolume = new Label();
        ProgressBar addedIngredientPercentBar = new ProgressBar();

        ingredientName.setLayoutX(16.0);
        ingredientVolume.setLayoutX(154.0);
        ingredientName.setText(selected.getName());
        ingredientVolume.setText(selected.getMagnitude() + "ml");
        addedIngredientPercentBar.setLayoutX(14.0);
        addedIngredientPercentBar.setLayoutY(29.0);
        addedIngredientPercentBar.setPrefWidth(163);
        addedIngredientPercentBar.setPrefHeight(8);
        addedIngredientPercentBar.setProgress((selected.getMagnitude() / (printGlass.getVolume() / 100.00)) / 100.00);//<-- 1.2 is glass volume/100
        System.out.println((selected.getMagnitude() / (printGlass.getVolume() / 100.00)) / 100.00);
        Group ingredient = new Group();
        ingredient.getChildren().addAll(ingredientName, ingredientVolume, addedIngredientPercentBar);
        ingredientsList.getChildren().add(ingredient);
    }

    private int countMagnitude() {
        int magnitude = 0;
        for (Ingredient i : ingredients) {
            magnitude += i.getMagnitude();
        }
        return magnitude;
    }

    @FXML
    private void populateVbox() {
        for (Ingredient a : ingredients) {
            addIngredientWidget2(a);
        }
    }

    public void buttonPrint(ActionEvent event) {
        Button btn = (Button) event.getSource();
        Stage stage = (Stage) btn.getScene().getWindow();
        printDialog(printView, stage);

    }

    private void printDialog(Node subject, Stage window) {
        PrinterJob newPrint = PrinterJob.createPrinterJob();
        if (newPrint == null) {
            return;
        }

        boolean isAvailable = newPrint.showPrintDialog(window);

        if (isAvailable) {
            print(newPrint, subject);
        }
    }

    private void print(PrinterJob job, Node subject) {
        System.out.println(job.jobStatusProperty().asString());

        boolean jobDone = job.printPage(subject);

        if (jobDone) {
            job.endJob();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {



    }
}


