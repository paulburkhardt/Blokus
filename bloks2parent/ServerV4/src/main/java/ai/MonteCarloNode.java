package ai;

import gamelogic.Game;
import gamelogic.Player;
import java.util.ArrayList;
import java.util.List;

/**
 * node to build the search tree - contains the statistics and playerID as well as the children and
 * parent node.
 *
 * @author pboensch
 */
public class MonteCarloNode {
  Play play;
  MonteCarloNode parent;
  List<MonteCarloNode> childArray;
  boolean turnsLeft;
  int playerId;
  int visitCount;
  int winScore;

  /** create new node. */
  public MonteCarloNode() {}

  /** create new node. */
  public MonteCarloNode(Game game, int playerId) {
    this.playerId = playerId;
    this.turnsLeft = hasTurnsLeft(game);
    this.childArray = new ArrayList<>();
  }

  /** create new node. */
  public MonteCarloNode(Play play, int playerId, MonteCarloNode parent, boolean hasTurnsLeft) {
    this.play = play;
    this.playerId = playerId;
    this.parent = parent;
    this.turnsLeft = hasTurnsLeft;
    this.childArray = new ArrayList<>();
  }

  /** create new node. */
  public MonteCarloNode(MonteCarloNode node) {
    this.playerId = node.getPlayerId();
    this.play = node.getPlay();
    this.parent = node.getParent();
    this.childArray = node.getChildArray();
  }

  /**
   * returns all possible nodes.
   *
   * @param playersTurn playerID of the player who is performing the next turn
   * @param gameCopy a copy of the current game
   * @return list of all possible nodes
   */
  public List<MonteCarloNode> getAllPossibleNodes(int playersTurn, Game gameCopy) {
    GetPlay randomTurn = new GetPlay();
    List<Play> possiblePlays = randomTurn.getPossiblePlays(gameCopy, playersTurn);
    List<MonteCarloNode> possibleNodes = new ArrayList<>();
    for (Play possiblePlay : possiblePlays) {
      // update Game
      MonteCarloNode newNode =
          new MonteCarloNode(possiblePlay, playersTurn, this, hasTurnsLeft(gameCopy));
      newNode.getPlay().setPlayerId(playersTurn);
      possibleNodes.add(newNode);
    }
    return possibleNodes;
  }

  /**
   * checks if a player has turns left.
   *
   * @param gameCopy a copy of the current game
   * @return boolean if player has turns left
   */
  public boolean hasTurnsLeft(Game gameCopy) {
    GetPlay randomTurn = new GetPlay();
    for (Player player : gameCopy.getPlayers()) {
      if (randomTurn.playsLeft(gameCopy, player.getPlayerId())) {
        return true;
      }
    }
    return false;
  }

  public boolean hasTurnsLeft() {
    return turnsLeft;
  }

  /**
   * checks which node has most wins.
   *
   * @return child node with maximum wins
   */
  public MonteCarloNode getChildWithMaxWins() {
    int highscore = 0;
    MonteCarloNode winnerNode = new MonteCarloNode();
    for (MonteCarloNode child : childArray) {
      if (child.getWinScore() > highscore) {
        highscore = child.getWinScore();
        winnerNode = child;
      }
    }
    return winnerNode;
  }

  public MonteCarloNode getParent() {
    return this.parent;
  }

  public List<MonteCarloNode> getChildArray() {
    return this.childArray;
  }

  public Play getPlay() {
    return play;
  }

  public int getPlayerId() {
    return playerId;
  }

  public int getVisitCount() {
    return visitCount;
  }

  public int getWinScore() {
    return winScore;
  }

  public void incrementVisit() {
    this.visitCount++;
  }

  public void addScore() {
    this.winScore++;
  }
}
