package com.espressoshock.drinkle.controllers.app;

import com.espressoshock.drinkle.appState.Current;
import com.espressoshock.drinkle.databaseLayer.ConnectionLayer;
import com.espressoshock.drinkle.models.Account;
import com.espressoshock.drinkle.models.AccountType;
import com.espressoshock.drinkle.viewLoader.EventDispatcherAdapter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class AccountController extends EventDispatcherAdapter {

  @FXML
  TextField emailTextField, passwordTextField, nameTextField;

  @FXML
  Label accountTypeLabel, updateResultLabel;


//  @FXML
//  ImageView logoImageView;


  @FXML
  public void initialize() {
    setupComponents();
    setupUI();
    System.out.println("Account view loaded");
  }

  private void setupUI() {
    updateResultLabel.setVisible(false);
  }

  private void setupComponents() {
    if (accountType == AccountType.Private) {
      accountTypeLabel.setText("logged as: Private");
    } else if (accountType == AccountType.Business){
      accountTypeLabel.setText("logged as: Business entity");
    }

    emailTextField.setText(currentAccount.getEmail());
    passwordTextField.setText(currentAccount.getPassword());
  }

  private Connection connection = null;
  private Statement statement = null;
  private ResultSet resultSet = null;

  private AccountType accountType = Current.environment.userType();
  private Account currentAccount = Current.environment.currentUser;


  public void onUpdateCalled() {
    if ((nameTextField.getText().isEmpty()) ||
        (emailTextField.getText().isEmpty()) ||
        (passwordTextField.getText().isEmpty()))
    {
      updateResultLabel.setText("There is something wrong with your input");
      updateResultLabel.setVisible(true);
      return;
    }

    if (updateAccount(emailTextField.getText(),
        passwordTextField.getText(),
        nameTextField.getText())) {

      updateResultLabel.setText("Done!");
      updateResultLabel.setVisible(true);
    }
  }



  private boolean updateAccount(String email, String password, String name) {

    int accID = currentAccount.getId();

    try {
      connection = ConnectionLayer.getConnection();

      String sqlUpdate;

      if (accountType.equals(AccountType.Private)) {
        sqlUpdate = "UPDATE `drinkleg7`.`company_account` t SET t.`name` = ?, t.`email` = ?, t.`password` = ? WHERE t.`id` = ?";
      } else {
        sqlUpdate = "UPDATE `drinkleg7`.`private_account` t SET t.`name` = ?, t.`email` = ?, t.`password` = ? WHERE t.`id` = ?";
      }

      PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate,
          Statement.RETURN_GENERATED_KEYS);

      preparedStatement.setString(1,name);
      preparedStatement.setString(2,email);
      preparedStatement.setString(3,password);
      preparedStatement.setInt(4,accID);

      int rowsAffected = preparedStatement.executeUpdate();
      if (rowsAffected >= 1) {
        return true;
      }
    } catch (SQLException ex) {
      System.out.println("Registration exception: ");
      System.out.println(ex);
      return false;
    } finally {
      ConnectionLayer.cleanUp(statement, resultSet);
    }
    return false;
  }
}
