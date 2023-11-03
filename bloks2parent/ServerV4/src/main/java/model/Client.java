package model;

import gamelogic.Player;
import java.net.ConnectException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import websockets.WsClient;

/**
 * Is the client represenation of a user.
 *
 * @author myenler
 * @author lfiebig
 */
public class Client {

  private static final String WEBSOCKET_SERVER_URL_PREFIX = "ws://";
  private static final String WEBSOCKET_SERVER_URL_SUFFIX = ":8020/websocket/chat";
  private static final String RESTFUL_SERVER_URL_PREFIX = "http://";
  private static final String RESTFUL_SERVER_URL_SUFFIX = "/account";

  private String serverAdress =
      ""; /*Is the network adress (IP-adr.) with :port of device the server is running on
          and you would like to play*/

  private String ipAdress = "";
  private final int portOfRestServer = 8085;

  private String myrestfulserverurl =
      ""; // The full url adress for the rest server you want to play on
  private String mywebsocketserverurl =
      ""; // The full url adress for the rest server you want to play on
  private String lobbyId = ""; // lobby ID of WebSocket connection an lobby the game is played on


  private String accessToken =
      ""; // Bearer authentication token for Requests and connection with Server
  private boolean loggedIn = false;
  private String username = ""; // does not have to match with User.username (offline)
  // This is a WebSocket Client
  private WsClient wsClient;

  private Player myPlayer;
  private boolean wantsToDisconnect = false;

  /**
   * constructor to create client.
   *
   * @param ipAdress String representing ip adress
   */
  public Client(String ipAdress) {
    this.ipAdress = ipAdress;
    this.serverAdress = ipAdress + ":" + portOfRestServer;
    myrestfulserverurl = RESTFUL_SERVER_URL_PREFIX + serverAdress + RESTFUL_SERVER_URL_SUFFIX;
    mywebsocketserverurl = WEBSOCKET_SERVER_URL_PREFIX + serverAdress + WEBSOCKET_SERVER_URL_SUFFIX;
  }


  public boolean isWantsToDisconnect() {
    return wantsToDisconnect;
  }

  public void setWantsToDisconnect(boolean wantsToDisconnect) {
    this.wantsToDisconnect = wantsToDisconnect;
  }

  public String getIpAdress() {
    return ipAdress;
  }

  public void setIpAdress(String ipAdress) {
    this.ipAdress = ipAdress;
  }

  public void setWsClient() {
    wsClient = new WsClient();
  }

  // connects to WsServer with adress ws:// IP :8020/websockets/chat
  public void connectToWsServer(String ip) throws Exception {
    // wsClient.connectToServer(ip);
  }

  public void sendChatMessage(String sender, String context) {
    wsClient.sendChatMessage(sender, context);
  }


  public void sendMessage(int status, String content) {
    wsClient.sendMessage(status, content);
  }

  public String getServerAdress() {
    return serverAdress;
  }

  public void setServerAdress(String serverAdress) {
    this.serverAdress = serverAdress;
  }

  public WsClient getWsClient() {
    return wsClient;
  }

  /**
   * enum for the task.
   */
  public enum Task {
    CHANGE_USERNAME,
    CHANGE_PASSWORD,
    DELETE_TOKEN,
    DELETE_USER
  }

  HttpClient httpClient = HttpClient.newHttpClient();

  /**
   * Does send a Http-Post Request.
   *
   * @param url        url request is send to
   * @param headersMap headers the request should contain
   * @return returns message of server
   * @author myenler
   */
  public Message sendHttpPostRequest(String url, Map<String, String> headersMap) {
    var requestBuilder =
        HttpRequest.newBuilder().uri(URI.create(url)).POST(HttpRequest.BodyPublishers.ofString(""));

    /*Stream<String> stream = headersMap.keySet().stream();
    stream.forEach((key) -> {
        requestBuilder.header(key, headersMap.get(key));
    });*/

    // putting the headersMap into the HTTP-request and also encoding of the values to UTF-8
    // (because special characters)
    for (String key : headersMap.keySet()) {
      requestBuilder.header(key, URLEncoder.encode(headersMap.get(key)));
    }

    HttpRequest request = requestBuilder.build();

    try {
      HttpResponse response =
          httpClient.send((HttpRequest) request, HttpResponse.BodyHandlers.ofString());
      Message respMessage = new Message(response.body().toString());
      System.out.println(respMessage);
      return respMessage;
    } catch (ConnectException connectException) {
      connectException.printStackTrace();
      return new Message(470,
          "Unable to connect to IP address from server. Check your specification");
    } catch (Exception e) {
      e.printStackTrace();
      return new Message(599);
    }
  }

