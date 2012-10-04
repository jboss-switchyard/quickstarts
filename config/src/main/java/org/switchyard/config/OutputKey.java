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
package org.switchyard.config;

import org.switchyard.common.lang.Strings;

/**
 * OutputKey hints for Configuration output.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public enum OutputKey {

    /** The normalize hint. */
    NORMALIZE("normalize"),

    /** The omit-xml-declaration hint. */
    OMIT_XML_DECLARATION("omit-xml-declaration"),

    /** The order-children hint. */
    ORDER_CHILDREN("order-children"),

    /** The pretty-print hint. */
    PRETTY_PRINT("pretty-print");

    private final String _hint;

    /**
     * Constructs a new OutputKey with the specified hint.
     * @param hint the specified hint
     */
    private OutputKey(String hint) {
        _hint = hint;
    }

    /**
     * Gets the hint.
     * @return the hint
     */
    public String hint() {
        return _hint;
    }

    /**
     * Derives an OutputKey from the specified hint.
     * @param hint the hint
     * @return the OutputKey
     */
    public static OutputKey fromHint(String hint) {
        hint = Strings.trimToNull(hint);
        if (hint != null) {
            for (OutputKey ok : values()) {
                if (ok.hint().equalsIgnoreCase(hint)) {
                    return ok;
                }
            }
        }
        return null;
    }

}
