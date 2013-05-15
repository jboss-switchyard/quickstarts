/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
