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
package org.switchyard.component.common.label;

import org.switchyard.label.Label;

/**
 * Represents property labels.
 */
public enum PropertyLabel implements Label {

    /** Property labels. */
    HEADER, PROPERTY;

    private final String _label;

    private PropertyLabel() {
        _label = Label.Util.toSwitchYardLabel("property", name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String label() {
        return _label;
    }

    /**
     * Gets the PropertyLabel enum via case-insensitive short-name.
     * @param name the case-insensitive short-name
     * @return the PropertyLabel enum
     */
    public static final PropertyLabel ofName(String name) {
        return Label.Util.ofName(PropertyLabel.class, name);
    }

    /**
     * Gets the full-form property label from the case-insensitive short-name.
     * @param name the case-insensitive short-name
     * @return the full-form property label
     */
    public static final String toLabel(String name) {
        PropertyLabel label = ofName(name);
        return label != null ? label.label() : null;
    }

    /**
     * Prints all known property labels.
     * @param args ignored
     */
    public static void main(String... args) {
        Label.Util.print(values());
    }

}
