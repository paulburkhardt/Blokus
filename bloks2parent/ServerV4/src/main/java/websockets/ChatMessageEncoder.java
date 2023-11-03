package websockets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import model.ChatMessage;

/**
 * Jackson (json) encoder for chatMessage Object.
 *
 * @author jbuechs
 */
public class ChatMessageEncoder implements Encoder.Text<ChatMessage> {

  /**
   * encodes chatMessage object to a json String.
   *
   * @param message The chatMessage which will be encoded
   * @return encoded chatMessage (jsonString)
   */
  @Override
  public String encode(ChatMessage message) {

    ObjectMapper objectMapper = new ObjectMapper();
    String jsonMessage = "";
    try {
      jsonMessage = objectMapper.writeValueAsString(message);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return jsonMessage;
  }

  /**
   * standard Method from interface Encoder.Text
   *
   * @param config EndpointConfig
   */
  @Override
  public void init(EndpointConfig config) {}

  /** standard Method from interface Encoder.Text */
  @Override
  public void destroy() {}
}
