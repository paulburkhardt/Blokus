package websockets;

import gamelogic.GameHandler;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.Session;
import model.Lobby;
import org.glassfish.tyrus.server.Server;

/**
 * WebSocket Server for stateful communication and data transfer.
 *
 * @author jbuechs
 */
public class WebSocketServer {

  private static Server server;
  private GameHandler gameHandler;
  private Lobby lobby;
  private Set<Session> connectedSessions = new HashSet<>();

  /**
   * Creates and runs a websocket Server with the given port.
   *
   * @param port server will run on this port
   * @return the created WebSocketServer Object
   */
  public WebSocketServer runServer(int port) {

    server = new Server("localhost", port, "/websockets/" + port, ServerEndpoint.class);

    try {
      server.start();
    } catch (Exception e) {

      throw new RuntimeException(e);
    }
    return this;
  }

  public GameHandler getGameHandler() {
    return this.gameHandler;
  }

  public Lobby getLobby() {
    return lobby;
  }

  public void setLobby(Lobby lobby) {
    this.lobby = lobby;
  }

  public void setGameHandler(GameHandler gameHandler) {
    this.gameHandler = gameHandler;
  }

  /** stops the webSocketServer if running. */
  public void stopServer() {
    server.stop();
  }

  public Lobby setUpSinglePlayerLobby() {
    return new Lobby(this);
  }

  /**
   * will add a client session to the Set of connectedSessions.
   *
   * @param session the client session to be added
   */
  public void addSession(Session session) {
    connectedSessions.add(session);
  }

  /**
   * deletes client session from the connectedSessions.
   *
   * @param session the client session to be deleted
   */
  public void deleteSession(Session session) {
    if (connectedSessions.contains(session)) {
      connectedSessions.remove(session);
    }
  }

  /**
   * return the connected clients (sessions).
   *
   * @return Set of the connected sessions from clients
   */
  public Set<Session> getConnectedSessions() {
    return connectedSessions;
  }
}
