package model;

import gamelogic.Game;
import gui.SceneController;

/**
 * Session is the global distributor for the user information.
 *
 * @author myenler
 * @author lfiebig
 */
public class Session {

  private static User myUser;
  private static Game game;
  private static SceneController sceneController = new SceneController();

  public static void setMyUser(User user) {
    myUser = user;
  }

  public static User getMyUser() {
    return myUser;
  }

  public static Game getGame() {
    return game;
  }

  public static void setGame(Game game) {
    Session.game = game;
  }

  public static SceneController getSceneController() {
    return sceneController;
  }

  public static void setSceneController(SceneController sceneController) {
    Session.sceneController = sceneController;
  }
}
