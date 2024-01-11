package restapi.psp_mapping.json_processing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import psp.mappings.*;

public class MapperSupplier {

  private static final Map<String, Supplier<PatternMapper>> MAPPER_SUPPLIER;

  static {
      final Map<String, Supplier<PatternMapper>>
      mappers = new HashMap<>();
      mappers.put("SEL", SELMapper::new);
      mappers.put("LTL", LTLMapper::new);
      mappers.put("MTL", MTLMapper::new);
      mappers.put("Prism", PrismMapper::new);
      mappers.put("Quantitative Prism", QuantitativePrismMapper::new);
      mappers.put("TBV (timed)", TimedTBVMapper::new);
      mappers.put("TBV (untimed)", UntimedTBVMapper::new);

      MAPPER_SUPPLIER = Collections.unmodifiableMap(mappers);
   }


   public PatternMapper supplyMapper(JsonParser p, String mapperType) throws JsonMappingException {
      Supplier<PatternMapper> mapper = MAPPER_SUPPLIER.get(mapperType);
      if (mapper == null) {
         throw JsonMappingException.from(p, String.format("Unsupported target logic: %s",mapperType));
      }
      return mapper.get();
   }

}