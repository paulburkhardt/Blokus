package gamelogic;

import boards.Board;
import boards.BoardSpace;
import boards.ClassicBoard;
import boards.TrigonBoard;
import boards.TwoPlayerBoard;
import datatypes.Coordinate;
import gametiles.GameTile;
import gametiles.GameTileForm;
import gametiles.TrigonGameTile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

/**
 * abstract class to define similar things in the gamelogic of all GameModes.
 *
 * @author pburkhar
 */
public abstract class Game {

  /**
   * Array with all players in the current game.
   */
  private ArrayList<Player> players;
  /**
   * Defines the board on which is played.
   */
  private Board board;
  /**
   * Defines the Game Tiles every player gets in the beginning of the Game.
   */
  private HashMap<String, GameTile> gameTiles;


  /**
   * constructor for the basic values.
   *
   * @author pburkhar
   */
  public Game() {
    players = new ArrayList<>();
    gameTiles = new HashMap<>();
  }

  /**
   * Constructor to make a deep copy of the Game given as a parameter.
   *
   * @param game that should be copied
   * @author pburkhar
   */
  public Game(Game game) {
    switch (game.getGameMode()) {
      case ("Classic"):
        this.setBoard(new ClassicBoard());
        break;
      case ("Trigon"):
        this.setBoard(new TrigonBoard());
        break;
      default:
        this.setBoard(new TwoPlayerBoard());
    }
    BoardSpace[][] boardSpacesTemp = game.getBoard().getBoardSpaces();
    for (int i = 0; i < boardSpacesTemp.length; i++) {
      for (int j = 0; j < boardSpacesTemp[i].length; j++) {
        HashSet<Integer> blockedBy = new HashSet<>();
        for (Integer id : boardSpacesTemp[i][j].getIsBlockedBy()) {
          blockedBy.add(id);
        }
        this.getBoard().getBoardSpaces()[i][j].setIsBlockedBy(blockedBy);
        this.getBoard()
            .getBoardSpaces()[i][j]
            .setIsCoveredByWhom(boardSpacesTemp[i][j].getIsCoveredByWhom());
      }
    }
    this.gameTiles = new HashMap<>();
    for (String gt : game.getGameTiles().keySet()) {
      for (GameTileForm gtf : GameTileForm.values()) {
        if (gtf.toString().equals(gt)) {
          if (game.isTrigon()) {
            this.gameTiles.put(gt, new TrigonGameTile(gtf));
          } else {
            this.gameTiles.put(gt, new GameTile(gtf));
          }
        }
      }
    }
    this.players = new ArrayList<>();
    for (Player p : game.getPlayers()) {
      Player player = new Player(p.getUsername(), p.getBoardTheme());
      player.addScore(p.getScore());
      player.setPlayerId(p.getPlayerId());
      player.setHasTurnsLeft(p.getHasTurnsLeft());
      TreeMap<String, GameTile> gameTilesCopy = new TreeMap<>();
      for (String gt : p.getGameTiles().keySet()) {
        GameTile oldGameTile = p.getGameTiles().get(gt);
        GameTile newGameTile = new GameTile(oldGameTile.getGameTileForm());
        newGameTile.setPlayed(oldGameTile.isPlayed());
        ArrayList<Coordinate> positionNew = new ArrayList<>();
        for (Coordinate c : oldGameTile.getPosition()) {
          positionNew.add(new Coordinate(c.getXcoord(), c.getYcoord()));
        }
        newGameTile.setPosition(positionNew);
        gameTilesCopy.put(gt, newGameTile);
      }
      player.setGameTiles(gameTilesCopy);
      if (p.getSelectedGameTile() != null) {
        player.setSelectedGameTile(p.getSelectedGameTile().getGameTileForm().toString());
      }
      for (BoardSpace bs : p.getViableSpaces()) {
        if (this.getGameMode().equals("Trigon")) {
          player.addViableSpace(
              this.getBoard()
                  .getBoardSpaces()[bs.getPosition().trigonX()][bs.getPosition().trigonY()]);
        } else {
          player.addViableSpace(
              this.getBoard().getBoardSpaces()[bs.getPosition().getXcoord()][bs.getPosition()
                  .getYcoord()]);
        }
      }
      for (BoardSpace bs : p.getPossibleSpaces()) {
        if (this.getGameMode().equals("Trigon")) {
          player.addPossibleSpace(
              this.getBoard()
                  .getBoardSpaces()[bs.getPosition().trigonX()][bs.getPosition().trigonY()]);
        } else {
          player.addPossibleSpace(
              this.getBoard().getBoardSpaces()[bs.getPosition().getXcoord()][bs.getPosition()
                  .getYcoord()]);
        }
        if (p.getSelectedGameTile() != null) {
          player.setSelectedGameTile(p.getSelectedGameTile().getGameTileForm().toString());
        }
      }
      this.players.add(player);
    }
  }

  public ArrayList<Player> getPlayers() {
    return players;
  }

  public void setPlayers(ArrayList<Player> players) {
    this.players = players;
  }

  /**
   * method to get a Player with a specific id.
   *
   * @param playerid id of the player that is wanted
   * @return Player with the provided id
   */
  public Player getPlayerByid(int playerid) {
    for (Player player : players) {
      if (player.getPlayerId() == playerid) {
        return player;
      }
    }
    return null;
  }

  public Board getBoard() {
    return board;
  }

  public void setBoard(Board board) {
    this.board = board;
  }

  public HashMap<String, GameTile> getGameTiles() {
    return gameTiles;
  }

  public void setGameTiles(HashMap<String, GameTile> gameTiles) {
    this.gameTiles = gameTiles;
  }


  public abstract String getGameMode();

  public boolean isTrigon() {
    return this.getGameMode().equals("Trigon");
  }

  /**
   * Method to add a player to the game.
   *
   * @author pburkhar
   */
  public void addPlayer(Player player) {
    TreeMap<String, GameTile> gameTilesCopy = new TreeMap<>();
    for (String gt : this.gameTiles.keySet()) {
      GameTile newGameTile = new GameTile(this.gameTiles.get(gt).getGameTileForm());
      gameTilesCopy.put(gt, newGameTile);
    }

    player.setGameTiles(gameTilesCopy);
    this.players.add(player);
    System.out.println("Player: " + player.getUsername() + " was added to the Game");
  }


  public abstract void updatePossibleSpaces(Player player);

  public abstract HashSet<BoardSpace> calculateViableSpaces(Player player);

  public abstract boolean gameTilePlaced(Player player, BoardSpace position);

  public abstract boolean checkHasTurnsLeft(Player player);

  public abstract void setInfo();

  /**
   * returns the id of a Player with the given username.
   *
   * @param username username of the Player
   * @return id of the player
   */
  public int getPlayerIdByUsername(String username) {
    for (Player player : this.getPlayers()) {
      if (player.getUsername().equals(username)) {
        return player.getPlayerId();
      }
    }
    return -1;
  }


  /**
   * returns Player with the given username.
   *
   * @param username of the Player
   * @return Player to the username
   */
  public Player getPlayerByUsername(String username) {
    for (Player p : players) {
      if (p.getUsername().equals(username)) {
        return p;
      }
    }
    return null;
  }
}
