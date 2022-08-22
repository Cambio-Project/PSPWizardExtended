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
 *   Markus languageDefintiions.getUntil()mpe, Swinburne University of Technology
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

import psp.constraints.EventConstraint;
import psp.engine.PSPConstants;
import psp.sel.Event;
import psp.sel.patterns.Pattern;
import psp.sel.patterns.order.*;
import psp.sel.patterns.occurrence.*;
import psp.sel.scopes.Scope;

public class LTLMapper extends GenericMapper {
    private static final String defaultInf = "∞";
    private static final String defaultAlways = "☐";
    private static final String defaultEventually = "◇";
    private static final String defaultNext = "○";
    private static final String defaultImplication = " → ";
    private static final String defaultNot = "¬";
    private static final String defaultAnd = " ∧ ";
    private static final String defaultOr = " ∨ ";
    private static final String defaultUntil = " U";
    private static final String defaultWeakUntil = " W";
    private static final LanguageDefinitions DEFAULT_LANGUAGE_DEFINITION = new LanguageDefinitions(defaultInf,
        defaultAlways, defaultEventually, defaultNext, defaultImplication, defaultNot, defaultAnd, defaultOr,
        defaultUntil, defaultWeakUntil);

    public LTLMapper() {
        super(DEFAULT_LANGUAGE_DEFINITION);
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

    public String getMapping(Scope aScope, Pattern aPattern) {
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
                case PSPConstants.P_MinimumDuration:
                case PSPConstants.P_MaximumDuration:
                    return ""; // not supported indicator
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
            return e.getMessage();
        }

        return "";
    }

