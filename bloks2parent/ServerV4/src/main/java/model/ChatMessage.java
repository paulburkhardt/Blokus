package model;

/**
 * ChatMessage class, will be used for WebSocket chat communication.
 *
 * @author jbuechs
 */
public class ChatMessage extends Message {
  private String sender;

  /**
   * creates ChatMessage.
   *
   * @param content content of the message
   * @param sender sender of the message
   */
  public ChatMessage(String content, String sender) {
    super(710, content);
    this.sender = sender;
  }

  /** creates ChatMessage. */
  public ChatMessage() {
    super(710);
  }

  public String getSender() {
    return this.sender;
  }
}
