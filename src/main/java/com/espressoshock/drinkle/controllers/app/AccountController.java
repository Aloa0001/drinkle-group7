package com.espressoshock.drinkle.controllers.app;

import com.espressoshock.drinkle.appState.Current;
import com.espressoshock.drinkle.models.AccountType;
import com.espressoshock.drinkle.viewLoader.EventDispatcherAdapter;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javax.swing.text.html.ImageView;


public class AccountController extends EventDispatcherAdapter {

  @FXML
  TextField emailTextField, passwordTextField, nameTextField;

  @FXML
  ImageView logoImageView;

  @FXML
  public void initialize() {
    setupComponents();
    System.out.println("Account view loaded");
  }
  

  private void setupComponents() {
    AccountType type = Current.environment.userType();

    if (type == AccountType.Private) {
      // set image for private
    } else if (type == AccountType.Business){
      // set image for business
    }
  }





}
