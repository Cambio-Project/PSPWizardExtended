package psp.io_api.json_processing;

import psp.sel.EventImpl;
import  psp.sel.patterns.Pattern;
import psp.sel.patterns.occurrence.Absence;
import psp.sel.patterns.occurrence.BoundedExistence;
import psp.sel.patterns.occurrence.Existence;
import psp.sel.patterns.occurrence.MaximumDuration;
import psp.sel.patterns.occurrence.MinimumDuration;
import psp.sel.patterns.occurrence.Recurrence;
import psp.sel.patterns.occurrence.SteadyState;
import psp.sel.patterns.occurrence.TransientState;
import psp.sel.patterns.occurrence.Universality;
import psp.sel.patterns.order.ChainEvents;
import psp.sel.patterns.order.Precedence;
import psp.sel.patterns.order.PrecedenceChain1N;
import psp.sel.patterns.order.PrecedenceChainN1;
import psp.sel.patterns.order.Response;
import psp.sel.patterns.order.ResponseChain1N;
import psp.sel.patterns.order.ResponseChainN1;
import psp.sel.patterns.order.ResponseInvariance;
import psp.sel.patterns.order.Until;
import psp.constraints.EventConstraint;
import psp.constraints.ProbabilityBound;
import psp.constraints.TimeBound;
import psp.io_api.exceptions.UnsupportedTypeException;


public class PatternFactory {

	public static Pattern getPattern(String type, EventImpl pEvent, EventImpl sEvent, ChainEvents chainEvents, ProbabilityBound probabilityBound, TimeBound timeBound, EventConstraint constrainEvent, int upperLimit, int frequency, String timeUnit) throws UnsupportedTypeException {
    // Occurrence
		if("Universality".equalsIgnoreCase(type)) {
      return new Universality(pEvent, timeBound,probabilityBound);
    }
		else if("Absence".equalsIgnoreCase(type)) {
      return new Absence(pEvent, timeBound,probabilityBound);
    }
    else if("Existence".equalsIgnoreCase(type)) {
      return new Existence(pEvent, timeBound,probabilityBound);
    }
    else if("BoundedExistence".equalsIgnoreCase(type)) {
      return new BoundedExistence(pEvent, frequency,timeBound, probabilityBound);
    }
    else if("TransientState".equalsIgnoreCase(type)) {
      return new TransientState(pEvent, upperLimit, timeUnit, probabilityBound);
    }
    else if("SteadyState".equalsIgnoreCase(type)) {
      return new SteadyState(pEvent, probabilityBound);
    }
    else if("MinimumDuration".equalsIgnoreCase(type)) {
      return new MinimumDuration(pEvent, upperLimit, timeUnit, probabilityBound);
    }
    else if("MaximumDuration".equalsIgnoreCase(type)) {
      return new MaximumDuration(pEvent, upperLimit, timeUnit, probabilityBound);
    }
    else if("Recurrence".equalsIgnoreCase(type)) {
      return new Recurrence(pEvent, upperLimit, timeUnit, probabilityBound);
    }
    // Order
    else if("Precedence".equalsIgnoreCase(type)) {
      return new Precedence(pEvent, sEvent, timeBound, probabilityBound);
    }
    else if("PrecedenceChain1N".equalsIgnoreCase(type)) {
      return new PrecedenceChain1N(pEvent, sEvent, chainEvents, timeBound, constrainEvent, probabilityBound);
    }
    else if("PrecedenceChainN1".equalsIgnoreCase(type)) {
      return new PrecedenceChainN1(pEvent, sEvent, chainEvents, timeBound, constrainEvent, probabilityBound);
    }
    else if("Until".equalsIgnoreCase(type)) {
      return new Until(pEvent, sEvent, timeBound, probabilityBound);
    }
    else if("Response".equalsIgnoreCase(type)) {
      return new Response(pEvent, sEvent, timeBound, constrainEvent.getEvent(), probabilityBound);
    } // TODO check the inconsistency regardin constraint type
    else if("ResponseChain1N".equalsIgnoreCase(type)) {
      return new ResponseChain1N(pEvent, sEvent, chainEvents, timeBound,constrainEvent, probabilityBound);
    }
    else if("ResponseChainN1".equalsIgnoreCase(type)) {
      return new ResponseChainN1(pEvent, sEvent, chainEvents, timeBound,constrainEvent, probabilityBound);
    }
    else if("ResponseInvariance".equalsIgnoreCase(type)) {
      return new ResponseInvariance(pEvent, sEvent, timeBound, probabilityBound);
    }
    else {
      throw new UnsupportedTypeException(String.format("Unsupported pattern: %s", type));
    }
	}

}
