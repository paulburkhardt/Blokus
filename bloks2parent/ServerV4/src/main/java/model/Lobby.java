package model;

import gamelogic.Game;
import java.util.ArrayList;
import java.util.HashMap;
import javax.websocket.Session;
import websockets.WebSocketServer;

/**
 * Lobby object.
 *
 * @author lfiebig
 * @author myenler
 */
public class Lobby {

  private int port = 0;
  private ArrayList<User> userList = new ArrayList<>(); // change to client
  private ArrayList<Game> rounds = new ArrayList<>();
  private int playerAmount;
  private ArrayList<ChatMessage> chat = new ArrayList<>();
  private WebSocketServer webSocketServer;
  private HashMap<String, Integer> stats = new HashMap<>();

  public HashMap<String, Integer> getStats() {
    return stats;
  }

  public ArrayList<User> getUserList() {
    return userList;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public ArrayList<Game> getRounds() {
    return rounds;
  }

  public void setRounds(ArrayList<Game> rounds) {
    this.rounds = rounds;
  }

  public int getPlayerAmount() {
    return playerAmount;
  }

  public void setPlayerAmount(int playerAmount) {
    this.playerAmount = playerAmount;
  }

  public ArrayList<ChatMessage> getChat() {
    return chat;
  }

  /**
   * Creates Lobby object via port and player amount.
   *
   * @param port port where the lobby will be created
   * @param playerAmount amount of players in the lobby
   */
  public Lobby(int port, int playerAmount) {
    this.port = port;
    this.playerAmount = playerAmount;
  }

  /**
   * Creates Lobby object via WebSocketServer.
   *
   * @param webSocketServer where the lobby will be created
   */
  public Lobby(WebSocketServer webSocketServer) {
    this.webSocketServer = webSocketServer;
  }

  /**
   * Creates Lobby object via port.
   *
   * @param port port where the lobby will be created
   */
  public Lobby(int port) {
    this.port = port;
  }

  /**
   * Adds a user to a lobby.
   *
   * @param user given user
   */
  public void addUser(User user) {
    System.out.println("ADD USER: " + user.getUsername());
    this.userList.add(user);
  }

  /**
   * deletes a user from a lobby.
   *
   * @param usernameToDelete given user
   * @return deleted user
   */
  public User deleteUserByUsername(String usernameToDelete) {
    for (User u : this.userList) {
      if (u.getUsername().equals(usernameToDelete)) {
        this.userList.remove(u);
        return u;
      }
    }
    return null;
  }

  /**
   * Adds chat message to lobby chat.
   *
   * @param chat given chat message
   */
  public void addChatMessage(ChatMessage chat) {
    this.chat.add(chat);
  }

  /**
   * returns session by given username.
   *
   * @param username given username
   * @return session
   */
  public Session getSessionByUsername(String username) {
    for (User u : this.userList) {
      if (username.equals(u.getUsername())) {
        return u.getSession();
      }
    }
    throw new RuntimeException("unexpected Error, because no username to User match");
  }

  /**
   * returns User by given session.
   *
   * @param session given session
   * @return user with given session
   */
  public String getUsernameBySession(Session session) {
    for (User u : this.userList) {
      if (u.getSession().equals(session)) {
        return u.getUsername();
      }
    }
    return "";
  }

  /**
   * returns user by given username.
   *
   * @param username given username
   * @return user with given username
   */
  public User getUserByUsername(String username) {
    for (User u : this.userList) {
      if (u.getUsername().equals(username)) {
        return u;
      }
    }
    return null;
  }

  /**
   * returns all users in the lobby, excluding one user by given session.
   *
   * @param session given session
   * @return String usernames of the userns
   */
  public String getUserListAsStringExcludeSession(Session session) {
    StringBuffer sb = new StringBuffer();
    for (User u : this.userList) {
      if (u.getSession() != session) {
        sb.append(u.getUsername() + ":");
      }
    }
    if (sb.length() == 0) {
      return "";
    }
    sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }
}
