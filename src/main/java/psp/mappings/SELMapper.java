/**
 *
 * Copyright (C) 2011-2014 Swinburne University of Technology
 *
 * This file is part of PSPWizard, a tool for machine-assisted 
 * definition of temporal formulae capturing pattern-based system
 * properties, developed at the Faculty of Science, Engineering and
 * Technology at Swinburne University of Technology, Australia.
 * The patterns, structured English grammar and mappings are due to
 *
 *   Marco Autili, Universita` dell'Aquila
 *   Lars Grunske, University of Stuttgart
 *   Markus Lumpe, Swinburne University of Technology
 *   Patrizio Pelliccione, University of Gothenburg
 *   Antony Tang, Swinburne University of Technology
 *
 * Details about the PSP framework can found in
 *   "Aligning Qualitative, Real-Time, and Probabilistic
 *    Property Specification Patterns Using a Structured
 *    English Grammar"
 *
 *
 * PSPWizard is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * PSPWizard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PSPWizard; see the file COPYING.  If not, write to
 * the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

package psp.mappings;

import java.util.ArrayList;
import java.util.List;

import psp.mappings.elements.Element;
import psp.mappings.elements.MiscElement;
import psp.sel.patterns.Pattern;
import psp.sel.scopes.Scope;

public class SELMapper extends GenericMapper {
    public SELMapper() {
        super(new LanguageDefinitions(null, null, null, null, null, null, null, null, null, null));
    }

    public boolean isScopeSupported(Scope aScope) {
        return true;
    }

    public boolean isPatternSupported(Pattern aPattern) {
        return true;
    }

    public boolean isCombinationSupported(Scope aScope, Pattern aPattern) {
        return true;
    }

    @Override
    public  List<Element> mapToElements(Scope aScope, Pattern aPattern) {
        //StringBuilder sb = new StringBuilder();
        List<Element> elements = new ArrayList();
        
        if (aScope != null && aPattern != null) {
            elements.add(new MiscElement(aScope.getSpecificationAsSEL()));
            elements.add(new MiscElement(", "));
            elements.add(new MiscElement(aPattern.getSpecificationAsSEL()));
            elements.add(new MiscElement("."));
        }

        return elements;
    }

    public String getNotSupportedMessage() {
        return "Mapping not supported in Structured English Grammar.";
    }

    public String toString() {
        return "SEG";
    }

}
