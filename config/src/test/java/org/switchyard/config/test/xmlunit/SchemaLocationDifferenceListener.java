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
package org.switchyard.config.test.xmlunit;

import java.util.StringTokenizer;

import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.NodeDetail;
import org.switchyard.common.lang.Strings;
import org.w3c.dom.Node;

/**
 * Helps compare the xsi:schemaLocation attribute, who's potentially non-standard whitespace in between parts can cause problems.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class SchemaLocationDifferenceListener implements DifferenceListener {

    /**
     * {@inheritDoc}
     */
    @Override
    public int differenceFound(Difference diff) {
        if (diff.getId() == DifferenceConstants.SCHEMA_LOCATION_ID) {
            String control = reduceWhitespace(diff.getControlNodeDetail());
            String test = reduceWhitespace(diff.getTestNodeDetail());
            if ((control == null && test == null) || (control != null && control.equals(test))) {
                return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
            }
        }
        return RETURN_ACCEPT_DIFFERENCE;
    }

    private String reduceWhitespace(NodeDetail nd) {
        String value = nd.getValue();
        if (value != null) {
            StringBuilder sb = new StringBuilder();
            StringTokenizer st = new StringTokenizer(value);
            while (st.hasMoreTokens()) {
                sb.append(st.nextToken());
                if (st.hasMoreTokens()) {
                    sb.append(' ');
                }
            }
            value = Strings.trimToNull(sb.toString());
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void skippedComparison(Node control, Node test) {
    }

}
