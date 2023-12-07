package psp.io_api.json_processing.custom_deserializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import psp.io_api.json_processing.ScopeFactory;
import psp.sel.EventImpl;
import psp.sel.scopes.Scope;


public class ScopeDeserializer extends StdDeserializer<Scope> {

  public ScopeDeserializer() {
    this(null);
  }

  public ScopeDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public Scope deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JacksonException {

    JsonNode node = parser.getCodec().readTree(parser);
    String type = node.get("type").asText();
    boolean isQEvent = node.has("q_event");
    boolean isREvent = node.has("r_event");
    EventImpl qEvent = null;
    EventImpl rEvent = null;

    if(isQEvent) {
      String name = node.get("q_event").get("name").asText();
      String specification = node.get("q_event").get("specification").asText();
      qEvent = new EventImpl(name,specification);
    }

    if (isREvent) {
      String name = node.get("r_event").get("name").asText();
      String specification = node.get("r_event").get("specification").asText();
      qEvent = new EventImpl(name,specification);
    }

    return ScopeFactory.getScope(type,qEvent,rEvent);
  }

}
