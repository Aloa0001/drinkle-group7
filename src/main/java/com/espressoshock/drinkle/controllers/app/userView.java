package com.espressoshock.drinkle.controllers.app;

import com.espressoshock.drinkle.appState.Current;
import com.espressoshock.drinkle.databaseLayer.ConnectionLayer;
import com.espressoshock.drinkle.models.*;
import com.espressoshock.drinkle.viewLoader.EventDispatcherAdapter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class userView extends EventDispatcherAdapter implements Initializable {
    private Connection connection = null;
    private PreparedStatement prepStatement = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private int usrId= 0;
    @FXML
    private Label lblUserName, lblEmail, lblPassword;
    @FXML
    private TextField textFieldName, textFieldMail, textFieldPassword;
    ArrayList<Beverage> beveragesList = new ArrayList<>();
    public void test(){
        System.out.println("test");
    }

    public void getUserData() throws Exception{
        usrId = Current.environment.currentUser.getId();
        /////////////////////////////
        int userID = 0;
        String userName = "";
        String userMail = "";
        String userPassword = "";
        if(Current.environment.currentUser instanceof PrivateAccount){
            try {
                connection = ConnectionLayer.getConnection();
                prepStatement = connection.prepareStatement("SELECT * " +
                        "FROM private_account WHERE private_account.id = ?");
                prepStatement.setString(1,Current.environment.currentUser.getId().toString());
                resultSet = prepStatement.executeQuery();


                while (resultSet.next()) {
                    userID = resultSet.getInt(1);
                    userName = resultSet.getString(2);
                    userMail = resultSet.getString(3);
                    userPassword = resultSet.getString(4);


//
                }
            } catch (SQLException ex) {
                System.out.println("Exception: ");
                ex.printStackTrace();
            } finally {
                ConnectionLayer.cleanUp(statement, resultSet);
            }
            connection.close();
        }
        if(Current.environment.currentUser instanceof BusinessAccount){
            try {
                connection = ConnectionLayer.getConnection();
                prepStatement = connection.prepareStatement("SELECT * " +
                        "FROM company_account WHERE company_account.id = ?");
                prepStatement.setString(1,Current.environment.currentUser.getId().toString());
                resultSet = prepStatement.executeQuery();
                while (resultSet.next()) {
                    userID = resultSet.getInt(1);
                    userName = resultSet.getString(2);
                    userMail = resultSet.getString(3);
                    userPassword = resultSet.getString(4);

                }
            } catch (SQLException ex) {
                System.out.println("Exception: ");
                ex.printStackTrace();
            } finally {
                ConnectionLayer.cleanUp(statement, resultSet);
            }
            connection.close();
        }
        lblUserName.setText(userName);
        lblEmail.setText(userMail);
        lblPassword.setText(userPassword);


    }
    public void beverageItem(Beverage bvg){
        Label beverageName = new Label(bvg.getName());
    }

    public void loadBeverages(){
        int bvgID = 0;
        String bvgName = "";
        int bvgAlcohol = 0;
        double bvgCost = 0.0;
        String bvgNotes= "";
        String sql = "";
        if(Current.environment.currentUser instanceof PrivateAccount){
            sql = "Select beverage.id,beverage.name,beverage.alcohol,beverage.cost,beverage.notes from private_account_has_beverage, " +
                    "beverage where private_account_has_beverage.beverage_id = beverage.id and private_account_has_beverage.private_account_id = ?";
        } else if (Current.environment.currentUser instanceof BusinessAccount){
            sql = "Select beverage.id,beverage.name,beverage.alcohol,beverage.cost,beverage.notes from company_account_has_beverage, " +
                    "beverage where company_account_has_beverage.beverage_id = beverage.id and company_account_has_beverage.company_account_id = ?";
        }
        try {
            connection = ConnectionLayer.getConnection();
            prepStatement = connection.prepareStatement(sql);
            prepStatement.setString(1,Current.environment.currentUser.getId().toString());
            resultSet = prepStatement.executeQuery();
            while (resultSet.next()) {
                bvgID = resultSet.getInt(1);
                bvgName = resultSet.getString(2);
                bvgAlcohol = resultSet.getInt(3);
                bvgCost = resultSet.getDouble(4);
                bvgNotes = resultSet.getString(5);

                Beverage newBvg = new Beverage(bvgName,bvgAlcohol,bvgCost,)

            }
        } catch (SQLException ex) {
            System.out.println("Exception: ");
            ex.printStackTrace();
        } finally {
            ConnectionLayer.cleanUp(statement, resultSet);
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            getUserData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
