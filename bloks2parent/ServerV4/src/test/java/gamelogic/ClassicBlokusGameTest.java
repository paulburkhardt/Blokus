package gamelogic;

import static org.junit.Assert.*;

import boards.BoardSpace;
import boards.GameTheme;
import org.junit.Before;
import org.junit.Test;

public class ClassicBlokusGameTest {
private ClassicBlokusGame game;
private BoardSpace[][] boardspaces;
private Player player1;
  @Before
  public void setUp(){
    this.game = new ClassicBlokusGame();
    this.player1 = new Player("TiloTest", GameTheme.NEON);
    boardspaces = game.getBoard().getBoardSpaces();
    player1.addPossibleSpace(boardspaces[0][0]);
    game.addPlayer(player1);
    this.player1.setSelectedGameTile("fourSquaresPieceThree");
    player1.setViableSpaces(game.calculateViableSpaces(player1));
  }


  @Test
  public void testCalculateViableSpaces() {
    assertTrue(game.calculateViableSpaces(player1).contains(boardspaces[0][0]));
    this.game.gameTilePlaced(this.player1, boardspaces[0][0]);
    assertTrue(game.calculateViableSpaces(player1).contains(boardspaces[2][2]));
    assertTrue(game.calculateViableSpaces(player1).contains(boardspaces[1][3]));
  }

  @Test
  public void testGameTilePlaced() {
    assertEquals(this.player1.getSelectedGameTile().toCoordinates(), " [0,0] [0,1] [0,2] [1,1]");
    assertTrue(this.game.gameTilePlaced(this.player1, boardspaces[0][0]));
    assertEquals(1, boardspaces[0][0].getIsCoveredByWhom());
    assertTrue(boardspaces[1][0].getIsBlockedBy().contains(1));
  }
}