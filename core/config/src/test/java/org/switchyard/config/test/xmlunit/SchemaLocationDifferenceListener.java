/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
