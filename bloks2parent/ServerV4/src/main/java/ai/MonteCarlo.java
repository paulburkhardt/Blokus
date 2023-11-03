package ai;

import gamelogic.BlokusDuoGame;
import gamelogic.BlokusJuniorGame;
import gamelogic.BlokusTrigonGame;
import gamelogic.ClassicBlokusGame;
import gamelogic.Game;
import gamelogic.Player;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * Monte Carlo Tree Search Algorithm.
 *
 * @author pboensch
 */
public class MonteCarlo {
  private int aiPlayerId;
  private int numberOfPlayers;

  public MonteCarlo(int aiPlayerId) {
    this.aiPlayerId = aiPlayerId;
  }

  /**
   * 1. Selection with Uct 2. Expansion 3. Simulation 4. Backpropagation
   *
   * @param game current game
   * @return returns the best play through four phases
   */
  // returns best play
  public Play findBestPlay(Game game) {
    this.numberOfPlayers = game.getPlayers().size();
    long start = System.currentTimeMillis();
    long end = start + 60 * 100;
    int opponentId;
    // get opponent who did the last turn
    switch (aiPlayerId - 1 % game.getPlayers().size()) {
      case 0:
        opponentId = game.getPlayers().size();
        break;
      default:
        opponentId = aiPlayerId - 1 % game.getPlayers().size();
    }
    // Set up to root node
    MonteCarloNode rootNode = new MonteCarloNode(game, opponentId);
    System.out.println("MCTS is running...");
    int simulations = 0;
    // MCTS
    while (System.currentTimeMillis() < end) {
      Game gameCopy = getGameType(game);
      // 1. Select Node
      MonteCarloNode node = select(rootNode);
      // 2. Expand this node if there is no winner
      if (node.hasTurnsLeft()) {
        expand(node, gameCopy);
      }
      // get a random child node
      MonteCarloNode nodeToExplore = node;
      if (node.getChildArray().size() > 0) {
        Random generator = new Random();
        int randomIndex = generator.nextInt(nodeToExplore.childArray.size());
        nodeToExplore = node.childArray.get(randomIndex);
      }
      // 3. Simulate
      int winner = simulate(nodeToExplore, gameCopy);
      // update stats
      backpropagate(nodeToExplore, winner);
      simulations++;
    }
    // calculate next Play from the Statistics - takes node with most wins
    MonteCarloNode winnerNode = rootNode.getChildWithMaxWins();
    System.out.println("Number of simulations: " + simulations);
    if (winnerNode.getWinScore() == 0) {
      GetPlay play = new GetPlay();
      return play.getRandomTurn(game, this.aiPlayerId);
    }
    return winnerNode.getPlay();
  }

  /**
   * 1. select new node with uct.
   *
   * @param node rootNode
   * @return MonteCarloNode node that is going to be expanded
   */
  public MonteCarloNode select(MonteCarloNode node) {
    MonteCarloNode selectedNode = node;
    while (selectedNode.getChildArray().size() != 0) {
      selectedNode = Uct.findBestNodeWithUct(selectedNode);
    }
    return selectedNode;
  }

  /**
   * 2. expand. adds all possible child nodes.
   *
   * @param node node that has no child nodes and should be expanded
   * @param gameCopy copy of current game
   */
  public void expand(MonteCarloNode node, Game gameCopy) {
    Stack<Play> stack = new Stack<>();
    MonteCarloNode tempNode = new MonteCarloNode(node);
    while (tempNode.getParent() != null) {
      stack.push(tempNode.getPlay());
      tempNode = tempNode.getParent();
    }
    for (int i = 0; i < stack.size(); i++) {
      Play play = stack.pop();
      makePlay(gameCopy, play, play.getPlayerId());
    }
    List<MonteCarloNode> possibleNodes =
        node.getAllPossibleNodes(getPlayersTurn(node.getPlayerId()), gameCopy);
    for (MonteCarloNode possibleNode : possibleNodes) {
      node.getChildArray().add(possibleNode);
    }
  }

