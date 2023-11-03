package gamelogic;

import boards.BoardSpace;
import boards.TrigonBoard;
import datatypes.Coordinate;
import gametiles.GameTile;
import gametiles.GameTileForm;
import gametiles.TrigonGameTile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

/**
 * Class for the gamelogic of the BlokusTrigonGame-Mode.
 *
 * @author pburkhar
 */
public class BlokusTrigonGame extends Game {


  /**
   * constructor initializing the TrigonGameTiles.
   *
   * @author pburkhar
   */
  public BlokusTrigonGame() {
    super();
    super.setGameTiles(
        new HashMap<>() {
          {
            put("trigonOneSpaceOne", new TrigonGameTile(GameTileForm.trigonOneSpaceOne));
            put("trigonTwoSpacesOne", new TrigonGameTile(GameTileForm.trigonTwoSpacesOne));
            put("trigonThreeSpacesOne", new TrigonGameTile(GameTileForm.trigonThreeSpacesOne));
            put("trigonFourSpacesOne", new TrigonGameTile(GameTileForm.trigonFourSpacesOne));
            put("trigonFourSpacesTwo", new TrigonGameTile(GameTileForm.trigonFourSpacesTwo));
            put("trigonFourSpacesThree", new TrigonGameTile(GameTileForm.trigonFourSpacesThree));
            put("trigonFiveSpacesOne", new TrigonGameTile(GameTileForm.trigonFiveSpacesOne));
            put("trigonFiveSpacesTwo", new TrigonGameTile(GameTileForm.trigonFiveSpacesTwo));
            put("trigonFiveSpacesThree", new TrigonGameTile(GameTileForm.trigonFiveSpacesThree));
            put("trigonFiveSpacesFour", new TrigonGameTile(GameTileForm.trigonFiveSpacesFour));
            put("trigonSixSpacesOne", new TrigonGameTile(GameTileForm.trigonSixSpacesOne));
            put("trigonSixSpacesTwo", new TrigonGameTile(GameTileForm.trigonSixSpacesTwo));
            put("trigonSixSpacesThree", new TrigonGameTile(GameTileForm.trigonSixSpacesThree));
            put("trigonSixSpacesFour", new TrigonGameTile(GameTileForm.trigonSixSpacesFour));
            put("trigonSixSpacesFive", new TrigonGameTile(GameTileForm.trigonSixSpacesFive));
            put("trigonSixSpacesSix", new TrigonGameTile(GameTileForm.trigonSixSpacesSix));
            put("trigonSixSpacesSeven", new TrigonGameTile(GameTileForm.trigonSixSpacesSeven));
            put("trigonSixSpacesEight", new TrigonGameTile(GameTileForm.trigonSixSpacesEight));
            put("trigonSixSpacesNine", new TrigonGameTile(GameTileForm.trigonSixSpacesNine));
            put("trigonSixSpacesTen", new TrigonGameTile(GameTileForm.trigonSixSpacesTen));
            put("trigonSixSpacesEleven", new TrigonGameTile(GameTileForm.trigonSixSpacesEleven));
            put("trigonSixSpacesTwelve", new TrigonGameTile(GameTileForm.trigonSixSpacesTwelve));
          }
        });
    this.setBoard(new TrigonBoard());
  }

  public BlokusTrigonGame(Game game) {
    super(game);
  }

  public String getGameMode() {
    return "Trigon";
  }

  /**
   * Method to add a player to the game.
   *
   * @author pburkhar
   */
  @Override
  public void addPlayer(Player player) {
    TreeMap<String, GameTile> gameTilesCopy = new TreeMap<>();
    for (String gt : this.getGameTiles().keySet()) {
      TrigonGameTile newGameTile =
          new TrigonGameTile(this.getGameTiles().get(gt).getGameTileForm());
      gameTilesCopy.put(gt, newGameTile);
    }

    player.setGameTiles(gameTilesCopy);
    this.getPlayers().add(player);
    System.out.println("Player: " + player.getUsername() + " was added to the Game");
  }

