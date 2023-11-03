package gametiles;

import boards.BoardSpace;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import datatypes.Coordinate;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * GameTile for the Game.
 *
 * @author pburkhar
 */
public class GameTile {

  /**
   * form = the form of the GameTile.GameTile (GameTile.GameTileForm entry) size = the size of the
   * GameTile.GameTile and therefore how many points it is worth
   */
  private final ArrayList<Coordinate> form;
  private final int size;
  private ArrayList<Coordinate> position;
  private boolean isPlayed = false;
  private final GameTileForm gameTileForm;

  /**
   * Constructor setting the attributes.
   *
   * @param gameTileForm gameTileForm enum for the GameTile
   */
  public GameTile(GameTileForm gameTileForm) {
    this.gameTileForm = gameTileForm;
    this.form = new ArrayList<>();
    this.position = new ArrayList<>();
    for (Coordinate c : gameTileForm.getCoordinates()) {
      this.form.add(new Coordinate(c.getXcoord(), c.getYcoord()));
      this.position.add(new Coordinate(c.getXcoord(), c.getYcoord()));
    }
    this.size = gameTileForm.getCoordinates().size();
  }

  /**
   * Json Creator for GameTile.
   *
   * @param form         GameTile form
   * @param size         size of the GameTile
   * @param position     position of the GameTile
   * @param isPlayed     is the GameTile played?
   * @param gameTileForm gameTileForm enum of the GameTile
   */
  @JsonCreator
  public GameTile(
      @JsonProperty("form") ArrayList<Coordinate> form,
      @JsonProperty("size") int size,
      @JsonProperty("position") ArrayList<Coordinate> position,
      @JsonProperty("isPlayed") boolean isPlayed,
      @JsonProperty("gameTileForm") GameTileForm gameTileForm) {
    this.form = form;
    this.size = size;
    this.position = position;
    this.isPlayed = isPlayed;
    this.gameTileForm = gameTileForm;
  }

  public GameTileForm getGameTileForm() {
    return gameTileForm;
  }

  public ArrayList<Coordinate> getForm() {
    return form;
  }

  public ArrayList<Coordinate> getPosition() {
    return position;
  }

  public void setPosition(ArrayList<Coordinate> position) {
    this.position = position;
  }

  public int getSize() {
    return size;
  }

  public boolean isPlayed() {
    return isPlayed;
  }

  public void setPlayed(boolean played) {
    isPlayed = played;
  }


  /**
   * Method rotating the GameTile clockwise.
   */
  public void rotateGameTile() {
    for (Coordinate c : this.position) {
      int tmp = c.getXcoord();
      c.setXcoord(c.getYcoord());
      c.setYcoord(-tmp);
    }
  }

  /**
   * mirrors GameTile on the x Axis.
   *
   * @author pburkhar
   */
  public void mirrorGameTile() {
    for (Coordinate c : this.position) {
      c.setXcoord(c.getXcoord() * -1);
    }
  }

  /**
   * moves GameTile on the board.
   *
   * @param bc BoardSpace to move to
   */
  public void moveGameTileOnBoard(BoardSpace bc) {
    for (Coordinate c : this.position) {
      c.setXcoord(c.getXcoord() + bc.getPosition().getXcoord());
      c.setYcoord(c.getYcoord() + bc.getPosition().getYcoord());
    }
  }

  /**
   * Method to reset the GameTile to the GameTileForm.
   */
  public void resetGameTile() {
    ArrayList<Coordinate> coordinatesTemp = new ArrayList<>();
    for (Coordinate c : gameTileForm.getCoordinates()) {
      coordinatesTemp.add(new Coordinate(c.getXcoord(), c.getYcoord()));
    }
    this.position = coordinatesTemp;
  }

  /**
   * GameTile als String visualisiert.
   *
   * @return visualization of the GamePiece in the console
   * @author pburkhar
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    for (int i = size - 1; i >= 0; i--) {
      for (int j = 0; j < size; j++) {
        if (form.contains(new Coordinate(j, i))) {
          sb.append("x");
        } else {
          sb.append(" ");
        }
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  /**
   * GameTile is converted to String of the Coordinates of the psoition.
   *
   * @return String of the Coordinates
   */
  public String toCoordinates() {
    StringBuffer sb = new StringBuffer();
    for (Coordinate c : this.getPosition()) {
      sb.append(" [" + c.getXcoord() + "," + c.getYcoord() + "]");
    }
    return sb.toString();
  }

  /**
   * Method for initializing the coordinates for the corneredSpace not being blocked by being next
   * to the GameTile directly.
   *
   * @return a HashSet with all the coordinates edging the GameTile
   * @author pburkhar
   */
  @JsonIgnore
  public HashSet<Coordinate> getCorneredSpaces() {
    HashSet<Coordinate> corneredSpaces = new HashSet<>();
    HashSet<Coordinate> blockedCoordinates = getBlockedCoordinates();
    for (Coordinate c : getAllCorners()) {
      if (!blockedCoordinates.contains(c) && !position.contains(c)) {
        corneredSpaces.add(c);
      }
    }
    return corneredSpaces;
  }

  /**
   * Method to get all the coordinates directly near a coordinate of the GameTile.
   *
   * @return HashSet with all blockedCoordinates by the GameTile
   * @author pburkhar
   */
  @JsonIgnore
  public HashSet<Coordinate> getBlockedCoordinates() {
    HashSet<Coordinate> blockedCoordinates = new HashSet<>();
    for (Coordinate c : position) {
      blockedCoordinates.add(new Coordinate(c.getXcoord() + 1, c.getYcoord()));
      blockedCoordinates.add(new Coordinate(c.getXcoord() - 1, c.getYcoord()));
      blockedCoordinates.add(new Coordinate(c.getXcoord(), c.getYcoord() + 1));
      blockedCoordinates.add(new Coordinate(c.getXcoord(), c.getYcoord() - 1));
    }
    for (Coordinate c : position) {
      blockedCoordinates.remove(c);
    }
    return blockedCoordinates;
  }

  /**
   * Method to get all the coordinates that are corner to corner with the coordinates of the
   * GameTile.
   *
   * @return HashSet with all coordinates that corner at least one coordinate of the GameTile
   * @author pburkhar
   */
  @JsonIgnore
  private HashSet<Coordinate> getAllCorners() {
    HashSet<Coordinate> allCorners = new HashSet<>();
    for (Coordinate c : position) {
      allCorners.add(new Coordinate(c.getXcoord() + 1, c.getYcoord() + 1));
      allCorners.add(new Coordinate(c.getXcoord() - 1, c.getYcoord() - 1));
      allCorners.add(new Coordinate(c.getXcoord() - 1, c.getYcoord() + 1));
      allCorners.add(new Coordinate(c.getXcoord() + 1, c.getYcoord() - 1));
    }
    return allCorners;
  }
}
