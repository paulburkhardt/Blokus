package gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import model.Session;

/**
 * Controller for LeaderBoardView.
 *
 * @author lfiebig
 */
public class LeaderBoardViewController {

  @FXML public Text playerOne;
  @FXML public Text playerTwo;
  @FXML public Text playerThree;
  @FXML public Text playerFour;
  @FXML public Text playerOneName;
  @FXML public Text playerTwoName;
  @FXML public Text playerThreeName;
  @FXML public Text playerFourName;
  @FXML public Text playerOneScore;
  @FXML public Text playerTwoScore;
  @FXML public Text playerThreeScore;
  @FXML public Text playerFourScore;
  @FXML public Button leaderBoardButton;

  // score of every player
  HashMap<Integer, String> stats = new HashMap<>();

  /**
   * initialises the Leaderboard with the players scores.
   *
   * @param leaderBoardString score of every player
   */
  public void setLeaderBoard(String leaderBoardString) {
    String[] content = leaderBoardString.split(",");
    for (String s : content) {
      String[] values = s.split(":");
      stats.put(Integer.valueOf(values[1]), values[0]);
    }
    ArrayList<Integer> ranking = new ArrayList<>();
    for (int score : stats.keySet()) {
      ranking.add(score);
    }
    Collections.sort(ranking);

    if (ranking.size() == 2) {
      playerThree.setVisible(false);
      playerFour.setVisible(false);
      playerThreeName.setVisible(false);
      playerFourName.setVisible(false);
      playerThreeScore.setVisible(false);
      playerFourScore.setVisible(false);

      playerOneName.setText(stats.get(ranking.get(1)));
      playerOneScore.setText(ranking.get(1) + "");
      playerTwoName.setText(stats.get(ranking.get(0)));
      playerTwoScore.setText(ranking.get(0) + "");

    } else {
      playerOneName.setText(stats.get(ranking.get(3)));
      playerOneScore.setText(ranking.get(3) + "");
      playerTwoName.setText(stats.get(ranking.get(2)));
      playerTwoScore.setText(ranking.get(2) + "");
      playerThreeName.setText(stats.get(ranking.get(1)));
      playerThreeScore.setText(ranking.get(1) + "");
      playerFourName.setText(stats.get(ranking.get(0)));
      playerFourScore.setText(ranking.get(0) + "");
    }
    // Todo: request from server if rounds array is empty. if so -> change buttonText to "back to
    // main menu"
  }

  /** Method is called when user clicks the button on the Leaderboard. starts new game round. */
  public void onClickLeaderBoardButton() {
    Session.getMyUser().getClient().getWsClient().sendMessage(704, "");
  }
}
