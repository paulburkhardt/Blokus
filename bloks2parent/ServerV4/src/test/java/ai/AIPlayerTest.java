package ai;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import boards.GameTheme;
import gamelogic.BlokusDuoGame;
import gamelogic.Game;
import org.junit.Before;
import org.junit.Test;

/**
 * AI test to demonstrate the execution of valid turns
 *
 * @author pboensch
 */
public class AIPlayerTest {
  AiPlayer player1;
  AiPlayer player2;
  BlokusDuoGame game;

  @Before
  public void start() {
    game = new BlokusDuoGame();
    player1 = new AiPlayer("Lennart", GameTheme.NEON);
    player2 = new AiPlayer("Pirmin", GameTheme.NEON);
    player1.setLevel(2);
    player2.setLevel(1);
    player1.addPossibleSpace(game.getBoard().getBoardSpaces()[4][9]);
    player2.addPossibleSpace(game.getBoard().getBoardSpaces()[9][4]);
    game.addPlayer(player1);
    game.addPlayer(player2);
  }

  @Test
  public void doTurn() {
    while (game.checkHasTurnsLeft(player1) || game.checkHasTurnsLeft(player2)) {
      Game gameCopy = new BlokusDuoGame(game);
      player1.doTurn(game);
      game.getBoard().printBoard();
      assertNotEquals(game, gameCopy);
      gameCopy = new BlokusDuoGame(game);
      player2.doTurn(game);
      assertNotEquals(game, gameCopy);
    }
  }
}
