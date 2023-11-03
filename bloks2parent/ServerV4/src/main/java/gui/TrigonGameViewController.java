package gui;

import boards.BoardSpace;
import boards.GameTheme;
import datatypes.Coordinate;
import gamelogic.BlokusTrigonGame;
import gamelogic.Player;
import java.io.IOException;
import java.util.HashMap;
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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import model.ChatMessage;
import model.Session;

/**
 * Controller for TrigonGameView.
 *
 * @author pburkhar
 */
public class TrigonGameViewController {

  @FXML
  public Group boardView;
  @FXML
  public Text playerOne;
  @FXML
  public Text playerTwo;
  @FXML
  public Text playerThree;
  @FXML
  public Text playerFour;
  @FXML
  public Text scorePlayerOne;
  @FXML
  public Text scorePlayerTwo;
  @FXML
  public Text scorePlayerThree;
  @FXML
  public Text scorePlayerFour;
  @FXML
  public AnchorPane scoreBoard;
  @FXML
  public Pane gameTilePane;
  @FXML
  public Text turnStatusText;
  @FXML
  public Text finishedStatusText;

  @FXML
  public Text chatDisplay;
  @FXML
  public TextField chatTextField;
  @FXML
  public Button sendChatMessageButton;
  @FXML
  public ScrollPane scrollPane;
  @FXML
  public Pane chatPane;

  private HashMap<Polygon, Coordinate> gridPane = new HashMap<>();
  private Polygon[][] gridPaneArray = null;
  private int piecePositionRow;
  private int piecePositionColumn;
  private HashSet<BoardSpace> oldViableSpaces = new HashSet<>();
  private BlokusTrigonGame gameSession;
  private Player player;
  private Color strokeColor;

  private Polygon trash = new Polygon();

  private Group selectedPiece;

  /**
   * initializes the gridPane with the Polygons.
   */
  public void initializeGridPaneArray() {
    strokeColor = player.getBoardTheme().equals(GameTheme.ARCTIC) ? Color.BLACK : Color.WHITE;
    this.gridPaneArray = new Polygon[35][18];
    int limit = 8;
    int x = limit;
    int y = 17;
    for (Node node : this.boardView.getChildren()) {
      if (x > 34 - (limit >= 0 ? limit : (limit * -1) - 1)) {
        limit--;
        x = limit >= 0 ? limit : (limit * -1) - 1;
        y--;
      }
      this.gridPaneArray[x][y] = (Polygon) node;
      ((Polygon) node).setFill(Color.valueOf(player.getBoardTheme().getColors()[4]));
      ((Polygon) node).setStroke(strokeColor);
      ((Polygon) node).setStrokeWidth(1);
      gridPane.put((Polygon) node, new Coordinate(x, y));
      x++;
    }
  }

