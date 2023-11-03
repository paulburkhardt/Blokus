package websockets;

import boards.BoardSpace;
import gametiles.GameTile;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import model.ChatMessage;
import model.Message;
import model.TurnMessage;
import org.glassfish.tyrus.client.ClientManager;

/**
 * WebSocket Client for stateful communication and data transfer.
 *
 * @author jbuechs
 */
public class WsClient {
  private Session session;

  /**
   * creates client and connects to a WebSocket Server. The Server must be started before a client
   * tries to connect to it.
   *
   * @param port port of started server
   * @param ip ip of started server
   */
  public void connectToServer(int port, String ip) {
    ClientManager client = ClientManager.createClient();
    model.Session.getMyUser().getClient().setWantsToDisconnect(false);
    try {
      session =
          client.connectToServer(
              ClientEndpoint.class,
              new URI("ws://" + ip + ":" + port + "/websockets/" + port + "/chat"));
    } catch (DeploymentException e) {
      e.printStackTrace();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
  }

  /**
   * client sends chatMessage to server.
   *
   * @param sender sender of chatMessage
   * @param content content of chatMessage
   */
  public void sendChatMessage(String sender, String content) {
    try {
      session.getBasicRemote().sendObject(new ChatMessage(content, sender));
    } catch (IOException e) {
      e.printStackTrace();
    } catch (EncodeException e) {
      e.printStackTrace();
    }
  }

  /**
   * client sends Message to server.
   *
   * @param status status of chatMessage
   * @param content content of chatMessage
   */
  public void sendMessage(int status, String content) {
    try {
      session.getBasicRemote().sendObject(new Message(status, content));
    } catch (IOException e) {
      e.printStackTrace();
    } catch (EncodeException e) {
      e.printStackTrace();
    }
  }

  /**
   * client sends turnMessage to server.
   *
   * @param boardSpace given boardSpace
   * @param gameTile given gameTile
   */
  public void sendTurnMessage(BoardSpace boardSpace, GameTile gameTile) {
    try {
      session.getBasicRemote().sendObject(new TurnMessage(boardSpace, gameTile));
    } catch (IOException e) {
      e.printStackTrace();
    } catch (EncodeException e) {
      e.printStackTrace();
    }
  }

  public Session getSession() {
    return session;
  }
}
