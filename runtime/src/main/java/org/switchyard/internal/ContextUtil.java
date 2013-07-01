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
package org.switchyard.internal;

import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.label.BehaviorLabel;

/**
 * Utility class to handle {@link Context} related operations.
 */
public final class ContextUtil {

    private ContextUtil() { }

    /**
     * Copy properties from source context to destination context. Properties with
     * TRANSIENT label will be skipped.
     * 
     * @param source Source context.
     * @param destination Destination context.
     * @return Destination context.
     */
    public static Context copy(Context source, Context destination) {
        for (Property property : source.getProperties()) {
            if (!property.hasLabel(BehaviorLabel.TRANSIENT.label())) {
                destination.setProperty(property.getName(), property.getValue())
                    .addLabels(property.getLabels());
            }
        }
        return destination;
    }

    /**
     * Verify if source scope is same as target scope. If not {@link IllegalArgumentException} will be thrown.
     * 
     * @param source Source scope.
     * @param target Target scope.
     */
    public static void checkScope(Scope source, Scope target) {
        if (source != target) {
            throw new IllegalArgumentException("Scope " + target + " is different than expected " + source);
        }
    }
}
