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
package org.switchyard.remote.http;

import org.switchyard.label.Label;

/**
 * Represents httpinvoker labels.
 */
public enum HttpInvokerLabel implements Label {
    /** httpinvoker labels. */
    HEADER;

    private final String _label;

    private HttpInvokerLabel() {
        _label = Label.Util.toSwitchYardLabel("httpinvoker", name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String label() {
        return _label;
    }

    /**
     * Gets the HttpInvokerLabel enum via case-insensitive short-name.
     * @param name the case-insensitive short-name
     * @return the HttpInvokerLabel enum
     */
    public static final HttpInvokerLabel ofName(String name) {
        return Label.Util.ofName(HttpInvokerLabel.class, name);
    }

    /**
     * Gets the full-form httpinvoker label from the case-insensitive short-name.
     * @param name the case-insensitive short-name
     * @return the full-form httpinvoker label
     */
    public static final String toLabel(String name) {
        HttpInvokerLabel label = ofName(name);
        return label != null ? label.label() : null;
    }

    /**
     * Prints all known httpinvoker labels.
     * @param args ignored
     */
    public static void main(String... args) {
        Label.Util.print(values());
    }
}
