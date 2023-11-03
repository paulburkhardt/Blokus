package model;

import boards.GameTheme;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gamelogic.Player;
import javax.websocket.Session;

/**
 * User Object Class.
 *
 * @myenler
 * @lfiebig
 */
public class User {
  private String username = "";
  private Client client;
  private Player player;
  private Lobby lobby;
  private int difficulty = 0;
  private javax.websocket.Session session;
  private boolean online = false;
  private GameTheme theme = GameTheme.NEON;

  private String boardString = "";

  public String getBoardString() {
    return boardString;
  }

  public void setBoardString(String boardString) {
    this.boardString = boardString;
  }

  public Session getSession() {
    return session;
  }

  public void setSession(Session session) {
    this.session = session;
  }

  public GameTheme getTheme() {
    return theme;
  }

  /**
   * sets gameTheme for user.
   *
   * @param theme the theme which will be saved (Neon, Tropical, Arctic)
   */
  @JsonIgnore
  public void setTheme(String theme) {
    switch (theme) {
      case "Neon":
        this.theme = GameTheme.NEON;
        break;
      case "Tropical":
        this.theme = GameTheme.TROPICAL;
        break;
      case "Arctic":
        this.theme = GameTheme.ARCTIC;
        break;
      default:
        break;
    }
  }

  /**
   * creates a User Object.
   *
   * @param username username for the new user object
   */
  public User(String username) {
    this.username = username;
  }

  /**
   * Constructor for JSON processing.
   *
   * @param username username
   * @param client client
   * @param player player
   * @param lobby lobby
   */
  @JsonCreator
  public User(
      @JsonProperty("username") String username,
      @JsonProperty("client") Client client,
      @JsonProperty("player") Player player,
      @JsonProperty("lobby") Lobby lobby) {
    this.username = username;
    this.client = client;
    this.player = player;
    this.lobby = lobby;
  }

  public String getUsername() {
    return username;
  }

  public Client getClient() {
    return client;
  }

  public Player getPlayer() {
    return player;
  }

  public Lobby getLobby() {
    return lobby;
  }

  public void setLobby(Lobby lobby) {
    this.lobby = lobby;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setClient(Client client) {
    this.client = client;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public int getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(int difficulty) {
    this.difficulty = difficulty;
  }

  public boolean isOnline() {
    return online;
  }

  public void setOnline(boolean online) {
    this.online = online;
  }
}
