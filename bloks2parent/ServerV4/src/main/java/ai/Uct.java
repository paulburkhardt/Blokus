package ai;

import java.util.Collections;
import java.util.Comparator;

/**
 * calculates the uct value to select a node.
 *
 * @author pboensch
 */
public class Uct {

  /**
   * calculates the uct value.
   *
   * @param totalVisit number of total visits - visits from the parent node
   * @param nodeWinScore number of node wins
   * @param nodeVisit number of node visits
   * @return uct value
   */
  public static double uctValue(int totalVisit, int nodeWinScore, int nodeVisit) {
    if (nodeVisit == 0) {
      return Integer.MAX_VALUE;
    }
    return ((double) nodeWinScore / (double) nodeVisit)
        + 1.41 * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
  }

  /**
   * chooses the node with the lowest uct value.
   *
   * @param node parent node form which the selection starts
   * @return node with max uct value
   */
  public static MonteCarloNode findBestNodeWithUct(MonteCarloNode node) {
    int parentVisit = node.getVisitCount();
    return Collections.max(
        node.getChildArray(),
        Comparator.comparing(c -> uctValue(parentVisit, c.getWinScore(), c.getVisitCount())));
  }
}
