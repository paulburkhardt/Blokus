package websockets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import model.Message;

/**
 * Jackson (json) encoder for Message Object.
 *
 * @author jbuechs
 */
public class MessageEncoder implements Encoder.Text<Message> {

  /**
   * encodes Message object to a json String.
   *
   * @param message The Message which will be encoded
   * @return encoded Message (jsonString)
   */
  @Override
  public String encode(Message message) {
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