  /**
   * Http-rest to start a new server/lobby.
   *
   * @param rounds       rounds of a lobby (up to 10)
   * @param playerAmount of a lobby (2 or 4)
   * @return return the port of created server
   */
  public int sendStartNewWsServer(String rounds, String playerAmount) {
    Map map = createStartWsServerMap(rounds, playerAmount);
    String url = myrestfulserverurl + "/createlobby";
    Message serverMessage = sendHttpPostRequest(url, map);
    if (serverMessage.getStatus() == 200) {
      return Integer.valueOf(serverMessage.content);
    }
    return -1;
  }

  /**
   * Does try to register the paramaters (User) on the server (serverUrl: myRestfulServerURL).
   *
   * @param username username of user
   * @param password password of user
   * @return Message of server (containing Bearer accesToken for further requests if successful)
   * @author myenler
   */
  public Message register(String username, String password) {
    Map map = createAuthhenticationMap(username, password);
    String url = myrestfulserverurl + "/register";
    Message serverMessage = sendHttpPostRequest(url, map);
    if (serverMessage.getStatus() == 200) {
      this.username = username;
    }
    return serverMessage;
  }

  /**
   * Does try to login the paramaters (User) on the server (serverUrl: myRestfulServerURL).
   *
   * @param username username of user
   * @param password password of user
   * @return Message of server (containing Bearer accesToken for further requests if successful)
   * @author myenler
   */
  public Message login(String username, String password) {
    Map map = createAuthhenticationMap(username, password);
    String url = myrestfulserverurl + "/login";
    Message serverMessage = sendHttpPostRequest(url, map);
    if (serverMessage.getStatus() == 200) {
      this.username = username;
    }
    return serverMessage;
  }

  /**
   * Does try to edit a User attribute (given as parameter task) in the Database.
   *
   * @param task          the task, which attribute should be editted for the User by the Server
   * @param authorization Bearer accesToken
   * @param newValue      the new Value of what ever attribute should be changed (unnecessary for
   *                      DELETE operations. Can be empty then)
   * @return Message of server
   * @author myenler
   */
  public Message editUser(Task task, String authorization, String newValue) {
    String taskString = "";
    switch (task) {
      case DELETE_USER:
        taskString = "deleteUser";
        break;
      case DELETE_TOKEN:
        taskString = "deleteToken";
        break;
      case CHANGE_PASSWORD:
        taskString = "changePassword";
        break;
      case CHANGE_USERNAME:
        taskString = "changeUsername";
        break;
      default:
        break;
    }

    Map map = createEditUserMap(this.username, taskString, authorization, newValue);
    String url = myrestfulserverurl + "/editUser";
    Message serverMessage = sendHttpPostRequest(url, map);
    if (serverMessage.getStatus() == 200 && task == Task.CHANGE_USERNAME) {
      this.username = newValue;
    }
    return serverMessage;
  }

  /*Send HttpRequest Methods require a Map as a parameter to define the headers of a request.
  To create easy and predefined maps, dependent on the wanted Use Case, we implemented two Methods*/

  // 1. createEditUserMap for editUser Use Case

  /**
   * Does create a Map with the following parameter names as the key of the KeyValue-Pair and the
   * value the parameter contains as the value of the KeyValue-Pair.
   *
   * @param username      username
   * @param task          task
   * @param authorization auth
   * @param newValue      newValue string
   * @return the created Map
   * @author myenler
   */
  public static Map<String, String> createEditUserMap(
      String username, String task, String authorization, String newValue) {
    Map<String, String> map = new HashMap<>();
    map.put("username", username);
    map.put("task", task);
    map.put("Authorization", authorization);
    map.put("newValue", newValue);

    return map;
  }

  // 2. createAuthenticationMap for register and login Use Case

  /**
   * Does create a Map with the following parameter names as the key of the KeyValue-Pair and the
   * value the parameter contains as the value of the KeyValue-Pair.
   *
   * @param username username
   * @param password password
   * @return the created Map
   * @author myenler
   */
  public static Map<String, String> createAuthhenticationMap(String username, String password) {
    Map<String, String> map = new HashMap<>();
    map.put("username", username);
    map.put("password", password);

    return map;
  }


  /**
   * Does create a Map with the following parameter names as the key of the KeyValue-Pair and the
   * value the parameter contains as the value of the KeyValue-Pair.
   *
   * @param rounds     header value of rounds string
   * @param playAmount header value of player amount
   * @return return a map of header mappings
   */
  public static Map<String, String> createStartWsServerMap(String rounds, String playAmount) {
    Map<String, String> map = new HashMap<>();
    map.put("rounds", rounds);
    map.put("playerAmount", playAmount);

    return map;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }
}
