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

package psp.mappings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import psp.constraints.EventConstraint;
import psp.constraints.Interval;
import psp.constraints.TimeBound;
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
        // actually, infinity is not included, but our implementation is not able to handle this, so we use ] instead
        // of ) for infinity
        super(DEFAULT_LANGUAGE_DEFINITION, new TimeLanguageDefinitions("[", "]", "[", "]"));
    }

    public boolean isScopeSupported(Scope aScope) {
        return true;
    }

    public boolean isPatternSupported(Pattern aPattern) {
        switch (aPattern.getType()) {
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
                    return mapBoundedExistence(aScope, (BoundedExistence) aPattern);
                case PSPConstants.P_BoundedEventExistence:
                    return mapBoundedEventExistence(aScope, (BoundedEventExistence) aPattern);
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
                case PSPConstants.P_RelaxedPrecedenceChain1N:
                    return mapRelaxedPrecedenceChain1N(aScope, (RelaxedPrecedenceChain1N) aPattern);
                case PSPConstants.P_RelaxedPrecedenceChainN1:
                    return mapRelaxedPrecedenceChainN1(aScope, (RelaxedPrecedenceChainN1) aPattern);
                case PSPConstants.P_Until:
                    return mapUntil(aScope, (Until) aPattern);
                case PSPConstants.P_Response:
                    return mapResponse(aScope, (Response) aPattern);
                case PSPConstants.P_ResponseChain1N:
                    return mapResponseChain1N(aScope, (ResponseChain1N) aPattern);
                case PSPConstants.P_ResponseChainN1:
                    return mapResponseChainN1(aScope, (ResponseChainN1) aPattern);
                case PSPConstants.P_RelaxedResponseChain1N:
                    return mapRelaxedResponseChain1N(aScope, (RelaxedResponseChain1N) aPattern);
                case PSPConstants.P_RelaxedResponseChainN1:
                    return mapRelaxedResponseChainN1(aScope, (RelaxedResponseChainN1) aPattern);
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
        return switch (aPattern.getSubType()) {
            case PSPConstants.SP_All_Basic -> mapUniversalityBasic(aScope, aPattern);
            case PSPConstants.SP_Conservative -> mapUniversalityConservative(aScope, aPattern);
            case PSPConstants.SP_Liberal -> mapUniversalityLiberal(aScope, aPattern);
            default -> throw new IllegalStateException("Unexpected value: " + aPattern.getSubType());
        };
    }

    private List<Element> mapUniversalityBasic(Scope aScope, Universality aPattern) {
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
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(lowerExact(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(lowerExact(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(lowerExact(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }


    private List<Element> mapUniversalityLiberal(Scope aScope, Universality aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(umintime(aPattern.getPTimeBound())));
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(lowerExact(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getUntil());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(lowerExact(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQ:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(umintime(aPattern.getPTimeBound())));
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(lowerExact(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getUntil());
                elements.add(languageDefinitions.getEndPlaceholder());
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
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(lowerExact(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(lowerExact(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(umintime(aPattern.getPTimeBound())));
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(lowerExact(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getUntil());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }


    private List<Element> mapUniversalityConservative(Scope aScope, Universality aPattern) {
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
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(lowerExact(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(lowerExact(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(lowerExact(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }

    private List<Element> mapAbsence(Scope aScope, Absence aPattern) {
        return switch (aPattern.getSubType()) {
            case PSPConstants.SP_All_Basic -> mapAbsenceBasic(aScope, aPattern);
            case PSPConstants.SP_Conservative -> mapAbsenceConservative(aScope, aPattern);
            default -> throw new IllegalStateException("Unexpected value: " + aPattern.getSubType());
        };
    }

    private List<Element> mapAbsenceBasic(Scope aScope, Absence aPattern) {
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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


    private List<Element> mapAbsenceConservative(Scope aScope, Absence aPattern) {
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
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(umintime(aPattern.getPTimeBound())));
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(languageDefinitions.getEventually());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(umintime(aPattern.getPTimeBound())));
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(umintime(aPattern.getPTimeBound())));
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }

    private List<Element> mapBoundedEventExistence(Scope aScope, BoundedEventExistence aPattern) {
        List<Element> elements = new ArrayList<>();

        if (aPattern.getFrequency() < 1) {
            return elements;
        }

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getNot());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.addAll(mapBoundedEventExistenceGloballyRepeating(aPattern, aPattern.getFrequency()));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BeforeR:
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.addAll(mapBoundedEventExistenceBeforeRepeating(aScope, aPattern, aPattern.getFrequency()));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQ:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                // this part is same as globally
                elements.add(languageDefinitions.getNot());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.addAll(mapBoundedEventExistenceGloballyRepeating(aPattern, aPattern.getFrequency()));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BetweenQandR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                // this part is same as in BEFORE after implication
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.addAll(mapBoundedEventExistenceBeforeRepeating(aScope, aPattern, aPattern.getFrequency()));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
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
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                // this part is almost same as in BEFORE after implication (except for Until R)
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.addAll(mapBoundedEventExistenceBeforeRepeating(aScope, aPattern, aPattern.getFrequency()));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                // elements.add(languageDefinitions.getUntil());
                // elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }

    private List<Element> mapBoundedEventExistenceGloballyRepeating(BoundedEventExistence aPattern,
                                                                    int remainingFrequency) {
        List<Element> elements = new ArrayList<>();
        if (remainingFrequency > 0) {
            elements.add(languageDefinitions.getAnd());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNext());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getEventually());
            elements.add(new RoundBracketOpenElement());
            elements.add(new RoundBracketOpenElement());
            elements.add(aPattern.getP());
            elements.add(languageDefinitions.getAnd());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNot());
            elements.add(languageDefinitions.getEndPlaceholder());
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
            elements.addAll(mapBoundedEventExistenceGloballyRepeating(aPattern, remainingFrequency - 1));
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
        }
        return elements;
    }

    private List<Element> mapBoundedEventExistenceBeforeRepeating(Scope aScope, BoundedEventExistence aPattern,
                                                                  int remainingFrequency) {
        List<Element> elements = new ArrayList<>();
        if (remainingFrequency > 0) {
            elements.add(languageDefinitions.getAnd());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNext());
            elements.add(new RoundBracketOpenElement());

            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNot());
            elements.add(aScope.getR());
            elements.add(new RoundBracketCloseElement());
            elements.add(languageDefinitions.getUntil());
            elements.add(new RoundBracketOpenElement());
            elements.add(new RoundBracketOpenElement());
            elements.add(new RoundBracketOpenElement());
            elements.add(aPattern.getP());
            elements.add(languageDefinitions.getAnd());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNot());
            elements.add(languageDefinitions.getEndPlaceholder());
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
            elements.add(languageDefinitions.getAnd());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNot());
            elements.add(aScope.getR());
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
            elements.addAll(mapBoundedEventExistenceBeforeRepeating(aScope, aPattern, remainingFrequency - 1));
            elements.add(new RoundBracketCloseElement());

            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
        }
        return elements;
    }

    private List<Element> mapBoundedExistenceGlobally(BoundedExistence aPattern, int remainingFrequency) {
        List<Element> elements = new ArrayList<>();
        if (remainingFrequency > 0) {
            elements.add(new RoundBracketOpenElement());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNot());
            elements.add(aPattern.getP());
            elements.add(new RoundBracketCloseElement());
            elements.add(languageDefinitions.getWeakUntil());
            elements.add(new RoundBracketOpenElement());
            elements.add(aPattern.getP());
            elements.add(languageDefinitions.getWeakUntil());
            elements.addAll(mapBoundedExistenceGlobally(aPattern, remainingFrequency - 1));
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
        } else {
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getAlways());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNot());
            elements.add(aPattern.getP());
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
        }
        return elements;
    }

    private List<Element> mapBoundedExistenceBefore(Scope aScope, BoundedExistence aPattern, int remainingFrequency) {
        List<Element> elements = new ArrayList<>();
        if (remainingFrequency > 0) {
            elements.add(new RoundBracketOpenElement());
            elements.add(aScope.getR());
            elements.add(languageDefinitions.getOr());
            elements.add(new RoundBracketOpenElement());
            elements.add(new RoundBracketOpenElement());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNot());
            elements.add(aPattern.getP());
            elements.add(new RoundBracketCloseElement());
            elements.add(languageDefinitions.getAnd());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNot());
            elements.add(aScope.getR());
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
            elements.add(languageDefinitions.getUntil());
            elements.add(new RoundBracketOpenElement());
            elements.add(aScope.getR());
            elements.add(languageDefinitions.getOr());
            elements.add(new RoundBracketOpenElement());
            elements.add(new RoundBracketOpenElement());
            elements.add(aPattern.getP());
            elements.add(languageDefinitions.getAnd());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNot());
            elements.add(aScope.getR());
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
            elements.add(languageDefinitions.getUntil());
            elements.addAll(mapBoundedExistenceBefore(aScope, aPattern, remainingFrequency - 1));
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
        } else {
            elements.add(new RoundBracketOpenElement());
            elements.add(aScope.getR());
            elements.add(languageDefinitions.getOr());
            elements.add(new RoundBracketOpenElement());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNot());
            elements.add(aPattern.getP());
            elements.add(new RoundBracketCloseElement());
            elements.add(languageDefinitions.getUntil());
            elements.add(aScope.getR());
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
        }
        return elements;
    }

    private List<Element> mapBoundedExistenceAfterUntil(Scope aScope, BoundedExistence aPattern,
                                                        int remainingFrequency) {
        List<Element> elements = new ArrayList<>();
        if (remainingFrequency > 0) {
            elements.add(new RoundBracketOpenElement());
            elements.add(aScope.getR());
            elements.add(languageDefinitions.getOr());
            elements.add(new RoundBracketOpenElement());
            elements.add(new RoundBracketOpenElement());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNot());
            elements.add(aPattern.getP());
            elements.add(new RoundBracketCloseElement());
            elements.add(languageDefinitions.getAnd());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNot());
            elements.add(aScope.getR());
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
            elements.add(languageDefinitions.getUntil());
            elements.add(new RoundBracketOpenElement());
            elements.add(aScope.getR());
            elements.add(languageDefinitions.getOr());
            elements.add(new RoundBracketOpenElement());
            elements.add(new RoundBracketOpenElement());
            elements.add(aPattern.getP());
            elements.add(languageDefinitions.getAnd());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNot());
            elements.add(aScope.getR());
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
            elements.add(languageDefinitions.getUntil());
            elements.addAll(mapBoundedExistenceAfterUntil(aScope, aPattern, remainingFrequency - 1));
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
        } else {
            elements.add(new RoundBracketOpenElement());
            elements.add(aScope.getR());
            elements.add(languageDefinitions.getOr());
            elements.add(new RoundBracketOpenElement());
            elements.add(new RoundBracketOpenElement());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNot());
            elements.add(aPattern.getP());
            elements.add(new RoundBracketCloseElement());
            elements.add(languageDefinitions.getWeakUntil());
            elements.add(aScope.getR());
            elements.add(new RoundBracketCloseElement());
            elements.add(languageDefinitions.getOr());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getAlways());
            elements.add(aPattern.getP());
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
        }
        return elements;
    }

    private List<Element> mapRecurrence(Scope aScope, Recurrence aPattern) {
        return switch (aPattern.getSubType()) {
            case PSPConstants.SP_All_Basic -> mapRecurrenceBasic(aScope, aPattern);
            case PSPConstants.SP_Liberal -> mapRecurrenceLiberal(aScope, aPattern);
            default -> throw new IllegalStateException("Unexpected value: " + aPattern.getSubType());
        };
    }

    private List<Element> mapBoundedExistence(Scope aScope, BoundedExistence aPattern) {
        return switch (aPattern.getSubType()) {
            case PSPConstants.SP_All_Basic -> mapBoundedExistenceBasic(aScope, aPattern);
            case PSPConstants.SP_Liberal -> mapBoundedExistenceLiberal(aScope, aPattern);
            default -> throw new IllegalStateException("Unexpected value: " + aPattern.getSubType());
        };
    }

    private List<Element> mapBoundedExistenceBasic(Scope aScope, BoundedExistence aPattern) {
        List<Element> elements = new ArrayList<>();

        if (aPattern.getFrequency() < 1) {
            return elements;
        }

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.addAll(mapBoundedExistenceGlobally(aPattern, aPattern.getFrequency()));
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
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.addAll(mapBoundedExistenceBefore(aScope, aPattern, aPattern.getFrequency() - 1));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQ:
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.addAll(mapBoundedExistenceGlobally(aPattern, aPattern.getFrequency()));
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.addAll(mapBoundedExistenceBefore(aScope, aPattern, aPattern.getFrequency() - 1));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.addAll(mapBoundedExistenceAfterUntil(aScope, aPattern, aPattern.getFrequency() - 1));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }


    private List<Element> mapBoundedExistenceLiberal(Scope aScope, BoundedExistence aPattern) {
        List<Element> elements = new ArrayList<>();

        if (aPattern.getFrequency() < 1) {
            return elements;
        }

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:

                // start with -R
                elements.addAll(mapBoundedExistenceGlobally(aPattern, aPattern.getFrequency()));

                elements.add(languageDefinitions.getOr());

                // start with R
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getWeakUntil());
                elements.addAll(mapBoundedExistenceGlobally(aPattern, aPattern.getFrequency() - 1));
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BeforeR:
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                // start with -R
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.addAll(mapBoundedExistenceBefore(aScope, aPattern, aPattern.getFrequency() - 1));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getOr());

                // start with R
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.addAll(mapBoundedExistenceBefore(aScope, aPattern, aPattern.getFrequency() - 1));
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQ:
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());

                // start with -R
                elements.addAll(mapBoundedExistenceGlobally(aPattern, aPattern.getFrequency()));

                elements.add(languageDefinitions.getOr());

                // start with R
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getWeakUntil());
                elements.addAll(mapBoundedExistenceGlobally(aPattern, aPattern.getFrequency() - 1));
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

                // start with -R
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.addAll(mapBoundedExistenceBefore(aScope, aPattern, aPattern.getFrequency() - 1));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getOr());

                // start with R
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.addAll(mapBoundedExistenceBefore(aScope, aPattern, aPattern.getFrequency() - 1));
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                // start with -R
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.addAll(mapBoundedExistenceAfterUntil(aScope, aPattern, aPattern.getFrequency() - 1));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getOr());

                // start with R
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.addAll(mapBoundedExistenceAfterUntil(aScope, aPattern, aPattern.getFrequency() - 1));
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }

    private List<Element> mapExistence(Scope aScope, Existence aPattern) {
        return switch (aPattern.getSubType()) {
            case PSPConstants.SP_All_Basic -> mapExistenceBasic(aScope, aPattern);
            case PSPConstants.SP_Conservative -> mapExistenceConservative(aScope, aPattern);
            case PSPConstants.SP_Liberal -> mapExistenceLiberal(aScope, aPattern);
            default -> throw new IllegalStateException("Unexpected value: " + aPattern.getSubType());
        };
    }

    private List<Element> mapExistenceBasic(Scope aScope, Existence aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                break;
            case PSPConstants.S_BeforeR:
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(lmintime(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
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

    private List<Element> mapExistenceLiberal(Scope aScope, Existence aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BeforeR:
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(lmintime(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
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
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
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

    private List<Element> mapExistenceConservative(Scope aScope, Existence aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                break;
            case PSPConstants.S_BeforeR:
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
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
            case PSPConstants.S_AfterQuntilR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
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
        return switch (aPattern.getSubType()) {
            case PSPConstants.SP_All_Basic -> mapMinimumDurationBasic(aScope, aPattern);
            case PSPConstants.SP_Liberal -> mapMinimumDurationLiberal(aScope, aPattern);
            case PSPConstants.SP_Conservative -> mapMinimumDurationConservative(aScope, aPattern);
            default -> throw new IllegalStateException("Unexpected value: " + aPattern.getSubType());
        };
    }

    private List<Element> mapMinimumDurationBasic(Scope aScope, MinimumDuration aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
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

    private List<Element> mapMinimumDurationLiberal(Scope aScope, MinimumDuration aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());

                // regular end
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getOr());

                // liberal end
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(languageDefinitions.getEndPlaceholder());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
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
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getOr());

                // liberal end
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getOr());

                // liberal end
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

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
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());

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
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getOr());

                // liberal end
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());

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
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getOr());

                // liberal end
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());

                // include end
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }
        return elements;
    }

    private List<Element> mapMinimumDurationConservative(Scope aScope, MinimumDuration aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                // Also check duration at start
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                // basic mapping
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                // exclude end
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

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

                // Also check duration at start
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
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
            case PSPConstants.S_AfterQ:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());

                elements.add(new RoundBracketOpenElement());

                // Also check duration at start
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                // exclude end
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());

                elements.add(new RoundBracketOpenElement());

                // Also check duration at start
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
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

                // Also check duration at start
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());

                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }

    private List<Element> mapMaximumDuration(Scope aScope, MaximumDuration aPattern) {
        return switch (aPattern.getSubType()) {
            case PSPConstants.SP_All_Basic -> mapMaximumDurationBasic(aScope, aPattern);
            case PSPConstants.SP_Liberal -> mapMaximumDurationLiberal(aScope, aPattern);
            case PSPConstants.SP_Conservative -> mapMaximumDurationConservative(aScope, aPattern);
            default -> throw new IllegalStateException("Unexpected value: " + aPattern.getSubType());
        };
    }

    private List<Element> mapMaximumDurationBasic(Scope aScope, MaximumDuration aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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

    private List<Element> mapMaximumDurationLiberal(Scope aScope, MaximumDuration aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                // also accept END as termination
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                // also accept END as termination
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

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
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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

                // also accept END as termination
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

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


    private List<Element> mapMaximumDurationConservative(Scope aScope, MaximumDuration aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                // also check duration at start
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                // also accept END as termination
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
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

                // also check duration at start
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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
            case PSPConstants.S_AfterQ:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                // also check duration at start
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                // also accept END as termination
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());

                elements.add(new RoundBracketOpenElement());

                // also check duration at start
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());

                elements.add(new RoundBracketOpenElement());

                // also check duration at start
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
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

                // also accept END as termination
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

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
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }

    private List<Element> mapRecurrenceBasic(Scope aScope, Recurrence aPattern) {
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
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
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
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }

    private List<Element> mapRecurrenceLiberal(Scope aScope, Recurrence aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
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
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
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
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BetweenQandR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
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
        return switch (aPattern.getSubType()) {
            case PSPConstants.SP_All_Basic -> mapPrecedenceBasic(aScope, aPattern);
            case PSPConstants.SP_Conservative -> mapPrecedenceConservative(aScope, aPattern);
            default -> throw new IllegalStateException("Unexpected value: " + aPattern.getSubType());
        };
    }

    private List<Element> mapPrecedenceBasic(Scope aScope, Precedence aPattern) {
        EventConstraint lZS = aPattern.getSConstraint();
        String lcntZS = lZS != null ? cnt(lZS.getEvent()) : "true";
        boolean lHasConstraint = !lcntZS.equals("true");

        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
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

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
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
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
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

    private List<Element> addBasicStartPrecedenceCheck(List<Element> elements, Precedence aPattern, Scope aScope,
                                                       boolean withR) {
        TimeBound timeBound = aPattern.getPTimeBound();

        elements.add(new RoundBracketOpenElement());
        elements.add(new RoundBracketOpenElement());
        elements.add(languageDefinitions.getNot());
        elements.add(aPattern.getP());
        elements.add(new RoundBracketCloseElement());
        elements.add(languageDefinitions.getWeakUntil());
        elements.add(new TimeBoundElement(utb(timeBound)));
        elements.add(new RoundBracketOpenElement());
        elements.add(aPattern.getS());
        if (withR) {
            elements.add(languageDefinitions.getOr());
            elements.add(aScope.getR());
        }
        elements.add(new RoundBracketCloseElement());
        elements.add(new RoundBracketCloseElement());

        return elements;
    }

    private List<Element> mapPrecedenceConservative(Scope aScope, Precedence aPattern) {
        EventConstraint lZS = aPattern.getSConstraint();
        String lcntZS = lZS != null ? cnt(lZS.getEvent()) : "true";
        boolean lHasConstraint = !lcntZS.equals("true");

        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                addBasicStartPrecedenceCheck(elements, aPattern, aScope, false);
                elements.add(languageDefinitions.getAnd());

                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
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

                addBasicStartPrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
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
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQ:
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addBasicStartPrecedenceCheck(elements, aPattern, aScope, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());

                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BetweenQandR:
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addBasicStartPrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNext());
                elements.add(languageDefinitions.getEventually());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addBasicStartPrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());

                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
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
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addBasicStartPrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNext());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addBasicStartPrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());

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
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gap((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
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
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }

    private List<Element> mapPrecedenceChain1N(Scope aScope, PrecedenceChain1N aPattern) {
        return switch (aPattern.getSubType()) {
            case PSPConstants.SP_All_Basic -> mapPrecedenceChain1NBasic(aScope, aPattern);
            case PSPConstants.SP_Conservative -> mapPrecedenceChain1NConservative(aScope, aPattern);
            case PSPConstants.SP_Liberal -> mapPrecedenceChain1NLiberal(aScope, aPattern);
            default -> throw new IllegalStateException("Unexpected value: " + aPattern.getSubType());
        };
    }

    // Tis addressed from 0 to n-1.

    private void PC1N_Ch(List<Element> elements, ChainEvents Tis, int i) {
        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";

            elements.add(languageDefinitions.getAnd());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNext());
            elements.add(new RoundBracketOpenElement());
            if (lcntZi.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());

            } else {
                // with Zi
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
            PC1N_Ch(elements, Tis, i + 1);
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
        }
    }

    private List<Element> mapPrecedenceChain1NBasic(Scope aScope, PrecedenceChain1N aPattern) {
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());
                PC1N_Ch(elements, Tis, 0);
                elements.add(new RoundBracketCloseElement());
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

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());
                PC1N_Ch(elements, Tis, 0);
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());
                PC1N_Ch(elements, Tis, 0);
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());
                PC1N_Ch(elements, Tis, 0);
                elements.add(new RoundBracketCloseElement());
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
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());
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

    private void PC1N_ChConservative(List<Element> elements, ChainEvents Tis, int i, boolean withR, Scope aScope) {
        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";

            if (lcntZi.equals("true")) {
                // no Zi
                if (withR) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNext());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(Ti.getEvent());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                    PC1N_ChConservative(elements, Tis, i + 1, withR, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNext());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getEventually());
                    elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(Ti.getEvent());
                    PC1N_ChConservative(elements, Tis, i + 1, withR, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
            } else {
                // with Zi

                if (withR) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZi));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNext());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZi));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(Ti.getEvent());
                    elements.add(new RoundBracketCloseElement());
                    PC1N_ChConservative(elements, Tis, i + 1, withR, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZi));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNext());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZi));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(Ti.getEvent());
                    elements.add(new RoundBracketCloseElement());
                    PC1N_ChConservative(elements, Tis, i + 1, withR, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
            }
        }
    }

    private List<Element> addBasicStart1NPrecedenceCheck(List<Element> elements, PrecedenceChain1N aPattern,
                                                         Scope aScope,
                                                         boolean withR) {
        TimeBound timeBound = aPattern.getSTimeBound();

        elements.add(new RoundBracketOpenElement());
        elements.add(new RoundBracketOpenElement());
        elements.add(languageDefinitions.getNot());
        elements.add(new RoundBracketOpenElement());
        elements.add(aPattern.getS());
        PC1N_ChConservative(elements, aPattern.getTis(), 0, withR, aScope);
        elements.add(new RoundBracketCloseElement());
        elements.add(new RoundBracketCloseElement());
        elements.add(languageDefinitions.getWeakUntil());
        elements.add(new TimeBoundElement(utb(timeBound)));
        elements.add(new RoundBracketOpenElement());
        elements.add(new RoundBracketOpenElement());
        elements.add(aPattern.getP());
        elements.add(new RoundBracketCloseElement());
        if (withR) {
            elements.add(languageDefinitions.getOr());
            elements.add(aScope.getR());
        }
        elements.add(new RoundBracketCloseElement());
        elements.add(new RoundBracketCloseElement());

        return elements;
    }

    private List<Element> mapPrecedenceChain1NConservative(Scope aScope, PrecedenceChain1N aPattern) {
        List<Element> elements = new ArrayList<>();

        EventConstraint lZS = aPattern.getSConstraint();
        String lcntZS = lZS != null ? cnt(lZS.getEvent()) : "true";
        boolean lHasConstraint = !lcntZS.equals("true");
        ChainEvents Tis = aPattern.getTis();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                addBasicStart1NPrecedenceCheck(elements, aPattern, aScope, false);
                elements.add(languageDefinitions.getAnd());

                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                PC1N_ChConservative(elements, Tis, 0, false, aScope);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    PC1N_ChConservative(elements, Tis, 0, false, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
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
                addBasicStart1NPrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                PC1N_ChConservative(elements, Tis, 0, true, aScope);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    PC1N_ChConservative(elements, Tis, 0, true, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
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
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQ:
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addBasicStart1NPrecedenceCheck(elements, aPattern, aScope, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());


                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                PC1N_ChConservative(elements, Tis, 0, false, aScope);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    PC1N_ChConservative(elements, Tis, 0, false, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BetweenQandR:
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addBasicStart1NPrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNext());
                elements.add(languageDefinitions.getEventually());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addBasicStart1NPrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                PC1N_ChConservative(elements, Tis, 0, true, aScope);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    PC1N_ChConservative(elements, Tis, 0, true, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
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
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addBasicStart1NPrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNext());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addBasicStart1NPrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());

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
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                PC1N_ChConservative(elements, Tis, 0, true, aScope);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    PC1N_ChConservative(elements, Tis, 0, true, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
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
                elements.add(new RoundBracketCloseElement());
                break;
        }
        return elements;
    }

    private List<Element> mapPrecedenceChain1NLiberal(Scope aScope, PrecedenceChain1N aPattern) {
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                PC1N_ChConservative(elements, Tis, 0, false, aScope);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    PC1N_ChConservative(elements, Tis, 0, false, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
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

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                PC1N_ChConservative(elements, Tis, 0, true, aScope);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    PC1N_ChConservative(elements, Tis, 0, true, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
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
            case PSPConstants.S_AfterQ:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                PC1N_ChConservative(elements, Tis, 0, false, aScope);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    PC1N_ChConservative(elements, Tis, 0, false, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                PC1N_ChConservative(elements, Tis, 0, true, aScope);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    PC1N_ChConservative(elements, Tis, 0, true, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
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
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                PC1N_ChConservative(elements, Tis, 0, true, aScope);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    PC1N_ChConservative(elements, Tis, 0, true, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
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


    private List<Element> mapPrecedenceChainN1(Scope aScope, PrecedenceChainN1 aPattern) {
        return switch (aPattern.getSubType()) {
            case PSPConstants.SP_All_Basic -> mapPrecedenceChainN1Basic(aScope, aPattern);
            case PSPConstants.SP_Conservative -> mapPrecedenceChainN1Conservative(aScope, aPattern);
            case PSPConstants.SP_Liberal -> mapPrecedenceChainN1Liberal(aScope, aPattern);
            default -> throw new IllegalStateException("Unexpected value: " + aPattern.getSubType());
        };
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
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketCloseElement());
            }
        }
    }


    private List<Element> mapPrecedenceChainN1Basic(Scope aScope, PrecedenceChainN1 aPattern) {
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

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
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

                    elements.add(languageDefinitions.getAnd());

                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getAlways());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(languageDefinitions.getEndPlaceholder());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());

                    elements.add(new RoundBracketCloseElement());

                    elements.add(languageDefinitions.getImplication());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
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

                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getEventually());
                    elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());

                    elements.add(languageDefinitions.getAnd());

                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getAlways());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(languageDefinitions.getEndPlaceholder());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());

                    elements.add(new RoundBracketCloseElement());

                    elements.add(languageDefinitions.getImplication());
                    elements.add(new RoundBracketOpenElement());
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

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
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

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
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

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
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

    private void PCN1_ChConservative(List<Element> elements, PrecedenceChainN1 aPattern, int i) {
        ChainEvents Tis = aPattern.getTis();

        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";

            elements.add(languageDefinitions.getAnd());
            elements.add(new RoundBracketOpenElement());
            elements.add(new RoundBracketOpenElement());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNot());
            elements.add(aPattern.getP());
            elements.add(new RoundBracketCloseElement());

            if (!lcntZi.equals("true")) {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
            }

            elements.add(new RoundBracketCloseElement());
            elements.add(languageDefinitions.getAnd());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNext());
            elements.add(new RoundBracketOpenElement());
            elements.add(new RoundBracketOpenElement());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNot());
            elements.add(aPattern.getP());
            elements.add(new RoundBracketCloseElement());

            if (!lcntZi.equals("true")) {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
            }

            elements.add(new RoundBracketCloseElement());
            elements.add(languageDefinitions.getUntil());
            elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
            elements.add(new SpaceElement());
            elements.add(new RoundBracketOpenElement());
            elements.add(Ti.getEvent());
            PCN1_ChConservative(elements, aPattern, i + 1);
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
        }
    }

    private List<Element> addBasicStartN1PrecedenceCheck(List<Element> elements, PrecedenceChainN1 aPattern,
                                                         Scope aScope,
                                                         boolean withR) {
        TimeBound timeBound = aPattern.getPTimeBound();

        elements.add(new RoundBracketOpenElement());
        elements.add(new RoundBracketOpenElement());
        elements.add(languageDefinitions.getNot());
        elements.add(aPattern.getP());
        elements.add(new RoundBracketCloseElement());
        elements.add(languageDefinitions.getWeakUntil());
        elements.add(new TimeBoundElement(utb(timeBound)));
        elements.add(new RoundBracketOpenElement());
        elements.add(new RoundBracketOpenElement());
        elements.add(aPattern.getS());
        PCN1_ChConservative(elements, aPattern, 0);
        elements.add(new RoundBracketCloseElement());
        if (withR) {
            elements.add(languageDefinitions.getOr());
            elements.add(aScope.getR());
        }
        elements.add(new RoundBracketCloseElement());
        elements.add(new RoundBracketCloseElement());

        return elements;
    }

    private List<Element> mapPrecedenceChainN1Conservative(Scope aScope, PrecedenceChainN1 aPattern) {
        List<Element> elements = new ArrayList<>();

        EventConstraint lZP = aPattern.getPConstraint();
        String lcntZP = lZP != null ? cnt(lZP.getEvent()) : "true";
        boolean lHasConstraint = !lcntZP.equals("true");
        ChainEvents Tis = aPattern.getTis();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                addBasicStartN1PrecedenceCheck(elements, aPattern, aScope, false);
                elements.add(languageDefinitions.getAnd());

                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                PCN1_ChConservative(elements, aPattern, 0);
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

                addBasicStartN1PrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                PCN1_ChConservative(elements, aPattern, 0);
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
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQ:
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addBasicStartN1PrecedenceCheck(elements, aPattern, aScope, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());


                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                PCN1_ChConservative(elements, aPattern, 0);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BetweenQandR:
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addBasicStartN1PrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNext());
                elements.add(languageDefinitions.getEventually());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addBasicStartN1PrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
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

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                PCN1_ChConservative(elements, aPattern, 0);
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
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addBasicStartN1PrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNext());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addBasicStartN1PrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());

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

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                PCN1_ChConservative(elements, aPattern, 0);
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
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }

    private List<Element> mapPrecedenceChainN1Liberal(Scope aScope, PrecedenceChainN1 aPattern) {
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

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                PCN1_ChConservative(elements, aPattern, 0);
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
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                PCN1_ChConservative(elements, aPattern, 0);
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

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                PCN1_ChConservative(elements, aPattern, 0);
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
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

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                PCN1_ChConservative(elements, aPattern, 0);
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

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                PCN1_ChConservative(elements, aPattern, 0);
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

    private List<Element> mapRelaxedPrecedenceChain1N(Scope aScope, RelaxedPrecedenceChain1N aPattern) {
        return switch (aPattern.getSubType()) {
            case PSPConstants.SP_All_Basic -> mapRelaxedPrecedenceChain1NBasic(aScope, aPattern);
            case PSPConstants.SP_Conservative -> mapRelaxedPrecedenceChain1NConservative(aScope, aPattern);
            case PSPConstants.SP_Liberal -> mapRelaxedPrecedenceChain1NLiberal(aScope, aPattern);
            default -> throw new IllegalStateException("Unexpected value: " + aPattern.getSubType());
        };
    }

    // Tis addressed from 0 to n-1.

    private void RelaxedPC1N_Ch(List<Element> elements, ChainEvents Tis, int i) {
        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";

            elements.add(languageDefinitions.getAnd());
            elements.add(new RoundBracketOpenElement());
            if (lcntZi.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());

            } else {
                // with Zi
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
            RelaxedPC1N_Ch(elements, Tis, i + 1);
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
        }
    }

    private List<Element> mapRelaxedPrecedenceChain1NBasic(Scope aScope, RelaxedPrecedenceChain1N aPattern) {
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());
                RelaxedPC1N_Ch(elements, Tis, 0);
                elements.add(new RoundBracketCloseElement());
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

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());
                RelaxedPC1N_Ch(elements, Tis, 0);
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());
                RelaxedPC1N_Ch(elements, Tis, 0);
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());
                RelaxedPC1N_Ch(elements, Tis, 0);
                elements.add(new RoundBracketCloseElement());
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
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());
                RelaxedPC1N_Ch(elements, Tis, 0);
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

    private void RelaxedPC1N_ChConservative(List<Element> elements, ChainEvents Tis, int i, boolean withR,
                                            Scope aScope) {
        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";

            if (lcntZi.equals("true")) {
                // no Zi
                if (withR) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(Ti.getEvent());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                    RelaxedPC1N_ChConservative(elements, Tis, i + 1, withR, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getEventually());
                    elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(Ti.getEvent());
                    RelaxedPC1N_ChConservative(elements, Tis, i + 1, withR, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
            } else {
                // with Zi

                if (withR) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZi));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(Ti.getEvent());
                    elements.add(new RoundBracketCloseElement());
                    RelaxedPC1N_ChConservative(elements, Tis, i + 1, withR, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZi));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(Ti.getEvent());
                    elements.add(new RoundBracketCloseElement());
                    RelaxedPC1N_ChConservative(elements, Tis, i + 1, withR, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
            }
        }
    }

    private List<Element> addRelaxedBasicStart1NPrecedenceCheck(List<Element> elements,
                                                                RelaxedPrecedenceChain1N aPattern,
                                                                Scope aScope,
                                                                boolean withR) {
        TimeBound timeBound = aPattern.getSTimeBound();

        elements.add(new RoundBracketOpenElement());
        elements.add(new RoundBracketOpenElement());
        elements.add(languageDefinitions.getNot());
        elements.add(new RoundBracketOpenElement());
        elements.add(aPattern.getS());
        RelaxedPC1N_ChConservative(elements, aPattern.getTis(), 0, withR, aScope);
        elements.add(new RoundBracketCloseElement());
        elements.add(new RoundBracketCloseElement());
        elements.add(languageDefinitions.getWeakUntil());
        elements.add(new TimeBoundElement(utb(timeBound)));
        elements.add(new RoundBracketOpenElement());
        elements.add(new RoundBracketOpenElement());
        elements.add(aPattern.getP());
        elements.add(new RoundBracketCloseElement());
        if (withR) {
            elements.add(languageDefinitions.getOr());
            elements.add(aScope.getR());
        }
        elements.add(new RoundBracketCloseElement());
        elements.add(new RoundBracketCloseElement());

        return elements;
    }

    private List<Element> mapRelaxedPrecedenceChain1NConservative(Scope aScope, RelaxedPrecedenceChain1N aPattern) {
        List<Element> elements = new ArrayList<>();

        EventConstraint lZS = aPattern.getSConstraint();
        String lcntZS = lZS != null ? cnt(lZS.getEvent()) : "true";
        boolean lHasConstraint = !lcntZS.equals("true");
        ChainEvents Tis = aPattern.getTis();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                addRelaxedBasicStart1NPrecedenceCheck(elements, aPattern, aScope, false);
                elements.add(languageDefinitions.getAnd());

                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                RelaxedPC1N_ChConservative(elements, Tis, 0, false, aScope);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    RelaxedPC1N_ChConservative(elements, Tis, 0, false, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
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
                addRelaxedBasicStart1NPrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                RelaxedPC1N_ChConservative(elements, Tis, 0, true, aScope);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    RelaxedPC1N_ChConservative(elements, Tis, 0, true, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
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
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQ:
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addRelaxedBasicStart1NPrecedenceCheck(elements, aPattern, aScope, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());


                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                RelaxedPC1N_ChConservative(elements, Tis, 0, false, aScope);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    RelaxedPC1N_ChConservative(elements, Tis, 0, false, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BetweenQandR:
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addRelaxedBasicStart1NPrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNext());
                elements.add(languageDefinitions.getEventually());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addRelaxedBasicStart1NPrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                RelaxedPC1N_ChConservative(elements, Tis, 0, true, aScope);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    RelaxedPC1N_ChConservative(elements, Tis, 0, true, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
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
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addRelaxedBasicStart1NPrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNext());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addRelaxedBasicStart1NPrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());

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
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                RelaxedPC1N_ChConservative(elements, Tis, 0, true, aScope);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    RelaxedPC1N_ChConservative(elements, Tis, 0, true, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
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
                elements.add(new RoundBracketCloseElement());
                break;
        }
        return elements;
    }

    private List<Element> mapRelaxedPrecedenceChain1NLiberal(Scope aScope, RelaxedPrecedenceChain1N aPattern) {
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                RelaxedPC1N_ChConservative(elements, Tis, 0, false, aScope);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    RelaxedPC1N_ChConservative(elements, Tis, 0, false, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
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

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                RelaxedPC1N_ChConservative(elements, Tis, 0, true, aScope);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    RelaxedPC1N_ChConservative(elements, Tis, 0, true, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
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
            case PSPConstants.S_AfterQ:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                RelaxedPC1N_ChConservative(elements, Tis, 0, false, aScope);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    RelaxedPC1N_ChConservative(elements, Tis, 0, false, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                RelaxedPC1N_ChConservative(elements, Tis, 0, true, aScope);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    RelaxedPC1N_ChConservative(elements, Tis, 0, true, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
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
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());
                RelaxedPC1N_ChConservative(elements, Tis, 0, true, aScope);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(maxgap((Interval) aPattern.getSTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    RelaxedPC1N_ChConservative(elements, Tis, 0, true, aScope);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
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


    private List<Element> mapRelaxedPrecedenceChainN1(Scope aScope, RelaxedPrecedenceChainN1 aPattern) {
        return switch (aPattern.getSubType()) {
            case PSPConstants.SP_All_Basic -> mapRelaxedPrecedenceChainN1Basic(aScope, aPattern);
            case PSPConstants.SP_Conservative -> mapRelaxedPrecedenceChainN1Conservative(aScope, aPattern);
            case PSPConstants.SP_Liberal -> mapRelaxedPrecedenceChainN1Liberal(aScope, aPattern);
            default -> throw new IllegalStateException("Unexpected value: " + aPattern.getSubType());
        };
    }

    // Tis addressed from 0 to n-1.
    private void RelaxedPCN1_Ch(List<Element> elements, RelaxedPrecedenceChainN1 aPattern, int i) {
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
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapPN(i + 1, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RelaxedPCN1_Ch(elements, aPattern, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(gapPN(i + 1, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RelaxedPCN1_Ch(elements, aPattern, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        }
    }


    private List<Element> mapRelaxedPrecedenceChainN1Basic(Scope aScope, RelaxedPrecedenceChainN1 aPattern) {
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

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
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
                RelaxedPCN1_Ch(elements, aPattern, 0);
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

                    elements.add(languageDefinitions.getAnd());

                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getAlways());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(languageDefinitions.getEndPlaceholder());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());

                    elements.add(new RoundBracketCloseElement());

                    elements.add(languageDefinitions.getImplication());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    RelaxedPCN1_Ch(elements, aPattern, 0);
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());
                } else {
                    // no ZS

                    elements.add(new RoundBracketOpenElement());

                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getEventually());
                    elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());

                    elements.add(languageDefinitions.getAnd());

                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getAlways());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(languageDefinitions.getEndPlaceholder());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(new RoundBracketCloseElement());

                    elements.add(new RoundBracketCloseElement());

                    elements.add(languageDefinitions.getImplication());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getEventually());
                    elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(aPattern.getS());
                    RelaxedPCN1_Ch(elements, aPattern, 0);
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

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
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
                RelaxedPCN1_Ch(elements, aPattern, 0);
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
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

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
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
                RelaxedPCN1_Ch(elements, aPattern, 0);
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

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    // has ZS
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
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
                RelaxedPCN1_Ch(elements, aPattern, 0);
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

    private void RelaxedPCN1_ChConservative(List<Element> elements, RelaxedPrecedenceChainN1 aPattern, int i) {
        ChainEvents Tis = aPattern.getTis();

        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";

            elements.add(languageDefinitions.getAnd());
            elements.add(new RoundBracketOpenElement());
            elements.add(new RoundBracketOpenElement());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNot());
            elements.add(aPattern.getP());
            elements.add(new RoundBracketCloseElement());

            if (!lcntZi.equals("true")) {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
            }

            elements.add(new RoundBracketCloseElement());
            elements.add(languageDefinitions.getUntil());
            elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
            elements.add(new SpaceElement());
            elements.add(new RoundBracketOpenElement());
            elements.add(Ti.getEvent());
            RelaxedPCN1_ChConservative(elements, aPattern, i + 1);
            elements.add(new RoundBracketCloseElement());
            elements.add(new RoundBracketCloseElement());
        }
    }

    private List<Element> addBasicStartRelaxedN1PrecedenceCheck(List<Element> elements,
                                                                RelaxedPrecedenceChainN1 aPattern,
                                                                Scope aScope,
                                                                boolean withR) {
        TimeBound timeBound = aPattern.getPTimeBound();

        elements.add(new RoundBracketOpenElement());
        elements.add(new RoundBracketOpenElement());
        elements.add(languageDefinitions.getNot());
        elements.add(aPattern.getP());
        elements.add(new RoundBracketCloseElement());
        elements.add(languageDefinitions.getWeakUntil());
        elements.add(new TimeBoundElement(utb(timeBound)));
        elements.add(new RoundBracketOpenElement());
        elements.add(new RoundBracketOpenElement());
        elements.add(aPattern.getS());
        RelaxedPCN1_ChConservative(elements, aPattern, 0);
        elements.add(new RoundBracketCloseElement());
        if (withR) {
            elements.add(languageDefinitions.getOr());
            elements.add(aScope.getR());
        }
        elements.add(new RoundBracketCloseElement());
        elements.add(new RoundBracketCloseElement());

        return elements;
    }

    private List<Element> mapRelaxedPrecedenceChainN1Conservative(Scope aScope, RelaxedPrecedenceChainN1 aPattern) {
        List<Element> elements = new ArrayList<>();

        EventConstraint lZP = aPattern.getPConstraint();
        String lcntZP = lZP != null ? cnt(lZP.getEvent()) : "true";
        boolean lHasConstraint = !lcntZP.equals("true");
        ChainEvents Tis = aPattern.getTis();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                addBasicStartRelaxedN1PrecedenceCheck(elements, aPattern, aScope, false);
                elements.add(languageDefinitions.getAnd());

                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                RelaxedPCN1_ChConservative(elements, aPattern, 0);
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

                addBasicStartRelaxedN1PrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                RelaxedPCN1_ChConservative(elements, aPattern, 0);
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
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQ:
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addBasicStartRelaxedN1PrecedenceCheck(elements, aPattern, aScope, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());


                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                RelaxedPCN1_ChConservative(elements, aPattern, 0);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BetweenQandR:
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addBasicStartRelaxedN1PrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNext());
                elements.add(languageDefinitions.getEventually());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addBasicStartRelaxedN1PrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
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

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                RelaxedPCN1_ChConservative(elements, aPattern, 0);
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
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_AfterQuntilR:
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addBasicStartRelaxedN1PrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNext());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                addBasicStartRelaxedN1PrecedenceCheck(elements, aPattern, aScope, true);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());

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

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                RelaxedPCN1_ChConservative(elements, aPattern, 0);
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
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getWeakUntil());
                elements.add(new SpaceElement());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }

    private List<Element> mapRelaxedPrecedenceChainN1Liberal(Scope aScope, RelaxedPrecedenceChainN1 aPattern) {
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

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                RelaxedPCN1_ChConservative(elements, aPattern, 0);
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
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                RelaxedPCN1_ChConservative(elements, aPattern, 0);
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

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(trigger((Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                RelaxedPCN1_ChConservative(elements, aPattern, 0);
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
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

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                RelaxedPCN1_ChConservative(elements, aPattern, 0);
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

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(gapPN(0, Tis, (Interval) aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                if (lHasConstraint) {
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZP));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                    elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                    elements.add(new SpaceElement());
                    elements.add(aPattern.getP());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(new RoundBracketCloseElement());

                RelaxedPCN1_ChConservative(elements, aPattern, 0);
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
        return switch (aPattern.getSubType()) {
            case PSPConstants.SP_All_Basic -> mapUntilBasic(aScope, aPattern);
            case PSPConstants.SP_Liberal -> mapUntilLiberal(aScope, aPattern);
            case PSPConstants.SP_Conservative -> mapUntilConservative(aScope, aPattern);
            default -> throw new IllegalStateException("Unexpected value: " + aPattern.getSubType());
        };
    }

    private List<Element> mapUntilBasic(Scope aScope, Until aPattern) {
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
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
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
        }

        return elements;
    }

    private List<Element> mapUntilLiberal(Scope aScope, Until aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(umintime(aPattern.getPTimeBound())));
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
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

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(umintime(aPattern.getPTimeBound())));
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(time(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
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
                elements.add(new RoundBracketOpenElement());
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
                elements.add(new TimeBoundElement(umintime(aPattern.getPTimeBound())));
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getOr());

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
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getR());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
                break;
        }

        return elements;
    }

    private List<Element> mapUntilConservative(Scope aScope, Until aPattern) {
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
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
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
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
        }

        return elements;
    }

    private List<Element> mapResponse(Scope aScope, Response aPattern) {
        return switch (aPattern.getSubType()) {
            case PSPConstants.SP_All_Basic -> mapResponseBasic(aScope, aPattern);
            case PSPConstants.SP_Liberal -> mapResponseLiberal(aScope, aPattern);
            case PSPConstants.SP_Conservative -> mapResponseConservative(aScope, aPattern);
            default -> throw new IllegalStateException("Unexpected value: " + aPattern.getSubType());
        };
    }

    private List<Element> mapResponseLiberal(Scope aScope, Response aPattern) {
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                } else {
                    elements.add(languageDefinitions.getEventually());
                }
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());

                // Also accept the End Placeholder as a valid response
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

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
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                } else {
                    elements.add(languageDefinitions.getEventually());
                }
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());

                // Also accept the End Placeholder as a valid response
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
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
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    // elements.add(languageDefinitions.getAnd());
                    // TODO: sb.deleteCharAt(sb.length() - 1);
                    elements.add(new RoundBracketCloseElement());
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                // Also accept the End Placeholder as a valid response
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

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

    private List<Element> mapResponseConservative(Scope aScope, Response aPattern) {
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
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
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    // elements.add(languageDefinitions.getAnd());
                    // TODO: sb.deleteCharAt(sb.length() - 1);
                    elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
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

    private List<Element> mapResponseBasic(Scope aScope, Response aPattern) {
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketCloseElement());
                break;
            case PSPConstants.S_BetweenQandR:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
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
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                if (lHasConstraint) {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    // elements.add(languageDefinitions.getAnd());
                    // TODO: sb.deleteCharAt(sb.length() - 1);
                    elements.add(new RoundBracketCloseElement());
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RC1N_Ch(elements, Tis, i + 1);
                elements.add(new RoundBracketCloseElement());
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

    private void RC1N_Ch_Liberal(List<Element> elements, ChainEvents Tis, int i) {
        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";
            boolean lHasConstraint = !lcntZi.equals("true");

            if (lHasConstraint) {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                // Also accept End of Time Bound as a valid case for Ti
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                RC1N_Ch_Liberal(elements, Tis, i + 1);
                elements.add(new RoundBracketCloseElement());
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

                // Also accept End of Time Bound as a valid case for Ti
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                RC1N_Ch_Liberal(elements, Tis, i + 1);
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
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RC1N_ChR(elements, Tis, R, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketCloseElement());
            }
        }
    }


    private void RC1N_ChR_Alt_Liberal(List<Element> elements, ChainEvents Tis, EventImpl R, int i) {
        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";
            boolean lHasConstraint = !lcntZi.equals("true");

            if (lHasConstraint) {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                // Also accept End of Time Bound as a valid case for Ti
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                RC1N_ChR_Alt_Liberal(elements, Tis, R, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                // Also accept End of Time Bound as a valid case for Ti
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                RC1N_ChR_Alt_Liberal(elements, Tis, R, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        } else {
            elements.add(languageDefinitions.getAnd());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNot());
            elements.add(R);
            elements.add(new RoundBracketCloseElement());
        }
    }

    private void RC1N_ChR_Alt(List<Element> elements, ChainEvents Tis, EventImpl R, int i) {
        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";
            boolean lHasConstraint = !lcntZi.equals("true");

            if (lHasConstraint) {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RC1N_ChR_Alt(elements, Tis, R, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RC1N_ChR_Alt(elements, Tis, R, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        } else {
            elements.add(languageDefinitions.getAnd());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNot());
            elements.add(R);
            elements.add(new RoundBracketCloseElement());
        }
    }

    private List<Element> mapResponseChain1N(Scope aScope, ResponseChain1N aPattern) {
        return switch (aPattern.getSubType()) {
            case PSPConstants.SP_All_Basic -> mapResponseChain1NBasic(aScope, aPattern);
            case PSPConstants.SP_Liberal -> mapResponseChain1NLiberal(aScope, aPattern);
            case PSPConstants.SP_Conservative -> mapResponseChain1NConservative(aScope, aPattern);
            default -> throw new IllegalStateException("Unexpected value: " + aPattern.getSubType());
        };
    }

    private List<Element> mapResponseChain1NBasic(Scope aScope, ResponseChain1N aPattern) {
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                } else {
                    elements.add(languageDefinitions.getEventually());
                }
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                } else {
                    elements.add(languageDefinitions.getEventually());
                }
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
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

    private List<Element> mapResponseChain1NLiberal(Scope aScope, ResponseChain1N aPattern) {
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                } else {
                    elements.add(languageDefinitions.getEventually());
                }
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                // Also accept End of Time Bound as a valid case for S
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                RC1N_Ch_Liberal(elements, aPattern.getTis(), 0);
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RC1N_ChR_Alt_Liberal(elements, aPattern.getTis(), aScope.getR(), 0);
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                } else {
                    elements.add(languageDefinitions.getEventually());
                }
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                // Also accept End of Time Bound as a valid case for S
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                RC1N_Ch_Liberal(elements, aPattern.getTis(), 0);
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RC1N_ChR_Alt_Liberal(elements, aPattern.getTis(), aScope.getR(), 0);
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                // Also accept End of Time Bound as a valid case for S
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                RC1N_ChR_Alt_Liberal(elements, aPattern.getTis(), aScope.getR(), 0);
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

    private List<Element> mapResponseChain1NConservative(Scope aScope, ResponseChain1N aPattern) {
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                } else {
                    elements.add(languageDefinitions.getEventually());
                }
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RC1N_ChR_Alt(elements, aPattern.getTis(), aScope.getR(), 0);
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                } else {
                    elements.add(languageDefinitions.getEventually());
                }
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RC1N_ChR_Alt(elements, aPattern.getTis(), aScope.getR(), 0);
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RC1N_ChR_Alt(elements, aPattern.getTis(), aScope.getR(), 0);
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

    private void RCN1_Ch(List<Element> elements, ResponseChainN1 aPattern, int i, boolean withP) {
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
                RCN1_Ch(elements, aPattern, i + 1, withP);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RCN1_Ch(elements, aPattern, i + 1, withP);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        } else if (withP) {
            EventConstraint lZP = aPattern.getPConstraint();
            String lcntZP = lZP != null ? cnt(lZP.getEvent()) : "true";

            if (lcntZP.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZP));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        }
    }

    private void RCN1_Ch_Liberal(List<Element> elements, ResponseChainN1 aPattern, int i, boolean withP) {
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
                RCN1_Ch_Liberal(elements, aPattern, i + 1, withP);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RCN1_Ch_Liberal(elements, aPattern, i + 1, withP);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        } else if (withP) {
            EventConstraint lZP = aPattern.getPConstraint();
            String lcntZP = lZP != null ? cnt(lZP.getEvent()) : "true";

            if (lcntZP.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                // Also accept End of Time Bound as a valid case for P
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZP));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                // Also accept End of Time Bound as a valid case for P
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
            }
        }
    }

    private void RCN1_ChR(List<Element> elements, ResponseChainN1 aPattern, EventImpl R, int i, boolean withP) {
        ChainEvents Tis = aPattern.getTis();

        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";

            if (lcntZi.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RCN1_ChR(elements, aPattern, R, i + 1, withP);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RCN1_ChR(elements, aPattern, R, i + 1, withP);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        } else if (withP) {
            EventConstraint lZP = aPattern.getPConstraint();
            String lcntZP = lZP != null ? cnt(lZP.getEvent()) : "true";

            if (lcntZP.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZP));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        }
    }

    private void RCN1_ChR_Alt_Liberal(List<Element> elements, ResponseChainN1 aPattern, EventImpl R, int i,
                                      boolean withP) {
        ChainEvents Tis = aPattern.getTis();

        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";

            if (lcntZi.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());

                RCN1_ChR_Alt_Liberal(elements, aPattern, R, i + 1, withP);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                RCN1_ChR_Alt_Liberal(elements, aPattern, R, i + 1, withP);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        } else if (withP) {
            EventConstraint lZP = aPattern.getPConstraint();
            String lcntZP = lZP != null ? cnt(lZP.getEvent()) : "true";

            if (lcntZP.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZP));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
            }
        }
    }


    private void RCN1_ChR_Alt(List<Element> elements, ResponseChainN1 aPattern, EventImpl R, int i, boolean withP) {
        ChainEvents Tis = aPattern.getTis();

        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";

            if (lcntZi.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());

                RCN1_ChR_Alt(elements, aPattern, R, i + 1, withP);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getAnd());

                elements.add(languageDefinitions.getNext());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                RCN1_ChR_Alt(elements, aPattern, R, i + 1, withP);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        } else if (withP) {
            EventConstraint lZP = aPattern.getPConstraint();
            String lcntZP = lZP != null ? cnt(lZP.getEvent()) : "true";

            if (lcntZP.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZP));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
            }
        }
    }

    private List<Element> mapResponseChainN1(Scope aScope, ResponseChainN1 aPattern) {
        return switch (aPattern.getSubType()) {
            case PSPConstants.SP_All_Basic -> mapResponseChainN1Basic(aScope, aPattern);
            case PSPConstants.SP_Liberal -> mapResponseChainN1Liberal(aScope, aPattern);
            case PSPConstants.SP_Conservative -> mapResponseChainN1Conservative(aScope, aPattern);
            default -> throw new IllegalStateException("Unexpected value: " + aPattern.getSubType());
        };
    }

    private List<Element> mapResponseChainN1Basic(Scope aScope, ResponseChainN1 aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_Ch(elements, aPattern, 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_Ch(elements, aPattern, 0, true);
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
                elements.add(aPattern.getS());
                RCN1_ChR(elements, aPattern, aScope.getR(), 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_ChR(elements, aPattern, aScope.getR(), 0, true);
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
                elements.add(aPattern.getS());
                RCN1_Ch(elements, aPattern, 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_Ch(elements, aPattern, 0, true);
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_ChR(elements, aPattern, aScope.getR(), 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_ChR(elements, aPattern, aScope.getR(), 0, true);
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
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_ChR(elements, aPattern, aScope.getR(), 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_ChR(elements, aPattern, aScope.getR(), 0, true);
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

    private List<Element> mapResponseChainN1Liberal(Scope aScope, ResponseChainN1 aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_Ch_Liberal(elements, aPattern, 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_Ch_Liberal(elements, aPattern, 0, true);
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
                elements.add(aPattern.getS());
                RCN1_ChR_Alt_Liberal(elements, aPattern, aScope.getR(), 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_ChR_Alt_Liberal(elements, aPattern, aScope.getR(), 0, true);
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
                elements.add(aPattern.getS());
                RCN1_Ch_Liberal(elements, aPattern, 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_Ch_Liberal(elements, aPattern, 0, true);
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_ChR_Alt_Liberal(elements, aPattern, aScope.getR(), 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_ChR_Alt_Liberal(elements, aPattern, aScope.getR(), 0, true);
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
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_ChR_Alt_Liberal(elements, aPattern, aScope.getR(), 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_ChR_Alt_Liberal(elements, aPattern, aScope.getR(), 0, true);
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

    private List<Element> mapResponseChainN1Conservative(Scope aScope, ResponseChainN1 aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_Ch(elements, aPattern, 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_Ch(elements, aPattern, 0, true);
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
                elements.add(aPattern.getS());
                RCN1_ChR_Alt(elements, aPattern, aScope.getR(), 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_ChR_Alt(elements, aPattern, aScope.getR(), 0, true);
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
                elements.add(aPattern.getS());
                RCN1_Ch(elements, aPattern, 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_Ch(elements, aPattern, 0, true);
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_ChR_Alt(elements, aPattern, aScope.getR(), 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_ChR_Alt(elements, aPattern, aScope.getR(), 0, true);
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
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_ChR_Alt(elements, aPattern, aScope.getR(), 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RCN1_ChR_Alt(elements, aPattern, aScope.getR(), 0, true);
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
    private void RelaxedRC1N_Ch(List<Element> elements, ChainEvents Tis, int i) {
        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";
            boolean lHasConstraint = !lcntZi.equals("true");

            if (lHasConstraint) {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RelaxedRC1N_Ch(elements, Tis, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RelaxedRC1N_Ch(elements, Tis, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        }
    }


    private void RelaxedRC1N_Ch_Liberal(List<Element> elements, ChainEvents Tis, int i) {
        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";
            boolean lHasConstraint = !lcntZi.equals("true");

            if (lHasConstraint) {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                // Also accept End of Time Bound as a valid case for Ti
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                RelaxedRC1N_Ch_Liberal(elements, Tis, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                // Also accept End of Time Bound as a valid case for Ti
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                RelaxedRC1N_Ch_Liberal(elements, Tis, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        }
    }

    private void RelaxedRC1N_ChR(List<Element> elements, ChainEvents Tis, EventImpl R, int i) {
        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";
            boolean lHasConstraint = !lcntZi.equals("true");

            if (lHasConstraint) {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RelaxedRC1N_ChR(elements, Tis, R, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RelaxedRC1N_ChR(elements, Tis, R, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        }
    }


    private void RelaxedRC1N_ChR_Alt_Liberal(List<Element> elements, ChainEvents Tis, EventImpl R, int i) {
        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";
            boolean lHasConstraint = !lcntZi.equals("true");

            if (lHasConstraint) {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                // Also accept End of Time Bound as a valid case for Ti
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                RelaxedRC1N_ChR_Alt_Liberal(elements, Tis, R, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                // Also accept End of Time Bound as a valid case for Ti
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                RelaxedRC1N_ChR_Alt_Liberal(elements, Tis, R, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        } else {
            elements.add(languageDefinitions.getAnd());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNot());
            elements.add(R);
            elements.add(new RoundBracketCloseElement());
        }
    }

    private void RelaxedRC1N_ChR_Alt(List<Element> elements, ChainEvents Tis, EventImpl R, int i) {
        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";
            boolean lHasConstraint = !lcntZi.equals("true");

            if (lHasConstraint) {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RelaxedRC1N_ChR_Alt(elements, Tis, R, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RelaxedRC1N_ChR_Alt(elements, Tis, R, i + 1);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        } else {
            elements.add(languageDefinitions.getAnd());
            elements.add(new RoundBracketOpenElement());
            elements.add(languageDefinitions.getNot());
            elements.add(R);
            elements.add(new RoundBracketCloseElement());
        }
    }

    private List<Element> mapRelaxedResponseChain1N(Scope aScope, RelaxedResponseChain1N aPattern) {
        return switch (aPattern.getSubType()) {
            case PSPConstants.SP_All_Basic -> mapRelaxedResponseChain1NBasic(aScope, aPattern);
            case PSPConstants.SP_Liberal -> mapRelaxedResponseChain1NLiberal(aScope,
                    aPattern);
            case PSPConstants.SP_Conservative -> mapRelaxedResponseChain1NConservative(aScope, aPattern);
            default -> throw new IllegalStateException("Unexpected value: " + aPattern.getSubType());
        };
    }

    private List<Element> mapRelaxedResponseChain1NBasic(Scope aScope, RelaxedResponseChain1N aPattern) {
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                } else {
                    elements.add(languageDefinitions.getEventually());
                }
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRC1N_Ch(elements, aPattern.getTis(), 0);
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRC1N_ChR(elements, aPattern.getTis(), aScope.getR(), 0);
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                } else {
                    elements.add(languageDefinitions.getEventually());
                }
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRC1N_Ch(elements, aPattern.getTis(), 0);
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRC1N_ChR(elements, aPattern.getTis(), aScope.getR(), 0);
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRC1N_ChR(elements, aPattern.getTis(), aScope.getR(), 0);
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


    private List<Element> mapRelaxedResponseChain1NLiberal(Scope aScope, RelaxedResponseChain1N aPattern) {
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                } else {
                    elements.add(languageDefinitions.getEventually());
                }
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                // also accept End of Time Bound as a valid case for S
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                RelaxedRC1N_Ch_Liberal(elements, aPattern.getTis(), 0);
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                // also accept End of Time Bound as a valid case for S
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                RelaxedRC1N_ChR_Alt_Liberal(elements, aPattern.getTis(), aScope.getR(), 0);
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                } else {
                    elements.add(languageDefinitions.getEventually());
                }
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                // also accept End of Time Bound as a valid case for S
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                RelaxedRC1N_Ch_Liberal(elements, aPattern.getTis(), 0);
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                // also accept End of Time Bound as a valid case for S
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                RelaxedRC1N_ChR_Alt_Liberal(elements, aPattern.getTis(), aScope.getR(), 0);
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                // also accept End of Time Bound as a valid case for S
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                RelaxedRC1N_ChR_Alt_Liberal(elements, aPattern.getTis(), aScope.getR(), 0);
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


    private List<Element> mapRelaxedResponseChain1NConservative(Scope aScope, RelaxedResponseChain1N aPattern) {
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                } else {
                    elements.add(languageDefinitions.getEventually());
                }
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRC1N_Ch(elements, aPattern.getTis(), 0);
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRC1N_ChR_Alt(elements, aPattern.getTis(), aScope.getR(), 0);
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getUntil());
                } else {
                    elements.add(languageDefinitions.getEventually());
                }
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRC1N_Ch(elements, aPattern.getTis(), 0);
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRC1N_ChR_Alt(elements, aPattern.getTis(), aScope.getR(), 0);
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
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                    elements.add(languageDefinitions.getAnd());
                    elements.add(new MiscElement(lcntZS));
                    elements.add(new RoundBracketCloseElement());
                } else {
                    elements.add(new RoundBracketOpenElement());
                    elements.add(languageDefinitions.getNot());
                    elements.add(aScope.getR());
                    elements.add(new RoundBracketCloseElement());
                }
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRC1N_ChR_Alt(elements, aPattern.getTis(), aScope.getR(), 0);
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

    private void RelaxedRCN1_Ch(List<Element> elements, RelaxedResponseChainN1 aPattern, int i, boolean withP) {
        ChainEvents Tis = aPattern.getTis();

        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";

            if (lcntZi.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RelaxedRCN1_Ch(elements, aPattern, i + 1, withP);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RelaxedRCN1_Ch(elements, aPattern, i + 1, withP);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        } else if (withP) {
            EventConstraint lZP = aPattern.getPConstraint();
            String lcntZP = lZP != null ? cnt(lZP.getEvent()) : "true";

            if (lcntZP.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZP));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        }
    }

    private void RelaxedRCN1_Ch_Liberal(List<Element> elements, RelaxedResponseChainN1 aPattern, int i, boolean withP) {
        ChainEvents Tis = aPattern.getTis();

        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";

            if (lcntZi.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RelaxedRCN1_Ch_Liberal(elements, aPattern, i + 1, withP);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RelaxedRCN1_Ch_Liberal(elements, aPattern, i + 1, withP);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        } else if (withP) {
            EventConstraint lZP = aPattern.getPConstraint();
            String lcntZP = lZP != null ? cnt(lZP.getEvent()) : "true";

            if (lcntZP.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                // Also accept End of Time Bound as a valid case for P
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZP));
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                // Also accept End of Time Bound as a valid case for P
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
            }
        }
    }

    private void RelaxedRCN1_ChR(List<Element> elements, RelaxedResponseChainN1 aPattern, EventImpl R, int i,
                                 boolean withP) {
        ChainEvents Tis = aPattern.getTis();

        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";

            if (lcntZi.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RelaxedRCN1_ChR(elements, aPattern, R, i + 1, withP);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                RelaxedRCN1_ChR(elements, aPattern, R, i + 1, withP);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        } else if (withP) {
            EventConstraint lZP = aPattern.getPConstraint();
            String lcntZP = lZP != null ? cnt(lZP.getEvent()) : "true";

            if (lcntZP.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZP));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        }
    }

    private void RelaxedRCN1_ChR_Alt_Liberal(List<Element> elements, RelaxedResponseChainN1 aPattern, EventImpl R,
                                             int i,
                                             boolean withP) {
        ChainEvents Tis = aPattern.getTis();

        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";

            if (lcntZi.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());

                RelaxedRCN1_ChR_Alt_Liberal(elements, aPattern, R, i + 1, withP);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                RelaxedRCN1_ChR_Alt_Liberal(elements, aPattern, R, i + 1, withP);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        } else if (withP) {
            EventConstraint lZP = aPattern.getPConstraint();
            String lcntZP = lZP != null ? cnt(lZP.getEvent()) : "true";

            if (lcntZP.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZP));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getOr());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
            }
        }
    }

    private void RelaxedRCN1_ChR_Alt(List<Element> elements, RelaxedResponseChainN1 aPattern, EventImpl R, int i,
                                     boolean withP) {
        ChainEvents Tis = aPattern.getTis();

        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";

            if (lcntZi.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());

                RelaxedRCN1_ChR_Alt(elements, aPattern, R, i + 1, withP);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZi));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(Ti.getTimeBound())));
                elements.add(new SpaceElement());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(Ti.getEvent());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                RelaxedRCN1_ChR_Alt(elements, aPattern, R, i + 1, withP);
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
            }
        } else if (withP) {
            EventConstraint lZP = aPattern.getPConstraint();
            String lcntZP = lZP != null ? cnt(lZP.getEvent()) : "true";

            if (lcntZP.equals("true")) {
                // no Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
            } else {
                // with Zi
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(new MiscElement(lcntZP));
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getUntil());
                elements.add(new TimeBoundElement(utb(aPattern.getPTimeBound())));
                elements.add(new SpaceElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getAnd());
                elements.add(languageDefinitions.getNot());
                elements.add(R);
                elements.add(new RoundBracketCloseElement());

                elements.add(new RoundBracketCloseElement());
            }
        }
    }

    private List<Element> mapRelaxedResponseChainN1(Scope aScope, RelaxedResponseChainN1 aPattern) {
        return switch (aPattern.getSubType()) {
            case PSPConstants.SP_All_Basic -> mapRelaxedResponseChainN1Basic(aScope, aPattern);
            case PSPConstants.SP_Liberal -> mapRelaxedResponseChainN1Liberal(aScope, aPattern);
            case PSPConstants.SP_Conservative -> mapRelaxedResponseChainN1Conservative(aScope, aPattern);
            default -> throw new IllegalStateException("Unexpected value: " + aPattern.getSubType());
        };
    }

    private List<Element> mapRelaxedResponseChainN1Basic(Scope aScope, RelaxedResponseChainN1 aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_Ch(elements, aPattern, 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_Ch(elements, aPattern, 0, true);
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
                elements.add(aPattern.getS());
                RelaxedRCN1_ChR(elements, aPattern, aScope.getR(), 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_ChR(elements, aPattern, aScope.getR(), 0, true);
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
                elements.add(aPattern.getS());
                RelaxedRCN1_Ch(elements, aPattern, 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_Ch(elements, aPattern, 0, true);
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_ChR(elements, aPattern, aScope.getR(), 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_ChR(elements, aPattern, aScope.getR(), 0, true);
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
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_ChR(elements, aPattern, aScope.getR(), 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_ChR(elements, aPattern, aScope.getR(), 0, true);
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

    private List<Element> mapRelaxedResponseChainN1Liberal(Scope aScope, RelaxedResponseChainN1 aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_Ch_Liberal(elements, aPattern, 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_Ch_Liberal(elements, aPattern, 0, true);
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
                elements.add(aPattern.getS());
                RelaxedRCN1_ChR_Alt_Liberal(elements, aPattern, aScope.getR(), 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_ChR_Alt_Liberal(elements, aPattern, aScope.getR(), 0, true);
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
                elements.add(aPattern.getS());
                RelaxedRCN1_Ch_Liberal(elements, aPattern, 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_Ch_Liberal(elements, aPattern, 0, true);
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_ChR_Alt_Liberal(elements, aPattern, aScope.getR(), 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_ChR_Alt_Liberal(elements, aPattern, aScope.getR(), 0, true);
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
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_ChR_Alt_Liberal(elements, aPattern, aScope.getR(), 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_ChR_Alt_Liberal(elements, aPattern, aScope.getR(), 0, true);
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

    private List<Element> mapRelaxedResponseChainN1Conservative(Scope aScope, RelaxedResponseChainN1 aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_Ch(elements, aPattern, 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_Ch(elements, aPattern, 0, true);
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
                elements.add(aPattern.getS());
                RelaxedRCN1_ChR_Alt(elements, aPattern, aScope.getR(), 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_ChR_Alt(elements, aPattern, aScope.getR(), 0, true);
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
                elements.add(aPattern.getS());
                RelaxedRCN1_Ch(elements, aPattern, 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_Ch(elements, aPattern, 0, true);
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
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_ChR_Alt(elements, aPattern, aScope.getR(), 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_ChR_Alt(elements, aPattern, aScope.getR(), 0, true);
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
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_ChR_Alt(elements, aPattern, aScope.getR(), 0, false);
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                RelaxedRCN1_ChR_Alt(elements, aPattern, aScope.getR(), 0, true);
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

    private List<Element> mapResponseInvariance(Scope aScope, ResponseInvariance aPattern) {
        return switch (aPattern.getSubType()) {
            case PSPConstants.SP_All_Basic -> mapResponseInvarianceBasic(aScope, aPattern);
            case PSPConstants.SP_Liberal -> mapResponseInvarianceLiberal(aScope, aPattern);
            case PSPConstants.SP_Conservative -> mapResponseInvarianceConservative(aScope, aPattern);
            default -> throw new IllegalStateException("Unexpected value: " + aPattern.getSubType());
        };
    }

    private List<Element> mapResponseInvarianceBasic(Scope aScope, ResponseInvariance aPattern) {
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


    private List<Element> mapResponseInvarianceLiberal(Scope aScope, ResponseInvariance aPattern) {
        List<Element> elements = new ArrayList<>();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                elements.add(languageDefinitions.getAlways());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());

                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(umintime(aPattern.getSTimeBound())));
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(lowerExact(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getUntil());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

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

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(umintime(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

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

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(time(aPattern.getSTimeBound())));
                elements.add(new SpaceElement());
                elements.add(aPattern.getS());
                elements.add(new RoundBracketCloseElement());

                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(umintime(aPattern.getSTimeBound())));
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(lowerExact(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getUntil());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
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
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(umintime(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

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

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(umintime(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

                elements.add(new RoundBracketOpenElement());

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

                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(umintime(aPattern.getSTimeBound())));
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getOr());
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getEventually());
                elements.add(new TimeBoundElement(lowerExact(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getS());
                elements.add(languageDefinitions.getUntil());
                elements.add(languageDefinitions.getEndPlaceholder());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());

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

    private List<Element> mapResponseInvarianceConservative(Scope aScope, ResponseInvariance aPattern) {
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
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(umintime(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

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
                elements.add(languageDefinitions.getEventually());
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

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(umintime(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

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
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aScope.getQ());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());
                elements.add(new RoundBracketOpenElement());
                elements.add(aPattern.getP());
                elements.add(languageDefinitions.getImplication());
                elements.add(new RoundBracketOpenElement());

                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getAlways());
                elements.add(new TimeBoundElement(umintime(aPattern.getSTimeBound())));
                elements.add(new RoundBracketOpenElement());
                elements.add(languageDefinitions.getNot());
                elements.add(aScope.getR());
                elements.add(new RoundBracketCloseElement());
                elements.add(new RoundBracketCloseElement());
                elements.add(languageDefinitions.getAnd());

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

    @Override
    public boolean isEndMode(Pattern aPattern) {
        switch (aPattern.getType()) {
            case PSPConstants.P_Recurrence, PSPConstants.P_MinimumDuration, PSPConstants.P_Response,
                 PSPConstants.P_Existence, PSPConstants.P_Universality,
                 PSPConstants.P_ResponseInvariance, PSPConstants.P_Until,
                 PSPConstants.P_ResponseChain1N, PSPConstants.P_ResponseChainN1,
                 PSPConstants.P_RelaxedResponseChain1N, PSPConstants.P_RelaxedResponseChainN1 -> {
                return PSPConstants.SP_Liberal == aPattern.getSubType();
            }
            case PSPConstants.P_BoundedEventExistence, PSPConstants.P_Precedence, PSPConstants.P_PrecedenceChainN1,
                 PSPConstants.P_PrecedenceChain1N -> {
                return true;
            }
            case PSPConstants.P_MaximumDuration -> {
                return PSPConstants.SP_Conservative == aPattern.getSubType() || PSPConstants.SP_Liberal == aPattern.getSubType();
            }
            default -> {
                return false;
            }
        }
    }
}
