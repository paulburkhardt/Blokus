package gamelogic;

import ai.AiPlayer;
import boards.BoardSpace;
import boards.GameTheme;
import gametiles.GameTile;
import gametiles.TrigonGameTile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import model.Lobby;
import model.User;
import service.WebSocketService;

/**
 * Class for managing the whole game process.
 *
 * @author lfiebig
 * @author myenler
 */
public class GameHandler {

  private Game game;
  private Lobby lobby;

  public GameHandler(Lobby lobby) {
    this.lobby = lobby;
  }

  public Game getGame() {
    return game;
  }

  public void setGame(Game game) {
    this.game = game;
  }

  /** creates and adds player objects to the every user in the lobby. */
  public void createAndSetPlayers() {
    for (User user : this.lobby.getUserList()) {
      if (user.getDifficulty() == 0) {
        user.setPlayer(new Player(user.getUsername(), GameTheme.NEON));
      } else {
        user.setPlayer(new AiPlayer(user.getUsername(), GameTheme.NEON, user.getDifficulty()));
      }
    }
  }

  /**
   * Collects all the player objects of a lobby in an ArrayList.
   *
   * @return created Arraylist of Players
   */
  public ArrayList<Player> collectPlayers() {
    ArrayList<Player> playerList = new ArrayList<>();
    for (User user : this.lobby.getUserList()) {
      playerList.add(user.getPlayer());
    }
    return playerList;
  }

  /**
   * Starts a game instance.
   *
   * @param game Game object
   */
  public void startGame(Game game) {
    this.game = game;
    game.getPlayers().get(0).setTurn(true);
  }

  /**
   * Creates and sets up a gameobject.
   *
   * @return if the lobby has games left to play
   */
  public boolean setUpAndStartGame() {
    if (this.lobby.getRounds().isEmpty()) {
      return false; // if no more rounds are left
    }
    Game game = this.lobby.getRounds().remove(0);
    this.createAndSetPlayers();

    for (int i = 1; i <= this.collectPlayers().size(); i++) {
      Player p = this.collectPlayers().get(i - 1);
      p.setPlayerId(i);
      game.addPlayer(p);
    }
    game.setPlayers(this.collectPlayers());
    game.setInfo();
    this.startGame(game);
    return true;
  }

  /**
   * manages a received game turn.
   *
   * @param playerId Player who did a turn
   * @param gameTile placed gameTile
   * @param position position where the gameTile was placed
   */
  public void gameTurn(int playerId, GameTile gameTile, BoardSpace position) {
    game.getPlayerByid(playerId).setGameTile(gameTile);
    game.getPlayerByid(playerId)
        .setViableSpaces(game.calculateViableSpaces(game.getPlayerByid(playerId)));
    game.gameTilePlaced(game.getPlayerByid(playerId), position);
    game.getPlayerByid(playerId).getSelectedGameTile().setPlayed(true);
    game.getPlayerByid(playerId).setSelectedGameTile("null");
    game.getPlayerByid(playerId).setTurn(false);
  }

  /**
   * manages a game turn and send the updated gameState to every player.
   *
   * @param playerId playerId of the player who did a turn
   * @param gameTile placed gameTile
   * @param position position where gameTile was placed
   * @param senderSession Session of the user who sent the turn
   * @throws EncodeException when encoding of the gameTile object fails
   * @throws IOException when Scene switch fails
   */
  public void manageTurn(
      int playerId, GameTile gameTile, BoardSpace position, Session senderSession)
      throws EncodeException, IOException {

    if (this.game.getGameMode().equals("Trigon")) {
      TrigonGameTile trigonGameTile = new TrigonGameTile(gameTile.getGameTileForm());
      trigonGameTile.setPosition(gameTile.getPosition());
      gameTile = trigonGameTile;
    }

    this.gameTurn(playerId, gameTile, position);
    WebSocketService.sendGameState707(this.lobby.getPort(), senderSession, false);

    manageTurnsInBackend(playerId, senderSession);
  }

