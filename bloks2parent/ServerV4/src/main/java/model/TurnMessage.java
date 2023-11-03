package model;

import boards.BoardSpace;
import gametiles.GameTile;

/**
 * ChatMessage class, will be used for WebSocket chat communication.
 *
 * @author jbuechs
 */
public class TurnMessage extends Message {
  private BoardSpace boardSpace;
  private GameTile gameTile;

  /** creates turMessage. */
  public TurnMessage() {
    super(730);
  }

  /**
   * creates turMessage.
   *
   * @param boardSpace given boardSpace
   * @param gameTile given gameTile
   */
  public TurnMessage(BoardSpace boardSpace, GameTile gameTile) {
    super(731);
    this.boardSpace = boardSpace;
    this.gameTile = gameTile;
  }

  public void setBoardSpace(BoardSpace boardSpace) {
    this.boardSpace = boardSpace;
  }

  public void setGameTile(GameTile gameTile) {
    this.gameTile = gameTile;
  }

  public BoardSpace getBoardSpace() {
    return boardSpace;
  }

  public GameTile getGameTile() {
    return gameTile;
  }
}
