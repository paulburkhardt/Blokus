package ai;

import boards.BoardSpace;
import gamelogic.Game;
import gamelogic.Player;
import gametiles.GameTile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Class handles different requests in connection with possible plays.
 *
 * @author pboensch
 */
public class GetPlay {
  Game game;
  Player player;
  List<Play> possiblePlays = new ArrayList<>();

  public GetPlay() {}

  /**
   * picks a play randomly out of all possible plays.
   *
   * @param game current game copy
   * @param playerId id of the player whose turn it is
   * @return randomPlay play that is going to be played by the player
   */
  public Play getRandomTurn(Game game, int playerId) {
    this.game = game;
    this.player = game.getPlayerByid(playerId);
    Play randomPlay = null;
    possiblePlays = getPossiblePlays(this.game, playerId);
    if (possiblePlays.size() > 0) {
      Random generator = new Random();
      int randomIndex = generator.nextInt(possiblePlays.size());
      randomPlay = possiblePlays.get(randomIndex);
    }
    return randomPlay;
  }

  /**
   * returns a play with the largest size possible.
   *
   * @param game current game copy
   * @param playerId id of the player whose turn it is
   * @return largestPlay picks a play randomly out of all possible plays
   */
  public Play getLargestPlay(Game game, int playerId) {
    this.game = game;
    this.player = game.getPlayerByid(playerId);
    List<Play> largestPlay = new ArrayList<>();
    possiblePlays = getPossiblePlays(this.game, playerId);
    int size = 0;
    if (possiblePlays.size() > 0) {
      for (Play play : possiblePlays) {
        if (size < play.getSize()) {
          size = play.getSize();
        }
      }
      for (Play play : possiblePlays) {
        if (play.getSize() == size) {
          largestPlay.add(play);
        }
      }
    }
    if (largestPlay.size() > 0) {
      Random generator = new Random();
      int randomIndex = generator.nextInt(largestPlay.size());
      return largestPlay.get(randomIndex);
    }
    return null;
  }

  /**
   * returns a play with the largest size possible and the closest to the center of the board.
   * Starts mcts.
   *
   * @param game current game copy
   * @param playerId id of the player whose turn it is
   * @return bestPlay picks a play randomly out of all possible plays
   */
  public Play getBestPlay(Game game, int playerId) {
    this.game = game;
    this.player = game.getPlayerByid(playerId);
    List<Play> largestPlay = new ArrayList<>();
    possiblePlays = getPossiblePlays(this.game, playerId);
    int size = 0;
    int center = Integer.MAX_VALUE;
    int boardCenter = game.getBoard().getBoardSpaces().length / 2;
    if (possiblePlays.size() > 0) {
      for (Play play : possiblePlays) {
        if (size < play.getSize()) {
          size = play.getSize();
        }
      }
      for (Play play : possiblePlays) {
        if (play.getSize() == size) {
          if (size == 5) {
            int playCenter =
                Math.abs(play.getPosition().getPosition().getXcoord() - boardCenter)
                    + Math.abs(play.getPosition().getPosition().getYcoord() - boardCenter);
            if (playCenter <= center) {
              center = playCenter;
              largestPlay.add(play);
            }
          } else {
            largestPlay.add(play);
          }
        }
      }
    }
    if (largestPlay.size() > 0) {
      Random generator = new Random();
      int randomIndex = generator.nextInt(largestPlay.size());
      return largestPlay.get(randomIndex);
    }
    return null;
  }

  /**
   * returns all possible plays.
   *
   * @param game current game copy
   * @param playerId id of the player whose turn it is
   * @return getPossiblePlays list with all possible plays
   */
  public List<Play> getPossiblePlays(Game game, int playerId) {
    this.game = game;
    this.player = game.getPlayerByid(playerId);
    int maxRotations = game.getGameMode().equals("Trigon") ? 6 : 4;
    List<Play> getPossiblePlays = new ArrayList<>();
    // all gametiles
    TreeMap<String, GameTile> tiles = player.getGameTiles();
    for (Map.Entry<String, GameTile> gameTile : tiles.entrySet()) {
      // select single GameTile
      String key = gameTile.getKey();
      player.setSelectedGameTile(key);
      player.getSelectedGameTile().resetGameTile();
      // check if Tile is already played
      if (player.getSelectedGameTile().isPlayed()) {
        player.setSelectedGameTile("null");
        continue;
      }
      if (game.getGameMode().equals("Trigon")) {
        // add every possible rotation of tile
        for (int i = 0; i < 6; i++) {
          for (int j = 0; j < 2; j++) {
            player.setViableSpaces(this.game.calculateViableSpaces(player));
            if (!player.getViableSpaces().isEmpty()) {
              HashSet<BoardSpace> viableSpaces = player.getViableSpaces();
              for (BoardSpace boardSpace : viableSpaces) {
                boolean isMirrored;
                if (j == 1) {
                  isMirrored = true;
                } else {
                  isMirrored = false;
                }
                getPossiblePlays.add(
                    new Play(
                        key,
                        player.getPlayerId(),
                        boardSpace,
                        player.getSelectedGameTile().getSize(),
                        i,
                        isMirrored));
              }
            }
            player.getSelectedGameTile().mirrorGameTile();
          }
          player.getSelectedGameTile().rotateGameTile();
        }
      } else {
        for (int j = 0; j < 2; j++) {
          for (int i = 0; i < maxRotations; i++) {
            player.setViableSpaces(this.game.calculateViableSpaces(player));
            if (!player.getViableSpaces().isEmpty()) {
              HashSet<BoardSpace> viableSpaces = player.getViableSpaces();
              for (BoardSpace boardSpace : viableSpaces) {
                boolean isMirrored;
                if (j == 1) {
                  isMirrored = true;
                } else {
                  isMirrored = false;
                }
                getPossiblePlays.add(
                    new Play(
                        key,
                        player.getPlayerId(),
                        boardSpace,
                        player.getSelectedGameTile().getSize(),
                        i,
                        isMirrored));
              }
            }
            player.getSelectedGameTile().rotateGameTile();
          }
          player.getSelectedGameTile().resetGameTile();
          player.getSelectedGameTile().mirrorGameTile();
        }
      }
      player.getSelectedGameTile().resetGameTile();
      player.setSelectedGameTile("null");
    }
    return getPossiblePlays;
  }

  /**
   * returns true if the player has plays left.
   *
   * @param game current game copy
   * @param playerId id of the player whose turn it is
   * @return playsLeft
   */
  public boolean playsLeft(Game game, int playerId) {
    if (getPossiblePlays(game, playerId).isEmpty()) {
      return false;
    } else {
      return true;
    }
  }
}
