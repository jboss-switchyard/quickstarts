/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.switchyard.component.rules;

import javax.xml.namespace.QName;

/**
 * Various constants and context variables.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class RulesConstants {

    /**
     * The default rules namespace.
     */
    public static final String RULES_NAMESPACE = "urn:switchyard-component-rules:rules:1.0";

    /** actionType . */
    public static final String ACTION_TYPE = "actionType";
    /** {urn:switchyard-component-rules:rules:1.0}actionType . */
    public static final String ACTION_TYPE_VAR = new QName(RULES_NAMESPACE, ACTION_TYPE).toString();
    /** execute . */
    public static final String EXECUTE = "execute";
    /** fireAllRules . */
    public static final String FIRE_ALL_RULES = "fireAllRules";
    /** fireUntilHalt . */
    public static final String FIRE_UNTIL_HALT = "fireUntilHalt";

    /** continue . */
    public static final String CONTINUE = "continue";
    /** {urn:switchyard-component-rules:rules:1.0}continue . */
    public static final String CONTINUE_VAR = new QName(RULES_NAMESPACE, CONTINUE).toString();

    /** dispose . */
    public static final String DISPOSE = "dispose";
    /** {urn:switchyard-component-rules:rules:1.0}dispose . */
    public static final String DISPOSE_VAR = new QName(RULES_NAMESPACE, DISPOSE).toString();

    /** message . */
    public static final String MESSAGE = "message";
    /** exchange . */
    public static final String EXCHANGE = "exchange";
    /** context . */
    public static final String CONTEXT = "context";
    /** context_in . */
    public static final String CONTEXT_IN = "context_in";
    /** context_out . */
    public static final String CONTEXT_OUT = "context_out";
    /** context_exchange . */
    public static final String CONTEXT_EXCHANGE = "context_exchange";

    private RulesConstants() {}

}
