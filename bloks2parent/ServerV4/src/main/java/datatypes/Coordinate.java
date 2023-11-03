package datatypes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Class modeling a Coordinate with a x-value and a y-value.
 *
 * @author lfiebig, pburkhar
 */
public class Coordinate {

  private int xcoord;
  private int ycoord;

  @JsonCreator
  public Coordinate(@JsonProperty("xcoord") int xcoord, @JsonProperty("ycoord") int ycoord) {
    this.xcoord = xcoord;
    this.ycoord = ycoord;
  }

  public int getXcoord() {
    return xcoord;
  }

  /**
   * Method for TrigonBoard to adjust the x-value for Trigon BoardSpaceArray.
   *
   * @return gives the x value normed for the BoardGrid of the Trigon Board
   * @author pburkhar
   */
  public int trigonX() {
    return (xcoord + 17);
  }

  public void setXcoord(int xcoord) {
    this.xcoord = xcoord;
  }

  public int getYcoord() {
    return ycoord;
  }

  /**
   * Method for TrigonBoard to adjust the y-value for Trigon BoardSpaceArray.
   *
   * @return gives the y value normed for the BoardGrid of the Trigon Board
   * @author pburkhar
   */
  public int trigonY() {
    return (ycoord + 9);
  }

  public void setYcoord(int ycoord) {
    this.ycoord = ycoord;
  }

  public boolean equals(Object o) {
    return this.xcoord == ((Coordinate) o).xcoord && this.ycoord == ((Coordinate) o).ycoord;
  }

  @JsonIgnore
  public int getOrientation() {
    return Math.abs(this.getXcoord() + this.getYcoord()) % 2;
  }

  public String toString() {
    return "x = " + this.xcoord + "; y = " + this.ycoord;
  }

  public int hashCode() {
    return Objects.hash(xcoord, ycoord);
  }

  public boolean validCoordinate(int maxX, int minX, int maxY, int minY) {
    return xcoord <= maxX && xcoord >= minX && ycoord <= maxY && ycoord >= minY;
  }
}
