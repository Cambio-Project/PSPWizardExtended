package restapi.psp_mapping.json_processing;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import psp.constraints.GreaterThanEqualProbability;
import psp.constraints.GreaterThanProbability;
import psp.constraints.LowerThanEqualProbability;
import psp.constraints.LowerThanProbability;
import psp.constraints.ProbabilityBound;

public class ProbabilityBoundFactory {
  	public static ProbabilityBound getProbabilityBound(JsonParser p, String type, double probability) throws JsonMappingException {
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
        throw JsonMappingException.from(p, String.format("Unsupported probability type: %s", type));
    }
	}
}
