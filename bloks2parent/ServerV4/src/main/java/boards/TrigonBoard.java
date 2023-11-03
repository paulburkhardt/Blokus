package boards;

/**
 * Board for Trigon-Gamemode.
 *
 * @author pburkhar
 */
public class TrigonBoard extends Board {

  /**
   * Fills BoardSpacesArray with BoardSpaces.
   *
   */
  @Override
  public void createBoard() {
    BoardSpace[][] boardSpacesArray = new BoardSpace[35][18];
    for (int j = 0; j < 9; j++) {
      for (int i = 8 - j; i < 27 + j; i++) {
        boardSpacesArray[i][j] = new BoardSpace(i - 17, j - 9);
      }
    }
    for (int j = 9; j < 18; j++) {
      for (int i = -9 + j; i < 35 + 9 - j; i++) {
        boardSpacesArray[i][j] = new BoardSpace(i - 17, j - 9);
      }
    }
    this.setBoardSpaces(boardSpacesArray);
  }

  /**
   * Method that prints Board into Console.
   */
  public void printBoard() {
    for (int j = 17; j >= 0; j--) {
      for (int i = 0; i < 35; i++) {
        if (getBoardSpaces()[i][j] != null) {
          switch (this.getBoardSpaces()[i][j].getIsCoveredByWhom()) {
            case 0:
              if ((i + j) % 2 == 0) {
                System.out.print("V");
              } else {
                System.out.print("A");
              }
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
            default: {
            }
          }

        } else {
          System.out.print(" ");
        }
      }
      System.out.println();
    }
  }
}
