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
package org.switchyard.component.common.knowledge.config.items;

import java.util.List;

import org.kie.api.event.kiebase.KieBaseEventListener;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.internal.runtime.manager.InternalRegisterableItemsFactory;

/**
 * ExtendedRegisterableItemsFactory.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public interface ExtendedRegisterableItemsFactory extends InternalRegisterableItemsFactory {

    /**
     * Gets the KieBaseEventListeners for the specified RuntimeEngine.
     * @param runtime the RuntimeEngine
     * @return the KieBaseEventListeners
     */
    public List<KieBaseEventListener> getKieBaseEventListeners(RuntimeEngine runtime);

    /**
     * Environment helper class.
     */
    public static final class Env {
        private static final String ERIF = ExtendedRegisterableItemsFactory.class.getName();
        private Env() {}
        /**
         * Adds the ExtendedRegisterableItemsFactory to the RuntimeEnvironmentBuilder's Environment.
         * @param reb the RuntimeEnvironmentBuilder
         * @param erif the ExtendedRegisterableItemsFactory
         */
        public static void addToEnvironment(RuntimeEnvironmentBuilder reb, ExtendedRegisterableItemsFactory erif) {
            reb.addEnvironmentEntry(ERIF, erif);
        }
        /**
         * Removes and returns the ExtendedRegisterableItemsFactory from the Environment.
         * @param e the Environment
         * @return the ExtendedRegisterableItemsFactory
         */
        public static ExtendedRegisterableItemsFactory removeFromEnvironment(Environment e) {
            Object erif = e.get(ERIF);
            if (erif != null) {
                e.set(ERIF, null);
            }
            return ExtendedRegisterableItemsFactory.class.cast(erif);
        }
    }

}
