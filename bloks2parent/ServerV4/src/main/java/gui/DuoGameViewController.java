package gui;

import boards.BoardSpace;
import boards.GameTheme;
import datatypes.Coordinate;
import gamelogic.BlokusDuoGame;
import gamelogic.Player;
import java.io.IOException;
import java.util.HashSet;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import model.ChatMessage;
import model.Session;

/**
 * Controller for duoGameView.
 *
 * @author lfiebig
 */
public class DuoGameViewController {

  @FXML public Group fourSquaresPieceTwo;
  @FXML public Group oneSquarePieceOne;
  @FXML public GridPane boardView;
  @FXML public Text turnStatusText;
  @FXML public Text playerOne;
  @FXML public Text playerTwo;
  @FXML public Text scorePlayerOne;
  @FXML public Text scorePlayerTwo;
  @FXML public AnchorPane scoreBoard;
  @FXML public Pane gameTilePane;
  @FXML public Text finishedStatusText;
  @FXML public Text chatDisplay;
  @FXML public TextField chatTextField;
  @FXML public Button sendChatMessageButton;
  @FXML public ScrollPane scrollPane;
  @FXML public Pane chatPane;

  // every Boardspace from the gameboard
  private Node[][] gridPaneArray = null;

  // position of the mousecursor on the gameboard
  private int piecePositionRow;
  private int piecePositionColumn;

  private HashSet<BoardSpace> oldViableSpaces = new HashSet<>();

  // information from the Lobby
  private BlokusDuoGame gameSession;
  private Player player;

  // selected gameTile
  private Group selectedPiece;

  /** initializes attribute gripPaneArray. */
  public void initializeGridPaneArray() {
    this.gridPaneArray = new Node[14][14];
    for (Node node : this.boardView.getChildren()) {
      node.setStyle("-fx-border-width: 1.5; -fx-border-color: white;");
      if (GridPane.getRowIndex(node) != null && GridPane.getColumnIndex(node) != null) {
        this.gridPaneArray[GridPane.getRowIndex(node)][GridPane.getColumnIndex(node)] = node;
      }
    }
  }

  /** initializes the Scoreboard. */
  public void initializeScoreBoard() {
    for (Player p : this.gameSession.getPlayers()) {
      switch (p.getPlayerId()) {
        case 1:
          playerOne.setText(p.getUsername());
          scorePlayerOne.setText(p.getScore() + "");
          break;
        case 2:
          playerTwo.setText(p.getUsername());
          scorePlayerTwo.setText(p.getScore() + "");
          break;
        default:
          break;
      }
    }
  }

  /**
   * sets the information from the Lobby for the gameView.
   *
   * @param gameSession the Game object
   * @param player the Player object
   * @param theme the selected Theme of the User
   */
  public void setInfo(BlokusDuoGame gameSession, Player player, GameTheme theme) {
    this.gameSession = gameSession;
    this.player = player;
    this.player.setBoardTheme(theme);

    System.out.println(gameTilePane);
    for (Node n : gameTilePane.getChildren()) {
      Group group = (Group) n;
      for (Node box : group.getChildren()) {
        box.setStyle(
            "-fx-background-color: "
                + this.player.getBoardTheme().getColors()[player.getPlayerId() - 1]);
      }
    }
    changeTurnMessage();
    updateChatView();
  }

  /** changes the text of the FXML attribute turnStatusText. */
  public void changeTurnMessage() {
    Platform.runLater(
        () -> {
          if (player.isTurn()) {
            turnStatusText.setText("It's your turn. Place a gametile!");
          } else {
            turnStatusText.setText("Wait for the other players to finish their turns");
          }
        });
  }

  /**
   * Method is called when the user clicks on the "mirror" button. Visualises the mirrored position
   * of the selected gameTile on the gameBoard.
   */
  public void onMirror() {
    if (player.getSelectedGameTile() != null) {
      for (Coordinate c : player.getSelectedGameTile().getPosition()) {

        fillBoardSpace(c, piecePositionRow, piecePositionColumn, "null");
      }
      player.getSelectedGameTile().mirrorGameTile();
      oldViableSpaces = player.getViableSpaces();
      player.setViableSpaces(gameSession.calculateViableSpaces(player));
      showViableSpaces();
      for (Coordinate c : player.getSelectedGameTile().getPosition()) {
        fillBoardSpace(c, piecePositionRow, piecePositionColumn, "#838383");
      }
    }
  }

  /**
   * Method is called when the user clicks on a gameTile. Sets the selected gameTile of the Player
   * to the clicked gameTile.
   *
   * @param evt Event to get the clicked gameTile
   */
  public void onSelectPiece(Event evt) {
    Group selectedPiece = (Group) evt.getSource();
    this.selectedPiece = selectedPiece;
    player.setSelectedGameTile(selectedPiece.getId());
    System.out.println(player.getSelectedGameTile());
    oldViableSpaces = player.getViableSpaces();
    player.setViableSpaces(gameSession.calculateViableSpaces(player));
    showViableSpaces();
  }

