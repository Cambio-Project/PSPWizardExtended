/**
 *
 * Copyright (C) 2011-2014 Swinburne University of Technology
 * <p>
 * This file is part of PSPWizard, a tool for machine-assisted
 * definition of temporal formulae capturing pattern-based system
 * properties, developed at the Faculty of Science, Engineering and
 * Technology at Swinburne University of Technology, Australia.
 * The patterns, structured English grammar and mappings are due to
 * <p>
 * Marco Autili, Universita` dell'Aquila
 * Lars Grunske, University of Stuttgart
 * Markus Lumpe, Swinburne University of Technology
 * Patrizio Pelliccione, University of Gothenburg
 * Antony Tang, Swinburne University of Technology
 * <p>
 * Details about the PSP framework can found in
 * "Aligning Qualitative, Real-Time, and Probabilistic
 * Property Specification Patterns Using a Structured
 * English Grammar"
 * <p>
 * <p>
 * PSPWizard is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 * <p>
 * PSPWizard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with PSPWizard; see the file COPYING.  If not, write to
 * the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

package psp.sel.patterns.order;

import psp.constraints.EventConstraint;
import psp.constraints.ProbabilityBound;
import psp.constraints.TimeBound;
import psp.engine.PSPConstants;
import psp.sel.EventImpl;
import psp.sel.patterns.Order;

public class Precedence extends Order {
    private TimeBound fPTimeBound;
    private EventConstraint fSConstraint;

    public TimeBound getPTimeBound() {
        return fPTimeBound;
    }

    public void setPTimeBound(TimeBound aPTimeBound) {
        fPTimeBound = aPTimeBound;
    }

    public EventConstraint getSConstraint() {
        return fSConstraint;
    }

    public void setSConstraint(EventConstraint aSConstraint) {
        fSConstraint = aSConstraint;
    }

    public Precedence() {
        this(EventImpl.getDefault(), EventImpl.getDefault(), null, null, null);
    }

    public Precedence(EventImpl aP, EventImpl aS, TimeBound aPTimeBound, EventConstraint aSConstraint,
                      ProbabilityBound aProbBound) {
        super(aP, aS, null, aProbBound);

        fPTimeBound = aPTimeBound;
        fSConstraint = aSConstraint;
    }

    public int getType() {
        return PSPConstants.P_Precedence;
    }

    public String getSpecificationAsSEL() {
        StringBuilder sb = new StringBuilder();

        sb.append("if ");
        sb.append(getP().getAsSELEvent());
        sb.append(" [holds]");

        sb.append(" then it must be the case that ");
        sb.append(getS().getAsSELEvent());
        sb.append(" [has occurred]");

        if (fPTimeBound != null) {
            sb.append(" ");
            sb.append(fPTimeBound.getSpecificationAsSEL());
        }

        sb.append(" before ");
        sb.append(getP().getAsSELEvent());
        sb.append(" [holds]");

        if (getProbabilityBound() != null) {
            sb.append(" ");
            sb.append(getProbabilityBound().getSpecificationAsSEL());
        }

        return sb.toString();
    }
}
