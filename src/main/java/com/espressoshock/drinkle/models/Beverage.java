package com.espressoshock.drinkle.models;

import java.util.ArrayList;

public class Beverage {

  private String name;
  private int alcoholPercentage;
  private double cost;
  private int volumePerMililiter;
  private String notes;

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  private ArrayList<Ingredient> ingredients;

  public Beverage(String name, int alcoholPercentage, double cost, int volumePerMililiter,
                  ArrayList<Ingredient> ingredients) {
    this.name = name;
    this.alcoholPercentage = alcoholPercentage;
    this.cost = cost;
    this.volumePerMililiter = volumePerMililiter;
    this.ingredients = ingredients;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAlcoholPercentage() {
    return alcoholPercentage;
  }

  public void setAlcoholPercentage(int alcoholPercentage) {
    this.alcoholPercentage = alcoholPercentage;
  }

  public double getCost() {
    return cost;
  }

  public void setCost(double cost) {
    this.cost = cost;
  }

  public int getVolumePerMililiter() {
    return volumePerMililiter;
  }

  public void setVolumePerMililiter(int volumePerMililiter) {
    this.volumePerMililiter = volumePerMililiter;
  }

  public ArrayList<Ingredient> getIngredients() {
    return ingredients;
  }

  public void setIngredients(ArrayList<Ingredient> ingredients) {
    this.ingredients = ingredients;
  }
}