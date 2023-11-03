package rest;

import exception.UsernameDoesNotExistException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to manage tokens of different Clients. For Authentication Management.
 */
public class TokenService {

  /**
   * Map, that is mapping a username (User) to its token.
   */
  private static final Map<String, String> tokenMap = new HashMap<>();

  /**
   * Checks the username and validates the token. If token does not exist for user then throw
   * Error.
   *
   * @param username username of user trying to validate
   * @param token    token of user
   * @return if it is valid
   * @throws UsernameDoesNotExistException if username does not exist in the tokenmap throw error.
   */
  public boolean validateToken(String username, String token) throws UsernameDoesNotExistException {
    if (tokenMap.containsKey(username)) {
      return tokenMap.get(username).equals(token);
    } else {
      throw new UsernameDoesNotExistException();
    }
  }

  /**
   * Does change the username of a User but still keeps the token of that User.
   *
   * @param oldUsername old username of user
   * @param newUsername new username of user
   */
  public void changeUsernameOfToken(String oldUsername, String newUsername) {
    String token = tokenMap.get(oldUsername);
    tokenMap.remove(oldUsername);
    tokenMap.put(newUsername, token);
  }

  /**
   * Insert a new token or updates (overwrites) an old token of a user.
   *
   * @param username username of User
   * @param token    token of User
   */
  //insert a new token or updates (overwrites) an old token of an user
  public void insertToken(String username, String token) {
    tokenMap.put(username, token);
  }

  /**
   * When logging out, resetting token, or deleting user-account delete Token from map.
   *
   * @param username username that is getting deleted in the map
   */
  public void deleteToken(String username) {
    tokenMap.remove(username);
  }


  private static final SecureRandom secureRandom = new SecureRandom();
  private static final Base64.Encoder base64Encoder = Base64.getEncoder();

  /**
   * Genereate a new random but secure Token.
   *
   * @return the generated Token
   */
  public String generateNewToken() {
    byte[] randomBytes = new byte[24];
    secureRandom.nextBytes(randomBytes);
    return base64Encoder.encodeToString(randomBytes);
  }
}
