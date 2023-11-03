package websockets;

import boards.Board;
import boards.ClassicBoard;
import boards.GameTheme;
import boards.TrigonBoard;
import boards.TwoPlayerBoard;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gamelogic.BlokusDuoGame;
import gamelogic.BlokusJuniorGame;
import gamelogic.BlokusTrigonGame;
import gamelogic.ClassicBlokusGame;
import gamelogic.Player;
import java.io.IOException;
import javafx.event.ActionEvent;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import model.ChatMessage;
import model.Lobby;
import model.Message;
import model.User;

/**
 * Websocket client endpoint.
 *
 * @author jbuechs
 * @author lfiebig
 * @author myenler
 */

// define encoder and decoder
@javax.websocket.ClientEndpoint(
    encoders = {ChatMessageEncoder.class, TurnMessageEncoder.class, MessageEncoder.class},
    decoders = MessageDecoder.class)
public class ClientEndpoint {

  /**
   * Defines what happens when new sesssion is opened.
   *
   * @param session opened session
   */
  @OnOpen
  public void onOpen(Session session) {
    System.out.println("Connected ... " + session.getId());
  }

  /**
   * Message has been received. For testing print line.
   *
   * @param message received Message
   */
  @OnMessage
  public void onMessage(Message message) throws IOException {
    if (message.status != 707) {
      System.out.println(
          "CLIENT--  STATUS: " + message.getStatus() + "   ||CONTENTN: " + message.getContent());

    } else {
      if (message.content.startsWith("PeaceL:")) {
        System.out.println(
            "CLIENT--  STATUS: " + message.getStatus() + "   ||CONTENTN: " + message.getContent());
      }
    }
    int status = message.getStatus();
    model.Session.getGame();
    switch (status) {
      case 701:
        lobbyJoinedMessage(message);
        break;
      case 704:
        parseStartGameMessage(message);
        break;
      case 706:
        someoneJoinedMessage(message.getContent());
        break;
      case 707:
        handleBoardStringPeaces707(message.getContent());
        break;
      case 708:
        model.Session.getMyUser().getLobby().deleteUserByUsername(message.getContent());
        model.Session.getSceneController().updatePlayerView();
        break;
      case 709:
        gotRejectedByLobby709();
        break;
      case 710:
        model.Session.getMyUser().getLobby().addChatMessage((ChatMessage) message);
        model.Session.getSceneController().updateChatView();
        break;

      case 730:
        model.Session.getGame()
            .getPlayerByUsername(model.Session.getMyUser().getUsername())
            .setTurn(true);
        model.Session.getSceneController().changeTurnMessage();
        break;
      case 742:
        model.Session.getSceneController().showLeaderBoard(message.getContent());
        break;
      case 743:
        try {
          model.Session.getMyUser().getClient().setWantsToDisconnect(true);
          model.Session.getMyUser().setLobby(new Lobby(1));
        } finally {
          model.Session.getSceneController().showMainMenuAfterGameLeave();
        }
        break;
      default:
        break;
    }
  }

  /**
   * Defines what happens when new the session is closed.
   *
   * @param closeReason reason, why session was closed
   */
  @OnClose
  public void onClose(CloseReason closeReason) {
    System.out.println("Session was closed");
    System.out.println(
        "CLose Reason: "
            + closeReason.getCloseCode()
            + "\n"
            + "phrase: "
            + closeReason.getReasonPhrase());
    User myUser = model.Session.getMyUser();

    model.Session.getGame().getPlayerByUsername(myUser.getUsername());
    if (!model.Session.getMyUser().getClient().isWantsToDisconnect()) {
      myUser
          .getClient()
          .getWsClient()
          .connectToServer(myUser.getLobby().getPort(), myUser.getClient().getIpAdress());
      myUser
          .getClient()
          .getWsClient()
          .sendMessage(744, myUser.getLobby().getPort() + "," + myUser.getUsername());
    }
  }