  /**
   * initializes the gameboard.
   */
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
        case 3:
          playerThree.setText(p.getUsername());
          scorePlayerThree.setText(p.getScore() + "");
          break;
        case 4:
          playerFour.setText(p.getUsername());
          scorePlayerFour.setText(p.getScore() + "");
          break;
        default:
      }
    }
  }

  /**
   * updates all Info like chat.
   *
   * @param gameSession BlokusTrigonGame gamesession
   * @param player      Player playing
   * @param theme       Theme of the player
   */
  public void setInfo(BlokusTrigonGame gameSession, Player player, GameTheme theme) {
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

  /**
   * changes the message if it's your turn.
   */
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
   * selects piece when clicking on a piece.
   *
   * @param evt Event triggering
   */
  public void onSelectPiece(Event evt) {
    Group selectedPiece = (Group) evt.getSource();
    this.selectedPiece = selectedPiece;
    System.out.println(selectedPiece.getId());
    player.setSelectedGameTile(selectedPiece.getId());
    System.out.println(player.getSelectedGameTile());
    oldViableSpaces = player.getViableSpaces();
    player.setViableSpaces(gameSession.calculateViableSpaces(player));
    showViableSpaces();
  }

  /**
   * shows the viable Spaces in red.
   */
  public void showViableSpaces() {
    for (BoardSpace bc : oldViableSpaces) {
      gridPaneArray[bc.getPosition().trigonX()][bc.getPosition().trigonY()].setId(null);
      gridPaneArray[bc.getPosition().trigonX()][bc.getPosition().trigonY()].setStrokeWidth(1);
      gridPaneArray[bc.getPosition().trigonX()][bc.getPosition().trigonY()].setStroke(strokeColor);
    }
    for (BoardSpace bc : player.getViableSpaces()) {
      gridPaneArray[bc.getPosition().trigonX()][bc.getPosition().trigonY()].setId("viableSpace");
      gridPaneArray[bc.getPosition().trigonX()][bc.getPosition().trigonY()].setStrokeWidth(2);
      gridPaneArray[bc.getPosition().trigonX()][bc.getPosition().trigonY()].setStroke(Color.RED);
    }
  }

  /**
   * When entering the board the GameTile is shwon in grey.
   *
   * @param evt Mouse entering the Polygon
   */
  public void onTileEnteredSpace(Event evt) {
    if (player.getSelectedGameTile() != null) {
      Polygon enteredSpace = (Polygon) evt.getSource();
      Coordinate enteredCoordinate = gridPane.get(enteredSpace);
      System.out.println(
          gameSession
              .getBoard()
              .getBoardSpaces()[enteredCoordinate.getXcoord()][enteredCoordinate.getYcoord()]
              .getIsBlockedBy());
      boolean flag =
          (enteredCoordinate.getOrientation()
              == this.player.getSelectedGameTile().getPosition().get(0).getOrientation());
      for (Coordinate c : player.getSelectedGameTile().getPosition()) {
        if (flag) {
          fillBoardSpace(c, enteredCoordinate.getXcoord(), enteredCoordinate.getYcoord(),
              "#838383");
        }
      }
      piecePositionRow = enteredCoordinate.getXcoord();
      piecePositionColumn = enteredCoordinate.getYcoord();
    }
  }

  private void fillBoardSpace(Coordinate c, int row, int column, String color) {
    if (row + (c.getXcoord()) >= 0
        && row + (c.getXcoord()) < 35
        && column + c.getYcoord() >= 0
        && column + c.getYcoord() < 18
        && gridPaneArray[row + c.getXcoord()][column + c.getYcoord()] != null) {
      if (gridPaneArray[row + c.getXcoord()][column + c.getYcoord()].getId() == null) {
        gridPaneArray[row + c.getXcoord()][column + c.getYcoord()].setFill(Color.valueOf(color));
        gridPaneArray[row + c.getXcoord()][column + c.getYcoord()].setStroke(strokeColor);
        gridPaneArray[row + c.getXcoord()][column + c.getYcoord()].setStrokeWidth(1);
      } else if (gridPaneArray[row + c.getXcoord()][column + c.getYcoord()].getId()
          .equals("viableSpace")) {
        gridPaneArray[row + c.getXcoord()][column + c.getYcoord()].setFill(Color.valueOf(color));
        gridPaneArray[row + c.getXcoord()][column + c.getYcoord()].setStroke(Color.RED);
        gridPaneArray[row + c.getXcoord()][column + c.getYcoord()].setStrokeWidth(2);
      }
    }
  }

  /**
   * removes grey color when moving mouse.
   *
   * @param evt mouse leaving Polygon
   */
  public void onTileExitedSpace(Event evt) {
    if (player.getSelectedGameTile() != null) {
      Polygon exitedSpace = (Polygon) evt.getSource();
      Coordinate exitedCoordinate = gridPane.get(exitedSpace);
      for (Coordinate c : player.getSelectedGameTile().getPosition()) {
        fillBoardSpace(
            c,
            exitedCoordinate.getXcoord(),
            exitedCoordinate.getYcoord(),
            this.player.getBoardTheme().getColors()[4]);
      }
    }
    piecePositionRow = 1000;
    piecePositionColumn = 1000;
  }

  /**
   * rotates the selected piece when right click is performed.
   *
   * @param evt right clicking with mouse.
   */
  public void onRotatePiece(Event evt) {
    if (player.getSelectedGameTile() != null) {
      for (Coordinate c : player.getSelectedGameTile().getPosition()) {
        fillBoardSpace(
            c, piecePositionRow, piecePositionColumn, this.player.getBoardTheme().getColors()[4]);
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
   * Places GameTile when clicking on valid Coordinate.
   *
   * @param evt clicking on valid Polygon
   */
  public void onTilePlaced(MouseEvent evt) {
    if (player.isTurn()) {
      Polygon clickedSpace1 = (Polygon) evt.getSource();

      if (player.getSelectedGameTile() != null && !player.getSelectedGameTile().isPlayed()) {
        if (evt.getButton() == MouseButton.PRIMARY) {
          Polygon clickedSpace = (Polygon) evt.getSource();
          Coordinate clickedCoordinate = gridPane.get(clickedSpace);
          if (player
              .getViableSpaces()
              .contains(
                  gameSession.getBoard()
                      .getBoardSpaces()[clickedCoordinate.getXcoord()]
                      [clickedCoordinate.getYcoord()])) {
            BoardSpace boardSpace =
                gameSession.getBoard()
                    .getBoardSpaces()[clickedCoordinate.getXcoord()]
                    [clickedCoordinate.getYcoord()];
            Session.getMyUser()
                .getClient()
                .getWsClient()
                .sendTurnMessage(boardSpace, player.getSelectedGameTile());
            gameSession.gameTilePlaced(player, boardSpace);
            for (Coordinate c : player.getSelectedGameTile().getPosition()) {
              gridPaneArray[c.trigonX()][c.trigonY()].setId("coveredSpace");
              gridPaneArray[c.trigonX()][c.trigonY()].setFill(
                  Color.valueOf(this.player.getBoardTheme().getColors()[player.getPlayerId() - 1]));
              gridPaneArray[c.trigonX()][c.trigonY()].setStroke(strokeColor);
              gridPaneArray[c.trigonX()][c.trigonY()].setStrokeWidth(1);
              gridPaneArray[c.trigonX()][c.trigonY()] = this.trash;
              player.deleteViableSpace(
                  gameSession.getBoard().getBoardSpaces()[c.trigonX()][c.trigonY()]);
            }
            player.getSelectedGameTile().resetGameTile();
            player.getSelectedGameTile().setPlayed(true);
            player.setSelectedGameTile("null");
            System.out.println("Tile placed");
            selectedPiece.setVisible(false);
            selectedPiece.setDisable(true);
            selectedPiece = null;
            for (BoardSpace bc : player.getViableSpaces()) {
              gridPaneArray[bc.getPosition().trigonX()][bc.getPosition().trigonY()].setStroke(
                  strokeColor);
              gridPaneArray[bc.getPosition().trigonX()][bc.getPosition().trigonY()].setStrokeWidth(
                  1);
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
   * ensures that the mirror button works.
   */
  public void onMirror() {
    if (player.getSelectedGameTile() != null) {
      for (Coordinate c : player.getSelectedGameTile().getPosition()) {
        fillBoardSpace(c, piecePositionRow, piecePositionColumn, "#093545");
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
   * handles the player leaving the GameSession.
   *
   * @param evt not used
   * @throws IOException exception
   */
  public void onLeaveGame(Event evt) throws IOException {
    try {
      Session.getMyUser()
          .getClient()
          .getWsClient()
          .sendMessage(743, "");
    } finally {
      Session.getMyUser().getClient().setWantsToDisconnect(true);
      Session.getSceneController().showMainMenuAfterGameLeave();
    }
  }

  /**
   * updates the scoreboard on the current status.
   */
  public void updateScoreboard() {
    switch (this.player.getPlayerId()) {
      case 1:
        scorePlayerOne.setText(player.getScore() + "");
        break;
      case 2:
        scorePlayerTwo.setText(player.getScore() + "");
        break;
      case 3:
        scorePlayerThree.setText(player.getScore() + "");
        break;
      case 4:
        scorePlayerFour.setText(player.getScore() + "");
        break;
      default:
    }
  }

  /**
   * handles the noTurnsLeft Button.
   */
  public void onPlayerHasNoTurnsLeft() {
    if (gameSession.checkHasTurnsLeft(player)) {
      finishedStatusText.setText("You are not finished!!!!");
      finishedStatusText.setStyle("-fx-fill: red");
    } else {
      player.setHasTurnsLeft(false);
      finishedStatusText.setText("You are done, wait for the other players to finish!");
      turnStatusText.setText("");
      finishedStatusText.setStyle("-fx-fill: red");

      Session.getMyUser().getClient().getWsClient().sendMessage(741, "");
    }
  }

  /**
   * updates view when other Players did their turn.
   */
  public void updateView() {
    Platform.runLater(
        () -> {
          int playerOneScore = 0;
          int playerTwoScore = 0;
          int playerThreeScore = 0;
          int playerFourScore = 0;
          for (int i = 0; i < 35; i++) {
            for (int j = 0; j < 18; j++) {
              try {
                switch (this.gameSession.getBoard().getBoardSpaces()[i][j].getIsCoveredByWhom()) {
                  case 1:
                    playerOneScore++;
                    gridPaneArray[i][j].setFill(
                        Color.valueOf(this.player.getBoardTheme().getColors()[0]));
                    gridPaneArray[i][j].setStroke(strokeColor);
                    gridPaneArray[i][j].setStrokeWidth(1);
                    gridPaneArray[i][j].setId("coveredSpace");
                    break;
                  case 2:
                    playerTwoScore++;
                    gridPaneArray[i][j].setFill(
                        Color.valueOf(this.player.getBoardTheme().getColors()[1]));
                    gridPaneArray[i][j].setStroke(strokeColor);
                    gridPaneArray[i][j].setStrokeWidth(1);
                    gridPaneArray[i][j].setId("coveredSpace");
                    break;
                  case 3:
                    playerThreeScore++;
                    gridPaneArray[i][j].setFill(
                        Color.valueOf(this.player.getBoardTheme().getColors()[2]));
                    gridPaneArray[i][j].setStroke(strokeColor);
                    gridPaneArray[i][j].setStrokeWidth(1);
                    gridPaneArray[i][j].setId("coveredSpace");
                    break;
                  case 4:
                    playerFourScore++;
                    gridPaneArray[i][j].setFill(
                        Color.valueOf(this.player.getBoardTheme().getColors()[3]));
                    gridPaneArray[i][j].setStroke(strokeColor);
                    gridPaneArray[i][j].setStrokeWidth(1);
                    gridPaneArray[i][j].setId("coveredSpace");
                    break;
                  default:
                }
              } catch (Exception e) {
                System.out.println(e.getMessage());
              }
              scorePlayerOne.setText(playerOneScore + "");
              scorePlayerTwo.setText(playerTwoScore + "");
              scorePlayerThree.setText(playerThreeScore + "");
              scorePlayerFour.setText(playerFourScore + "");
            }
          }
          if (player.isTurn()) {
            turnStatusText.setText("It's your turn. Place a gametile!");
          } else {
            turnStatusText.setText("Wait for the other players to finish their turns");
          }
        });
  }

  /**
   * handles chat.
   */
  public void onSend() {
    if (!chatTextField.getText().equals("")) {
      String messageContent = chatTextField.getText();
      Session.getMyUser()
          .getClient()
          .getWsClient()
          .sendChatMessage(Session.getMyUser().getUsername(), messageContent);
    }
  }

  /**
   * ensures that messages are received.
   */
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

