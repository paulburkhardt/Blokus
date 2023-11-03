package model;

import com.google.gson.Gson;

/**
 * Object that is sent between methods, classes and devices for communication.
 *
 * @author myener
 */
public class Message {

  public int status;
  public String content = "";

  /**
   * Constructor for a Message with a status and content.
   *
   * @param status  is used to define the purpose and meaning of a message
   * @param content additional information (like a body in HTTP-Request)
   */
  public Message(int status, String content) {
    this.status = status;
    this.content = content;
  }


  public Message(int status) {
    this.status = status;
    //this.timestamp = new ZonedDateTime();
  }

  /**
   * Default constructor.
   */
  public Message() {

  }

  /**
   * Constructor got generate a Message object from a Json String (encode).
   *
   * @param jsonString Json string representing a Message
   */
  public Message(String jsonString) {
    Gson gson = new Gson();
    Message message = new Message();
    message = gson.fromJson(jsonString, Message.class);
    this.status = message.status;
    this.content = message.content;
  }

  /**
   * Is decoding the Message object to a Json String.
   *
   * @return Json String representation of a Message
   */
  public String decodeToJson() {
    return new Gson().toJson(this);
  }

  public int getStatus() {
    return status;
  }


  @Override
  public String toString() {
    return "Message{"
        + "status="
        + status
        + ", content='"
        + content
        + '\''
        + '}';
  }
}