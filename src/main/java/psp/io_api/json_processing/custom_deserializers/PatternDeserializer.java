package psp.io_api.json_processing.custom_deserializers;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import psp.sel.EventImpl;
import psp.sel.patterns.Pattern;
import psp.sel.patterns.order.ChainEvent;
import psp.sel.patterns.order.ChainEvents;
import psp.constraints.EventConstraint;
import psp.constraints.ProbabilityBound;
import psp.constraints.TimeBound;
import psp.io_api.exceptions.UnsupportedTypeException;
import psp.io_api.json_processing.PatternFactory;
import psp.io_api.json_processing.ProbabilityBoundFactory;
import psp.io_api.json_processing.TimeBoundFactory;

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
    EventImpl pEvent = new EventImpl(node.get("p_event").get("name").asText(),
            node.get("p_event").get("specification").asText());
    EventImpl sEvent = null;
    ChainEvents chainEvents = null;
    ProbabilityBound probabilityBound = null;
    TimeBound timeBound = null;
    EventConstraint constrainEvent = null;
    int upperLimit = 0;
    int frequency = 0;
    String timeUnit = null;

    if(node.has("s_event")) {
      String name = node.get("s_event").get("name").asText();
      String specification = node.get("s_event").get("specification").asText();
      sEvent = new EventImpl(name,specification);
    }

    if (node.has("chained_events")) {
      JsonNode chainedEventsNode = node.get("chained_events");
      chainEvents = buildChainEvents(chainedEventsNode);
    }


    if(node.has("pattern_specifications")) {
      JsonNode specificationsNode = node.get("pattern_specifications");

      timeUnit = specificationsNode.get("time_unit").asText();
      upperLimit = specificationsNode.get("upper_limit").asInt();
      frequency = specificationsNode.get("frequency").asInt();
    }

    if(node.has("pattern_constrains")) {
      JsonNode constrainsNode = node.get("pattern_constrains");

      if(constrainsNode.has("time_bound")) {
        JsonNode timeBoundNode = constrainsNode.get("time_bound");

        timeBound = TimeBoundFactory.getTimeBound(timeBoundNode.get("type").asText(),
                pEvent,
                timeBoundNode.get("lower_limit").asLong(),
                timeBoundNode.get("upper_limit").asLong(),
                timeBoundNode.get("time_unit").asText());
      }

      if(constrainsNode.has("probability_bound")) {
        JsonNode probabilityBoundNode = constrainsNode.get("probability_bound");

        probabilityBound = ProbabilityBoundFactory.getProbabilityBound(probabilityBoundNode.get("type").asText(),
                probabilityBoundNode.get("probability").asDouble());

      }

      if(constrainsNode.has("constrain_event")) {
        JsonNode eventConstrainNode = constrainsNode.get("constrain_event");
        String name = eventConstrainNode.get("name").asText();
        String specification = eventConstrainNode.get("specification").asText();

        constrainEvent = new EventConstraint(new EventImpl(name,specification));
      }

    }

    return PatternFactory.getPattern(type, pEvent, sEvent, chainEvents, probabilityBound,
            timeBound,constrainEvent, upperLimit, frequency, timeUnit);
  }

  private ChainEvents buildChainEvents(JsonNode chainedEventsNode) throws UnsupportedTypeException {
    ArrayList<ChainEvent> listChainEvents = new ArrayList<>();
      for (JsonNode jsonNode : chainedEventsNode) {
          String name = jsonNode.get("event").get("name").asText();
          String specification = jsonNode.get("event").get("specification").asText();
          EventImpl event = new EventImpl(name, specification);
          EventImpl chainConstrainEvent = null;
          TimeBound chainTimeBound = null;

          if (jsonNode.has("constrain_event")) {
              chainConstrainEvent = new EventImpl(jsonNode.get("constrain_event").get("name").asText(),
                      jsonNode.get("constrain_event").get("specification").asText());
          }

          if (jsonNode.has("time_bound")) {
              chainTimeBound = TimeBoundFactory.getTimeBound(jsonNode.get("time_bound").get("type").asText(),
                      event,
                      jsonNode.get("time_bound").get("lower_limit").asLong(),
                      jsonNode.get("time_bound").get("upper_limit").asLong(),
                      jsonNode.get("time_bound").get("time_unit").asText());
          }
          ChainEvent chainEvent = new ChainEvent(event, chainConstrainEvent, chainTimeBound);
          listChainEvents.add(chainEvent);
      }
      return new ChainEvents(listChainEvents);
  }

}