  /**
   * Method is called whe the Mouse-cursor enters a BoardSpace.
   *
   * @param evt Event to get the entered BoardSpace
   */
  public void onTileEnteredSpace(Event evt) {
    if (player.getSelectedGameTile() != null) {
      Pane enteredSpace = (Pane) evt.getSource();
      int enteredRow = boardView.getRowIndex(enteredSpace);
      int enteredColumn = boardView.getColumnIndex(enteredSpace);
      for (Coordinate c : player.getSelectedGameTile().getPosition()) {
        fillBoardSpace(c, enteredRow, enteredColumn, "#838383");
      }
      piecePositionRow = enteredRow;
      piecePositionColumn = enteredColumn;
    }
  }

  /**
   * Method is called when the Mouse-cursor exits a BoardSpace.
   *
   * @param evt Event to get the exited BoardSpace
   */
  public void onTileExitedSpace(Event evt) {
    if (player.getSelectedGameTile() != null) {
      Pane exitedSpace = (Pane) evt.getSource();
      int exitedRow = boardView.getRowIndex(exitedSpace);
      int exitedColumn = boardView.getColumnIndex(exitedSpace);
      for (Coordinate c : player.getSelectedGameTile().getPosition()) {
        fillBoardSpace(c, exitedRow, exitedColumn, null);
      }
    }
    piecePositionRow = 1000;
    piecePositionColumn = 1000;
  }

  /**
   * Method is called when the user performs a right click. Visualises the rotated position of the
   * selected gameTile on the gameBoard.
   */
  public void onRotatePiece(Event evt) {
    if (player.getSelectedGameTile() != null) {
      for (Coordinate c : player.getSelectedGameTile().getPosition()) {

        fillBoardSpace(c, piecePositionRow, piecePositionColumn, "null");
      }
      player.getSelectedGameTile().rotateGameTile();
      oldViableSpaces = player.getViableSpaces();
      player.setViableSpaces(gameSession.calculateViableSpaces(player));
      showViableSpaces();
      for (Coordinate c : player.getSelectedGameTile().getPosition()) {
        fillBoardSpace(c, piecePositionRow, piecePositionColumn, "#838383");
      }
    }
    System.out.println(player.getViableSpaces());
  }

  /**
   * Method is called to paint in BoardSpaces to visualise the position of the selected gameTile on
   * the Board.
   *
   * @param c one part of the selected gameTile
   * @param row row of the BoardSpace
   * @param column column of the BoardSpace
   * @param color Color
   */
  private void fillBoardSpace(Coordinate c, int row, int column, String color) {
    if (row + (c.getXcoord()) >= 0
        && row + (c.getXcoord()) <= 13
        && column + c.getYcoord() >= 0
        && column + c.getYcoord() <= 13) {
      if (gridPaneArray[row + c.getXcoord()][column + c.getYcoord()].getId() == null) {
        gridPaneArray[row + c.getXcoord()][column + c.getYcoord()].setStyle(
            "-fx-background-color: " + color + "; -fx-border-width: 1.5; -fx-border-color: white");
      } else if (gridPaneArray[row + c.getXcoord()][column + c.getYcoord()].getId()
          == "viableSpace") {
        gridPaneArray[row + c.getXcoord()][column + c.getYcoord()].setStyle(
            "-fx-border-width: 3; -fx-border-color: red; -fx-background-color: " + color);
      }
    }
  }

  /** Marks the BoardSpaces the user can place his gameTile on. */
  public void showViableSpaces() {
    for (BoardSpace bc : oldViableSpaces) {
      gridPaneArray[bc.getPosition().getXcoord()][bc.getPosition().getYcoord()].setId(null);
      gridPaneArray[bc.getPosition().getXcoord()][bc.getPosition().getYcoord()].setStyle(
          "-fx-border-width: 1.5; -fx-border-color: white");
    }
    for (BoardSpace bc : player.getViableSpaces()) {
      gridPaneArray[bc.getPosition().getXcoord()][bc.getPosition().getYcoord()].setId(
          "viableSpace");
      gridPaneArray[bc.getPosition().getXcoord()][bc.getPosition().getYcoord()].setStyle(
          "-fx-border-width: 3; -fx-border-color: red");
    }
  }

