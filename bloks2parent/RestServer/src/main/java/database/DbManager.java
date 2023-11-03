package database;

import exception.UsernameDoesNotExistException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database Management.
 */
public class DbManager {

  private static final String URL = "jdbc:sqlite:bloksTwoDB.db";
  private Connection connection = null;

  /**
   * creates database if it doesnt exists already and establishes connection.
   *
   * @author jbuechs
   */
  public void connectToDatabase() {
    try {
      this.connection = DriverManager.getConnection(URL);
      Statement statement = this.connection.createStatement();
      statement.setQueryTimeout(30);

      statement.execute(
          "CREATE TABLE IF NOT EXISTS User(username TEXT NOT NULL, password TEXT NOT NULL)");
      System.out.println("Table created succesfully");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * checks if user already exists in database.
   *
   * @param usernameInput username which is checked for existence in database
   * @return true if user doesnt exists in database, else false
   * @author jbuechs
   */
  public boolean doesUserExists(String usernameInput) {
    boolean doesExist = false;
    try (Statement stm = this.connection.createStatement();
        ResultSet rs =
            stm.executeQuery(
                "SELECT username " + "FROM User " + "WHERE username = '" + usernameInput + "'")) {
      if (rs.next() == false) {
        doesExist = false;
      } else {
        System.out.println(
            "The username ("
                + rs.getString("username")
                + ") already exists. "
                + "Please choose a different one.");
        doesExist = true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return doesExist;
  }

  /**
   * inserts user element (username, password and theme) into database.
   *
   * @param username username which will be added to the database
   * @param password pasword which will be hashed and added to the database
   * @return true if user doesnt already exsists in database and was added to database successfully,
   *     else false
   * @author jbuechs
   */
  // inserts user element (username and password) into bloksTwoDB
  public boolean insertUser(String username, String password) throws SQLException {
    System.out.println(username + " _________" + password);
    Statement stm = this.connection.createStatement();

    if (!doesUserExists(username)) {
      stm.execute(
          "INSERT INTO User (username, password) VALUES "
              + "('"
              + username
              + "','"
              + password
              + "')");
      return true;
    } else {
      return false;
    }
  }

  /**
   * deletes user element in database.
   *
   * @param usernameInput username which refers to the element that will be deleted in database
   * @author jbuechs
   */
  public void deleteUser(String usernameInput) throws SQLException {
    try (Statement stm = this.connection.createStatement()) {
      stm.execute("DELETE FROM User WHERE username = '" + usernameInput + "'");
    }
  }

  /**
   * disconnects connection to the database.
   *
   * @author jbuechs
   */
  public void disconnect() {
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
        e.printStackTrace();
        System.exit(0);
      }
    }
  }

  /**
   * deletes user table including all entries in database.
   *
   * @author jbuechs
   */
  public void deleteUserTable() {
    try (Statement stm = this.connection.createStatement()) {
      stm.execute("DROP TABLE User");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * username and password will be checked in existing database username must exist, password must
   * be the same as the corresponding password in the database.
   *
   * @param username username that will be checked for authentication
   * @param passwordInput password that will be checked for authentication
   * @return true if authentication was successful, false if unsuccessful
   * @author jbuechs
   */
  public boolean checkAuthenticationOfUser(String username, String passwordInput)
      throws UsernameDoesNotExistException {
    // first get the password of the user from the database with a select statement
    String passwordInDataBase = "";
    try (Statement stm = this.connection.createStatement();
        ResultSet rs =
            stm.executeQuery(
                "SELECT password " + "FROM User " + "WHERE username = '" + username + "'")) {

      if (!rs.next()) { // checks if username exists in database
        System.out.println("This username doesn't exists in the database");
        throw new UsernameDoesNotExistException();
      } else {
        passwordInDataBase = rs.getString("password");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    // if passwords are the same --> authentication successful --> true
    return passwordInDataBase.equals(passwordInput);
  }

  /**
   * changes username in database.
   *
   * @param oldUsername the old username
   * @param newUsername the new username
   * @author jbuechs
   */
  public void changeUsername(String oldUsername, String newUsername) throws SQLException {
    try (Statement stm = this.connection.createStatement()) {
      stm.execute(
          "UPDATE User "
              + "SET username = '"
              + newUsername
              + "' "
              + "WHERE username = '"
              + oldUsername
              + "'");
    }
  }

  /**
   * changes password in database.
   *
   * @param usernameInput username to which the password will be changed
   * @param newPassword the new password
   * @author jbuechs
   */
  public void changePassword(String usernameInput, String newPassword) throws SQLException {
    try (Statement stm = this.connection.createStatement()) {
      stm.execute(
          "UPDATE User "
              + "SET password = '"
              + newPassword
              + "' "
              + "WHERE username = '"
              + usernameInput
              + "'");
    }
  }
}