  /**
   * Method to update the boardSpaces, a player can place a gameTile on.
   *
   * @param player to update possible playable spaces for
   * @author pburkhar
   */
  @Override
  public void updatePossibleSpaces(Player player) {
    ArrayList<BoardSpace> toRemove = new ArrayList<>();
    if (player.isFirstTurn()) {
      toRemove.add(this.getBoard().getBoardSpaces()[17][3]);
      toRemove.add(this.getBoard().getBoardSpaces()[9][6]);
      toRemove.add(this.getBoard().getBoardSpaces()[9][11]);
      toRemove.add(this.getBoard().getBoardSpaces()[25][6]);
      toRemove.add(this.getBoard().getBoardSpaces()[25][11]);
      toRemove.add(this.getBoard().getBoardSpaces()[17][14]);
      player.setFirstTurn(false);
    }
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
                  .getBoardSpaces()[possibleSpace.getPosition().trigonX() - partOfTile.getXcoord()][
                  possibleSpace.getPosition().trigonY() - partOfTile.getYcoord()]);
        }
      }
    }
    return newViableSpaces;
  }

  /**
   * Method to place a gameTile on the Board.
   *
   * @author pburkhar
   */
  @Override
  public boolean gameTilePlaced(Player player, BoardSpace position) {
    if (player.getViableSpaces().contains(position)) {
      player.getSelectedGameTile().moveGameTileOnBoard(position);
      for (Coordinate c : player.getSelectedGameTile().getCorneredSpaces()) {
        int tmpX = c.getXcoord() < 0 ? c.getXcoord() * -1 : c.getXcoord();
        int tmpY = c.getYcoord() < 0 ? c.getYcoord() * -1 - 1 : c.getYcoord();
        if (tmpY < 9 && tmpX + tmpY < 18) {
          player.addPossibleSpace(this.getBoard().getBoardSpaces()[c.trigonX()][c.trigonY()]);
        }
      }
      for (Coordinate c : player.getSelectedGameTile().getPosition()) {
        this.getBoard()
            .getBoardSpaces()[c.trigonX()][c.trigonY()]
            .setIsCoveredByWhom(player.getPlayerId());
        player.deletePossibleSpace(this.getBoard().getBoardSpaces()[c.trigonX()][c.trigonY()]);
      }
      HashSet<Coordinate> blocked = player.getSelectedGameTile().getBlockedCoordinates();
      for (Coordinate c : blocked) {
        if (c.getXcoord() == -1 && c.getYcoord() == 4) {
          System.out.println(blocked);
          player.getSelectedGameTile().getBlockedCoordinates();
        }
        int tmpX = c.getXcoord() < 0 ? c.getXcoord() * -1 : c.getXcoord();
        int tmpY = c.getYcoord() < 0 ? (c.getYcoord() * -1) - 1 : c.getYcoord();
        if (tmpY < 9 && tmpX + tmpY < 18) {
          this.getBoard()
              .getBoardSpaces()[c.trigonX()][c.trigonY()]
              .addBlockedBy(player.getPlayerId());
          player.deletePossibleSpace(this.getBoard().getBoardSpaces()[c.trigonX()][c.trigonY()]);
        }
      }
      player.addScore(player.getSelectedGameTile().getSize());
      player.getSelectedGameTile().setPlayed(true);
      this.updatePossibleSpaces(player);
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
        for (int i = 0; i < 6; i++) {
          for (int m = 0; m < 2; m++) {
            if (m == 1) {
              player.getSelectedGameTile().mirrorGameTile();
            }
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
   * Method to simulate a turn (used in Method calculate ViableSpaces).
   *
   * @author pburkhar
   */
  public boolean simulateTurn(Player player, BoardSpace possibleSpace, Coordinate partOfTile) {
    Coordinate tmpC;
    BoardSpace tmpBs;
    if (partOfTile.getOrientation() != possibleSpace.getPosition().getOrientation()) {
      return false;
    }
    // [possibleSpace.getPosition().TrigonX() - partOfTile.getX()]
    for (int i = 0; i < player.getSelectedGameTile().getPosition().size(); i++) {
      Coordinate c = player.getSelectedGameTile().getPosition().get(i);
      tmpC =
          new Coordinate(
              c.getXcoord() + possibleSpace.getPosition().getXcoord() + -partOfTile.getXcoord(),
              c.getYcoord() + possibleSpace.getPosition().getYcoord() + -partOfTile.getYcoord());
      int tmpX = tmpC.getXcoord() < 0 ? tmpC.getXcoord() * -1 : tmpC.getXcoord();
      int tmpY = tmpC.getYcoord() < 0 ? tmpC.getYcoord() * -1 - 1 : tmpC.getYcoord();
      if (tmpY > 8 || tmpX + tmpY > 17) {
        return false;
      } else {
        tmpBs = this.getBoard().getBoardSpaces()[tmpC.trigonX()][tmpC.trigonY()];
        if (tmpBs.getIsCoveredByWhom() != 0
            || tmpBs.getIsBlockedBy().contains(player.getPlayerId())) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * adds start spaces for the players.
   */
  public void setInfo() {
    BoardSpace[][] boardSpaces = this.getBoard().getBoardSpaces();
    for (Player player : this.getPlayers()) {
      player.addPossibleSpace(boardSpaces[17][3]);
      player.addPossibleSpace(boardSpaces[9][6]);
      player.addPossibleSpace(boardSpaces[9][11]);
      player.addPossibleSpace(boardSpaces[25][6]);
      player.addPossibleSpace(boardSpaces[25][11]);
      player.addPossibleSpace(boardSpaces[17][14]);
    }
  }

}
