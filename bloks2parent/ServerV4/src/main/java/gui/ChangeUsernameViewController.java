package gui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import model.Client.Task;
import model.Message;
import model.Session;

/**
 * Controller for ChangeUsernameView.
 *
 * @author jbuechs
 */
public class ChangeUsernameViewController {

  @FXML private Text currentUsername;
  @FXML private TextField newUsernameTextField;
  @FXML private Text errorText;

  /**
   * changes username of logged-in user.
   *
   * @param actionEvent change username button
   * @throws IOException error with scene switch
   */
  public void onChangeUsername(ActionEvent actionEvent) throws IOException {
    String newUsername = newUsernameTextField.getText();
    Message message =
        Session.getMyUser()
            .getClient()
            .editUser(
                Task.CHANGE_USERNAME,
                Session.getMyUser().getClient().getAccessToken(),
                newUsername);
    if (message.getStatus() == 200) {
      Session.getMyUser().setUsername(newUsername);
      Session.getSceneController().showProfileView(actionEvent);
    } else {
      newUsernameTextField.clear();
      System.out.println(message);
      if (message.getStatus() == 599) {
        errorText.setText("Please restart the game, internal error!");
      } else {
        errorText.setText(message.getContent());
      }
    }
  }

  /**
   * switches scene to profile view.
   *
   * @param mouseEvent click on profile icon
   * @throws IOException error with scene switch
   */
  public void onGoToProfile(MouseEvent mouseEvent) throws IOException {
    Session.getSceneController().showProfileView(mouseEvent);
  }

  /** shows current username of logged-in user. */
  public void showCurrentUsername() {
    currentUsername.setText(Session.getMyUser().getUsername());
  }
}
