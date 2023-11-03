package gui;

import static gui.LoginViewController.getSha256SecurePassword;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import model.Client;
import model.Message;
import model.Session;

/**
 * Controller for RegisterView.
 *
 * @author jbuechs
 */
public class RegisterViewController {
  @FXML public TextField usernameTextField;
  @FXML public PasswordField passwordTextField;
  @FXML public TextField ipTextField;
  @FXML public Text errorText;

  /**
   * Registers a user. Username will be set. IP-Address will be set. Security Token will be set. If
   * username already exits or does not match the regex, an error message is shown.
   *
   * @param actionEvent click on register
   * @throws IOException error with scene switch
   */
  public void onRegister(ActionEvent actionEvent) throws IOException {
    String username = usernameTextField.getText();
    if (username.matches("[a-zA-z0-9]{1,12}")) {
      String password = getSha256SecurePassword(passwordTextField.getText());

      Session.getMyUser().setClient(new Client(ipTextField.getText()));
      Message message = Session.getMyUser().getClient().register(username, password);
      if (message.getStatus() == 200) {
        Session.getMyUser().setOnline(true);
        Session.getMyUser().getClient().setAccessToken(message.getContent());
        Session.getMyUser().setUsername(username);
        Session.getSceneController().showMainMenu(actionEvent);

      } else {
        errorText.setText(message.getContent());
        if (message.getStatus() == 599) {
          errorText.setText("Please restart the game, internal error!");
          ipTextField.clear();
        } else if (message.getStatus() == 470) {
          ipTextField.clear();
        } else {
          usernameTextField.clear();
          passwordTextField.clear();
        }
      }
    } else {
      errorText.setText("Username must have 1 to 12 characters, no special characters allowed");
    }
  }

  /**
   * switches scene to main menu.
   *
   * @param mouseEvent click on menu icon (house)
   * @throws IOException error with scene switch
   */
  public void onGoToMainMenu(MouseEvent mouseEvent) throws IOException {
    Session.getSceneController().showMainMenu(mouseEvent);
  }
}
