package restapi.psp_mapping.json_processing.custom_deserializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import psp.sel.EventImpl;
import psp.sel.patterns.Pattern;
import psp.sel.patterns.order.ChainEvents;
import psp.constraints.EventConstraint;
import psp.constraints.ProbabilityBound;
import psp.constraints.TimeBound;
import restapi.psp_mapping.json_processing.PatternFactory;

public class PatternDeserializer extends StdDeserializer<Pattern> {

  public PatternDeserializer() {
    this(null);
  }

  public PatternDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public Pattern deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JacksonException {

    JsonNode node = parser.getCodec().readTree(parser);
    String type = node.get("type").asText();
    EventImpl pEvent = null;
    EventImpl sEvent = null;
    ChainEvents chainEvents = null;
    ProbabilityBound probabilityBound = null;
    TimeBound timeBound = null;
    EventConstraint constrainEvent = null;
    int upperLimit = 0;
    int frequency = 0;
    String timeUnit = null;

    return PatternFactory.getPattern(type, pEvent, sEvent, chainEvents, probabilityBound, timeBound,constrainEvent, upperLimit, frequency, timeUnit);
  }

}
