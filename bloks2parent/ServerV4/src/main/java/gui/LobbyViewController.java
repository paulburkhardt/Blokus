package gui;

import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import model.ChatMessage;
import model.Lobby;
import model.Session;
import model.User;

/**
 * Controller for lobbyView.
 *
 * @author lfiebig
 */
public class LobbyViewController {

  // selected AI difficulty
  private String aiDifficulty;
  // selected theme
  private String selectedTheme;

  private Pane aiPane = new Pane();
  private Pane themePane = new Pane();
  private Text text = new Text();

  @FXML public Pane playerOne;
  @FXML public Pane playerTwo;
  @FXML public Pane playerThree;
  @FXML public Pane playerFour;
  @FXML public Pane selectNeon;
  @FXML public Text startGameErrorText;
  @FXML public Text chatDisplay;
  @FXML public TextField chatTextField;
  @FXML public Button sendChatMessageButton;
  @FXML public ScrollPane scrollPane;
  @FXML public Pane chatPane;
  @FXML public Button startGameButton;
  @FXML public Pane addAiPane;
  @FXML public Text portDisplay;

  /** Sets the Lobby to a two or four player lobby. */
  public void setInfo() {
    if (Session.getMyUser().getLobby().getPlayerAmount() == 2) {
      playerThree.setVisible(false);
      playerFour.setVisible(false);
    }
    text = (Text) playerOne.getChildren().get(1);
    text.setText(Session.getMyUser().getUsername());
    themePane = selectNeon;
    if (!Session.getMyUser()
        .getLobby()
        .getUserList()
        .get(0)
        .getUsername()
        .equals(Session.getMyUser().getUsername())) {
      startGameButton.setVisible(false);
      addAiPane.setVisible(false);
    }
    if (Session.getMyUser().getLobby().getPort() == 8020) {
      portDisplay.setText("SINGEPLAYER");
    } else {
      portDisplay.setText("PORT: " + Session.getMyUser().getLobby().getPort());

    }
  }

  /**
   * Method is called when the host clicks on an AI difficulty. changes the selected AI difficulty
   *
   * @param evt Event to get the clicked on difficulty
   */
  public void onSelectDifficulty(Event evt) {
    if (aiPane != null) {
      aiPane.setStyle(
          "-fx-background-radius: 20 20 20 20;"
              + " -fx-effect: dropshadow(two-pass-box, rgba(0, 0, 0, 0.6), 5, 0.0, 0, 1);"
              + " -fx-background-color:  #224957;");
    }
    aiPane = (Pane) (evt.getSource());
    aiPane.setStyle(
        "-fx-background-radius: 20 20 20 20;"
            + " -fx-effect: dropshadow(two-pass-box, rgba(0, 0, 0, 0.6), 5, 0.0, 0, 1);"
            + " -fx-background-color: #20DF7F;");
    text = (Text) (aiPane.getChildren().get(0));
    aiDifficulty = text.getText();
    System.out.println(aiDifficulty);
  }

  /** Method is called when the host clicks on the "add ai" button. Adds an AI to the Lobby */
  public void onAddAi() {
    if (this.aiDifficulty != null
        && Session.getMyUser().getLobby().getUserList().size()
            < Session.getMyUser().getLobby().getPlayerAmount()) {
      String aiName = "";
      int aiLevel = 1;
      switch (Session.getMyUser().getLobby().getUserList().size()) {
        case 1:
          aiName = aiDifficulty + " Frog";
          break;
        case 2:
          aiName = aiDifficulty + " Rabbit";
          break;
        case 3:
          aiName = aiDifficulty + " Pig";
          break;
        default:
          break;
      }
      switch (aiDifficulty) {
        case "EASY":
          break;
        case "MEDIUM":
          aiLevel = 2;
          break;
        case "HARD":
          aiLevel = 3;
          break;
        default:
          break;
      }
      Session.getMyUser().getClient().getWsClient().sendMessage(702, aiName + "," + aiLevel);
    }
    updateErrorTexts();
  }

  /**
   * Method is called when the player clicks on a theme. Changes the board theme of the player.
   *
   * @param evt Event to get the selected theme
   */
  public void onSelectTheme(Event evt) {
    themePane.setOpacity(0.5);
    themePane = (Pane) (evt.getSource());
    themePane.setOpacity(1);
    text = (Text) (themePane.getChildren().get(0));
    selectedTheme = text.getText();
    Session.getMyUser().setTheme(selectedTheme);
    System.out.println(Session.getMyUser().getTheme());
  }

