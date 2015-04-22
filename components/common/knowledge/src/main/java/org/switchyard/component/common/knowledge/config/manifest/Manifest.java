/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.common.knowledge.config.manifest;

import org.kie.api.runtime.Environment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;

/**
 * Manifest.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public abstract class Manifest {

    /**
     * Adds the runtimeEnvironmentBuilder to the Environment.
     * @param runtimeEnvironmentBuilder the runtimeEnvironmentBuilder
     */
    public void addToEnvironment(RuntimeEnvironmentBuilder runtimeEnvironmentBuilder) {
        runtimeEnvironmentBuilder.addEnvironmentEntry(getClass().getName(), this);
    }

    /*
    public void addToEnvironment(Environment environment) {
        environment.set(getClass().getName(), this);
    }

    protected static <T extends Manifest> T getFromEnvironment(Environment environment, Class<T> type) {
        return type.cast(environment.get(type.getName()));
    }
    */

    /**
     * Removes and returns the Manifest from the Environment.
     * @param environment the Environment
     * @param type the type of Manifest
     * @return the Manifest
     */
    protected static <T extends Manifest> T removeFromEnvironment(Environment environment, Class<T> type) {
        String identifier = type.getName();
        Object manifest = environment.get(identifier);
        if (manifest != null) {
            environment.set(identifier, null);
        }
        return type.cast(manifest);
    }

}
