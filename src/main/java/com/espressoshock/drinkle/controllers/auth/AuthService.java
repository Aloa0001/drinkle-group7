package com.espressoshock.drinkle.controllers.auth;

import com.espressoshock.drinkle.appState.AppStatePersistence;
import com.espressoshock.drinkle.appState.Current;
import com.espressoshock.drinkle.databaseLayer.ConnectionLayer;
import com.espressoshock.drinkle.models.Account;
import com.espressoshock.drinkle.models.BusinessAccount;
import com.espressoshock.drinkle.models.PrivateAccount;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AuthService {

  private Connection connection = null;
  private Statement statement = null;
  private ResultSet resultSet = null;


  boolean loginAsPrivateAccount(String email, String password) {
    try {
      connection = ConnectionLayer.getConnection();
      statement = connection.createStatement();
      resultSet = statement.executeQuery("SELECT * FROM drinkleg7.private_account");

      while (resultSet.next()) {

        int id = resultSet.getInt("id");
        String nameFromDB = resultSet.getString("name");
        String emailFromDB = resultSet.getString("email");
        String passwordFromDB = resultSet.getString("password");

        if (email.equals(emailFromDB) && password.equals(passwordFromDB)) {
          PrivateAccount newAccount = new PrivateAccount(id,emailFromDB,passwordFromDB,null,nameFromDB);

          Current
              .environment
              .currentUser = newAccount;

          AppStatePersistence.saveObject(newAccount, AppStatePersistence.userFilename);

          System.out.printf("id: %d, name:%s, email: %s\n", id, nameFromDB, emailFromDB);
          System.out.println("Current user:");
          System.out.println(Current.environment.currentUser.toString());


          persistAccount(newAccount);

          return true;
        }

      }
    } catch (SQLException ex) {
      System.out.println("Login exception: ");
      System.out.println(ex);
    } finally {
      ConnectionLayer.cleanUp(statement, resultSet);
    }
    return true;/// NEMANJA
  }

  boolean loginAsCompanyAccount(String email, String password) {
    try {
      connection = ConnectionLayer.getConnection();
      statement = connection.createStatement();
      resultSet = statement.executeQuery("SELECT * FROM drinkleg7.company_account");

      while (resultSet.next()) {

        int id = resultSet.getInt("id");
        String nameFromDB = resultSet.getString("name");
        String emailFromDB = resultSet.getString("email");
        String passwordFromDB = resultSet.getString("password");

        if (email.equals(emailFromDB) && password.equals(passwordFromDB)) {
          BusinessAccount newAccount = new BusinessAccount(id,emailFromDB,passwordFromDB,null,nameFromDB);

          persistAccount(newAccount);

          return true;
        }
      }
    } catch (SQLException ex) {
      System.out.println("Login exception: ");
      System.out.println(ex);
    } finally {
      ConnectionLayer.cleanUp(statement, resultSet);
    }
    return true;/// NEMANJA
  }



  private void persistAccount(Account acc) {
    Current
        .environment
        .currentUser = acc;

    AppStatePersistence.saveObject(acc, AppStatePersistence.userFilename);

    System.out.println("Current user:");
    System.out.println(acc.toString());
    System.out.println(Current.environment.currentUser.toString());
  }


  public enum AccountType {Company, Private}

  boolean registerAccount(String email, String password, String name, AccountType accountType) {

    // brute force basic validation
    if (loginAsPrivateAccount(email,password)) {
      return false;
    }
    if (loginAsCompanyAccount(email,password)) {
      return false;
    }

    try {
      connection = ConnectionLayer.getConnection();

      String sql;

      if (accountType.equals(AccountType.Private)) {
        sql = "INSERT INTO `drinkleg7`.`private_account` (`name`, `email`, `password`) VALUES (?, ?, ?)";
      } else {
        sql = "INSERT INTO `drinkleg7`.`company_account` (`name`, `email`, `password`) VALUES (?, ?, ?)";
      }

      PreparedStatement preparedStatement = connection.prepareStatement(sql,
          Statement.RETURN_GENERATED_KEYS);

      preparedStatement.setString(1,name);
      preparedStatement.setString(2,email);
      preparedStatement.setString(3,password);

      int rowsAffected = preparedStatement.executeUpdate();
      if (rowsAffected >= 1) {

        if (accountType.equals(AccountType.Private)) {
          PrivateAccount newAccount = new PrivateAccount(null,email,password,null,name);
          persistAccount(newAccount);
        } else {
          BusinessAccount newAccount = new BusinessAccount(null,email,password,null,name);
          persistAccount(newAccount);
        }

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
