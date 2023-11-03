package rest;

import database.DbManager;
import exception.UsernameDoesNotExistException;
import gamelogic.BlokusDuoGame;
import gamelogic.BlokusJuniorGame;
import gamelogic.BlokusTrigonGame;
import gamelogic.ClassicBlokusGame;
import gamelogic.Game;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import model.Lobby;
import model.Message;
import service.ServerService;
import websockets.WebSocketServer;

/**
 * "Servlet" that is handling all incoming HTTP-Requests to the Server. All requests do receive a
 * username and password if header is set by Client.
 *
 * @author myenler
 */
@Path("/account")
public class UserResource {

  static DbManager dbManager = new DbManager();
  TokenService tokenService = new TokenService();
  // Username und Password Paramter (bzw. auch newValue) VOM REQUEST MUESSEN NOCH UTF-8 DECODED
  // WERDEN, oder man akzeptiert keine umlaute im request
  @HeaderParam("username")
  private String username;

  @HeaderParam("password")
  private String password;


  private static int counterForPort = 0;

  /**
   * Method that is handling create Lobby requests to the path /createLobby. Idealy is creating a
   * new Tyrus ws Server, which acts as a lobby.
   *
   * @param rounds       GameType of different rounds and the count of rounds in a Game as a String
   * @param playerAmount player Amount of a Game/Lobby (2 or 4).
   * @return HTTP-Response is a String representing a Message
   * @throws Exception throws all possible Exceptions
   */
  @POST
  @Path("/createlobby")
  //@Produces(MediaType.APPLICATION_JSON)
  public String createLobby(
      @HeaderParam("rounds") String rounds,
      @HeaderParam("playerAmount") int playerAmount) throws Exception {

    rounds = URLDecoder.decode(rounds, "UTF-8");

    int portSuggestion = 8090; //ServerService.giveAvailablePort();
    if (portSuggestion == -1) {
      return new Message(444, "no port is available at the moment").decodeToJson();
    }
    WebSocketServer wsServer = new WebSocketServer();
    System.out.println("Portsuggestion  " + portSuggestion);
    wsServer.runServer(portSuggestion);
    ServerService.addPortToServer(portSuggestion, wsServer);

    Lobby lobby = new Lobby(portSuggestion, playerAmount);
    ServerService.getServerViaPort(portSuggestion).setLobby(lobby);
    //initialize rounds of a lobby with Game variants
    lobby.setRounds(this.convertRoundsStringToGames(rounds));
    System.out.println("websocket RUN SERVER AAAA");

    return new Message(200, Integer.toString(portSuggestion)).decodeToJson();
  }

