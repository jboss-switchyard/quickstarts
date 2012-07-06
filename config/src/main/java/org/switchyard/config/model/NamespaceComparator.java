/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.config.model;

import java.util.Comparator;

/**
 * Used for ordering namespaces.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2011 Red Hat Inc.
 */
class NamespaceComparator implements Comparator<String> {

    private static final String W3 = "http://www.w3.org/";

    private static final String OASIS_WSS = "http://docs.oasis-open.org/wss/";
    private static final String OASIS_NS = "http://docs.oasis-open.org/ns/";

    private static final String SY = "urn:switchyard-";
    private static final String SY_CFG = SY + "config:";
    private static final String SY_CFG_SY = SY_CFG + "switchyard";
    private static final String SY_COMP = SY + "component-";

    /**
     * Package-private constructor.
     */
    NamespaceComparator() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(String o1, String o2) {
        if (o1 == null) {
            if (o2 == null) {
                return 0;
            }
            return -1;
        } else if (o2 == null) {
            return 1;
        }
        if (o1.startsWith(W3)) {
            if (!o2.startsWith(W3)) {
                return -1;
            }
        } else if (o1.startsWith(OASIS_WSS)) {
            if (o2.startsWith(W3)) {
                return 1;
            }
            if (!o2.startsWith(OASIS_WSS)) {
                return -1;
            }
        } else if (o1.startsWith(OASIS_NS)) {
            if (o2.startsWith(W3) || o2.startsWith(OASIS_WSS)) {
                return 1;
            }
            if (!o2.startsWith(OASIS_NS)) {
                return -1;
            }
        } else if (o1.startsWith(SY)) {
            if (o2.startsWith(W3) || o2.startsWith(OASIS_WSS) || o2.startsWith(OASIS_NS)) {
                return 1;
            }
            if (o1.startsWith(SY_CFG)) {
                if (!o2.startsWith(SY_CFG)) {
                    return -1;
                }
                if (o1.startsWith(SY_CFG_SY)) {
                    if (!o2.startsWith(SY_CFG_SY)) {
                        return -1;
                    }
                }
                if (o2.startsWith(SY_CFG_SY)) {
                    if (!o1.startsWith(SY_CFG_SY)) {
                        return 1;
                    }
                }
            }
            if (o1.startsWith(SY_COMP)) {
                if (!o2.startsWith(SY_COMP)) {
                    return -1;
                }
            }
            if (!o2.startsWith(SY)) {
                return -1;
            }
        } else if (o2.startsWith(W3) || o2.startsWith(OASIS_WSS) || o2.startsWith(OASIS_NS) || o2.startsWith(SY)) {
            return 1;
        }
        return o1.compareTo(o2) * -1;
    }

}
