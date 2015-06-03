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