    private String mapUniversality(Scope aScope, Universality aPattern) {
        StringBuilder sb = new StringBuilder();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append(aPattern.getP().getAsEvent());
                break;
            case PSPConstants.S_BeforeR:
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getNot());
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_AfterQ:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_BetweenQandR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getNot());
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_AfterQuntilR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getWeakUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                break;
        }

        return sb.toString();
    }

    private String mapAbsence(Scope aScope, Absence aPattern) {
        StringBuilder sb = new StringBuilder();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append(languageDefinitions.getNot());
                sb.append(aPattern.getP().getAsEvent());
                break;
            case PSPConstants.S_BeforeR:
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(languageDefinitions.getNot());
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getNot());
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append(languageDefinitions.getNot());
                sb.append(aPattern.getP().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_AfterQ:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append(languageDefinitions.getNot());
                sb.append(aPattern.getP().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_BetweenQandR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(languageDefinitions.getNot());
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getNot());
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append(languageDefinitions.getNot());
                sb.append(aPattern.getP().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_AfterQuntilR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append(languageDefinitions.getNot());
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getWeakUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                break;
        }

        return sb.toString();
    }

    private String mapExistence(Scope aScope, Existence aPattern) {
        StringBuilder sb = new StringBuilder();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aPattern.getP().getAsEvent());
                break;
            case PSPConstants.S_BeforeR:
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getWeakUntil());
                sb.append(" ");
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_AfterQ:
                sb.append(languageDefinitions.getAlways());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getNot());
                sb.append(languageDefinitions.getEventually());
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_BetweenQandR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getWeakUntil());
                sb.append(" ");
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                sb.append(")");
                break;
            case PSPConstants.S_AfterQuntilR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                sb.append(")");
                break;
        }

        return sb.toString();
    }

    private String mapRecurrence(Scope aScope, Recurrence aPattern) {
        StringBuilder sb = new StringBuilder();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_BeforeR:
                sb.append(languageDefinitions.getEventually());
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_AfterQ:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(")");
                sb.append(")");
                break;
            case PSPConstants.S_BetweenQandR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getEventually());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                sb.append(languageDefinitions.getUntil());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                break;
            case PSPConstants.S_AfterQuntilR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                sb.append(languageDefinitions.getWeakUntil());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                break;
        }

        return sb.toString();
    }

    private String mapPrecedence(Scope aScope, Precedence aPattern) {
        StringBuilder sb = new StringBuilder();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aPattern.getS().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_BeforeR:
                sb.append(languageDefinitions.getEventually());
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aPattern.getS().getAsEvent());
                sb.append(languageDefinitions.getNot());
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                break;
            case PSPConstants.S_AfterQ:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aPattern.getS().getAsEvent());
                sb.append(")");
                sb.append(")");
                break;
            case PSPConstants.S_BetweenQandR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getEventually());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aPattern.getS().getAsEvent());
                sb.append(languageDefinitions.getNot());
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_AfterQuntilR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aPattern.getS().getAsEvent());
                sb.append(languageDefinitions.getNot());
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                sb.append(languageDefinitions.getWeakUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                break;
        }

        return sb.toString();
    }

    // Tis addressed from 0 to n-1.

    private void PC1N_Ch(StringBuilder sb, ChainEvents Tis, int i) {
        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";

            if (lcntZi.equals("true")) {
                // no Zi
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNext());
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append("(");
                sb.append(Ti.getEvent().getAsEvent());
                PC1N_Ch(sb, Tis, i + 1);
                sb.append(")");
                sb.append(")");
            } else {
                // with Zi
                sb.append(languageDefinitions.getAnd());
                sb.append(lcntZi);
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNext());
                sb.append("(");
                sb.append(lcntZi);
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append("(");
                sb.append(Ti.getEvent().getAsEvent());
                PC1N_Ch(sb, Tis, i + 1);
                sb.append(")");
                sb.append(")");
            }
        }
    }

    private String mapPrecedenceChain1N(Scope aScope, PrecedenceChain1N aPattern) {
        StringBuilder sb = new StringBuilder();

        EventConstraint lZS = aPattern.getSConstraint();
        String lcntZS = lZS != null ? cnt(lZS.getEvent()) : "true";
        boolean lHasConstraint = !lcntZS.equals("true");
        ChainEvents Tis = aPattern.getTis();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                if (lHasConstraint) {
                    // has ZS
                    sb.append(lcntZS);
                    sb.append(languageDefinitions.getUntil());
                } else {
                    // no ZS
                    sb.append(languageDefinitions.getEventually());
                }
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                PC1N_Ch(sb, Tis, 0);
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_BeforeR:
                sb.append(languageDefinitions.getEventually());
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                if (lHasConstraint) {
                    // has ZS
                    sb.append(lcntZS);
                    sb.append(languageDefinitions.getUntil());
                } else {
                    // no ZS
                    sb.append(languageDefinitions.getEventually());
                }
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                PC1N_Ch(sb, Tis, 0);
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getNot());
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_AfterQ:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                if (lHasConstraint) {
                    sb.append(lcntZS);
                    sb.append(languageDefinitions.getUntil());
                } else {
                    sb.append(languageDefinitions.getEventually());
                }
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                PC1N_Ch(sb, Tis, 0);
                sb.append(")");
                sb.append(")");
                sb.append(")");
                sb.append(")");
                break;
            case PSPConstants.S_BetweenQandR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getEventually());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append("(");
                if (lHasConstraint) {
                    sb.append(lcntZS);
                    sb.append(languageDefinitions.getUntil());
                } else {
                    sb.append(languageDefinitions.getEventually());
                }
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                PC1N_Ch(sb, Tis, 0);
                sb.append(")");
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getNot());
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_AfterQuntilR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append("(");
                if (lHasConstraint) {
                    sb.append(lcntZS);
                    sb.append(languageDefinitions.getUntil());
                } else {
                    sb.append(languageDefinitions.getEventually());
                }
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                PC1N_Ch(sb, Tis, 0);
                sb.append(")");
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getNot());
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                sb.append(languageDefinitions.getWeakUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                break;
        }

        return sb.toString();
    }

    // Tis addressed from 0 to n-1.

    private void PCN1_Ch(StringBuilder sb, PrecedenceChainN1 aPattern, int i) {
        ChainEvents Tis = aPattern.getTis();

        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";

            if (lcntZi.equals("true")) {
                // no Zi
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNext());
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append("(");
                sb.append(Ti.getEvent().getAsEvent());
                PCN1_Ch(sb, aPattern, i + 1);
                sb.append(")");
                sb.append(")");
            } else {
                // with Zi
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(lcntZi);
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNext());
                sb.append("(");
                sb.append("(");
                sb.append(languageDefinitions.getNot());
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(lcntZi);
                sb.append(")");
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append("(");
                sb.append(Ti.getEvent().getAsEvent());
                PCN1_Ch(sb, aPattern, i + 1);
                sb.append(")");
                sb.append(")");
            }
        }
    }

    private String mapPrecedenceChainN1(Scope aScope, PrecedenceChainN1 aPattern) {
        StringBuilder sb = new StringBuilder();

        EventConstraint lZP = aPattern.getPConstraint();
        String lcntZP = lZP != null ? cnt(lZP.getEvent()) : "true";
        boolean lHasConstraint = !lcntZP.equals("true");
        ChainEvents Tis = aPattern.getTis();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                if (lHasConstraint) {
                    // has ZS
                    sb.append("(");
                    sb.append(languageDefinitions.getNot());
                    sb.append(aPattern.getP().getAsEvent());
                    sb.append(languageDefinitions.getAnd());
                    sb.append(lcntZP);
                    sb.append(")");
                    sb.append(languageDefinitions.getUntil());
                } else {
                    // no ZS
                    sb.append(languageDefinitions.getEventually());
                }
                sb.append(" ");
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                PCN1_Ch(sb, aPattern, 0);
                sb.append(")");
                sb.append(")");
                sb.append(")");
                break;
            case PSPConstants.S_BeforeR:
                sb.append(languageDefinitions.getEventually());
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append("(");
                if (lHasConstraint) {
                    // has ZS
                    sb.append("(");
                    sb.append(languageDefinitions.getEventually());
                    sb.append(" ");
                    sb.append(aPattern.getP().getAsEvent());
                    sb.append(languageDefinitions.getImplication());
                    sb.append("(");
                    sb.append("(");
                    sb.append(languageDefinitions.getNot());
                    sb.append(aPattern.getP().getAsEvent());
                    sb.append(languageDefinitions.getAnd());
                    sb.append(lcntZP);
                    sb.append(")");
                    sb.append(languageDefinitions.getUntil());
                    sb.append(" ");
                    sb.append("(");
                    sb.append(aPattern.getS().getAsEvent());
                    PCN1_Ch(sb, aPattern, 0);
                    sb.append(")");
                    sb.append(")");
                    sb.append(")");
                } else {
                    // no ZS
                    sb.append(languageDefinitions.getEventually());
                    sb.append(" ");
                    sb.append(aPattern.getP().getAsEvent());
                    sb.append(languageDefinitions.getImplication());
                    sb.append("(");
                    sb.append(languageDefinitions.getEventually());
                    sb.append(" ");
                    sb.append("(");
                    sb.append(aPattern.getS().getAsEvent());
                    PCN1_Ch(sb, aPattern, 0);
                    sb.append(")");
                    sb.append(")");
                }
                sb.append(languageDefinitions.getNot());
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_AfterQ:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                if (lHasConstraint) {
                    // has ZS
                    sb.append("(");
                    sb.append(languageDefinitions.getNot());
                    sb.append(aPattern.getP().getAsEvent());
                    sb.append(languageDefinitions.getAnd());
                    sb.append(lcntZP);
                    sb.append(")");
                    sb.append(languageDefinitions.getUntil());
                } else {
                    // no ZS
                    sb.append(languageDefinitions.getEventually());
                }
                sb.append(" ");
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                PCN1_Ch(sb, aPattern, 0);
                sb.append(")");
                sb.append(")");
                sb.append(")");
                sb.append(")");
                break;
            case PSPConstants.S_BetweenQandR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getEventually());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                if (lHasConstraint) {
                    // has ZS
                    sb.append("(");
                    sb.append(languageDefinitions.getNot());
                    sb.append(aPattern.getP().getAsEvent());
                    sb.append(languageDefinitions.getAnd());
                    sb.append(lcntZP);
                    sb.append(")");
                    sb.append(languageDefinitions.getUntil());
                } else {
                    // no ZS
                    sb.append(languageDefinitions.getEventually());
                }
                sb.append(" ");
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                PCN1_Ch(sb, aPattern, 0);
                sb.append(")");
                sb.append(")");
                sb.append(")");
                sb.append(languageDefinitions.getNot());
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_AfterQuntilR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                if (lHasConstraint) {
                    // has ZS
                    sb.append(languageDefinitions.getEventually());
                    sb.append(" ");
                    sb.append(aPattern.getP().getAsEvent());
                    sb.append(languageDefinitions.getImplication());
                    sb.append("(");
                    sb.append("(");
                    sb.append(languageDefinitions.getNot());
                    sb.append(aPattern.getP().getAsEvent());
                    sb.append(languageDefinitions.getAnd());
                    sb.append(lcntZP);
                    sb.append(")");
                    sb.append(languageDefinitions.getUntil());
                    sb.append(" ");
                    sb.append("(");
                    sb.append(aPattern.getS().getAsEvent());
                    PCN1_Ch(sb, aPattern, 0);
                    sb.append(")");
                    sb.append(")");
                } else {
                    // no ZS
                    sb.append("(");
                    sb.append(languageDefinitions.getEventually());
                    sb.append(" ");
                    sb.append(aPattern.getP().getAsEvent());
                    sb.append(languageDefinitions.getImplication());
                    sb.append("(");
                    sb.append(languageDefinitions.getEventually());
                    sb.append(" ");
                    sb.append("(");
                    sb.append(aPattern.getS().getAsEvent());
                    PCN1_Ch(sb, aPattern, 0);
                    sb.append(")");
                    sb.append(")");
                    sb.append(")");
                }
                sb.append(languageDefinitions.getNot());
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getWeakUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                break;
        }

        return sb.toString();
    }

    private String mapUntil(Scope aScope, Until aPattern) {
        StringBuilder sb = new StringBuilder();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append(aPattern.getS().getAsEvent());
                break;
            case PSPConstants.S_BeforeR:
                sb.append(languageDefinitions.getEventually());
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                break;
            case PSPConstants.S_AfterQ:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append(aPattern.getS().getAsEvent());
                sb.append(")");
                sb.append(")");
                break;
            case PSPConstants.S_BetweenQandR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                sb.append(")");
                break;
            case PSPConstants.S_AfterQuntilR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append(aPattern.getS().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getWeakUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                break;
        }

        return sb.toString();
    }

    private String mapResponse(Scope aScope, Response aPattern) {
        StringBuilder sb = new StringBuilder();

        EventConstraint lZS = aPattern.getSConstraint();
        String lcntZS = lZS != null ? cnt(lZS.getEvent()) : "true";
        boolean lHasConstraint = !lcntZS.equals("true");

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                if (lHasConstraint) {
                    sb.append(lcntZS);
                    sb.append(languageDefinitions.getUntil());
                } else {
                    sb.append(languageDefinitions.getEventually());
                }
                sb.append(" ");
                sb.append(aPattern.getS().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_BeforeR:
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                if (lHasConstraint) {
                    sb.append("(");
                    sb.append(languageDefinitions.getNot());
                    sb.append(aScope.getR().getAsEvent());
                    sb.append(languageDefinitions.getAnd());
                    sb.append(lcntZS);
                    sb.append(")");
                } else {
                    sb.append(languageDefinitions.getNot());
                    sb.append(aScope.getR().getAsEvent());
                }
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                sb.append(")");
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                break;
            case PSPConstants.S_AfterQ:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                if (lHasConstraint) {
                    sb.append(lcntZS);
                    sb.append(languageDefinitions.getUntil());
                } else {
                    sb.append(languageDefinitions.getEventually());
                }
                sb.append(" ");
                sb.append(aPattern.getS().getAsEvent());
                sb.append(")");
                sb.append(")");
                break;
            case PSPConstants.S_BetweenQandR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                if (lHasConstraint) {
                    sb.append("(");
                    sb.append(languageDefinitions.getNot());
                    sb.append(aScope.getR().getAsEvent());
                    sb.append(languageDefinitions.getAnd());
                    sb.append(lcntZS);
                    sb.append(")");
                } else {
                    sb.append(languageDefinitions.getNot());
                    sb.append(aScope.getR().getAsEvent());
                    sb.append(languageDefinitions.getAnd());
                    sb.deleteCharAt(sb.length() - 1);
                }
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                sb.append(")");
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_AfterQuntilR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                if (lHasConstraint) {
                    sb.append("(");
                    sb.append(languageDefinitions.getNot());
                    sb.append(aScope.getR().getAsEvent());
                    sb.append(languageDefinitions.getAnd());
                    sb.append(lcntZS);
                    sb.append(")");
                } else {
                    sb.append(languageDefinitions.getNot());
                    sb.append(aScope.getR().getAsEvent());
                }
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                sb.append(")");
                sb.append(languageDefinitions.getWeakUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                break;
        }

        return sb.toString();
    }

    // Tis addressed from 0 to n-1.

    private void RC1N_Ch(StringBuilder sb, ChainEvents Tis, int i) {
        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";
            boolean lHasConstraint = !lcntZi.equals("true");

            if (lHasConstraint) {
                // with Zi
                sb.append(languageDefinitions.getAnd());
                sb.append(lcntZi);
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNext());
                sb.append("(");
                sb.append(lcntZi);
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append("(");
                sb.append(Ti.getEvent().getAsEvent());
                RC1N_Ch(sb, Tis, i + 1);
                sb.append(")");
                sb.append(")");
            } else {
                // no Zi
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNext());
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append("(");
                sb.append(Ti.getEvent().getAsEvent());
                RC1N_Ch(sb, Tis, i + 1);
                sb.append(")");
                sb.append(")");
            }
        }
    }

    private void RC1N_ChR(StringBuilder sb, ChainEvents Tis, Event R, int i) {
        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";
            boolean lHasConstraint = !lcntZi.equals("true");

            if (lHasConstraint) {
                // with Zi
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(R.getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(lcntZi);
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNext());
                sb.append("(");
                sb.append(lcntZi);
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append("(");
                sb.append(Ti.getEvent().getAsEvent());
                RC1N_ChR(sb, Tis, R, i + 1);
                sb.append(")");
                sb.append(")");
            } else {
                // no Zi
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(R.getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNext());
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append("(");
                sb.append(Ti.getEvent().getAsEvent());
                RC1N_ChR(sb, Tis, R, i + 1);
                sb.append(")");
                sb.append(")");
            }
        }
    }

    private String mapResponseChain1N(Scope aScope, ResponseChain1N aPattern) {
        StringBuilder sb = new StringBuilder();

        EventConstraint lZS = aPattern.getSConstraint();
        String lcntZS = lZS != null ? cnt(lZS.getEvent()) : "true";
        boolean lHasConstraint = !lcntZS.equals("true");

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                if (lHasConstraint) {
                    sb.append(lcntZS);
                    sb.append(languageDefinitions.getUntil());
                } else {
                    sb.append(languageDefinitions.getEventually());
                }
                sb.append(" ");
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                RC1N_Ch(sb, aPattern.getTis(), 0);
                sb.append(")");
                sb.append(")");
                sb.append(")");
                break;
            case PSPConstants.S_BeforeR:
                sb.append(languageDefinitions.getEventually());
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                if (lHasConstraint) {
                    sb.append("(");
                    sb.append(languageDefinitions.getNot());
                    sb.append(aScope.getR().getAsEvent());
                    sb.append(languageDefinitions.getAnd());
                    sb.append(lcntZS);
                    sb.append(")");
                } else {
                    sb.append(languageDefinitions.getNot());
                    sb.append(aScope.getR().getAsEvent());
                }
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                RC1N_ChR(sb, aPattern.getTis(), aScope.getR(), 0);
                sb.append(")");
                sb.append(")");
                sb.append(")");
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                break;
            case PSPConstants.S_AfterQ:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                if (lHasConstraint) {
                    sb.append(lcntZS);
                    sb.append(languageDefinitions.getUntil());
                } else {
                    sb.append(languageDefinitions.getEventually());
                }
                sb.append(" ");
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                RC1N_Ch(sb, aPattern.getTis(), 0);
                sb.append(")");
                sb.append(")");
                sb.append(")");
                sb.append(")");
                break;
            case PSPConstants.S_BetweenQandR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getEventually());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                if (lHasConstraint) {
                    sb.append("(");
                    sb.append(languageDefinitions.getNot());
                    sb.append(aScope.getR().getAsEvent());
                    sb.append(languageDefinitions.getAnd());
                    sb.append(lcntZS);
                    sb.append(")");
                } else {
                    sb.append(languageDefinitions.getNot());
                    sb.append(aScope.getR().getAsEvent());
                }
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                RC1N_ChR(sb, aPattern.getTis(), aScope.getR(), 0);
                sb.append(")");
                sb.append(")");
                sb.append(")");
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_AfterQuntilR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                if (lHasConstraint) {
                    sb.append("(");
                    sb.append(languageDefinitions.getNot());
                    sb.append(aScope.getR().getAsEvent());
                    sb.append(languageDefinitions.getAnd());
                    sb.append(lcntZS);
                    sb.append(")");
                } else {
                    sb.append(languageDefinitions.getNot());
                    sb.append(aScope.getR().getAsEvent());
                }
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                RC1N_ChR(sb, aPattern.getTis(), aScope.getR(), 0);
                sb.append(")");
                sb.append(")");
                sb.append(")");
                sb.append(languageDefinitions.getWeakUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                break;
        }

        return sb.toString();
    }

    // Tis addressed from 0 to n-1.

    private void RCN1_Ch(StringBuilder sb, ResponseChainN1 aPattern, int i) {
        ChainEvents Tis = aPattern.getTis();

        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";

            if (lcntZi.equals("true")) {
                // no Zi
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNext());
                sb.append("(");
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append("(");
                sb.append(Ti.getEvent().getAsEvent());
                RCN1_Ch(sb, aPattern, i + 1);
                sb.append(")");
                sb.append(")");
            } else {
                // with Zi
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNext());
                sb.append("(");
                sb.append(lcntZi);
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append("(");
                sb.append(Ti.getEvent().getAsEvent());
                RCN1_Ch(sb, aPattern, i + 1);
                sb.append(")");
                sb.append(")");
            }
        } else {
            EventConstraint lZP = aPattern.getPConstraint();
            String lcntZP = lZP != null ? cnt(lZP.getEvent()) : "true";

            sb.append(languageDefinitions.getImplication());
            if (lcntZP.equals("true")) {
                // no Zi
                sb.append(languageDefinitions.getEventually());
            } else {
                // with Zi
                sb.append(lcntZP);
                sb.append(languageDefinitions.getUntil());
            }
            sb.append(" ");
            sb.append(aPattern.getP().getAsEvent());
        }
    }

    private void RCN1_ChR(StringBuilder sb, ResponseChainN1 aPattern, Event R, int i) {
        ChainEvents Tis = aPattern.getTis();

        if (i < Tis.size()) {
            ChainEvent Ti = Tis.getTi(i);

            EventConstraint lZi = Ti.getConstraint();
            String lcntZi = lZi != null ? cnt(lZi.getEvent()) : "true";

            if (lcntZi.equals("true")) {
                // no Zi
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNext());
                sb.append("(");
                sb.append(languageDefinitions.getNot());
                sb.append(R.getAsEvent());
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append("(");
                sb.append(Ti.getEvent().getAsEvent());
                RCN1_ChR(sb, aPattern, R, i + 1);
                sb.append(")");
                sb.append(")");
            } else {
                // with Zi
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNext());
                sb.append("(");
                sb.append(languageDefinitions.getNot());
                sb.append(R.getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(lcntZi);
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append("(");
                sb.append(Ti.getEvent().getAsEvent());
                RCN1_ChR(sb, aPattern, R, i + 1);
                sb.append(")");
                sb.append(")");
            }
        } else {
            EventConstraint lZP = aPattern.getPConstraint();
            String lcntZP = lZP != null ? cnt(lZP.getEvent()) : "true";

            sb.append(languageDefinitions.getImplication());
            if (lcntZP.equals("true")) {
                // no Zi
                sb.append(languageDefinitions.getEventually());
            } else {
                // with Zi
                sb.append(lcntZP);
                sb.append(languageDefinitions.getUntil());
            }
            sb.append(" ");
            sb.append(aPattern.getP().getAsEvent());
        }
    }

    private String mapResponseChainN1(Scope aScope, ResponseChainN1 aPattern) {
        StringBuilder sb = new StringBuilder();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                RCN1_Ch(sb, aPattern, 0);
                sb.append(")");
                break;
            case PSPConstants.S_BeforeR:
                sb.append(languageDefinitions.getEventually());
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                RCN1_ChR(sb, aPattern, aScope.getR(), 0);
                sb.append(")");
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_AfterQ:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                RCN1_Ch(sb, aPattern, 0);
                sb.append(")");
                sb.append(")");
                break;
            case PSPConstants.S_BetweenQandR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getEventually());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                RCN1_ChR(sb, aPattern, aScope.getR(), 0);
                sb.append(")");
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                break;
            case PSPConstants.S_AfterQuntilR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                RCN1_ChR(sb, aPattern, aScope.getR(), 0);
                sb.append(")");
                sb.append(languageDefinitions.getWeakUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                break;
        }

        return sb.toString();
    }

    private String mapResponseInvariance(Scope aScope, ResponseInvariance aPattern) {
        StringBuilder sb = new StringBuilder();

        switch (aScope.getType()) {
            case PSPConstants.S_Globally:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append(aPattern.getS().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_BeforeR:
                sb.append(languageDefinitions.getEventually());
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                sb.append(")");
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_AfterQ:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append(aPattern.getS().getAsEvent());
                sb.append(")");
                sb.append(")");
                break;
            case PSPConstants.S_BetweenQandR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getEventually());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                sb.append(")");
                sb.append(languageDefinitions.getUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                break;
            case PSPConstants.S_AfterQuntilR:
                sb.append(languageDefinitions.getAlways());
                sb.append("(");
                sb.append("(");
                sb.append(aScope.getQ().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(languageDefinitions.getImplication());
                sb.append("(");
                sb.append("(");
                sb.append(aPattern.getP().getAsEvent());
                sb.append(languageDefinitions.getImplication());
                sb.append(languageDefinitions.getAlways());
                sb.append(" ");
                sb.append("(");
                sb.append(aPattern.getS().getAsEvent());
                sb.append(languageDefinitions.getAnd());
                sb.append(languageDefinitions.getNot());
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                sb.append(")");
                sb.append(languageDefinitions.getWeakUntil());
                sb.append(" ");
                sb.append(aScope.getR().getAsEvent());
                sb.append(")");
                sb.append(")");
                break;
        }

        return sb.toString();
    }

    public String getNotSupportedMessage() {
        return "Mapping not supported in Linear Temporal Logic.";
    }

    public String toString() {
        return "LTL";
    }
}
