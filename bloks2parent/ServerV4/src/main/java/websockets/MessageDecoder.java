package websockets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import model.ChatMessage;
import model.Message;
import model.TurnMessage;

/**
 * Jackson (json) decoder for all Message Objects.
 *
 * @author jbuechs
 */
public class MessageDecoder implements Decoder.Text<Message> {

  public static boolean isBetween(int x, int lower, int upper) {
    return lower <= x && x <= upper;
  }

  @Override
  public Message decode(String jsonMessage) {
    Message message = new Message();
    ObjectMapper objectMapper = new ObjectMapper();
    int status = 0;
    try {
      ObjectNode node = objectMapper.readValue(jsonMessage, ObjectNode.class);
      status = Integer.parseInt(node.get("status").toString());

      if (isBetween(status, 700, 709) || isBetween(status, 740, 749)) {
        return objectMapper.readValue(jsonMessage, Message.class);
      } else if (isBetween(status, 710, 719)) {
        return objectMapper.readValue(jsonMessage, ChatMessage.class);
      } else if (isBetween(status, 730, 739)) {
        return objectMapper.readValue(jsonMessage, TurnMessage.class);
      }
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return message;
  }

  @Override
  public boolean willDecode(String s) {
    return true;
  }

  @Override
  public void init(EndpointConfig config) {}

  @Override
  public void destroy() {}
}
