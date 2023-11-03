package gui;

import static gui.LoginViewController.getSha256SecurePassword;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import model.Client.Task;
import model.Message;
import model.Session;

/**
 * Controller for ChangePasswordView.
 *
 * @author jbuechs
 */
public class ChangePasswordViewController {
  @FXML private PasswordField passwordTextField;
  @FXML private Text errorText;

  /**
   * changes password of logged-in user.
   *
   * @param actionEvent click on change button
   * @throws IOException error with scene switch
   */
  public void onChangePassword(ActionEvent actionEvent) throws IOException {
    String newPassword = getSha256SecurePassword(passwordTextField.getText());
    Message message =
        Session.getMyUser()
            .getClient()
            .editUser(
                Task.CHANGE_PASSWORD,
                Session.getMyUser().getClient().getAccessToken(),
                newPassword);
    if (message.getStatus() == 200) {
      Session.getSceneController().showProfileView(actionEvent);
    } else {
      passwordTextField.clear();
      System.out.println(message);
      if (message.getStatus() == 599) {
        errorText.setText("Please restart the game, internal error!");
      } else {
        errorText.setText(message.getContent());
      }
    }
  }

  /**
   * switches scene to Profile View.
   *
   * @param mouseEvent click on profile icon
   * @throws IOException error with scene switch
   */
  public void onGoToProfile(MouseEvent mouseEvent) throws IOException {
    Session.getSceneController().showProfileView(mouseEvent);
  }
}
