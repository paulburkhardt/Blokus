package websockets;

import static service.WebSocketService.broadCastWithoutOneSession;

import boards.Board;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import model.Message;
import model.TurnMessage;
import model.User;
import service.ServerService;
import service.WebSocketService;

/**
 * Websocket server endpoint.
 *
 * @author jbuechs
 * @author lfiebig
 * @author myenler
 */

// define encoder and decoder
@javax.websocket.server.ServerEndpoint(
    value = "/chat",
    encoders = {ChatMessageEncoder.class, TurnMessageEncoder.class, MessageEncoder.class},
    decoders = MessageDecoder.class)
public class ServerEndpoint {

  private WebSocketService webSocketService = new WebSocketService();
  static Set<Session> clientSessions = Collections.synchronizedSet(new HashSet<>());
  static Board board;

  /**
   * Defines what happens when new sesssion connects with the server.
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
  public void onMessage(Message message, Session session) throws EncodeException, IOException {
    System.out.println(message.status + ": " + message.content);
    WebSocketServer webSocketServer = ServerService.getServerViaSession(session);
    int status = message.getStatus();
    switch (status) {
      case 700:
        break;
      case 710:
        WebSocketService.broadCast(message, webSocketServer);
        break;
      case 701:
        if (!parseJoinLobby(message, session)) {
          WebSocketService.rejectedByLobbyMessage709(
              session, "You can not join the lobby, because lobby is already full");
          webSocketServer.deleteSession(session);
        } else {
          webSocketServer = ServerService.getServerViaSession(session);
          if (!(webSocketServer.getLobby().getPort() == 8020)) {
            System.out.println("WICHTIG- sendet join message an joinende Session");
            String usersss = webSocketServer.getLobby().getUserListAsStringExcludeSession(session);
            Message message1 = new Message(701, usersss);
            session.getBasicRemote().sendObject(message1);
          }
          if (!(webSocketServer.getLobby().getPort() == 8020)) {
            broadCastWithoutOneSession(
                new Message(706, webSocketServer.getLobby().getUsernameBySession(session)),
                webSocketServer,
                session);
          }
        }
        break;
      case 702:
        parseCreateAi(message, webSocketServer);
        break;
      case 703:
        parseInitializeLobby(message);
        break;
      case 704:
        WebSocketService.startGame704(webSocketServer, session);
        break;
      case 705:
        WebSocketService.nextRound705(webSocketServer, session);
        break;
      case 708:
        WebSocketService.leaveLobby708(session, webSocketServer);
        break;
      case 731:
        System.out.println(
            "-- Server: received turn: "
                + webSocketServer.getLobby().getUsernameBySession(session));
        WebSocketService.receiveTurn731((TurnMessage) message, webSocketServer, session);
        break;
      case 741:
        WebSocketService.setPlayerisDone741(webSocketServer, session);
        break;
      case 743:
        WebSocketService.deleteLobby743(webSocketServer, session);
        break;
      case 744:
        String[] contents = message.content.split(",");

        webSocketServer = ServerService.getServerViaPort(Integer.valueOf(contents[0]));
        User myUser = webSocketServer.getLobby().getUserByUsername(contents[1]);
        myUser.setSession(session);

        ServerService.addSessionToSever(session, webSocketServer);

        Board board = webSocketServer.getGameHandler().getGame().getBoard();
        String[] boardPeaces = WebSocketService.splitBoardIntoJsonString(board);
        for (String boardStringPeace : boardPeaces) {
          session.getBasicRemote().sendObject(new Message(707, boardStringPeace));
        }

        if (webSocketServer
            .getGameHandler()
            .isTurnOfUsername(webSocketServer.getLobby().getUsernameBySession(session))) {
          session.getBasicRemote().sendObject(new Message(730));
        }
        break;
      default:
        break;
    }
  }

  /**
   * Defines what happens when new a session is closed.
   *
   * @param session closed Session
   */
  @OnClose
  public void onClose(Session session) {
    clientSessions.remove(session);
    System.out.println(session.getId() + " Session was closed");
  }

  /**
   * handles received 702 message.
   *
   * @param message         received Message
   * @param webSocketServer current webSocketServer
   */
  public void parseCreateAi(Message message, WebSocketServer webSocketServer) {
    String[] content = message.getContent().split(",");
    String aiName = content[0];
    int difficulty = Integer.parseInt(content[1]);
    webSocketService.createAiPlayer702(aiName, difficulty, webSocketServer);
  }

  /**
   * handles received 701 message.
   *
   * @param message received message
   * @param session joined Session
   * @return if lobby join was successfull
   */
  public boolean parseJoinLobby(Message message, Session session) {
    String[] content = message.getContent().split(",");
    int port = Integer.parseInt(content[1]);
    WebSocketServer webSocketServer = ServerService.getServerViaPort(port);

    if (webSocketServer.getLobby().getUserList().size()
        >= webSocketServer.getLobby().getPlayerAmount()) {
      return false;
    }
    ServerService.addSessionToSever(session, webSocketServer);
    webSocketServer.addSession(session);
    String username = content[0];
    webSocketService.joinLobby701(session, username, webSocketServer);
    return true;
  }

  /**
   * handles receives 703 message.
   *
   * @param message received message
   */
  public void parseInitializeLobby(Message message) {
    String[] content = message.getContent().split(",");
    int port = Integer.parseInt(content[0]);
    int playerAmount = Integer.parseInt(content[1]);
    String rounds = content[2];
    WebSocketServer webSocketServer = ServerService.getServerViaPort(port);

    webSocketService.initializeLobby703(webSocketServer, playerAmount, rounds, port);
  }
}
