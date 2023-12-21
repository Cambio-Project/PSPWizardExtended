package restapi.psp_mapping.json_processing;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import psp.sel.EventImpl;
import psp.sel.scopes.AfterQ;
import psp.sel.scopes.AfterQuntilR;
import psp.sel.scopes.BeforeR;
import psp.sel.scopes.BetweenQandR;
import psp.sel.scopes.Globally;
import psp.sel.scopes.Scope;

public class ScopeFactory {

	public static Scope getScope(JsonParser p, String type, EventImpl qEvent, EventImpl rEvent) throws JsonMappingException {
		if("Globally".equalsIgnoreCase(type)) {
      return new Globally();
    }
		else if("BeforeR".equalsIgnoreCase(type)) {
      return new BeforeR(rEvent);
    }
    else if("AfterQ".equalsIgnoreCase(type)) {
      return new AfterQ(qEvent);
    }
    else if("BetweenQandR".equalsIgnoreCase(type)) {
      return new BetweenQandR(qEvent, rEvent);
    }
    else if("AfterQUntilR".equalsIgnoreCase(type)) {
       return new AfterQuntilR(qEvent, rEvent);
    }
    else {
        throw JsonMappingException.from(p, String.format("Unsupported scope: %s", type));
    }
	}
}