  /**
   * manages AI turns and skips players who are done playing.
   *
   * @param playerId playerId of the player who did a turn
   * @param senderSession Session of the user who sent the turn
   * @throws EncodeException when encoding of the gameTile object fails
   * @throws IOException when Scene switch fails
   */
  public void manageTurnsInBackend(int playerId, Session senderSession)
      throws EncodeException, IOException {

    int nextPlayerId = this.getNextPlayerId(playerId);

    while (game.getPlayerByid(nextPlayerId).getLevel() != 0
        || !game.getPlayerByid(nextPlayerId).getHasTurnsLeft()) {

      if (checkIfGameFinished()) {
        System.out.println("game finished");
        updateStats();
        WebSocketService.showLeaderBoard742(this.lobby.getPort(), lobby.getStats());
        return;
      }
      if (game.getPlayerByid(nextPlayerId).getLevel() != 0) {
        AiPlayer aiPlayer = (AiPlayer) game.getPlayerByid(nextPlayerId);
        if (aiPlayer.getHasTurnsLeft()) {
          aiPlayer.doTurn(this.game);
          System.out.println("AI made turn " + aiPlayer.getUsername());
          WebSocketService.sendGameState707(this.lobby.getPort(), senderSession, true);
        }
        nextPlayerId = getNextPlayerId(nextPlayerId);
      } else if (game.getPlayerByid(nextPlayerId).getLevel() == 0
          && !game.getPlayerByid(nextPlayerId).getHasTurnsLeft()) {
        nextPlayerId = getNextPlayerId(nextPlayerId);
      }
    }
    String usernameOfNextPlayerPlayer = this.game.getPlayerByid(nextPlayerId).getUsername();
    WebSocketService.sendItsYourTurnMessage730(
        this.lobby.getSessionByUsername(usernameOfNextPlayerPlayer));
  }

  /**
   * Sets player to done playing.
   *
   * @param playerId Id of the player
   * @param session Session of the player
   * @throws EncodeException when encoding of the gameTile object fails
   * @throws IOException when Scene switch fails
   */
  public void setPlayerIsDone(int playerId, Session session) throws EncodeException, IOException {
    Player player = this.game.getPlayerByid(playerId);
    player.setHasTurnsLeft(false);
    manageTurnsInBackend(playerId, session);
  }

  /**
   * Gets the scores of every player in the lobby.
   *
   * @return String that contains username and score of every player
   */
  public String getScores() {
    StringBuffer sb = new StringBuffer();
    for (Player player : this.game.getPlayers()) {
      sb.append(player.getUsername() + ":" + player.getScore());
      sb.append(",");
    }
    sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }

  /**
   * Get the id of the next Player.
   *
   * @param id current player Id
   * @return next player Id
   */
  public int getNextPlayerId(int id) {
    int nextPlayerId = (id + 1) % this.game.getPlayers().size();
    if (nextPlayerId == 0) {
      return this.game.getPlayers().size();
    }
    return nextPlayerId;
  }

  /**
   * Checks if the game is finished.
   *
   * @return true if game is finished
   */
  public boolean checkIfGameFinished() {
    for (Player p : game.getPlayers()) {
      if (p.getHasTurnsLeft()) {
        return false;
      }
    }
    return true;
  }

  /**
   * checks if a given user is on the turn.
   *
   * @param username User to check
   * @return true if user is on the turn
   */
  public boolean isTurnOfUsername(String username) {
    return this.game.getPlayerByUsername(username).isTurn();
  }

  /** updates the stats of every player in the game session. */
  public void updateStats() {
    if (lobby.getStats().isEmpty()) {
      for (Player p : game.getPlayers()) {
        lobby.getStats().put(p.getUsername(), p.getScore());
      }
    } else {
      for (Player p : game.getPlayers()) {
        p.getUsername();
        for (Map.Entry<String, Integer> statsEntry : lobby.getStats().entrySet()) {
          statsEntry.getKey();
          if (p.getUsername().equals(statsEntry.getKey())) {
            lobby
                .getStats()
                .replace(
                    statsEntry.getKey(),
                    statsEntry.getValue(),
                    statsEntry.getValue() + p.getScore());
          }
        }
      }
    }
  }
}
