package gui;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main Class for UI.
 *
 * @author lfiebig
 */
public class App extends Application {

  @Override
  public void start(Stage stage) throws IOException, URISyntaxException {
    FXMLLoader loader = new FXMLLoader();
    URL xmlUrl = getClass().getResource("/startScreenView.fxml");
    loader.setLocation(xmlUrl);
    Parent root = loader.load();
    StartScreenViewController controller = loader.getController();

    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.setTitle("ANYBLOKS");
    stage.setResizable(false);
    stage.getIcons().add(new Image("/AnybloksIcon.png"));
    stage.show();
  }

  /**
   * Main method of project.
   *
   * @param args program arguments
   */

  public static void main(String[] args) {
    launch();
  }
}
