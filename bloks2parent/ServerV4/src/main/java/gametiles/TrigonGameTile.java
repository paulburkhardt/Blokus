package gametiles;

import datatypes.Coordinate;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * GameTile for the TrigonGameMode overriding some Methods and adding some attributes.
 */
public class TrigonGameTile extends GameTile {

  private final ArrayList<ArrayList<Coordinate>> states;
  private int rotation = 0;


  public TrigonGameTile(GameTileForm gameTileForm) {
    super(gameTileForm);
    this.states = gameTileForm.getTrigonStates();
  }


  @Override
  public void rotateGameTile() {
    if (this.rotation == this.states.size() - 1) {
      this.rotation = 0;
    } else {
      this.rotation++;
    }
    ArrayList<Coordinate> tempPos = new ArrayList<>();
    for (Coordinate c : states.get(rotation)) {
      tempPos.add(new Coordinate(c.getXcoord(), c.getYcoord()));
    }
    this.setPosition(tempPos);
  }

  /**
   * Method to get all the coordinates that are corner to corner with the coordinates of the
   * GameTile.
   *
   * @return HashSet with all coordinates that corner at least one coordinate of the GameTile
   * @author pburkhar
   */
  private HashSet<Coordinate> getAllCorners() {
    HashSet<Coordinate> allCorners = new HashSet<>();
    for (Coordinate c : this.getPosition()) {
      if ((c.getXcoord() + c.getYcoord()) % 2 == 0) {
        allCorners.add(new Coordinate(c.getXcoord(), c.getYcoord() - 1));
        allCorners.add(new Coordinate(c.getXcoord() + 1, c.getYcoord() + 1));
        allCorners.add(new Coordinate(c.getXcoord() - 1, c.getYcoord() + 1));
        allCorners.add(new Coordinate(c.getXcoord() - 2, c.getYcoord() + 1));
        allCorners.add(new Coordinate(c.getXcoord() + 2, c.getYcoord() + 1));
        allCorners.add(new Coordinate(c.getXcoord() - 2, c.getYcoord()));
        allCorners.add(new Coordinate(c.getXcoord() + 2, c.getYcoord()));
        allCorners.add(new Coordinate(c.getXcoord() - 1, c.getYcoord() - 1));
        allCorners.add(new Coordinate(c.getXcoord() + 1, c.getYcoord() - 1));
      } else {
        allCorners.add(new Coordinate(c.getXcoord(), c.getYcoord() + 1));
        allCorners.add(new Coordinate(c.getXcoord() + 1, c.getYcoord() - 1));
        allCorners.add(new Coordinate(c.getXcoord() - 1, c.getYcoord() - 1));
        allCorners.add(new Coordinate(c.getXcoord() - 2, c.getYcoord() - 1));
        allCorners.add(new Coordinate(c.getXcoord() + 2, c.getYcoord() - 1));
        allCorners.add(new Coordinate(c.getXcoord() - 2, c.getYcoord()));
        allCorners.add(new Coordinate(c.getXcoord() + 2, c.getYcoord()));
        allCorners.add(new Coordinate(c.getXcoord() - 1, c.getYcoord() + 1));
        allCorners.add(new Coordinate(c.getXcoord() + 1, c.getYcoord() + 1));
      }
    }
    return allCorners;
  }

  /**
   * Method to get all the coordinates directly near a coordinate of the GameTile.
   *
   * @return HashSet with all blockedCoordinates by the GameTile
   * @author pburkhar
   */
  @Override
  public HashSet<Coordinate> getBlockedCoordinates() {
    HashSet<Coordinate> blockedCoordinates = new HashSet<>();
    for (Coordinate c : this.getPosition()) {
      blockedCoordinates.add(new Coordinate(c.getXcoord() + 1, c.getYcoord()));
      blockedCoordinates.add(new Coordinate(c.getXcoord() - 1, c.getYcoord()));
      if ((c.getXcoord() + c.getYcoord()) % 2 == 0) {
        blockedCoordinates.add(new Coordinate(c.getXcoord(), c.getYcoord() + 1));
      } else {
        blockedCoordinates.add(new Coordinate(c.getXcoord(), c.getYcoord() - 1));
      }
    }
    return blockedCoordinates;
  }

  /**
   * Method for initializing the coordinates for the corneredSpace not being blocked by being next
   * to the GameTile directly.
   *
   * @return a HashSet with all the coordinates edging the GameTile
   * @author pburkhar
   */
  @Override
  public HashSet<Coordinate> getCorneredSpaces() {
    HashSet<Coordinate> corneredSpaces = new HashSet<>();
    HashSet<Coordinate> blockedCoordinates = getBlockedCoordinates();
    for (Coordinate c : getAllCorners()) {
      if (!blockedCoordinates.contains(c) && !this.getPosition().contains(c)) {
        corneredSpaces.add(c);
      }
    }
    return corneredSpaces;
  }
}
