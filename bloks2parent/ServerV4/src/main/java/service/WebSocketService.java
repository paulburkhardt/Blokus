package service;

import boards.Board;
import gamelogic.BlokusDuoGame;
import gamelogic.BlokusJuniorGame;
import gamelogic.BlokusTrigonGame;
import gamelogic.ClassicBlokusGame;
import gamelogic.Game;
import gamelogic.GameHandler;
import gamelogic.Player;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import model.Lobby;
import model.Message;
import model.TurnMessage;
import model.User;
import websockets.WebSocketServer;

/**
 * Offers most of the relevant Methods for websocket management.
 *
 * @author lfiebig
 * @author myenler
 */
public class WebSocketService {

  /**
   * adds given user to a lobby on a WebSocketServer.
   *
   * @param session         Session of the client
   * @param username        given user
   * @param webSocketServer Server to get the lobby
   */
  public static void joinLobby701(
      javax.websocket.Session session, String username, WebSocketServer webSocketServer) {
    User user = new User(username);
    user.setSession(session);
    Lobby lobby = webSocketServer.getLobby();
    user.setLobby(lobby);
    lobby.addUser(user);
    System.out.println(
        "SERVER: JOIN LOBBY SEND joined message to all -- PORT:"
            + webSocketServer.getLobby().getPort());
  }

  /**
   * Adds AI player to a lobby on a WebSocketServer.
   *
   * @param username        username of the AI
   * @param difficulty      difficulty of the AI
   * @param webSocketServer Server to get the lobby
   */
  public static void createAiPlayer702(
      String username, int difficulty, WebSocketServer webSocketServer) {
    User user = new User(username);
    user.setDifficulty(difficulty);
    Lobby lobby = webSocketServer.getLobby();
    user.setLobby(lobby);
    lobby.addUser(user);
    broadCast(new Message(706, username), webSocketServer);
  }

  /**
   * Creates a lobby on a WebSocketServer.
   *
   * @param webSocketServer WebsocketServer where the lobby is created
   * @param playerAmount    player amount of the lobby
   * @param rounds          rounds of game modes to play in the lobby
   * @param port            port that the Server is running on. (often to get websocketServer
   *                        instance)
   */
  public static void initializeLobby703(
      WebSocketServer webSocketServer, int playerAmount, String rounds, int port) {
    Lobby lobby = new Lobby(port, playerAmount);
    webSocketServer.setLobby(lobby);
    lobby.setRounds(convertRoundsStringToGames(rounds));
  }

  /**
   * Is converting the rounds string to a Array List contain alls rounds as a Game instance.
   *
   * @param roundsString contaings all rounds as string
   * @return return array list with all game instances
   */
  public static ArrayList<Game> convertRoundsStringToGames(String roundsString) {
    String[] roundsArray = roundsString.split(":");
    ArrayList<Game> rounds = new ArrayList<>();
    for (String s : roundsArray) {
      // ändere die bezeichnungen auf DUO....
      switch (s) {
        case "CLASSIC":
          rounds.add(new ClassicBlokusGame());
          break;
        case "TRIGON":
          rounds.add(new BlokusTrigonGame());
          break;
        case "DUO":
          rounds.add(new BlokusDuoGame());
          break;
        case "JUNIOR":
          rounds.add(new BlokusJuniorGame());
          break;
        default:
          break;
      }
    }
    return rounds;
  }

  /**
   * Handler, when Server receives a Message by Client to start a Game.
   *
   * @param webSocketServer Instance that contains and manages all necessary attributes
   * @param session         Client session
   * @throws EncodeException Exception when encoding
   * @throws IOException     IO/Frontend Exception getting throwed
   */
  public static void startGame704(WebSocketServer webSocketServer, Session session)
      throws EncodeException, IOException {
    webSocketServer.setGameHandler(new GameHandler(webSocketServer.getLobby()));
    if (!webSocketServer.getGameHandler().setUpAndStartGame()) { // when no more rounds left, do if:
      System.out.println("party finished");
      broadCast(new Message(743, ""), webSocketServer);
      return;
    }
    ArrayList<Player> playerList = webSocketServer.getGameHandler().getGame().getPlayers();
    Game game = webSocketServer.getGameHandler().getGame();
    StringBuffer playerListString = new StringBuffer("");
    for (Player p : playerList) {
      if (playerListString.toString().equals("")) {
        playerListString.append(p.getUsername());
      } else {
        playerListString.append(":" + p.getUsername());
      }
    }
    Message message = new Message(704, game.getClass() + "," + playerListString.toString());
    broadCast(message, webSocketServer);
    WebSocketService.sendItsYourTurnMessage730(session);
  }

