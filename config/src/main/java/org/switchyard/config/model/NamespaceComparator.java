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
