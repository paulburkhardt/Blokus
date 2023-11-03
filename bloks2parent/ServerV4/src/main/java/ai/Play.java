package ai;

import boards.BoardSpace;

/**
 * class to safe all important attributes to perform a turn / play.
 *
 * @author pboensch Class to perform a turn
 */
public class Play {
  private int playerId;
  private String gameTile;
  private BoardSpace position;
  private int size;
  private int rotation;
  private boolean mirrored;

  public Play() {}

  /**
   * creates a play.
   *
   * @param gameTile string of the gameTile
   * @param playerId id of the player who is performing the play
   * @param size size of the gameTile
   * @param rotation number of rotations
   * @param mirrored true if the tile is mirrored
   */
  public Play(
      String gameTile,
      int playerId,
      BoardSpace position,
      int size,
      int rotation,
      boolean mirrored) {
    this.gameTile = gameTile;
    this.playerId = playerId;
    this.position = position;
    this.size = size;
    this.rotation = rotation;
    this.mirrored = mirrored;
  }

  public int getPlayerId() {
    return playerId;
  }

  public void setPlayerId(int playerId) {
    this.playerId = playerId;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public String getGameTile() {
    return gameTile;
  }

  public void setGameTile(String gameTile) {
    this.gameTile = gameTile;
  }

  public BoardSpace getPosition() {
    return position;
  }

  public int getRotation() {
    return rotation;
  }

  public boolean isMirrored() {
    return mirrored;
  }
}
