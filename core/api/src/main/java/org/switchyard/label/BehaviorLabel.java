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
package org.switchyard.label;

/**
 * Represents behavior labels.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public enum BehaviorLabel implements Label {

    /** Behavior labels. */
    TRANSIENT; // the property will not be preserved in a copy of the Exchange via Context.copy()

    private final String _label;

    private BehaviorLabel() {
        _label = Label.Util.toSwitchYardLabel("behavior", name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String label() {
        return _label;
    }

    /**
     * Gets the BehaviorLabel enum via case-insensitive short-name.
     * @param name the case-insensitive short-name
     * @return the BehaviorLabel enum
     */
    public static final BehaviorLabel ofName(String name) {
        return Label.Util.ofName(BehaviorLabel.class, name);
    }

    /**
     * Gets the full-form behavior label from the case-insensitive short-name.
     * @param name the case-insensitive short-name
     * @return the full-form behavior label
     */
    public static final String toLabel(String name) {
        BehaviorLabel label = ofName(name);
        return label != null ? label.label() : null;
    }

    /**
     * Prints all known behavior labels.
     * @param args ignored
     */
    public static void main(String... args) {
        Label.Util.print(values());
    }

}
