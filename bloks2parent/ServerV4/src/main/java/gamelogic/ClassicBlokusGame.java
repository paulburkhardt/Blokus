package gamelogic;

import boards.BoardSpace;
import boards.ClassicBoard;
import datatypes.Coordinate;
import gametiles.GameTile;
import gametiles.GameTileForm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Class for the gamelogic of the ClassicBlokusGame-Mode.
 *
 * @author lfiebig, pburkhar
 */
public class ClassicBlokusGame extends Game {


  /**
   * constructor initializing the TrigonGameTiles.
   *
   * @author lfiebig
   */
  public ClassicBlokusGame() {
    super();
    super.setGameTiles(
        new HashMap<>() {
          {
            put("oneSquarePieceOne", new GameTile(GameTileForm.oneSquarePieceOne));
            put("twoSquaresPieceOne", new GameTile(GameTileForm.twoSquaresPieceOne));
            put("threeSquaresPieceOne", new GameTile(GameTileForm.threeSquaresPieceOne));
            put("threeSquaresPieceTwo", new GameTile(GameTileForm.threeSquaresPieceTwo));
            put("fourSquaresPieceOne", new GameTile(GameTileForm.fourSquaresPieceOne));
            put("fourSquaresPieceTwo", new GameTile(GameTileForm.fourSquaresPieceTwo));
            put("fourSquaresPieceThree", new GameTile(GameTileForm.fourSquaresPieceThree));
            put("fourSquaresPieceFour", new GameTile(GameTileForm.fourSquaresPieceFour));
            put("fourSquaresPieceFive", new GameTile(GameTileForm.fourSquaresPieceFive));
            put("fiveSquaresPieceOne", new GameTile(GameTileForm.fiveSquaresPieceOne));
            put("fiveSquaresPieceTwo", new GameTile(GameTileForm.fiveSquaresPieceTwo));
            put("fiveSquaresPieceThree", new GameTile(GameTileForm.fiveSquaresPieceThree));
            put("fiveSquaresPieceFour", new GameTile(GameTileForm.fiveSquaresPieceFour));
            put("fiveSquaresPieceFive", new GameTile(GameTileForm.fiveSquaresPieceFive));
            put("fiveSquaresPieceSix", new GameTile(GameTileForm.fiveSquaresPieceSix));
            put("fiveSquaresPieceSeven", new GameTile(GameTileForm.fiveSquaresPieceSeven));
            put("fiveSquaresPieceEight", new GameTile(GameTileForm.fiveSquaresPieceEight));
            put("fiveSquaresPieceNine", new GameTile(GameTileForm.fiveSquaresPieceNine));
            put("fiveSquaresPieceTen", new GameTile(GameTileForm.fiveSquaresPieceTen));
            put("fiveSquaresPieceEleven", new GameTile(GameTileForm.fiveSquaresPieceEleven));
            put("fiveSquaresPieceTwelve", new GameTile(GameTileForm.fiveSquaresPieceTwelve));
            setBoard(new ClassicBoard());
          }
        });
  }

  public ClassicBlokusGame(Game game) {
    super(game);
  }

  public String getGameMode() {
    return "Classic";
  }

  // TODO ist das richtig barry?
  @Override
  public boolean isTrigon() {
    return true;
  }

  /**
   * Method to update the boardSpaces, a player can place a gameTile on.
   *
   * @author lfiebig
   */
  public void updatePossibleSpaces(Player player) {
    ArrayList<BoardSpace> toRemove = new ArrayList<>();
    for (BoardSpace bc : player.getPossibleSpaces()) {
      if ((bc.getIsBlockedBy().contains(player.getPlayerId()) || bc.getIsCoveredByWhom() != 0)) {
        toRemove.add(bc);
      }
    }
    for (BoardSpace bs : toRemove) {
      player.getPossibleSpaces().remove(bs);
    }
  }

  /**
   * Method to calculate every valid boardSpace the selected gameTile can be put on.
   *
   * @author lfiebig
   */
  public HashSet<BoardSpace> calculateViableSpaces(Player player) {
    HashSet<BoardSpace> newViableSpaces = new HashSet<>();
    for (BoardSpace possibleSpace : player.getPossibleSpaces()) {
      for (Coordinate partOfTile : player.getSelectedGameTile().getPosition()) {
        if (this.simulateTurn(player, possibleSpace, partOfTile)) {
          newViableSpaces.add(
              this.getBoard()
                  .getBoardSpaces()[possibleSpace.getPosition().getXcoord()
                  - partOfTile.getXcoord()][
                  possibleSpace.getPosition().getYcoord() - partOfTile.getYcoord()]);
        }
      }
    }
    return newViableSpaces;
  }

