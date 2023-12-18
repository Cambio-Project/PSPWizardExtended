package psp.io_api.json_processing;

import psp.io_api.exceptions.UnsupportedTypeException;
import psp.sel.EventImpl;
import psp.sel.scopes.AfterQ;
import psp.sel.scopes.AfterQuntilR;
import psp.sel.scopes.BeforeR;
import psp.sel.scopes.BetweenQandR;
import psp.sel.scopes.Globally;
import psp.sel.scopes.Scope;

public class ScopeFactory {

	public static Scope getScope(String type, EventImpl qEvent, EventImpl rEvent) throws UnsupportedTypeException {
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
        throw new UnsupportedTypeException(String.format("Unsupported scope: %s", type));
    }
	}
}
