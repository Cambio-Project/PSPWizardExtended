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
import java.util.Collections;
import java.util.List;

import psp.constraints.EventConstraint;
import psp.constraints.Interval;
import psp.engine.PSPConstants;
import psp.mappings.elements.AlwaysElement;
import psp.mappings.elements.AndElement;
import psp.mappings.elements.Element;
import psp.mappings.elements.ErrorElement;
import psp.mappings.elements.EventuallyElement;
import psp.mappings.elements.ImplicationElement;
import psp.mappings.elements.MiscElement;
import psp.mappings.elements.NextElement;
import psp.mappings.elements.NotElement;
import psp.mappings.elements.OrElement;
import psp.mappings.elements.RoundBracketCloseElement;
import psp.mappings.elements.RoundBracketOpenElement;
import psp.mappings.elements.SpaceElement;
import psp.mappings.elements.TimeBoundElement;
import psp.mappings.elements.UntilElement;
import psp.mappings.elements.WeakUntilElement;
import psp.sel.EventImpl;
import psp.sel.patterns.Pattern;
import psp.sel.patterns.order.*;
import psp.sel.patterns.occurrence.*;
import psp.sel.scopes.Scope;

public class MTLMapper extends MTLSupport {

    private static final String defaultInf = "∞";
    private static final AlwaysElement defaultAlways = new AlwaysElement("☐");
    private static final EventuallyElement defaultEventually = new EventuallyElement("◇");
    private static final NextElement defaultNext = new NextElement("○");
    private static final ImplicationElement defaultImplication = new ImplicationElement(" → ");
    private static final NotElement defaultNot = new NotElement("¬");
    private static final AndElement defaultAnd = new AndElement(" ∧ ");
    private static final OrElement defaultOr = new OrElement(" ∨ ");
    private static final UntilElement defaultUntil = new UntilElement(" U");
    private static final WeakUntilElement defaultWeakUntil = new WeakUntilElement(" W");
    private static final LanguageDefinitions DEFAULT_LANGUAGE_DEFINITION = new LanguageDefinitions(defaultInf,
        defaultAlways, defaultEventually, defaultNext, defaultImplication, defaultNot, defaultAnd, defaultOr,
        defaultUntil, defaultWeakUntil);

    public MTLMapper() {
        super(DEFAULT_LANGUAGE_DEFINITION, new TimeLanguageDefinitions("[", "]", "(", ")"));
    }

    public boolean isScopeSupported(Scope aScope) {
        return true;
    }

    public boolean isPatternSupported(Pattern aPattern) {
        switch (aPattern.getType()) {
            case PSPConstants.P_BoundedExistence:
            case PSPConstants.P_TransientState:
            case PSPConstants.P_SteadyState:
                return false;
            default:
        }

        return true;
    }

    public boolean isCombinationSupported(Scope aScope, Pattern aPattern) {
        return isPatternSupported(aPattern);
    }

