package restapi.psp_mapping.json_processing;

import psp.constraints.GreaterThanEqualProbability;
import psp.constraints.GreaterThanProbability;
import psp.constraints.LowerThanEqualProbability;
import psp.constraints.LowerThanProbability;
import psp.constraints.ProbabilityBound;
import restapi.psp_mapping.exceptions.UnsupportedTypeException;

public class ProbabilityBoundFactory {
  	public static ProbabilityBound getProbabilityBound(String type, double probability) throws UnsupportedTypeException {
		if("Lower".equalsIgnoreCase(type)) {
      return new LowerThanProbability(probability);
    }
		else if("LowerEqual".equalsIgnoreCase(type)) {
      return new LowerThanEqualProbability(probability);
    }
    else if("Greater".equalsIgnoreCase(type)) {
      return new GreaterThanProbability(probability);
    }
    else if("GreaterEqual".equalsIgnoreCase(type)) {
      return new GreaterThanEqualProbability(probability);
    }
    else {
        throw new UnsupportedTypeException(String.format("Unsupported probability type: %s", type));
    }
	}
}
