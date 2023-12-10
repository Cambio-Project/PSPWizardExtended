package restapi.psp_mapping.json_processing.custom_deserializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import restapi.psp_mapping.json_processing.MapperSupplier;
import psp.mappings.PatternMapper;

public class MapperDeserializer extends StdDeserializer<PatternMapper> {

  public MapperDeserializer() {
    this(null);
  }

  public MapperDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public PatternMapper deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JacksonException {
    MapperSupplier supplier = new MapperSupplier();

    JsonNode node = parser.getCodec().readTree(parser);
    String targetLogic = node.asText();

    return supplier.supplyMapper(targetLogic);
  }

}
