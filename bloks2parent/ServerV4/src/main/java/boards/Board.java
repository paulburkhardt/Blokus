package boards;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Abstract class for all boards of the different GameModes.
 *
 * @author lfiebig
 */

// @JsonDeserialize(as=ClassicBoard.class)
public abstract class Board {

  private BoardSpace[][] boardSpaces;

  public Board() {
    createBoard();
  }

  public BoardSpace[][] getBoardSpaces() {
    return boardSpaces;
  }

  public void setBoardSpaces(BoardSpace[][] boardSpaces) {
    this.boardSpaces = boardSpaces;
  }

  public abstract void createBoard();

  public abstract void printBoard();

  /**
   * Serializes Board to Json String for Websocket transfer.
   *
   * @return Json String containing Board
   */
  // is necessary for websocket transfer
  public String serializeBoard() {
    ObjectMapper objectMapper = new ObjectMapper();
    String jsonString = "";
    try {
      jsonString = objectMapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return jsonString;
  }
}
