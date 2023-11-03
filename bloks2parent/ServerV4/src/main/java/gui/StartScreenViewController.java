package gui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import model.Session;
import model.User;

/**
 * Controller for startScreenView.
 *
 * @author lfiebig
 */
public class StartScreenViewController {

  @FXML public TextField usernameTextField;

  /**
   * Method is called when user clicks on "Start game".
   * redirects do mainMenuView.
   *
   * @param evt ButtonEvent
   * @throws IOException when scene loading fails
   */
  public void onStartGame(ActionEvent evt) throws IOException {
    User user = new User(usernameTextField.getText());
    Session.setMyUser(user);
    Session.getSceneController().showMainMenu(evt);
  }
}