  /**
   * Method that is handling register requests by Clients to the path /register. Idealy is creating
   * a new User in the database and returning a new Token.
   *
   * @return HTTP-Response is a String representing a Message. Information how register went.
   */
  @POST
  @Path("/register")
  //@Produces(MediaType.APPLICATION_JSON)
  public String register() {
    if (username == null || password == null) {
      System.out.println("499 ------");
      return new Message(499, "Not all required parameters and headers where set."
          + " The Function needs all of the following headers: username, password").decodeToJson();
    }
    dbManager.connectToDatabase();

    try {
      //if user inserted succesfully run if-code: create token, add token in HashMap, return
      // token to Client
      if (dbManager.insertUser(URLDecoder.decode(username, "UTF-8"),
          URLDecoder.decode(password))) {
        String token = tokenService.generateNewToken();
        tokenService.insertToken(username, token);
        return new Message(200, token).decodeToJson();
      } else { // username does already exist in the DB and run else-code: return error message
        // to Client
        return new Message(450, "Username already exists. Try another one")
            .decodeToJson();
      }
    } catch (SQLException e) {
      e.printStackTrace();
      dbManager.disconnect(); // needen, because inserUser() does not close
      // the connection in case of an Exception
      return new Message(599, "unexpected Error occurred").decodeToJson();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return new Message(599).decodeToJson();
  }

  /**
   * Method that is handling login requests by Clients to the path /login. Idealy is validating the
   * Client and returning auth. Token.
   *
   * @return HTTP-Response is a String representing a Message. Information how login went.
   */
  @POST
  @Path("/login")
  //@Produces(MediaType.TEXT_PLAIN)
  public String login() {
    dbManager.connectToDatabase();
    if (username == null || password == null) {
      System.out.println("499 ------");
      return new Message(499, "Not all required parameters and headers where set."
          + " The Function needs all of the following headers: username, password").decodeToJson();
    }

    try {
      if (dbManager.checkAuthenticationOfUser(URLDecoder.decode(username),
          URLDecoder.decode(password))) {
        String token = tokenService.generateNewToken();
        tokenService.insertToken(username, token);
        return new Message(200, token).decodeToJson();
      } else {
        return new Message(403, "password is not correct for username").decodeToJson();
      }
    } catch (UsernameDoesNotExistException e) {
      return new Message(460, "Username does not exist").decodeToJson();
    }
  }

  /**
   * Method that is handling editUser (change Username, change Password, delete User, delete Token)
   * requests by Clients to the path /editUser. Idealy is doing the requested task by the Client and
   * returning successful message.
   *
   * @param authorization authroization token
   * @param task          task what should be done by the Server e.g. change Username
   * @param newValue      the new Value if a value should be changed
   * @return HTTP-Response is a String representing a Message. Information how editUser went.
   */
  @POST
  //@Secured
  @Path("/editUser")
  public String editAttributes(
      @HeaderParam("Authorization") String authorization,
      @HeaderParam("task") String task,
      @HeaderParam("newValue") String newValue) {
    username = URLDecoder.decode(username);
    newValue = URLDecoder.decode(newValue);
    authorization = URLDecoder.decode(authorization);
    Message authenticationMessage = checkAuthentication(username, authorization);

    //If the authentication Result is not Successful = 200, then return
    // the error Code from authentication method.
    if (authenticationMessage.getStatus() != 200) {
      return authenticationMessage.decodeToJson();
    }
    try {
      switch (task) {
        case "changeUsername":
          if (dbManager.doesUserExists(newValue)) {
            return new Message(450, "Username " + newValue + " is already taken")
                .decodeToJson();
          } else {
            dbManager.changeUsername(username, newValue);
            tokenService.changeUsernameOfToken(username, newValue);
          }
          return new Message(200).decodeToJson();
        case "changePassword":
          dbManager.changePassword(username, newValue);
          return new Message(200).decodeToJson();
        case "deleteUser":
          dbManager.deleteUser(username);
          tokenService.deleteToken(username);
          return new Message(200).decodeToJson();
        case "deleteToken":
          tokenService.deleteToken(username);
          return new Message(200).decodeToJson();
        default:
          return new Message(461, "header \"task\" is not supported").decodeToJson();
      }
    } catch (SQLException e) {
      return new Message(599, "SQLException occured "
          + "while doing operations on following task"
          + "in the User-Database: " + task).decodeToJson();
    }
  }

  /**
   * Does check the Authentication token of a Client with help of TokenService.
   *
   * @param username username of requester
   * @param token    token of requester that should match the mapped token to username
   * @return Response String, json String representing a Message object
   */
  public Message checkAuthentication(String username, String token) {
    try {
      if (tokenService.validateToken(username, token)) {
        return new Message(200);
      } else {
        return new Message(403, "Bearer accesToken is not valid for username");
      }
    } catch (UsernameDoesNotExistException e) {
      return new Message(460, "username does not exist");
    }
  }

  /**
   * Is converting the rounds String to Game objects.
   *
   * @param roundsString String that is parsed in each round
   * @return Array List containing the Game objects representing a round
   */
  public ArrayList<Game> convertRoundsStringToGames(String roundsString) {
    String[] roundsArray = roundsString.split(":");
    ArrayList<Game> rounds = new ArrayList<>();
    for (String s : roundsArray) {
      //ändere die bezeichnungen auf DUO....
      switch (s) {
        case "CLASSIC":
          rounds.add(new ClassicBlokusGame());
          break;
        case "TRIGON":
          rounds.add(new BlokusTrigonGame());
          break;
        case "DUO":
          rounds.add(new BlokusDuoGame());
          break;
        case "JUNIOR":
          rounds.add(new BlokusJuniorGame());
          break;
        default:
          break;
      }
    }
    return rounds;
  }
}