    // pattern mapping
    public List<Element> mapToElements(Scope aScope, Pattern aPattern) {
        clearError();

        try // if something goes wrong
        {
            switch (aPattern.getType()) {
                case PSPConstants.P_Universality:
                    return mapUniversality(aScope, (Universality) aPattern);
                case PSPConstants.P_Absence:
                    return mapAbsence(aScope, (Absence) aPattern);
                case PSPConstants.P_Existence:
                    return mapExistence(aScope, (Existence) aPattern);
                case PSPConstants.P_BoundedExistence:
                case PSPConstants.P_TransientState:
                case PSPConstants.P_SteadyState:
                    return Collections.emptyList(); // not supported indicator
                case PSPConstants.P_MinimumDuration:
                    return mapMinimumDuration(aScope, (MinimumDuration) aPattern);
                case PSPConstants.P_MaximumDuration:
                    return mapMaximumDuration(aScope, (MaximumDuration) aPattern);
                case PSPConstants.P_Recurrence:
                    return mapRecurrence(aScope, (Recurrence) aPattern);
                case PSPConstants.P_Precedence:
                    return mapPrecedence(aScope, (Precedence) aPattern);
                case PSPConstants.P_PrecedenceChain1N:
                    return mapPrecedenceChain1N(aScope, (PrecedenceChain1N) aPattern);
                case PSPConstants.P_PrecedenceChainN1:
                    return mapPrecedenceChainN1(aScope, (PrecedenceChainN1) aPattern);
                case PSPConstants.P_Until:
                    return mapUntil(aScope, (Until) aPattern);
                case PSPConstants.P_Response:
                    return mapResponse(aScope, (Response) aPattern);
                case PSPConstants.P_ResponseChain1N:
                    return mapResponseChain1N(aScope, (ResponseChain1N) aPattern);
                case PSPConstants.P_ResponseChainN1:
                    return mapResponseChainN1(aScope, (ResponseChainN1) aPattern);
                case PSPConstants.P_ResponseInvariance:
                    return mapResponseInvariance(aScope, (ResponseInvariance) aPattern);
            }
        } catch (Exception e) {
            markError();
            return List.of(new ErrorElement(e));
        }

        return Collections.emptyList();
    }