  /**
   * Parses received 701 message.
   *
   * @param message received message
   */
  public void lobbyJoinedMessage(Message message) {
    if (message.getContent().isBlank()) {
      System.out.println("IS BLANK");
      return;
    }
    model.Session.getMyUser().getLobby().getUserList().clear();
    String[] users = message.getContent().split(":");
    for (String username : users) {
      model.Session.getMyUser().getLobby().addUser(new User(username));
    }
    model.Session.getMyUser().getLobby().addUser(model.Session.getMyUser());
    System.out.println("SOLLTE ANSICHT AUF LOBBY VIEW WECHSELN");
    model.Session.getSceneController().showLobbyView();
  }

  /**
   * parses received 704 message.
   *
   * @param message received message
   * @throws IOException when scene loading fails
   */
  public void parseStartGameMessage(Message message) throws IOException {
    String[] content = message.getContent().split(",");
    String gamemode = "";
    switch (content[0]) {
      case "class gamelogic.ClassicBlokusGame":
        gamemode = "CLASSIC";
        model.Session.setGame(new ClassicBlokusGame());
        break;
      case "class gamelogic.BlokusTrigonGame":
        gamemode = "TRIGON";
        model.Session.setGame(new BlokusTrigonGame());
        break;
      case "class gamelogic.BlokusDuoGame":
        gamemode = "DUO";
        model.Session.setGame(new BlokusDuoGame());
        break;
      case "class gamelogic.BlokusJuniorGame":
        gamemode = "JUNIOR";
        model.Session.setGame(new BlokusJuniorGame());
        break;
      default:
        break;
    }
    String[] playerList = content[1].split(":");
    for (int i = 1; i <= playerList.length; i++) {
      Player p = new Player(playerList[i - 1], GameTheme.NEON);
      p.setPlayerId(i);
      model.Session.getGame().addPlayer(p);
    }
    model.Session.getGame().setInfo();
    model.Session.getSceneController().showGameView(model.Session.getGame().getGameMode());
  }

  /**
   * handles received 707 Message.
   *
   * @param boardPeace received message String
   * @throws JsonProcessingException when Json(de-)serialising failed
   */
  public void handleBoardStringPeaces707(String boardPeace) throws JsonProcessingException {
    User user = model.Session.getMyUser();
    user.setBoardString(user.getBoardString() + boardPeace.substring(boardPeace.indexOf(":") + 1));
    if (boardPeace.startsWith("Peace" + "L" + ":")) {
      updateOwnGameState(user.getBoardString());
      user.setBoardString("");
    }
  }

  /**
   * Updates local state of the game board.
   *
   * @param boardString current game board
   * @throws JsonProcessingException when Json(de-)serialising failed
   */
  public void updateOwnGameState(String boardString) throws JsonProcessingException {
    model.Session.getGame().setBoard(handleReceivedBoard707(boardString));
    model.Session.getSceneController().updateGameView(model.Session.getGame().getGameMode());
  }

  /**
   * handles received 707 message.
   *
   * @param boardString received board state
   * @return created board
   * @throws JsonProcessingException when Json(de-)serialising failed
   */
  public Board handleReceivedBoard707(String boardString) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    Board board = null;
    if (model.Session.getGame().getBoard() instanceof ClassicBoard) {
      board = objectMapper.readValue(boardString, ClassicBoard.class);
    } else if ((model.Session.getGame().getBoard() instanceof TwoPlayerBoard)) {
      board = objectMapper.readValue(boardString, TwoPlayerBoard.class); // exception 12971
    } else if ((model.Session.getGame().getBoard() instanceof TrigonBoard)) {
      board = objectMapper.readValue(boardString, TrigonBoard.class);
    }
    return board;
  }

  /**
   * handles received 709 message.
   *
   * @throws IOException when scene switch fails
   */
  public void gotRejectedByLobby709() throws IOException {
    model.Session.getMyUser().setLobby(null);
    model.Session.getSceneController().showMainMenu(new ActionEvent());
  }

  /**
   * handles received 706 message.
   *
   * @param username username of player who joined the game
   */
  public void someoneJoinedMessage(String username) {
    model.Session.getMyUser().getLobby().addUser(new User(username));
    model.Session.getSceneController().updatePlayerView();
  }
}
