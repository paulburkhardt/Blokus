package gamelogic;

import boards.BoardSpace;
import boards.GameTheme;
import gametiles.GameTile;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class for the Player playing the GameModes.
 *
 * @author pburkhar, lfiebig
 */
public class Player {

  private String username;
  private int score = 0;
  private boolean turn = false;
  private static int playerIdCounter = 1;
  private int playerId;
  private GameTheme boardTheme;
  private GameTile selectedGameTile;
  private TreeMap<String, GameTile> gameTiles;
  private boolean hasTurnsLeft = true;
  protected int level = 0;
  private boolean firstTurn = true;

  /**
   * HashSet with all BoardSpaces bordering the players laid Tiles on a corner.
   */
  private HashSet<BoardSpace> possibleSpaces = new HashSet<>();
  /**
   * Hashset with all BoardSpaces that are viable for the current selectedGameTile.
   */
  private HashSet<BoardSpace> viableSpaces = new HashSet<>();

  /**
   * Constructor setting the attributes.
   *
   * @param username  username of the player
   * @param gameTheme selected gameTheme od the player
   */
  public Player(String username, GameTheme gameTheme) {
    this.username = username;
    this.score = 0;
    this.turn = false;
    this.playerId = playerIdCounter++;
    this.boardTheme = gameTheme;
  }

  public boolean isFirstTurn() {
    return firstTurn;
  }

  public void setFirstTurn(boolean firstTurn) {
    this.firstTurn = firstTurn;
  }

  /**
   * Method to calculate if the player has pieces left to place or not.
   *
   * @author lfiebig
   */
  public boolean hasGameTilesLeft() {
    for (Map.Entry<String, GameTile> entry : this.getGameTiles().entrySet()) {
      if (!entry.getValue().isPlayed()) {
        return true;
      }
    }
    return false;
  }

  public void setHasTurnsLeft(boolean hastTurnsLeft) {
    this.hasTurnsLeft = hastTurnsLeft;
  }

  public boolean getHasTurnsLeft() {
    return this.hasTurnsLeft;
  }

  public void setGameTiles(TreeMap<String, GameTile> gameTiles) {
    this.gameTiles = gameTiles;
  }

  public TreeMap<String, GameTile> getGameTiles() {
    return this.gameTiles;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public int getScore() {
    return score;
  }

  public void addScore(int score) {
    this.score = this.score + score;
  }

  public boolean isTurn() {
    return turn;
  }

  public void setTurn(boolean turn) {
    this.turn = turn;
  }

  public int getPlayerId() {
    return playerId;
  }

  public void setPlayerId(int playerId) {
    this.playerId = playerId;
  }

  public GameTheme getBoardTheme() {
    return boardTheme;
  }

  public void setBoardTheme(GameTheme boardTheme) {
    this.boardTheme = boardTheme;
  }

  public static int getPlayerIdCounter() {
    return playerIdCounter;
  }

  public static void setPlayerIdCounter(int playerIdCounter) {
    Player.playerIdCounter = playerIdCounter;
  }

  public GameTile getSelectedGameTile() {
    return selectedGameTile;
  }

  /**
   * method to set the GameTile selected by the player.
   *
   * @param selectedGameTile String representing the GameTile
   */
  public void setSelectedGameTile(String selectedGameTile) {
    if (selectedGameTile.equals("null")) {
      this.selectedGameTile = null;
    } else {
      this.selectedGameTile = this.getGameTiles().get(selectedGameTile);
    }
  }

  public HashSet<BoardSpace> getPossibleSpaces() {
    return possibleSpaces;
  }

  public void addPossibleSpace(BoardSpace possibleSpace) {
    this.possibleSpaces.add(possibleSpace);
  }

  public HashSet<BoardSpace> getViableSpaces() {
    return viableSpaces;
  }

  public void addViableSpace(BoardSpace viableSpace) {
    this.viableSpaces.add(viableSpace);
  }

  public void resetViableSpaces() {
    this.viableSpaces.clear();
  }

  public void deleteViableSpace(BoardSpace viableSpace) {
    this.viableSpaces.remove(viableSpace);
  }

  public void deletePossibleSpace(BoardSpace possibleSpace) {
    this.possibleSpaces.remove(possibleSpace);
  }

  public void setViableSpaces(HashSet<BoardSpace> viableSpaces) {
    this.viableSpaces = viableSpaces;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public void setGameTile(GameTile gameTile) {
    this.selectedGameTile = gameTile;
  }
}
