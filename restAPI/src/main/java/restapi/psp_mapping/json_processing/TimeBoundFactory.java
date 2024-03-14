package restapi.psp_mapping.json_processing;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import psp.constraints.Interval;
import psp.constraints.LowerTimeBound;
import psp.constraints.TimeBound;
import psp.constraints.UpperTimeBound;
import psp.sel.EventImpl;

public class TimeBoundFactory {
	public static TimeBound getTimeBound(JsonParser p, String type, EventImpl timedEvent, Long lowerLimit, Long upperLimit, String timeUnit) throws JsonMappingException {
		if("Upper".equalsIgnoreCase(type)) {
            if(upperLimit == null) {
                throw JsonMappingException.from(p, "An upper time limit needs to be specified.");
            }
      return new UpperTimeBound(timedEvent, upperLimit, timeUnit);
    }
		else if("Lower".equalsIgnoreCase(type)) {
            if(lowerLimit == null) {
                throw JsonMappingException.from(p, "A lower time limit needs to be specified.");
            }
      return new LowerTimeBound(timedEvent, lowerLimit, timeUnit);
    }
    else if("Interval".equalsIgnoreCase(type)) {
            if(lowerLimit == null || upperLimit == null) {
                throw JsonMappingException.from(p, "An upper and a lower time limit need to be specified.");
            }
      return new Interval(timedEvent, lowerLimit, upperLimit, timeUnit);
    }
    else {
        throw JsonMappingException.from(p, String.format("Unsupported time bound: %s", type));
    }
	}

}
