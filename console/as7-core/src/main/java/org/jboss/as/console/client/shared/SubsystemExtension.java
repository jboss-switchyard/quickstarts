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
package org.jboss.as.console.client.shared;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SubsystemExtension
 * 
 * Provides information about a subsytem extension (e.g. group name, items list)
 * 
 * @author Rob Cernich
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SubsystemExtension {

    /**
     * Used to define a "group" in the subsystem tree.
     */
    public @interface SubsystemGroupDefinition {
        /**
         * The name of the group.
         */
        String name();

        /**
         * The items within the group.
         */
        SubsystemItemDefinition[] items();
    }

    /**
     * Used to define an item in the subsystem tree.
     */
    public @interface SubsystemItemDefinition {
        /**
         * The (display) name of the item.
         */
        String name();

        /**
         * The name token identifying the presenter for this item.
         */
        String presenter();
    }

    /**
     * The subsystem groups supported by the extension.
     */
    SubsystemGroupDefinition[] groups();

    /**
     * A runtime operations group item provided by the subsystem.
     */
    SubsystemItemDefinition[] runtime() default {};

    /**
     * A runtime metrics group item provided by the subsystem.
     */
    SubsystemItemDefinition[] metrics() default {};

    /**
     * The subsystem name for the item. Must match the name used to identify the
     * subsystem within the server configuration.
     */
    String subsystem();

}