  /**
   * Handler for when Clients needs to get a Message with information to show Leaderboard.
   *
   * @param port  port that the Server is running on. (often to get websocketServer instance)
   * @param stats stats of all Players
   */
  public static void showLeaderBoard742(int port, HashMap<String, Integer> stats) {
    WebSocketServer webSocketServer = ServerService.getServerViaPort(port);
    Game game = webSocketServer.getGameHandler().getGame();

    StringBuffer leaderboardString = new StringBuffer("");
    for (Map.Entry<String, Integer> statsEntry : stats.entrySet()) {
      if (leaderboardString.toString().equals("")) {
        leaderboardString.append(statsEntry.getKey() + ":" + statsEntry.getValue());
      } else {
        leaderboardString.append("," + statsEntry.getKey() + ":" + statsEntry.getValue());
      }
    }
    Message message = new Message(742, leaderboardString.toString());
    broadCast(message, webSocketServer);
  }

  /**
   * Message, if the host is pressing the next Game button after a finished Game. Is handling that.
   *
   * @param webSocketServer Instance that contains and manages all necessary attributes
   * @param session         Client session
   */
  public static void nextRound705(WebSocketServer webSocketServer, Session session) {
    if (!webSocketServer.getGameHandler().setUpAndStartGame()) { // when no more rounds left, do if:
      // send game finished message with score
      Message message = new Message(740, webSocketServer.getGameHandler().getScores());
      broadCast(message, webSocketServer);
    }
  }

  /**
   * Does send the current game state (board of GameHandler at the Server side).
   *
   * @param port       port that the Server is running on. (often to get websocketServer instance)
   * @param session    Client session
   * @param toEveryone flag if it should be broadcast to everyone or to everyone expects one user
   * @throws EncodeException Encode Exception
   * @throws IOException     Frontend Exception
   */
  public static void sendGameState707(int port, Session session, boolean toEveryone)
      throws EncodeException, IOException {
    WebSocketServer webSocketServer =
        ServerService.getServerViaPort(port); // TODO Können auch über Session auf webSS zugreifen
    Board board = webSocketServer.getGameHandler().getGame().getBoard();
    // board is too large to send as a whole via a network connection with websocket messages.
    // Therefore we are splitting the JsonString in peaces with a length of 10000 characters
    String[] boardPeaces = splitBoardIntoJsonString(board);
    if (toEveryone) {
      for (String boardStringPeace : boardPeaces) {
        broadCast(new Message(707, boardStringPeace), webSocketServer);
      }
    } else {
      for (String boardStringPeace : boardPeaces) {
        broadCastWithoutOneSession(new Message(707, boardStringPeace),
            webSocketServer, session);
      }
    }
  }

  /**
   * Does split the big Json String that represents the board into smaller pieces (not peaces).
   *
   * @param board current board on server side
   * @return returns a String array of board peaces represented as a string
   */
  public static String[] splitBoardIntoJsonString(Board board) {
    String boardWholeJsonString = board.serializeBoard();
    int pieces = 1 + (boardWholeJsonString.length() / 200);
    String[] boardStringPieces = new String[pieces];
    int i = 0;
    int j = 0;
    for (; i + 200 < boardWholeJsonString.length(); i = i + 200) {
      boardStringPieces[j] = "Peace" + j + ":" + boardWholeJsonString.substring(i, i + 200);
      j++;
    }
    boardStringPieces[j] =
        "Peace"
            + "L"
            + ":"
            + boardWholeJsonString.substring(
            i, boardWholeJsonString.length()); // l does mean last-peace

    return boardStringPieces;
  }

