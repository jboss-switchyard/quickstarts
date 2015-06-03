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
package org.switchyard.internal;

import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.label.BehaviorLabel;
import org.switchyard.runtime.RuntimeMessages;

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
            throw RuntimeMessages.MESSAGES.scopeDifferent(target.toString(), source.toString());
        }
    }
}
