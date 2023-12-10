package restapi.psp_mapping.json_processing;

import psp.constraints.Interval;
import psp.constraints.LowerTimeBound;
import psp.constraints.TimeBound;
import psp.constraints.UpperTimeBound;
import restapi.psp_mapping.exceptions.UnsupportedTypeException;
import psp.sel.EventImpl;

public class TimeBoundFactory {
	public static TimeBound getTimeBound(String type, EventImpl timedEvent, long lowerLimit, long upperLimit, String timeUnit) throws UnsupportedTypeException {
		if("Upper".equalsIgnoreCase(type)) {
      return new UpperTimeBound(timedEvent, upperLimit, timeUnit);
    }
		else if("Lower".equalsIgnoreCase(type)) {
      return new LowerTimeBound(timedEvent, lowerLimit, timeUnit);
    }
    else if("Interval".equalsIgnoreCase(type)) {
      return new Interval(timedEvent, lowerLimit, upperLimit, timeUnit);
    }
    else {
        throw new UnsupportedTypeException(String.format("Unsupported time bound: %s", type));
    }
	}
}
