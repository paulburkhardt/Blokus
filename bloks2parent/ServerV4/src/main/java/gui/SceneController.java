package gui;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gamelogic.BlokusDuoGame;
import gamelogic.BlokusJuniorGame;
import gamelogic.BlokusTrigonGame;
import gamelogic.ClassicBlokusGame;
import java.io.IOException;
import java.net.URL;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.ChatMessage;
import model.Session;

/**
 * Controller for managing Scene switches.
 *
 * @author lfiebig
 */
public class SceneController {

  @FXML public Text chatDisplay;
  @FXML public TextField chatTextField;
  @FXML public Button sendChatMessageButton;

  private Stage stage;
  private Scene scene;

  // controller
  private LobbyViewController lobbyViewController;
  private LeaderBoardViewController leaderBoardViewController;
  private DeleteUserViewController deleteUserViewController;
  private ChangePasswordViewController changePasswordViewController;
  private ChangeUsernameViewController changeUsernameViewController;
  private ClassicGameViewController classicGameViewController;
  private DuoGameViewController duoGameViewController;
  private JuniorGameViewController juniorGameViewController;
  private TrigonGameViewController trigonGameViewController;

  public Stage getStage() {
    return stage;
  }

  public void setStage(Stage stage) {
    this.stage = stage;
  }

  public LobbyViewController getLobbyViewController() {
    return lobbyViewController;
  }

  public void setLobbyViewController(LobbyViewController lobbyViewController) {
    this.lobbyViewController = lobbyViewController;
  }

