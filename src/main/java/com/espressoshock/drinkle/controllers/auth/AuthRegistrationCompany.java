package com.espressoshock.drinkle.controllers.auth;

import com.espressoshock.drinkle.controllers.auth.AuthService.AccountType;
import com.espressoshock.drinkle.viewLoader.EventDispatcherAdapter;
import com.espressoshock.drinkle.viewLoader.ViewLoader;
import com.espressoshock.drinkle.viewLoader.ViewMetadata;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class AuthRegistrationCompany extends EventDispatcherAdapter {

  @FXML
  TextField nameTextField, emailTextField, passwordTextField;

  @FXML
  Text errorText;

  @FXML
  public void initialize() {
    setupComponents();
    setupUI();
  }


  private void setupUI() {
    errorText.setVisible(false);
  }

  private void setupComponents() {}


  @FXML
  public void onSwitchAccountRegistrationTap(MouseEvent event) {
    super.dispatchViewChangeRequest(ViewMetadata.AUTH_REGISTRATION);
  }
  @FXML
  public void openLoginView(MouseEvent event) {
    super.dispatchViewChangeRequest(ViewMetadata.AUTH_LOGIN);
  }
  @FXML
  public void register(MouseEvent event){

    if ((nameTextField.getText().isEmpty()) ||
        (emailTextField.getText().isEmpty()) ||
        (passwordTextField.getText().isEmpty())) {
      errorText.setVisible(true);
      return;
    }

    AuthService registrationService = new AuthService();

    if (
        registrationService.registerAccount(
            emailTextField.getText(),
            passwordTextField.getText(),
            nameTextField.getText(),
            AccountType.Company
        )
    ) {
      errorText.setVisible(false);
      super.dispatchViewChangeRequest(ViewLoader.default_view);
    } else {
      errorText.setVisible(true);
      System.out.println("Something went wrong, try again");
    }
  }
}
