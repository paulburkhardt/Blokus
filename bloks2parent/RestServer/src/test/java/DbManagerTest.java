import static org.junit.Assert.*;

import database.DbManager;
import exception.UsernameDoesNotExistException;
import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Testing for Database Management
 *
 * @author jbuechs
 */
public class DbManagerTest {

  private static DbManager dbManager;

  @BeforeClass
  public static void setUp() {
    dbManager = new DbManager();
    dbManager.connectToDatabase();
  }


  @org.junit.Test
  public void insertUser() throws SQLException {
    dbManager.insertUser("josh", "hallo1234"); //in the normal project the password is always hashed
    assertTrue(dbManager.doesUserExists("josh"));
  }

  @org.junit.Test
  public void doesUserExists() throws SQLException {
    dbManager.insertUser("josh", "hallo1234"); //in the normal project the password is always hashed
    assertTrue(dbManager.doesUserExists("josh"));
  }

  @org.junit.Test
  public void deleteUser() throws SQLException {
    dbManager.insertUser("josh", "hallo1234"); //in the normal project the password is always hashed
    dbManager.deleteUser("josh");
    assertFalse(dbManager.doesUserExists("josh"));
  }

  @org.junit.Test
  public void changeUsername() throws SQLException {
    dbManager.insertUser("josh", "hallo1234"); //in the normal project the password is always hashed
    dbManager.changeUsername("josh", "joshua");
    assertTrue(dbManager.doesUserExists("joshua"));
    assertFalse(dbManager.doesUserExists("josh"));
  }

  @org.junit.Test
  public void checkAuthenticationOfUser() throws UsernameDoesNotExistException, SQLException {
    dbManager.insertUser("josh", "hallo1234"); //in the normal project the password is always hashed
    assertTrue(dbManager.checkAuthenticationOfUser("josh", "hallo1234"));
    assertFalse(dbManager.checkAuthenticationOfUser("josh", "hallo4321"));

  }

  @org.junit.Test
  public void changePassword() throws SQLException, UsernameDoesNotExistException {
    dbManager.insertUser("josh", "hallo1234"); //in the normal project the password is always hashed
    dbManager.changePassword("josh", "hallo4321");
    assertTrue(dbManager.checkAuthenticationOfUser("josh", "hallo4321"));
    assertFalse(dbManager.checkAuthenticationOfUser("josh", "hallo1234"));
  }

  @After
  public void deleteUserJunit() throws SQLException {
    dbManager.deleteUser("josh");
  }

  @AfterClass
  public static void deleteSetUp() {
    dbManager.deleteUserTable();
  }
}
