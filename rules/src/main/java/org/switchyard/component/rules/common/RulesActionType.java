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
package org.switchyard.component.rules.common;

import javax.xml.namespace.QName;

/**
 * Represents available rules actions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public enum RulesActionType {

    /** {urn:switchyard-component-rules:rules:1.0}executeRules . */
    EXECUTE_RULES(QName.valueOf(RulesConstants.EXECUTE_RULES_VAR));

    private final QName _qname;

    /**
     * Constructs a new RulesActionType with the specified qualified name.
     * @param qname the qualified name
     */
    RulesActionType(QName qname) {
        _qname = qname;
    }

    /**
     * Gets the qualified name.
     * @return the qualified name
     */
    public QName qname() {
        return _qname;
    }

    /**
     * Gets the RulesActionType matching the specified qualified name.
     * @param qname the qualified name
     * @return the matching RulesActionType
     */
    public static RulesActionType valueOf(QName qname) {
        for (RulesActionType pat : values()) {
            if (pat.qname().equals(qname)) {
                return pat;
            }
        }
        return null;
    }

}
