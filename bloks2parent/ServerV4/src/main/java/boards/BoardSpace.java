package boards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import datatypes.Coordinate;
import java.util.HashSet;
import java.util.Objects;

/**
 * A BoardSpace is a part of a Board building the entire Board.
 *
 * @author lfiebig
 */
public class BoardSpace {

  private int isCoveredByWhom = 0;
  private final Coordinate position;
  private HashSet<Integer> isBlockedBy = new HashSet<>();

  public BoardSpace(int x, int y) {
    this.position = new Coordinate(x, y);
    isBlockedBy.add(0);
  }

  /**
   * Json Constructor needed for Websocket transfer.
   *
   * @param isCoveredByWhom id of player covering BoardSpace
   * @param position        Coordinate of the position of the BoardSpace
   * @param isBlockedBy     HashSet containing all ids of players blocking this space
   */

  @JsonCreator
  public BoardSpace(
      @JsonProperty("isCoveredByWhom") int isCoveredByWhom,
      @JsonProperty("position") Coordinate position,
      @JsonProperty("isBlockedBy") HashSet<Integer> isBlockedBy) {
    this.isCoveredByWhom = isCoveredByWhom;
    this.position = position;
    this.isBlockedBy = isBlockedBy;
  }

  public int getIsCoveredByWhom() {
    return isCoveredByWhom;
  }

  public void setIsCoveredByWhom(int isCoveredByWhom) {
    this.isCoveredByWhom = isCoveredByWhom;
  }

  public Coordinate getPosition() {
    return position;
  }

  public HashSet<Integer> getIsBlockedBy() {
    return isBlockedBy;
  }

  public void setIsBlockedBy(HashSet<Integer> isBlockedBy) {
    this.isBlockedBy = isBlockedBy;
  }

  public void addBlockedBy(int id) {
    this.isBlockedBy.add(id);
  }

  public String toString() {
    return "[ " + position.getXcoord() + "," + position.getYcoord() + "]";
  }

  @Override
  public boolean equals(Object o) {
    return this.position.equals(((BoardSpace) o).position);
  }

  public int hashCode() {
    return Objects.hash(this.position.getXcoord(), this.position.getYcoord());
  }
}
