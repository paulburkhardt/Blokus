package gui;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
 * Controller for LoginView.
 *
 * @author jbuechs
 */
public class LoginViewController {
  @FXML public TextField usernameTextField;
  @FXML public PasswordField passwordTextField;
  @FXML public TextField ipTextField;
  @FXML public Text errorText;

  /**
   * saves ip address of restServer in user object and "connects" to it. Username for active Session
   * will be set. Security Token will be generated on successful login and also saved in user.
   *
   * @param actionEvent click on login
   * @throws IOException error with scene switch
   */
  public void onLogin(ActionEvent actionEvent) throws IOException {
    String username = usernameTextField.getText();
    String password = getSha256SecurePassword(passwordTextField.getText());

    Session.getMyUser().setClient(new Client(ipTextField.getText()));
    Message message = Session.getMyUser().getClient().login(username, password);
    if (message.getStatus() == 200) {
      Session.getMyUser().setOnline(true);
      Session.getMyUser().getClient().setAccessToken(message.getContent());
      Session.getMyUser().setUsername(username);
      Session.getSceneController().showMainMenu(actionEvent);

    } else {
      errorText.setText(message.getContent());
      if (message.getStatus() == 599) {
        errorText.setText("Please restart the game, internal error!");

      } else if (message.getStatus() == 470) {
        ipTextField.clear();

      } else {
        usernameTextField.clear();
        passwordTextField.clear();
      }
    }
  }

  /**
   * switches scene to register view.
   *
   * @param mouseEvent click on "click here"-text
   * @throws IOException error with scene switch
   */
  public void onClickHere(MouseEvent mouseEvent) throws IOException {
    Session.getSceneController().showRegisterView(mouseEvent);
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

  /**
   * hashes the input String(Password) with sha-256.
   *
   * @param password password that will be hashed
   * @return sha-256 hashed password
   * @author jbuechs
   */
  public static String getSha256SecurePassword(String password) {
    byte[] bytesOfPassword;
    StringBuilder sb = new StringBuilder();

    try {
      MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
      bytesOfPassword = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
      for (byte b : bytesOfPassword) {
        sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
      }
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return new String(sb);
  }
}
