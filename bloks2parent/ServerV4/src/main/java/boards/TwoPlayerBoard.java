package boards;

/**
 * Board for TwoPlayer-Gamemodes.
 *
 * @author pburkhar
 */
public class TwoPlayerBoard extends Board {

  /**
   * Fills BoardSpacesArray with BoardSpaces.
   */
  public void createBoard() {
    BoardSpace[][] boardSpacesArray = new BoardSpace[14][14];
    for (int i = 0; i < 14; i++) {
      for (int j = 0; j < 14; j++) {
        boardSpacesArray[i][j] = new BoardSpace(i, j);
      }
    }
    this.setBoardSpaces(boardSpacesArray);
  }

  /**
   * Method that prints Board into Console.
   */
  public void printBoard() {
    for (int i = 13; i >= 0; i--) {
      for (int j = 0; j < 14; j++) {
        switch (this.getBoardSpaces()[j][i].getIsCoveredByWhom()) {
          case 0:
            System.out.print("x ");
            break;
          case 1:
            System.out.print("1 ");
            break;
          case 2:
            System.out.print("2 ");
            break;
          default:
            break;
        }
      }
      System.out.println();
    }
  }
}