  /**
   * Method is called when the host clicks on "start game". Starts the first game round of the
   * lobby.
   */
  public void onStartGame() {
    updateErrorTexts();
    if (Session.getMyUser().getLobby().getUserList().size()
        == Session.getMyUser().getLobby().getPlayerAmount()) {
      Session.getMyUser().getClient().getWsClient().sendMessage(704, "");
    } else {
      startGameErrorText.setText(
          "You cant start the game, there aren't enough players in the Lobby");
    }
    // TODO Es soll mit einer Flag abgefragt werden ob der Nutzer der Host ist
  }

  /**
   * Method is called when a user clicks on "leave lobby" Disconnects user from the lobby.
   *
   * @param evt The ActionEvent to get if the user is the host.
   * @throws IOException when scene loading fails
   */
  public void onLeaveLobby(ActionEvent evt) throws IOException {
    try {
      Button button = (Button) evt.getSource();

      System.out.println(button.getParent());
      if (button.getParent().equals(playerOne)) { // if host of lobby
        Session.getMyUser().getClient().getWsClient().sendMessage(743, "");

      } else { // if NOT host of lobby
        // leave your lobby and send message to server
        model.Session.getMyUser().getClient().setWantsToDisconnect(true);
        Session.getMyUser().getClient().getWsClient().sendMessage(708, "");
        Session.getMyUser().setLobby(new Lobby(1));
        Session.getMyUser().getClient().getWsClient().getSession().close();
      }
    } finally {
      Session.getSceneController().showMainMenuAfterGameLeave();
    }
  }

  /** Method is called when the user clicks on the send button of the chat. */
  public void onSend() {
    if (!chatTextField.getText().equals("")) {
      String messageContent = chatTextField.getText();
      Session.getMyUser()
          .getClient()
          .getWsClient()
          .sendChatMessage(Session.getMyUser().getUsername(), messageContent);
    }
  }

  /** Displays incoming chat messages. */
  public void updateChatView() {
    Platform.runLater(
        () -> {
          StringBuffer stb = new StringBuffer();
          for (ChatMessage cm : Session.getMyUser().getLobby().getChat()) {
            stb.append("[" + cm.getSender() + "]:\t\t" + cm.getContent() + "\n");
          }
          chatDisplay.setText(stb.toString());
          chatTextField.setText("");
          scrollPane.vvalueProperty().bind(chatPane.heightProperty());
        });
  }

  /** empties error texts. */
  public void updateErrorTexts() {
    startGameErrorText.setText("");
  }

  /** Method is called when a player joins or leaves the lobby updates the player view. */
  public void updatePlayerView() {
    Platform.runLater(
        () -> {
          ImageView imageView;
          Text username;
          User user;

          for (int i = 0; i < Session.getMyUser().getLobby().getUserList().size(); i++) {
            user = Session.getMyUser().getLobby().getUserList().get(i);
            switch (i) {
              case 0:
                username = (Text) ((playerOne).getChildren().get(1));
                username.setText(user.getUsername());
                imageView = (ImageView) (playerOne.getChildren().get(0));
                imageView.setImage(new Image("userOne.png"));
                playerOne.setStyle(
                    "-fx-background-radius: 20 20 20 20;"
                        + " -fx-effect: dropshadow(two-pass-box, rgba(0, 0, 0, 0.6), 5, 0.0, 0, 1);"
                        + " -fx-background-color: #224957;");
                break;
              case 1:
                username = (Text) ((playerTwo).getChildren().get(1));
                username.setText(user.getUsername());
                imageView = (ImageView) (playerTwo.getChildren().get(0));
                imageView.setImage(new Image("userTwo.png"));
                playerTwo.setStyle(
                    "-fx-background-radius: 20 20 20 20;"
                        + " -fx-effect: dropshadow(two-pass-box, rgba(0, 0, 0, 0.6), 5, 0.0, 0, 1);"
                        + " -fx-background-color: #224957;");
                break;
              case 2:
                username = (Text) ((playerThree).getChildren().get(1));
                username.setText(user.getUsername());
                imageView = (ImageView) (playerThree.getChildren().get(0));
                imageView.setImage(new Image("userThree.png"));
                playerThree.setStyle(
                    "-fx-background-radius: 20 20 20 20;"
                        + " -fx-effect: dropshadow(two-pass-box, rgba(0, 0, 0, 0.6), 5, 0.0, 0, 1);"
                        + " -fx-background-color: #224957;");
                break;
              case 3:
                username = (Text) ((playerFour).getChildren().get(1));
                username.setText(user.getUsername());
                imageView = (ImageView) (playerFour.getChildren().get(0));
                imageView.setImage(new Image("userFour.png"));
                playerFour.setStyle(
                    "-fx-background-radius: 20 20 20 20;"
                        + " -fx-effect: dropshadow(two-pass-box, rgba(0, 0, 0, 0.6), 5, 0.0, 0, 1);"
                        + " -fx-background-color: #224957;");
                break;
              default:
                break;
            }
          }
        });
  }
}
