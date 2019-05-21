package com.espressoshock.drinkle.databaseLayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionLayer {

  private static String url = "jdbc:mysql://den1.mysql6.gear.host/drinkleg7?allowMultiQueries=true";
  private static String username = "drinkleg7";
  private static String password = "drinkle.123";
  private static Connection connection;

  public static Connection getConnection() {
    try {
      connection = DriverManager.getConnection(url,username,password);
      System.out.println("Connection to DB established successfully");
    } catch (SQLException ex) {
      System.out.println("Failed to create the database connection. Error:");
      System.out.println(ex);
    }
    return connection;
  }
  public static void cleanUp(Statement stmt, ResultSet rs){
    if (rs != null) {
      try {
        rs.close();
      } catch (SQLException sqlEx) {
      } // ignore
    }
    if (stmt != null) {
      try {
        stmt.close();
      } catch (SQLException sqlEx) {
      } // ignore
    }
  }
}
