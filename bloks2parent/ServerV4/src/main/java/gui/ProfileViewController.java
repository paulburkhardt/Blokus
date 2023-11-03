package gui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import model.Session;

/**
 * controller to view profile.
 */
public class ProfileViewController {

  public void onChangeUsername(ActionEvent evt) throws IOException {
    Session.getSceneController().showChangeUsernameView(evt);
  }

  public void onChangePassword(ActionEvent evt) throws IOException {
    Session.getSceneController().showChangePasswordView(evt);
  }

  public void onDeleteAccount(ActionEvent evt) throws IOException {
    Session.getSceneController().showDeleteUserView(evt);
  }

  public void onGoToMainMenu(MouseEvent mouseEvent) throws IOException {
    Session.getSceneController().showMainMenu(mouseEvent);
  }
}
