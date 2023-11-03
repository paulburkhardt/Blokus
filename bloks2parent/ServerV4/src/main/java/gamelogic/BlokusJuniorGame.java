package gamelogic;

import boards.BoardSpace;
import boards.TwoPlayerBoard;
import datatypes.Coordinate;
import gametiles.GameTile;
import gametiles.GameTileForm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Class for the gamelogic of the BlokusJuniorGame-Mode.
 *
 * @author pburkhar
 */
public class BlokusJuniorGame extends Game {

  /**
   * constructor initializing the gametiles.
   */
  public BlokusJuniorGame() {
    super();
    super.setGameTiles(
        new HashMap<>() {
          {
            put("oneSquarePieceOne", new GameTile(GameTileForm.oneSquarePieceOne));
            put("oneSquarePieceTwo", new GameTile(GameTileForm.oneSquarePieceOne));
            put("twoSquaresPieceOne", new GameTile(GameTileForm.twoSquaresPieceOne));
            put("twoSquaresPieceTwo", new GameTile(GameTileForm.twoSquaresPieceOne));
            put("threeSquaresPieceOne", new GameTile(GameTileForm.threeSquaresPieceOne));
            put("threeSquaresPieceTwo", new GameTile(GameTileForm.threeSquaresPieceOne));
            put("threeSquaresPieceThree", new GameTile(GameTileForm.threeSquaresPieceTwo));
            put("threeSquaresPieceFour", new GameTile(GameTileForm.threeSquaresPieceTwo));
            put("fourSquaresPieceOne", new GameTile(GameTileForm.fourSquaresPieceOne));
            put("fourSquaresPieceTwo", new GameTile(GameTileForm.fourSquaresPieceOne));
            put("fourSquaresPieceThree", new GameTile(GameTileForm.fourSquaresPieceTwo));
            put("fourSquaresPieceFour", new GameTile(GameTileForm.fourSquaresPieceTwo));
            put("fourSquaresPieceFive", new GameTile(GameTileForm.fourSquaresPieceThree));
            put("fourSquaresPieceSix", new GameTile(GameTileForm.fourSquaresPieceThree));
            put("fourSquaresPieceSeven", new GameTile(GameTileForm.fourSquaresPieceFour));
            put("fourSquaresPieceEight", new GameTile(GameTileForm.fourSquaresPieceFour));
            put("fourSquaresPieceNine", new GameTile(GameTileForm.fourSquaresPieceFive));
            put("fourSquaresPieceTen", new GameTile(GameTileForm.fourSquaresPieceFive));
            put("fiveSquaresPieceOne", new GameTile(GameTileForm.fiveSquaresPieceTwo));
            put("fiveSquaresPieceTwo", new GameTile(GameTileForm.fiveSquaresPieceTwo));
            put("fiveSquaresPieceThree", new GameTile(GameTileForm.fiveSquaresPieceThree));
            put("fiveSquaresPieceFour", new GameTile(GameTileForm.fiveSquaresPieceThree));
            put("fiveSquaresPieceFive", new GameTile(GameTileForm.fiveSquaresPieceFour));
            put("fiveSquaresPieceSix", new GameTile(GameTileForm.fiveSquaresPieceFour));
            setBoard(new TwoPlayerBoard());
          }
        });
  }

  public BlokusJuniorGame(Game game) {
    super(game);
  }

  public String getGameMode() {
    return "Junior";
  }

  @Override
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

  @Override
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

  @Override
  public boolean gameTilePlaced(Player player, BoardSpace position) {
    if (player.getViableSpaces().contains(position)) {
      player.getSelectedGameTile().moveGameTileOnBoard(position);
      for (Coordinate c : player.getSelectedGameTile().getCorneredSpaces()) {
        if (c.getXcoord() >= 0 && c.getYcoord() >= 0 && c.getXcoord() < 14 && c.getYcoord() < 14) {
          player.addPossibleSpace(this.getBoard().getBoardSpaces()[c.getXcoord()][c.getYcoord()]);
        }
      }
      for (Coordinate c : player.getSelectedGameTile().getPosition()) {
        this.getBoard()
            .getBoardSpaces()[c.getXcoord()][c.getYcoord()]
            .setIsCoveredByWhom(player.getPlayerId());
      }
      for (Coordinate c : player.getSelectedGameTile().getBlockedCoordinates()) {
        if (c.getXcoord() >= 0 && c.getXcoord() < 14 && c.getYcoord() >= 0 && c.getYcoord() < 14) {
          this.getBoard().getBoardSpaces()[c.getXcoord()][c.getYcoord()].addBlockedBy(
              player.getPlayerId());
        }
      }
      player.addScore(player.getSelectedGameTile().getSize());
      player.getSelectedGameTile().setPlayed(true);
      return true;
    } else {
      return false;
    }
  }

  @Override
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
   * Method to simulate a turn and see if a possibleSpace is valid.
   *
   * @param player        player doing the turn
   * @param possibleSpace possibleSpace to check.
   * @param partOfTile    partOfTile which should be placed on the viableSpace
   * @return returns if the turn placing the partOfTile on the possibleSpace is valid
   */
  public boolean simulateTurn(Player player, BoardSpace possibleSpace, Coordinate partOfTile) {
    Coordinate tmpC;
    BoardSpace tmpBs;
    for (Coordinate c : player.getSelectedGameTile().getPosition()) {
      tmpC =
          new Coordinate(
              c.getXcoord() + possibleSpace.getPosition().getXcoord() - partOfTile.getXcoord(),
              c.getYcoord() + possibleSpace.getPosition().getYcoord() - partOfTile.getYcoord());
      if (!tmpC.validCoordinate(13, 0, 13, 0)) {
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


  public void setInfo() {
    getPlayers().get(0).addPossibleSpace(this.getBoard().getBoardSpaces()[4][9]);
    getPlayers().get(1).addPossibleSpace(this.getBoard().getBoardSpaces()[9][4]);
  }
}
