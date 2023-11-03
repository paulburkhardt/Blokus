package gamelogic;

import static org.junit.Assert.*;

import boards.BoardSpace;
import boards.GameTheme;
import org.junit.Before;
import org.junit.Test;

public class BlokusTrigonGameTest {
  private BlokusTrigonGame game;
  private BoardSpace[][] boardspaces;
  private Player player1;


  @Before
  public void setUp() {
    this.game = new BlokusTrigonGame();
    this.player1 = new Player("TiloTest", GameTheme.NEON);
    boardspaces = game.getBoard().getBoardSpaces();
    player1.addPossibleSpace(boardspaces[17][9]);
    game.addPlayer(player1);
    this.player1.setSelectedGameTile("trigonSixSpacesFive");
    player1.setViableSpaces(game.calculateViableSpaces(player1));
  }

  @Test
  public void testCalculateViableSpaces() {
    System.out.println(game.calculateViableSpaces(player1));
    assertTrue(game.calculateViableSpaces(player1).contains(boardspaces[15][9]));
    this.game.gameTilePlaced(this.player1, boardspaces[15][9]);
    assertTrue(game.calculateViableSpaces(player1).contains(boardspaces[22][8]));
    assertTrue(game.calculateViableSpaces(player1).contains(boardspaces[15][11]));
  }

  @Test
  public void testGameTilePlaced() {
    assertEquals(this.player1.getSelectedGameTile().toCoordinates(), " [0,0] [1,0] [2,0] [2,1] [3,0] [4,0]");
    assertTrue(this.game.gameTilePlaced(this.player1, boardspaces[15][9]));
    assertEquals(1, this.boardspaces[17][9].getIsCoveredByWhom());
    assertTrue(this.boardspaces[15][10].getIsBlockedBy().contains(1));
  }
}