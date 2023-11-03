package rest;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Class for management of JAX-RS Classes.
 */
@ApplicationPath("/bloks2")
public class RestConfig extends ResourceConfig {

  public RestConfig() {
    register(UserResource.class);
  }
}
