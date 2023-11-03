package gui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import model.Client.Task;
import model.Message;
import model.Session;

/**
 * Controller for DeleteUserView.
 *
 * @author jbuechs
 */
public class DeleteUserViewController {

  @FXML private Text errorText;

  /**
   * deletes the logged-in user.
   *
   * @param actionEvent click on delete
   * @throws IOException error with scene switch
   */
  public void onDelete(ActionEvent actionEvent) throws IOException {
    Message message =
        Session.getMyUser()
            .getClient()
            .editUser(Task.DELETE_USER, Session.getMyUser().getClient().getAccessToken(), "");
    if (message.getStatus() == 200) {
      Session.getMyUser().setOnline(false);
      Session.getSceneController().showMainMenu(actionEvent);
    } else {
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
}
