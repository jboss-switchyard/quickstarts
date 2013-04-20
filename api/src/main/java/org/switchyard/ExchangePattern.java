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

package org.switchyard;

/**
 * The list of supported exchange patterns within SwitchYard.
 */
public enum ExchangePattern {

    /**
     * A message exchange consisting of a single request message.
     */
    IN_ONLY ("http://www.w3.org/ns/wsdl/in-only"),
    /**
     * A message exchange consisting of a single request message and a single
     * response message.
     */
    IN_OUT ("http://www.w3.org/ns/wsdl/in-out");

    /**
     * String representation of the pattern URI.
     */
    private String _patternURI;

    /**
     * Creates a new ExchangePattern instance with the specified URI string.
     * @param uri a URI uniquely identifying the exchange pattern
     */
    ExchangePattern(final String uri) {
        _patternURI = uri;
    }

    /**
     * Returns the string representation of the ExchangePattern instance.
     * @return exchange pattern URI
     */
    public String getURI() {
        return _patternURI;
    }

    /**
     * Converts an exchange pattern URI string into an instance of this enum.
     * @param uri exchange pattern URI
     * @return an instane of ExchangePattern corresponding to the specified
     * pattern URI.
     */
    public static ExchangePattern fromURI(final String uri) {
        if (IN_ONLY.getURI().equals(uri)) {
            return IN_ONLY;
        } else if (IN_OUT.getURI().equals(uri)) {
            return IN_OUT;
        } else {
            throw APIMessages.MESSAGES.unrecognizedURI(uri);
        }
    }
}
