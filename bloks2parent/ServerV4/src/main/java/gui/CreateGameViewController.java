package gui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import model.Client;
import model.Session;
import model.User;
import service.ServerService;
import websockets.WebSocketServer;

/**
 * Controller for createGameView.
 *
 * @author lfiebig
 */
public class CreateGameViewController {

  @FXML GridPane roundsSecondHalf;
  @FXML GridPane roundsFirstHalf;
  @FXML Pane gameModeOne;
  @FXML Pane gameModeTwo;

  // selected Game mode
  private String selectedMode;
  // counter for added game rounds
  private int roundsAdded = 0;

  // String with every added game round
  private StringBuffer rounds = new StringBuffer("");

  private Text text;
  private Pane pane;

  /** sets the selectable game modes. */
  public void setInfo() {
    if (Session.getMyUser().getLobby().getPlayerAmount() == 2) {
      System.out.println(gameModeOne);
      Text gameModeOneText = ((Text) gameModeOne.getChildren().get(0));
      gameModeOneText.setText("DUO");

      Text gameModeTwoText = ((Text) gameModeTwo.getChildren().get(0));
      gameModeTwoText.setText("JUNIOR");
    }
  }

  /**
   * Method is called when the user clicks on a game mode. changes the selected game mode.
   *
   * @param evt Event to get the clicked on game mode
   */
  public void onSelectMode(Event evt) {
    if (pane != null) {
      pane.setStyle(
          "-fx-background-radius: 20 20 20 20;"
              + " -fx-effect: dropshadow(two-pass-box, rgba(0, 0, 0, 0.6), 5, 0.0, 0, 1);"
              + " -fx-background-color:  #224957;");
    }
    pane = (Pane) (evt.getSource());
    pane.setStyle(
        "-fx-background-radius: 20 20 20 20;"
            + " -fx-effect: dropshadow(two-pass-box, rgba(0, 0, 0, 0.6), 5, 0.0, 0, 1);"
            + " -fx-background-color: #20DF7F;");
    text = (Text) (pane.getChildren().get(0));
    selectedMode = text.getText();
    System.out.println(selectedMode);
  }

  /** Method is called when the user clicks on the "+" button. */
  public void onAddRound() {
    if (this.selectedMode != null) {
      if (roundsAdded < 10) {
        if (roundsAdded < 5) {
          Pane roundPane = (Pane) (roundsFirstHalf.getChildren().get(roundsAdded));
          Text roundText = (Text) (roundPane.getChildren().get(1));
          roundText.setText(selectedMode);
          roundPane.setVisible(true);
          addRoundToArray();
          roundsAdded++;
        } else if (roundsAdded >= 5) {
          Pane roundPane = (Pane) (roundsSecondHalf.getChildren().get(roundsAdded - 5));
          Text roundText = (Text) (roundPane.getChildren().get(1));
          roundText.setText(selectedMode);
          roundPane.setVisible(true);
          addRoundToArray();
          roundsAdded++;
        }
      }
    }
  }

  /** Adds round to the rounds String. */
  public void addRoundToArray() {
    if (rounds.toString().equals("")) {
      rounds.append(selectedMode);
    } else {
      rounds.append(":" + selectedMode);
    }
  }

  /**
   * Method is called when the user clicks on the "cancel" button user gets back to the main menu.
   *
   * @param evt the ActionEvent
   * @throws IOException when scene loading fails
   */
  public void onCancel(ActionEvent evt) throws IOException {
    Session.getSceneController().showMainMenu(evt);
  }

  /**
   * Method is called when the user clicks on the "Create game" button A Lobby starts and the user.
   * relocates to the lobbyView.
   *
   * @param evt the ActionEvent
   * @throws IOException when scene loading fails
   */
  public void onCreateGame(ActionEvent evt) throws IOException {
    if (!rounds.toString().equals("")) {
      User myUser = Session.getMyUser();
      if (myUser.getLobby().getPort() == 0) { // IF MULTIPLAYER
        String rounds = this.rounds.toString();
        String playerAmount = Integer.toString(myUser.getLobby().getPlayerAmount());
        int port = Session.getMyUser().getClient().sendStartNewWsServer(rounds, playerAmount);

        myUser.getLobby().setPort(port);
        // TODO Der Port muss oben in der LobbyAnsicht angezeigt werden
        Session.getMyUser()
            .getClient()
            .getWsClient()
            .connectToServer(port, Session.getMyUser().getClient().getIpAdress());

        send701Message(port);
      } else { // IF SINGLEPLAYER
        WebSocketServer webSocketServer = new WebSocketServer();
        Session.getMyUser().getLobby().setPort(8020);
        ServerService.addPortToServer(8020, webSocketServer);

        webSocketServer.runServer(8020);

        Session.getMyUser().setClient(new Client("localhost"));
        Session.getMyUser().getClient().setWsClient();
        Session.getMyUser().getClient().getWsClient().connectToServer(8020, "localhost");

        int playerAmount = Session.getMyUser().getLobby().getPlayerAmount();

        System.out.println("___");
        Session.getMyUser()
            .getClient()
            .getWsClient()
            .sendMessage(703, "" + 8020 + "," + playerAmount + "," + rounds.toString());
        send701Message(8020);
      }
      Session.getSceneController().showLobbyView();
    }
  }

  /**
   * Sends a 701 Message.
   *
   * @param port port to get the websocketServer
   */
  public void send701Message(int port) {
    String username = Session.getMyUser().getUsername();
    Session.getMyUser().getClient().getWsClient().sendMessage(701, username + "," + port);
  }
}
