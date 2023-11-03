package ai;

import boards.GameTheme;
import gamelogic.Game;
import gamelogic.Player;

/**
 * AiPlayer extends the Player and plays a turn.
 *
 * @author pboensch
 */
public class AiPlayer extends Player {

  public AiPlayer(String username, GameTheme gameTheme) {
    super(username, gameTheme);
  }

  public AiPlayer(String username, GameTheme gameTheme, int level) {
    super(username, gameTheme);
    this.level = level;
  }

  /**
   * Executes a turn in the current game.
   *
   * @param game current game
   */
  public void doTurn(Game game) {
    MonteCarlo mcts;
    Play play;
    // check if the player has turns left
    if (!this.getHasTurnsLeft() || !game.checkHasTurnsLeft(this)) {
      this.setHasTurnsLeft(false);
    } else {
      // choose selected AI level
      if (level == 1) {
        GetPlay randomTurn = new GetPlay();
        play = randomTurn.getRandomTurn(game, this.getPlayerId());
      } else if (level == 2) {
        GetPlay randomTurn = new GetPlay();
        play = randomTurn.getLargestPlay(game, this.getPlayerId());
      } else {
        // monte carlo tree search if the number of possibel plays is under 100
        GetPlay randomTurn = new GetPlay();
        mcts = new MonteCarlo(this.getPlayerId());
        if (randomTurn.getPossiblePlays(game, this.getPlayerId()).size() < 50
            && !game.getGameMode().equals("Trigon")) {
          play = mcts.findBestPlay(game);
        } else {
          play = randomTurn.getBestPlay(game, this.getPlayerId());
        }
      }
      // selects the given GameTile
      this.setSelectedGameTile(play.getGameTile());

      // performs turn for trigon
      if (game.getGameMode().equals("Trigon")) {
        for (int i = 0; i < play.getRotation(); i++) {
          this.getSelectedGameTile().rotateGameTile();
        }
        if (play.isMirrored()) {
          this.getSelectedGameTile().mirrorGameTile();
        }
        // performs turn for classic blokus
      } else {
        if (play.isMirrored()) {
          this.getSelectedGameTile().mirrorGameTile();
        }
        for (int i = 0; i < play.getRotation(); i++) {
          this.getSelectedGameTile().rotateGameTile();
        }
      }
      // execute turn
      this.setViableSpaces(game.calculateViableSpaces(this));
      game.gameTilePlaced(this, play.getPosition());
      this.getSelectedGameTile().setPlayed(true);
      this.getSelectedGameTile().resetGameTile();
      this.setSelectedGameTile("null");
    }
  }
}
