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


package org.switchyard.component.camel;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Designates a class as a Camel route provider.  A required value element 
 * of type java.lang.Class is required to identify the service interface for the
 * route service. An optional name element of type java.lang.String is used to
 * define the service name.
 */
@Target({TYPE})
@Retention(RUNTIME)
@Documented
public @interface Route {

    /**
     * Service interface for the route.
     */
    Class<?> value();

    /**
     * Optional Service name.
     */
    String name() default EMPTY;

    /**
     * Constant representing a null (i.e. unassigned) value.
     * Annotations are not allowed to have empty values, so a default
     * representation for an empty value is used.
     */
    public static final String EMPTY = "";
}