  /**
   * shows leaderBoardView.
   *
   * @param leaderboardString scores from all players
   * @throws IOException when scene loading fails
   */
  public void showLeaderBoard(String leaderboardString) {
    Platform.runLater(
        () -> {
          Stage stage = Session.getSceneController().getStage();
          URL xmlUrl = SceneController.class.getResource("/leaderBoardView.fxml");
          FXMLLoader loader = new FXMLLoader(xmlUrl);
          Parent root;

          try {
            root = loader.load();

            leaderBoardViewController = loader.getController();
            leaderBoardViewController.setLeaderBoard(leaderboardString);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
  }

  /**
   * Shows mainMenuView.
   *
   * @param evt ActionEvent to get current Stage
   * @throws IOException when scene loading fails
   */
  public void showMainMenu(ActionEvent evt) throws IOException {
    URL xmlUrl = SceneController.class.getResource("/mainMenuView.fxml");
    FXMLLoader loader = new FXMLLoader(xmlUrl);
    Parent root = loader.load();
    MainMenuViewController controller = loader.getController();
    controller.showIsOnline();

    stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.setResizable(false);
    stage.show();
  }

  /**
   * Shows main menu.
   *
   * @author jbuechs
   * @param evt MouseEvent to get current Stage
   * @throws IOException when scene loading fails
   */
  public void showMainMenu(MouseEvent evt) throws IOException {
    URL xmlUrl = SceneController.class.getResource("/mainMenuView.fxml");
    FXMLLoader loader = new FXMLLoader(xmlUrl);
    Parent root = loader.load();
    MainMenuViewController controller = loader.getController();
    controller.showIsOnline();

    stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.setResizable(false);
    stage.show();
  }

  /** Method is called if one user leaves the lobby. Shows mainMenuView. */
  public void showMainMenuAfterGameLeave() {
    Platform.runLater(
        () -> {
          Stage stage = Session.getSceneController().getStage();
          URL xmlUrl = SceneController.class.getResource("/mainMenuView.fxml");
          FXMLLoader loader = new FXMLLoader(xmlUrl);
          Parent root;

          try {
            root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
  }

  /**
   * Shows playerSelectionView.
   *
   * @param evt ActionEvent to get current Stage
   * @throws IOException when scene loading fails
   */
  public void showPlayerSelectionPopUp(ActionEvent evt) throws IOException {
    URL xmlUrl = SceneController.class.getResource("/playerSelectionView.fxml");
    FXMLLoader loader = new FXMLLoader(xmlUrl);
    Parent root = loader.load();
    PlayerSelectionViewController controller = loader.getController();

    stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.setResizable(false);
    stage.show();
  }

  /**
   * Shows createGameView.
   *
   * @param evt Event to get current Stage
   * @throws IOException when scene loading fails
   */
  public void showCreateGameView(Event evt) throws IOException {
    URL xmlUrl = SceneController.class.getResource("/createGameView.fxml");
    FXMLLoader loader = new FXMLLoader(xmlUrl);
    Parent root = loader.load();
    CreateGameViewController controller = loader.getController();
    controller.setInfo();

    stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.setResizable(false);
    stage.show();
  }

  /**
   * Shows changeUsernameView.
   *
   * @author jbuechs
   * @param evt Event to get current Stage
   * @throws IOException when scene loading fails
   */
  public void showChangeUsernameView(Event evt) throws IOException {
    URL xmlUrl = SceneController.class.getResource("/changeUsernameView.fxml");
    FXMLLoader loader = new FXMLLoader(xmlUrl);
    Parent root = loader.load();

    ChangeUsernameViewController controller = loader.getController();
    controller.showCurrentUsername();

    stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.setResizable(false);
    stage.show();
  }

  /**
   * Shows changePasswortView.
   *
   * @author jbuechs
   * @param evt Event to get current Stage
   * @throws IOException when scene loading fails
   */
  public void showChangePasswordView(Event evt) throws IOException {
    URL xmlUrl = SceneController.class.getResource("/changePasswordView.fxml");
    FXMLLoader loader = new FXMLLoader(xmlUrl);
    Parent root = loader.load();

    stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.setResizable(false);
    stage.show();
  }

  /**
   * shows deleteUserView.
   *
   * @author jbuechs
   * @param evt Event to get current Stage
   * @throws IOException when scene loading fails
   */
  public void showDeleteUserView(Event evt) throws IOException {
    URL xmlUrl = SceneController.class.getResource("/deleteUserView.fxml");
    FXMLLoader loader = new FXMLLoader(xmlUrl);
    Parent root = loader.load();

    stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.setResizable(false);
    stage.show();
  }

  /**
   * shows registerView.
   *
   * @author jbuechs
   * @param evt Event to get current Stage
   * @throws IOException when scene loading fails
   */
  public void showRegisterView(Event evt) throws IOException {
    URL xmlUrl = SceneController.class.getResource("/registerView.fxml");
    FXMLLoader loader = new FXMLLoader(xmlUrl);
    Parent root = loader.load();

    stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.setResizable(false);
    stage.show();
  }

  /**
   * shows loginView.
   *
   * @author jbuechs
   * @param evt Event to get current Stage
   * @throws IOException when scene loading fails
   */
  public void showLoginView(Event evt) throws IOException {
    URL xmlUrl = SceneController.class.getResource("/loginView.fxml");
    FXMLLoader loader = new FXMLLoader(xmlUrl);
    Parent root = loader.load();

    stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.setResizable(false);
    stage.show();
  }

  /**
   * shows lobbyView.
   *
   * @throws IOException when scene loading fails
   */
  public void showLobbyView() {
    Platform.runLater(
        () -> {
          Stage stage = Session.getSceneController().getStage();
          URL xmlUrl = SceneController.class.getResource("/lobbyView.fxml");
          FXMLLoader loader = new FXMLLoader(xmlUrl);
          Parent root;

          try {
            root = loader.load();
            lobbyViewController = loader.getController();
            lobbyViewController.setInfo();
            lobbyViewController.updatePlayerView();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
  }

  /**
   * shows profileView.
   *
   * @param evt Event to get current Stage
   * @throws IOException when scene loading fails
   */
  public void showProfileView(Event evt) throws IOException {
    URL xmlUrl = SceneController.class.getResource("/profileView.fxml");
    FXMLLoader loader = new FXMLLoader(xmlUrl);
    Parent root = loader.load();

    stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.setResizable(false);
    stage.show();
  }

  /** Method is called if a chat message was send. handles updateChat for current Stage. */
  public void updateChatView() {
    StringBuffer stb = new StringBuffer();
    for (ChatMessage cm : Session.getMyUser().getLobby().getChat()) {
      stb.append(cm.getSender() + ":\t" + cm.getContent() + "\n");
    }
    switch (stage.getScene().getRoot().getId()) {
      case "lobbyPane":
        lobbyViewController.updateChatView();
        break;
      case "duoPane":
        duoGameViewController.updateChatView();
        break;
      case "classicPane":
        classicGameViewController.updateChatView();
        break;
      case "juniorPane":
        juniorGameViewController.updateChatView();
        break;
      case "trigonPane":
        trigonGameViewController.updateChatView();
        break;
      default:
        break;
    }
  }

  /** handles changeTurnMessage for current Stage. */
  public void changeTurnMessage() {
    StringBuffer stb = new StringBuffer();
    for (ChatMessage cm : Session.getMyUser().getLobby().getChat()) {
      stb.append(cm.getSender() + ":\t" + cm.getContent() + "\n");
    }
    switch (stage.getScene().getRoot().getId()) {
      case "duoPane":
        duoGameViewController.changeTurnMessage();
        break;
      case "classicPane":
        classicGameViewController.changeTurnMessage();
        break;
      case "juniorPane":
        juniorGameViewController.changeTurnMessage();
        break;
      case "trigonPane":
        trigonGameViewController.changeTurnMessage();
        break;
      default:
        break;
    }
  }

  /**
   * shows gameView for a specific game mode.
   *
   * @param gamemode specific game mode
   * @throws IOException when scene loading fails
   */
  public void showGameView(String gamemode) throws IOException {
    switch (gamemode) {
      case "Classic":
        showClassicView();
        break;
      case "Trigon":
        showTrigonView();
        break;
      case "Duo":
        showDuoView();
        break;
      case "Junior":
        showJuniorView();
        break;
      default:
        break;
    }
  }

  /** shows classicGameView. */
  public void showClassicView() {
    Platform.runLater(
        () -> {
          Stage stage = Session.getSceneController().getStage();
          URL xmlUrl = SceneController.class.getResource("/classicGameView.fxml");
          FXMLLoader loader = new FXMLLoader(xmlUrl);
          Parent root;

          try {
            root = loader.load();
            classicGameViewController = loader.getController();
            classicGameViewController.setInfo(
                (ClassicBlokusGame) Session.getGame(),
                Session.getGame().getPlayerByUsername(Session.getMyUser().getUsername()),
                Session.getMyUser().getTheme());
            classicGameViewController.initializeGridPaneArray();
            classicGameViewController.initializeScoreBoard();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource(getThemeCss()).toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
  }

  /** Shows trigonGameView. */
  public void showTrigonView() {
    Platform.runLater(
        () -> {
          Stage stage = Session.getSceneController().getStage();
          URL xmlUrl = SceneController.class.getResource("/trigonGameView.fxml");
          FXMLLoader loader = new FXMLLoader(xmlUrl);
          Parent root;

          try {
            root = loader.load();
            trigonGameViewController = loader.getController();
            trigonGameViewController.setInfo(
                (BlokusTrigonGame) Session.getGame(),
                Session.getGame().getPlayerByUsername(Session.getMyUser().getUsername()),
                Session.getMyUser().getTheme());

            trigonGameViewController.initializeGridPaneArray();
            trigonGameViewController.initializeScoreBoard();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource(getThemeCss()).toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
  }

  /** Shows duoGameView. */
  public void showDuoView() {
    Platform.runLater(
        () -> {
          Stage stage = Session.getSceneController().getStage();
          URL xmlUrl = SceneController.class.getResource("/duoGameView.fxml");
          FXMLLoader loader = new FXMLLoader(xmlUrl);
          Parent root;

          try {
            root = loader.load();
            duoGameViewController = loader.getController();
            duoGameViewController.setInfo(
                (BlokusDuoGame) Session.getGame(),
                Session.getGame().getPlayerByUsername(Session.getMyUser().getUsername()),
                Session.getMyUser().getTheme());
            duoGameViewController.initializeGridPaneArray();
            duoGameViewController.initializeScoreBoard();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource(getThemeCss()).toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
  }

  /** Shows juniorGameView. */
  public void showJuniorView() {
    Platform.runLater(
        () -> {
          Stage stage = Session.getSceneController().getStage();
          URL xmlUrl = SceneController.class.getResource("/juniorGameView.fxml");
          FXMLLoader loader = new FXMLLoader(xmlUrl);
          Parent root;

          try {
            root = loader.load();
            juniorGameViewController = loader.getController();
            juniorGameViewController.setInfo(
                (BlokusJuniorGame) Session.getGame(),
                Session.getGame().getPlayerByUsername(Session.getMyUser().getUsername()),
                Session.getMyUser().getTheme());
            juniorGameViewController.initializeGridPaneArray();
            juniorGameViewController.initializeScoreBoard();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource(getThemeCss()).toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            System.out.println(scene.getStylesheets());

          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
  }

  /**
   * handles updateView for current game mode.
   *
   * @param gamemode current game mode
   */
  public void updateGameView(String gamemode) {
    switch (gamemode) {
      case "Classic":
        classicGameViewController.updateView();
        break;
      case "Duo":
        duoGameViewController.updateView();
        break;
      case "Junior":
        juniorGameViewController.updateView();
        break;
      case "Trigon":
        trigonGameViewController.updateView();
        break;
      default:
        break;
    }
  }

  /** Updates playerView in lobbyView. */
  public void updatePlayerView() {
    lobbyViewController.updatePlayerView();
  }

  /**
   * Gets css File for the users board theme.
   *
   * @return css File for the users board theme
   */
  @JsonIgnore
  public String getThemeCss() {
    return Session.getMyUser().getTheme().getCssFile();
  }
}
