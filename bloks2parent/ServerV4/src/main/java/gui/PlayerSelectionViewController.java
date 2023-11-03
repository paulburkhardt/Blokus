package gui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import model.Session;

/**
 * Controller for playerSelecionView.
 *
 * @author lfiebig
 */
public class PlayerSelectionViewController {

  @FXML private Pane twoPlayers;
  @FXML private Pane fourPlayers;

  /**
   * Method is called when user selects a player amount. redirects to createGameView.
   *
   * @param evt Event to get the selected player amount
   * @throws IOException when scene loading fails
   */
  public void onSelect(Event evt) throws IOException {
    if (evt.getSource().equals(twoPlayers)) {
      Session.getMyUser().getLobby().setPlayerAmount(2);
    } else if (evt.getSource().equals(fourPlayers)) {
      Session.getMyUser().getLobby().setPlayerAmount(4);
    }
    Session.getSceneController().showCreateGameView(evt);
  }

  /**
   * Method is called when user clicks on "back to main menu". redirects to mainMenuView.
   *
   * @param evt ButtonEvent
   * @throws IOException when scene loading fails
   */
  public void onBackToMainMenu(ActionEvent evt) throws IOException {
    Session.getSceneController().showMainMenu(evt);
  }
}
