package psp.io_api.json_processing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import psp.mappings.PatternMapper;
import psp.mappings.LTLMapper;
import psp.mappings.MTLMapper;
import psp.mappings.PrismMapper;
import psp.mappings.QuantitativePrismMapper;
import psp.mappings.TimedTBVMapper;
import psp.mappings.UntimedTBVMapper;
import psp.io_api.exceptions.UnsupportedTypeException;


public class MapperSupplier {

  private static final Map<String, Supplier<PatternMapper>> MAPPER_SUPPLIER;

  static {
      final Map<String, Supplier<PatternMapper>>
      mappers = new HashMap<>();
      mappers.put("LTL", LTLMapper::new);
      mappers.put("MTL", MTLMapper::new);
      mappers.put("Prism", PrismMapper::new);
      mappers.put("Quantitative Prism", QuantitativePrismMapper::new);
      mappers.put("TBV (timed)", TimedTBVMapper::new);
      mappers.put("TBV (untimed)", UntimedTBVMapper::new);

      MAPPER_SUPPLIER = Collections.unmodifiableMap(mappers);
   }


   public PatternMapper supplyMapper(String mapperType) throws UnsupportedTypeException {
      Supplier<PatternMapper> mapper = MAPPER_SUPPLIER.get(mapperType);
      if (mapper == null) {
         throw new UnsupportedTypeException(String.format("Unsupported target logic: %s",mapperType));
      }
      return mapper.get();
   }

}