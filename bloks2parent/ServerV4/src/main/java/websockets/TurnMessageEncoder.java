package websockets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import model.TurnMessage;

/**
 * Jackson (json) encoder for turnMessage Object.
 *
 * @author jbuechs
 */
public class TurnMessageEncoder implements Encoder.Text<TurnMessage> {

  /**
   * encodes turnMessage object to a json String.
   *
   * @param message The turnMessage which will be encoded
   * @return encoded turnMessage (jsonString)
   */
  @Override
  public String encode(TurnMessage message) {

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
