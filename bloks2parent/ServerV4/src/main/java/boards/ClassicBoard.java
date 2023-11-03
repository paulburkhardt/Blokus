package boards;

/**
 * Board for Classic-Gamemode.
 *
 * @author lfiebig
 */
public class ClassicBoard extends Board {

  public ClassicBoard() {
  }

  /**
   * Fills BoardSpacesArray with BoardSpaces.
   */
  public void createBoard() {
    BoardSpace[][] boardSpacesArray = new BoardSpace[20][20];
    for (int i = 0; i < 20; i++) {
      for (int j = 0; j < 20; j++) {
        boardSpacesArray[i][j] = new BoardSpace(i, j);
      }
    }
    this.setBoardSpaces(boardSpacesArray);
  }

  /**
   * Method that prints Board into Console.
   */
  public void printBoard() {
    for (int i = 19; i >= 0; i--) {
      for (int j = 0; j < 20; j++) {
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
          case 3:
            System.out.print("3 ");
            break;
          case 4:
            System.out.print("4 ");
            break;
          default:
            break;
        }
      }
      System.out.println();
    }
  }
}