  /**
   * 3. simulate game randomly until the game terminates and returns a winner.
   *
   * @param node node from where the game should be simulated
   * @param gameCopy copy of current game
   * @return winner id of the winner
   */
  public int simulate(MonteCarloNode node, Game gameCopy) {
    int winner = checkStatus(gameCopy);
    makePlay(gameCopy, node.getPlay(), node.getPlay().getPlayerId());
    int playersTurn = getPlayersTurn(node.getPlayerId());
    while (winner == -1) {
      GetPlay getPlay = new GetPlay();
      Play play = getPlay.getRandomTurn(gameCopy, playersTurn);
      try {
        makePlay(gameCopy, play, playersTurn);
      } catch (Exception e) {
        System.out.println();
      }
      playersTurn = getPlayersTurn(playersTurn);
      winner = checkStatus(gameCopy);
    }
    return winner;
  }

  /**
   * 4. backpropagate winner.
   *
   * @param node node from where the simulation started
   * @param winner id of the winner
   */
  public void backpropagate(MonteCarloNode node, int winner) {
    MonteCarloNode tempNode = node;
    while (tempNode != null) {
      tempNode.incrementVisit();
      if (tempNode.getPlayerId() == winner) {
        tempNode.addScore();
      }
      tempNode = tempNode.getParent();
    }
  }

  /**
   * checks if the game has a winner or if the game is still running.
   *
   * @param gameCheck current game copy
   * @return winner id of the winner or -1 for not finished
   */
  public int checkStatus(Game gameCheck) {
    int winner = -1;
    int highscore = 0;
    GetPlay randomTurn = new GetPlay();
    for (Player player : gameCheck.getPlayers()) {
      if (randomTurn.playsLeft(gameCheck, player.getPlayerId())) {
        return -1;
      }
      if (highscore < player.getScore()) {
        highscore = player.getScore();
        winner = player.getPlayerId();
      }
    }
    for (Player player : gameCheck.getPlayers()) {
      if (player.getPlayerId() != winner && highscore == player.getScore()) {
        return 0;
      }
    }
    return winner;
  }

  /**
   * executes a play.
   *
   * @param gameCopy copy of the current game
   * @param play play that shall be performed
   * @param playerId of the player who is performing the play
   */
  public void makePlay(Game gameCopy, Play play, int playerId) {
    Player player = gameCopy.getPlayerByid(playerId);
    player.setSelectedGameTile(play.getGameTile());
    if (gameCopy.getGameMode().equals("Trigon")) {
      // performs turn for trigon
      for (int i = 0; i < play.getRotation(); i++) {
        player.getSelectedGameTile().rotateGameTile();
      }
      if (play.isMirrored()) {
        player.getSelectedGameTile().mirrorGameTile();
      }
      // performs turn for classic blokus
    } else {
      if (play.isMirrored()) {
        player.getSelectedGameTile().mirrorGameTile();
      }
      for (int i = 0; i < play.getRotation(); i++) {
        player.getSelectedGameTile().rotateGameTile();
      }
    }
    player.setViableSpaces(gameCopy.calculateViableSpaces(player));
    gameCopy.gameTilePlaced(player, play.getPosition());
    player.getSelectedGameTile().setPlayed(true);
    player.getSelectedGameTile().resetGameTile();
    player.setSelectedGameTile("null");
  }

  /**
   * determine the id of the next player.
   *
   * @param player player id
   * @return playersTurn playerID of the player whose turn it is
   */
  public int getPlayersTurn(int player) {
    int playersTurn = player;
    switch ((player + 1) % numberOfPlayers) {
      case 0:
        return numberOfPlayers;
      default:
        playersTurn = (playersTurn + 1) % numberOfPlayers;
    }
    return playersTurn;
  }

  /**
   * creates a copy of the game.
   *
   * @param getGame current game
   * @return game copy of game
   */
  public Game getGameType(Game getGame) {
    switch (getGame.getGameMode()) {
      case ("Duo"):
        return new BlokusDuoGame(getGame);
      case ("Classic"):
        return new ClassicBlokusGame(getGame);
      case ("Junior"):
        return new BlokusJuniorGame(getGame);
      default:
        return new BlokusTrigonGame(getGame);
    }
  }
}