  /**
   * Method to simulate a turn (used in Method calculate ViableSpaces).
   *
   * @author lfiebig
   */
  public boolean simulateTurn(Player player, BoardSpace possibleSpace, Coordinate partOfTile) {
    Coordinate tmpC;
    BoardSpace tmpBs;
    for (Coordinate c : player.getSelectedGameTile().getPosition()) {
      tmpC =
          new Coordinate(
              c.getXcoord() + possibleSpace.getPosition().getXcoord() - partOfTile.getXcoord(),
              c.getYcoord() + possibleSpace.getPosition().getYcoord() - partOfTile.getYcoord());
      if (!tmpC.validCoordinate(19, 0, 19, 0)) {
        return false;
      } else {
        tmpBs = this.getBoard().getBoardSpaces()[tmpC.getXcoord()][tmpC.getYcoord()];
        if (tmpBs.getIsCoveredByWhom() != 0
            || tmpBs.getIsBlockedBy().contains(player.getPlayerId())) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Method to place a gameTile on the Board.
   *
   * @author lfiebig
   */
  public boolean gameTilePlaced(Player player, BoardSpace position) {
    if (player.getViableSpaces().contains(position)) {
      player.getSelectedGameTile().moveGameTileOnBoard(position);
      for (Coordinate c : player.getSelectedGameTile().getCorneredSpaces()) {
        if (c.getXcoord() >= 0 && c.getYcoord() >= 0 && c.getXcoord() < 20 && c.getYcoord() < 20) {
          player.addPossibleSpace(this.getBoard().getBoardSpaces()[c.getXcoord()][c.getYcoord()]);
        }
      }
      for (Coordinate c : player.getSelectedGameTile().getPosition()) {
        this.getBoard()
            .getBoardSpaces()[c.getXcoord()][c.getYcoord()]
            .setIsCoveredByWhom(player.getPlayerId());
        player.deletePossibleSpace(this.getBoard().getBoardSpaces()[c.getXcoord()][c.getYcoord()]);
      }
      for (Coordinate c : player.getSelectedGameTile().getBlockedCoordinates()) {
        if (c.getXcoord() >= 0 && c.getXcoord() < 20 && c.getYcoord() >= 0 && c.getYcoord() < 20) {
          this.getBoard().getBoardSpaces()[c.getXcoord()][c.getYcoord()].addBlockedBy(
              player.getPlayerId());
          player.deletePossibleSpace(
              this.getBoard().getBoardSpaces()[c.getXcoord()][c.getYcoord()]);
        }
      }
      player.addScore(player.getSelectedGameTile().getSize());
      player.getSelectedGameTile().setPlayed(true);
      return true;
    } else {
      return false;
    }
  }

  /**
   * method to check if a player has turns left.
   *
   * @param player player to check for left turns
   * @return return if player has turns left
   * @author pburkhar
   */
  public boolean checkHasTurnsLeft(Player player) {
    for (String gameTile : player.getGameTiles().keySet()) {
      player.setSelectedGameTile(gameTile);
      if (!player.getSelectedGameTile().isPlayed()) {
        for (int m = 0; m < 2; m++) {
          if (m == 1) {
            player.getSelectedGameTile().mirrorGameTile();
          }
          for (int i = 0; i < 4; i++) {
            if (!this.calculateViableSpaces(player).isEmpty()) {
              player.getSelectedGameTile().resetGameTile();
              player.setSelectedGameTile("null");
              return true;
            }
            player.getSelectedGameTile().rotateGameTile();
          }
        }
      }
      player.getSelectedGameTile().resetGameTile();
    }
    player.setSelectedGameTile("null");
    return false;
  }


  /**
   * adds start spaces for the players.
   */
  public void setInfo() {
    getPlayers().get(0).addPossibleSpace(this.getBoard().getBoardSpaces()[0][0]);
    getPlayers().get(1).addPossibleSpace(this.getBoard().getBoardSpaces()[19][0]);
    getPlayers().get(2).addPossibleSpace(this.getBoard().getBoardSpaces()[0][19]);
    getPlayers().get(3).addPossibleSpace(this.getBoard().getBoardSpaces()[19][19]);
  }


}