  /**
   * Method is called when the player clicks on a BoardSpace. Places the selected gameTile on the
   * Board.
   *
   * @param evt Event to get the clicked on BoardSpace.
   */
  public void onTilePlaced(MouseEvent evt) {
    if (this.player.isTurn()) {
      Pane clickedSpace1 = (Pane) evt.getSource();
      System.out.println(clickedSpace1.getWidth());
      System.out.println(clickedSpace1.getHeight());

      if (player.getSelectedGameTile() != null && !player.getSelectedGameTile().isPlayed()) {
        if (evt.getButton() == MouseButton.PRIMARY) {
          Pane clickedSpace = (Pane) evt.getSource();
          int clickedRow = boardView.getRowIndex(clickedSpace);
          int clickedColumn = boardView.getColumnIndex(clickedSpace);
          if (player
              .getViableSpaces()
              .contains(gameSession.getBoard().getBoardSpaces()[clickedRow][clickedColumn])) {

            BoardSpace boardSpace =
                gameSession.getBoard().getBoardSpaces()[clickedRow][clickedColumn];

            Session.getMyUser()
                .getClient()
                .getWsClient()
                .sendTurnMessage(boardSpace, player.getSelectedGameTile());
            gameSession.gameTilePlaced(player, boardSpace);
            for (Coordinate c : player.getSelectedGameTile().getPosition()) {
              gridPaneArray[c.getXcoord()][c.getYcoord()].setId("coveredSpace");
              gridPaneArray[c.getXcoord()][c.getYcoord()].setStyle(
                  "-fx-background-color:"
                      + this.player.getBoardTheme().getColors()[player.getPlayerId() - 1]
                      + "; -fx-border-width: 1.5; -fx-border-color: white");
              player.deleteViableSpace(
                  gameSession.getBoard().getBoardSpaces()[c.getXcoord()][c.getYcoord()]);
            }
            player.getSelectedGameTile().resetGameTile();
            player.getSelectedGameTile().setPlayed(true);
            player.setSelectedGameTile("null");
            System.out.println("Tile placed");
            selectedPiece.setVisible(false);
            selectedPiece.setDisable(true);
            selectedPiece = null;
            for (BoardSpace bc : player.getViableSpaces()) {
              gridPaneArray[bc.getPosition().getXcoord()][bc.getPosition().getYcoord()].setStyle(
                  "-fx-border-color: null; -fx-border-width: 1.5; -fx-border-color: white");
            }
            this.player.setTurn(false);
            updateScoreboard();
            turnStatusText.setText("Wait for the other players to finish their turns");
          }
        }
      }
    }
  }

  /**
   * Method is called when the user clicks on the "Leave Game" button.
   *
   * @throws IOException when scene switch fails
   */
  public void onLeaveGame(Event evt) throws IOException {
    try {
      Session.getMyUser().getClient().getWsClient().sendMessage(743, "");
    } finally {
      Session.getMyUser().getClient().setWantsToDisconnect(true);
      Session.getSceneController().showMainMenuAfterGameLeave();
    }
  }

  /** Updates score of the user. */
  public void updateScoreboard() {
    switch (this.player.getPlayerId()) {
      case 1:
        scorePlayerOne.setText(player.getScore() + "");
        break;
      case 2:
        scorePlayerTwo.setText(player.getScore() + "");
        break;
      default:
        break;
    }
  }

  /** Method is called when the User clicks on the "No turns left" button. */
  public void onPlayerHasNoTurnsLeft() throws IOException {
    if (gameSession.checkHasTurnsLeft(player)) {
      turnStatusText.setText("You are not finished!!!!");
      turnStatusText.setStyle("-fx-fill: red");
    } else {
      player.setHasTurnsLeft(false);
      turnStatusText.setText("You are done, wait for the other players to finish!");
      turnStatusText.setText("");
      turnStatusText.setStyle("-fx-fill: red");

      Session.getMyUser().getClient().getWsClient().sendMessage(741, "");
    }
  }

  /** Updates the view of the Board to the current game state. */
  public void updateView() {
    Platform.runLater(
        () -> {
          int playerOneScore = 0;
          int playerTwoScore = 0;
          for (int i = 0; i < 14; i++) {
            for (int j = 0; j < 14; j++) {
              switch (this.gameSession.getBoard().getBoardSpaces()[i][j].getIsCoveredByWhom()) {
                case 1:
                  playerOneScore++;
                  gridPaneArray[i][j].setStyle(
                      "-fx-background-color: "
                          + this.player.getBoardTheme().getColors()[0]
                          + "; -fx-border-width: 1.5; -fx-border-color: white");
                  gridPaneArray[i][j].setId("coveredSpace");
                  break;
                case 2:
                  playerTwoScore++;
                  gridPaneArray[i][j].setStyle(
                      "-fx-background-color: "
                          + this.player.getBoardTheme().getColors()[1]
                          + "; -fx-border-width: 1.5; -fx-border-color: white");
                  gridPaneArray[i][j].setId("coveredSpace");
                  break;
                default:
                  break;
              }
              scorePlayerOne.setText(playerOneScore + "");
              scorePlayerTwo.setText(playerTwoScore + "");
            }
          }
          if (player.isTurn()) {
            turnStatusText.setText("It's your turn. Place a gametile!");
          } else {
            turnStatusText.setText("Wait for the other players to finish their turns");
          }
        });
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
}
