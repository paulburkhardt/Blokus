package gui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import model.Client;
import model.Lobby;
import model.Session;

/**
 * Controller for the mainMenuView.
 *
 * @author lfiebig
 */
public class MainMenuViewController {

  @FXML private TextField portInputField;
  @FXML private Button portButton;
  @FXML private Text onlineText;

  /**
   * Method is called when user clicks on "Single Player". Creats single player lobby and redirects
   * to lobbyView.
   *
   * @param evt ButtonEvent
   * @throws IOException when scene loading fails
   */
  public void onSinglePlayer(ActionEvent evt) throws IOException {
    Lobby lobby = new Lobby(8020);
    Session.getMyUser().setLobby(lobby);
    Session.getMyUser().getLobby().addUser(Session.getMyUser());
    Session.getSceneController().showPlayerSelectionPopUp(evt);
  }

  /**
   * Method is calles when user clicks on "Create multiplayer game" creates multiplayer lobby and
   * redirects to playerSelectionView.
   *
   * @param evt ButtonEvent
   * @throws IOException when scene loading fails
   */
  public void onCreateMultiplayerGame(ActionEvent evt) throws IOException {
    if (!Session.getMyUser().isOnline()) {
      Session.getSceneController().showLoginView(evt);
    } else {
      // Session.getMyUser().setClient(new Client("localhost"));
      Session.getMyUser().getClient().setWsClient();
      Lobby lobby = new Lobby(0);
      Session.getMyUser().setLobby(lobby);
      Session.getMyUser().getLobby().addUser(Session.getMyUser());
      Session.getSceneController().showPlayerSelectionPopUp(evt);
    }
  }

  /**
   * Method is called when user clicks on "Join multiplayer game". shows text field to type in an
   * IP-address.
   *
   * @param evt ButtonEvent
   * @throws IOException when scene loading fails
   */
  public void onJoinMultiplayerGame(ActionEvent evt) throws IOException {
    if (!Session.getMyUser().isOnline()) {
      Session.getSceneController().showLoginView(evt);
    } else {
      Button button = (Button) evt.getSource();
      button.setVisible(false);

      portButton.setVisible(true);
      portInputField.setVisible(true);

      // TODO port muss über ein Textfeld angegeben werden. Welcher Lobby/Port möchte der User
      // joinen
      //  Session.getMyUser().getClient().getWsClient().connectToServer(8090,
      // Session.getMyUser().getClient().getIpAdress());
    }
  }

  /**
   * Method is called when user clicks on the user icon. redirects to the profileView.
   *
   * @author jbuechs
   * @param evt MouseEvent
   * @throws IOException when scene loading fails
   */
  public void onGoToProfile(Event evt) throws IOException {
    if (!Session.getMyUser().isOnline()) {
      Session.getSceneController().showLoginView(evt);
    } else {
      Session.getSceneController().showProfileView(evt);
    }
  }

  /**
   * Method is called when user clicks on "go" Button connects with the typed in IP Address and
   * redirects to lobbyView.
   *
   * @author jbuechs
   * @throws IOException when scene loading fails
   */
  public void onConnect() { // Go button to join lobby
    Session.getMyUser().getClient().setWsClient();
    int port = Integer.valueOf(portInputField.getText());
    Lobby lobby = new Lobby(port);
    Session.getMyUser().setLobby(lobby);
    Session.getMyUser().getLobby().setPlayerAmount(4); // TODO is das nötig?
    // Session.getMyUser().getLobby().addUser(Session.getMyUser());

    Session.getMyUser()
        .getClient()
        .getWsClient()
        .connectToServer(lobby.getPort(), Session.getMyUser().getClient().getIpAdress());

    if (Session.getMyUser().getClient().getWsClient().getSession() != null) {
      String myUsername = Session.getMyUser().getUsername();
      Session.getMyUser()
          .getClient()
          .getWsClient()
          .sendMessage(701, myUsername + "," + lobby.getPort());
    }
  }

  /**
   * Method is called when user successfully logged into his account.
   *
   * @author jbuechs
   */
  public void showIsOnline() {
    if (Session.getMyUser().isOnline()) {
      onlineText.setText(
          "You are online! Other users can see you as: " + Session.getMyUser().getUsername());
    }
  }
}