    private List<Element> mapUniversality(Scope aScope, Universality aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                break;
            case PSPConstants.S_BeforeR:
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(lmintime(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQ:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BetweenQandR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(umintime(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(lmintime(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(umintime(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }

    private List<Element> mapAbsence(Scope aScope, Absence aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                break;
            case PSPConstants.S_BeforeR:
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(lmintime(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQ:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BetweenQandR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(umintime(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(lmintime(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(umintime(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }

    private List<Element> mapExistence(Scope aScope, Existence aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                break;
            case PSPConstants.S_BeforeR:
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQ:
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BetweenQandR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(umintime(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(lmintime(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(umintime(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }

    private List<Element> mapMinimumDuration(Scope aScope, MinimumDuration aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BeforeR:
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQ:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BetweenQandR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }

    private List<Element> mapMaximumDuration(Scope aScope, MaximumDuration aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BeforeR:
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQ:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BetweenQandR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }

    private List<Element> mapRecurrence(Scope aScope, Recurrence aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BeforeR:
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQ:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BetweenQandR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }

    private List<Element> mapPrecedence(Scope aScope, Precedence aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BeforeR:
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(elapsed((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQ:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BetweenQandR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(elapsed((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(elapsed((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }

    // Tis addressed from 0 to n-1.

    private void PC1N_Ch(List<Element> elements, ChainEvents Tis, int i) {
        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";

            if (lcntZi.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                PC1N_Ch(elements, Tis, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                elements.add(languageDefinitions.getAnd());
                elements.add(new MiscElement(lcntZi));
                PC1N_Ch(elements, Tis, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        }
    }

    private List<Element> mapPrecedenceChain1N(Scope aScope, PrecedenceChain1N aPattern) {
        List<Element> elements = new ArrayList<>();

        EventConstraint lZS = aPattern.getSConstraint();
        String lcntZS = lZS != null ? cnt(lZS.getEvent()) : "true";
        boolean lHasConstraint = !lcntZS.equals("true");
        ChainEvents Tis = aPattern.getTis();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                }
                PC1N_Ch(elements, Tis, 0);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BeforeR:
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                }
                PC1N_Ch(elements, Tis, 0);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapNP(Tis.size(), Tis, (Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQ:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                }
                PC1N_Ch(elements, Tis, 0);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BetweenQandR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                }
                PC1N_Ch(elements, Tis, 0);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapNP(Tis.size(), Tis, (Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                }
                PC1N_Ch(elements, Tis, 0);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapNP(Tis.size(), Tis, (Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }
        return elements;
    }

    // Tis addressed from 0 to n-1.

    private void PCN1_Ch(List<Element> elements, PrecedenceChainN1 aPattern, int i) {
        ChainEvents Tis = aPattern.getTis();

        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";

            // unconstrained
            if (lcntZi.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapPN(i + 1, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                PCN1_Ch(elements, aPattern, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new MiscElement(lcntZi));
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(gapPN(i + 1, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                PCN1_Ch(elements, aPattern, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        }
    }

    private List<Element> mapPrecedenceChainN1(Scope aScope, PrecedenceChainN1 aPattern) {
        List<Element> elements = new ArrayList<>();

        EventConstraint lZP = aPattern.getPConstraint();
        String lcntZP = lZP != null ? cnt(lZP.getEvent()) : "true";
        boolean lHasConstraint = !lcntZP.equals("true");
        ChainEvents Tis = aPattern.getTis();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aPattern.getP());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                } else {
                    // no ZS
                    elements.add(languageDefinitions.getEventually());
                }
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                PCN1_Ch(elements, aPattern, 0);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BeforeR:
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getEventually());
                    elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getImplication());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aPattern.getP());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    PCN1_Ch(elements, aPattern, 0);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                } else {
                    // no ZS
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getEventually());
                    elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getImplication());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getEventually());
                    elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    PCN1_Ch(elements, aPattern, 0);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(elapsed((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQ:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aPattern.getP());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                } else {
                    // no ZS
                    elements.add(languageDefinitions.getEventually());
                }
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                PCN1_Ch(elements, aPattern, 0);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BetweenQandR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aPattern.getP());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                } else {
                    // no ZS
                    elements.add(languageDefinitions.getEventually());
                }
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                PCN1_Ch(elements, aPattern, 0);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(elapsed((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aPattern.getP());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                } else {
                    // no ZS
                    elements.add(languageDefinitions.getEventually());
                }
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                PCN1_Ch(elements, aPattern, 0);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(elapsed((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }

    private List<Element> mapUntil(Scope aScope, Until aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getS());
                break;
            case PSPConstants.S_BeforeR:
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(lmintime(aPattern.getPTimeBound())));
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getOr());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQ:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BetweenQandR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(lmintime(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getOr());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(umintime(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }

    private List<Element> mapResponse(Scope aScope, Response aPattern) {
        List<Element> elements = new ArrayList<>();

        EventConstraint lZS = aPattern.getSConstraint();
        String lcntZS = lZS != null ? cnt(lZS.getEvent()) : "true";
        boolean lHasConstraint = !lcntZS.equals("true");

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    elements.add(new MiscElement(lcntZS));
                    elements.add(languageDefinitions.getUntil());
                } else {
                    elements.add(languageDefinitions.getEventually());
                }
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BeforeR:
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(lmintime(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQ:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                if (lHasConstraint) {
                    elements.add(new MiscElement(lcntZS));
                    elements.add(languageDefinitions.getUntil());
                } else {
                    elements.add(languageDefinitions.getEventually());
                }
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BetweenQandR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(umintime(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(lmintime(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    // elements.add(languageDefinitions.getAnd());
                    // TODO: sb.deleteCharAt(sb.length() - 1);
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(umintime(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }

    // Tis addressed from 0 to n-1.

    private void RC1N_Ch(List<Element> elements, ChainEvents Tis, int i) {
        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";
            boolean lHasConstraint = !lcntZi.equals("true");

            if (lHasConstraint) {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new MiscElement(lcntZi));
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RC1N_Ch(elements, Tis, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RC1N_Ch(elements, Tis, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        }
    }

    private void RC1N_ChR(List<Element> elements, ChainEvents Tis, EventImpl R, int i) {
        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";
            boolean lHasConstraint = !lcntZi.equals("true");

            if (lHasConstraint) {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(languageDefinitions.getAnd());
                elements.add(new MiscElement(lcntZi));
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RC1N_ChR(elements, Tis, R, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RC1N_ChR(elements, Tis, R, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        }
    }

    private List<Element> mapResponseChain1N(Scope aScope, ResponseChain1N aPattern) {
        List<Element> elements = new ArrayList<>();

        EventConstraint lZS = aPattern.getSConstraint();
        String lcntZS = lZS != null ? cnt(lZS.getEvent()) : "true";
        boolean lHasConstraint = !lcntZS.equals("true");

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    elements.add(new MiscElement(lcntZS));
                    elements.add(languageDefinitions.getUntil());
                } else {
                    elements.add(languageDefinitions.getEventually());
                }
                elements.add(new TimeBoundElement(umintime(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RC1N_Ch(elements, aPattern.getTis(), 0);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BeforeR:
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(umintime(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RC1N_ChR(elements, aPattern.getTis(), aScope.getR(), 0);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQ:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    elements.add(new MiscElement(lcntZS));
                    elements.add(languageDefinitions.getUntil());
                } else {
                    elements.add(languageDefinitions.getEventually());
                }
                elements.add(new TimeBoundElement(umintime(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RC1N_Ch(elements, aPattern.getTis(), 0);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BetweenQandR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(umintime(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RC1N_ChR(elements, aPattern.getTis(), aScope.getR(), 0);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(umintime(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RC1N_ChR(elements, aPattern.getTis(), aScope.getR(), 0);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }

    // Tis addressed from 0 to n-1.

    private void RCN1_Ch(List<Element> elements, ResponseChainN1 aPattern, int i) {
        ChainEvents Tis = aPattern.getTis();

        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";

            if (lcntZi.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RCN1_Ch(elements, aPattern, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RCN1_Ch(elements, aPattern, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        } else {
            EventConstraint lZP = aPattern.getPConstraint();
            String lcntZP = lZP != null ? cnt(lZP.getEvent()) : "true";

            elements.add(languageDefinitions.getImplication());
            elements.add(new RoundBracketOpenElement());
            if (lcntZP.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getEventually());
            } else {
                // with Zi
                elements.add(new MiscElement(lcntZP));
                elements.add(languageDefinitions.getUntil());
            }
            elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
            elements.add(new SpaceElement());
            elements.add(aPattern.getP());
            elements.add(new RoundBracketCloseElement());
        }
    }

    private void RCN1_ChR(List<Element> elements, ResponseChainN1 aPattern, EventImpl R, int i) {
        ChainEvents Tis = aPattern.getTis();

        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";

            if (lcntZi.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RCN1_ChR(elements, aPattern, R, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(languageDefinitions.getAnd());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RCN1_ChR(elements, aPattern, R, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        } else {
            EventConstraint lZP = aPattern.getPConstraint();
            String lcntZP = lZP != null ? cnt(lZP.getEvent()) : "true";

            elements.add(languageDefinitions.getImplication());
            elements.add(new RoundBracketOpenElement());
            if (lcntZP.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getEventually());
            } else {
                // with Zi
                elements.add(new MiscElement(lcntZP));
                elements.add(languageDefinitions.getUntil());
            }
            elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
            elements.add(new SpaceElement());
            elements.add(aPattern.getP());
            elements.add(new RoundBracketCloseElement());
        }
    }

    private List<Element> mapResponseChainN1(Scope aScope, ResponseChainN1 aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_Ch(elements, aPattern, 0);
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BeforeR:
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_ChR(elements, aPattern, aScope.getR(), 0);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQ:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_Ch(elements, aPattern, 0);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BetweenQandR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_ChR(elements, aPattern, aScope.getR(), 0);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_ChR(elements, aPattern, aScope.getR(), 0);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }

    private List<Element> mapResponseInvariance(Scope aScope, ResponseInvariance aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BeforeR:
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(lmintime(aPattern.getSTimeBound())));
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQ:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BetweenQandR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(umintime(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(lmintime(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(languageDefinitions.getAlways());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(umintime(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }
}
