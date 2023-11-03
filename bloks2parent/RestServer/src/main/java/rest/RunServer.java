package rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;

/**
 * Run Server class with a main method, to run RESTful-Service/Server.
 *
 * @author myenler
 */
public class RunServer {

  private Server server;

  /**
   * method to run server.
   *
   * @param args args
   * @throws Exception exception
   */
  public static void main(String[] args) throws Exception {
    RunServer runServer = new RunServer();
    runServer.startRestful(8085);
    String input;
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(System.in));
    do {
      input = reader.readLine();
    } while (!input.equalsIgnoreCase("quit"));
    runServer.server.stop();
  }

  /**
   * Method to start the RESTful Service Server.
   *
   * @param port port that the server is running on
   * @throws Exception Generell Exception
   */
  public void startRestful(int port) throws Exception {
    URI baseUri = UriBuilder.fromUri("http://localhost/").port(port).build();
    RestConfig config = new RestConfig();

    server = JettyHttpContainerFactory.createServer(baseUri, config);

    server.start();
  }
}