  /**
   * Handler if leave Lobby message is received by a Client. Does send a Message to all Clients.
   *
   * @param session         Session of Client that is leaving
   * @param webSocketServer Instance that contains and manages all necessary attributes
   * @throws IOException Frontend Exception
   */
  public static void leaveLobby708(Session session, WebSocketServer webSocketServer)
      throws IOException {
    broadCastWithoutOneSession(
        new Message(708, webSocketServer.getLobby().getUsernameBySession(session)),
        webSocketServer,
        session);
    webSocketServer.deleteSession(session);
    session.close();
  }

  /**
   * If Lobby/Server rejects a join Message by a Client.
   *
   * @param session   Session of client that is getting rejected
   * @param reasoning reason of rejection
   * @throws EncodeException Encode Exception when encoding
   * @throws IOException     Frontend Exception
   */
  public static void rejectedByLobbyMessage709(Session session, String reasoning)
      throws EncodeException, IOException {
    session.getBasicRemote().sendObject(new Message(709, reasoning));
  }

  /**
   * Send that its your turn message to a Client.
   *
   * @param session Client session
   * @throws EncodeException Encode Exception when encoding
   * @throws IOException     Frontend Exception
   */
  public static void sendItsYourTurnMessage730(Session session)
      throws EncodeException, IOException {
    System.out.println("send its your turn message to: " + session);
    session.getBasicRemote().sendObject(new Message(730));
  }

  /**
   * When Server is receiving a turn this Method is handling the game and lobby.
   *
   * @param turnMessage     Message that contains turn information
   * @param webSocketServer Instance that contains and manages all necessary attributes
   * @param session         Client session that did the turn
   * @throws EncodeException Encode Exception when encoding
   * @throws IOException     Frontend Exception
   */
  public static void receiveTurn731(
      TurnMessage turnMessage, WebSocketServer webSocketServer, Session session)
      throws EncodeException, IOException {
    String username = webSocketServer.getLobby().getUsernameBySession(session);
    int playerId = webSocketServer.getGameHandler().getGame().getPlayerIdByUsername(username);
    if (playerId == -1) {
      // send error message (runtime unexpected error)
    }
    webSocketServer
        .getGameHandler()
        .manageTurn(playerId, turnMessage.getGameTile(), turnMessage.getBoardSpace(), session);
  }

  /**
   * When lobby should be deleted this method is handling the received message.
   *
   * @param webSocketServer Instance that contains and manages all necessary attributes
   * @param session         Session of Client that send the delete message
   */
  public static void deleteLobby743(WebSocketServer webSocketServer, Session session) {
    broadCast(new Message(743, ""), webSocketServer);
    int port = webSocketServer.getLobby().getPort();
    ServerService.deleteServer(port);
    webSocketServer.stopServer();
  }

  /**
   * Sets that a Player/Session is not on the move at the moment.
   *
   * @param websocketServer Instance that contains and manages all necessary attributes
   * @param session         Session of Client
   * @throws EncodeException Encode Exception when encoding
   * @throws IOException     Frontend Exception
   */
  public static void setPlayerisDone741(WebSocketServer websocketServer, Session session)
      throws EncodeException, IOException {
    String username = websocketServer.getLobby().getUsernameBySession(session);
    int playerId = websocketServer.getGameHandler().getGame().getPlayerIdByUsername(username);
    websocketServer.getGameHandler().setPlayerIsDone(playerId, session);
  }

  /**
   * Broadcast a message to all connected Sessions.
   *
   * @param message         the message to broadcast
   * @param webSocketServer Instance that contains and manages all necessary attributes
   */
  public static void broadCast(Message message, WebSocketServer webSocketServer) {
    for (Session s : webSocketServer.getConnectedSessions()) {
      try {
        s.getBasicRemote().sendObject(message);
      } catch (IOException | EncodeException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Broadcast to all connected Session, but one Session.
   *
   * @param message         message to braodcast
   * @param webSocketServer Instance that contains and manages all necessary attributes
   * @param leftOutSession  left out session
   */
  public static void broadCastWithoutOneSession(
      Message message, WebSocketServer webSocketServer, Session leftOutSession) {
    for (Session s : webSocketServer.getConnectedSessions()) {
      if (s.equals(leftOutSession)) {
        continue;
      }
      try {
        s.getBasicRemote().sendObject(message);
      } catch (IOException | EncodeException e) {
        e.printStackTrace();
      }
    }
  }
}
