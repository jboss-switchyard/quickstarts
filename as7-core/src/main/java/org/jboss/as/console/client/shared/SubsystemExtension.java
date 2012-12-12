/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
