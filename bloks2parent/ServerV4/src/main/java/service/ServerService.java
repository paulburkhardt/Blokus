package service;

import java.util.HashMap;
import java.util.Map;
import javax.websocket.Session;
import websockets.WebSocketServer;

/**
 * manages the server ports.
 */
public class ServerService {

  public static Map<Integer, WebSocketServer> wsPortMap = new HashMap<>();
  public static Map<Session, WebSocketServer> wsSessionMap = new HashMap<>();
  private static int counter = 0;

  /**
   * adds port to server.
   *
   * @param port     port
   * @param wsServer server
   */
  public static void addPortToServer(int port, WebSocketServer wsServer) {
    wsPortMap.put(port, wsServer);
  }



  public static void deleteServer(int port) {
    wsPortMap.get(port).stopServer();
    wsPortMap.remove(port);
  }

  public static WebSocketServer getServerViaPort(int port) {
    return wsPortMap.get(port);
  }

  public static void addSessionToSever(Session session, WebSocketServer webSocketServer) {
    wsSessionMap.put(session, webSocketServer);
  }

  public static WebSocketServer getServerViaSession(Session session) {
    return wsSessionMap.get(session);
  }
}
