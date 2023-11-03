package ai;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import boards.GameTheme;
import gamelogic.BlokusDuoGame;
import org.junit.Before;
import org.junit.Test;

public class GetPlayTest {
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
  public void getRandomTurn() {
    GetPlay getPlay = new GetPlay();
    Play play = getPlay.getRandomTurn(game, player1.getPlayerId());
    assertFalse(game.getPlayerByid(player1.getPlayerId()).getGameTiles().get(play.getGameTile()).isPlayed());
    assertFalse(
        game.getPlayerByid(player1.getPlayerId())
            .getGameTiles()
            .get(play.getGameTile())
            .isPlayed());
    getPlay = new GetPlay();
    play = getPlay.getRandomTurn(game, player2.getPlayerId());
    assertFalse(
        game.getPlayerByid(player2.getPlayerId())
            .getGameTiles()
            .get(play.getGameTile())
            .isPlayed());
  }

  @Test
  public void getLargestPlay() {
    GetPlay getPlay = new GetPlay();
    Play play = getPlay.getLargestPlay(game, player1.getPlayerId());
    assertFalse(
        game.getPlayerByid(player1.getPlayerId())
            .getGameTiles()
            .get(play.getGameTile())
            .isPlayed());
    assertEquals(play.getSize(), 5);
    getPlay = new GetPlay();
    play = getPlay.getLargestPlay(game, player2.getPlayerId());
    assertFalse(
        game.getPlayerByid(player2.getPlayerId())
            .getGameTiles()
            .get(play.getGameTile())
            .isPlayed());
    assertEquals(play.getSize(), 5);
  }

  @Test
  public void getBestPlay() {
    GetPlay getPlay = new GetPlay();
    Play play = getPlay.getBestPlay(game, player1.getPlayerId());
    assertFalse(
        game.getPlayerByid(player1.getPlayerId())
            .getGameTiles()
            .get(play.getGameTile())
            .isPlayed());
    assertEquals(play.getSize(), 5);
    getPlay = new GetPlay();
    play = getPlay.getBestPlay(game, player2.getPlayerId());
    assertFalse(
        game.getPlayerByid(player2.getPlayerId())
            .getGameTiles()
            .get(play.getGameTile())
            .isPlayed());
    assertEquals(play.getSize(), 5);
  }
}
