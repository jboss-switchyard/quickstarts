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

package org.switchyard.component.bean;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Bean Service Annotation.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 * @author <a href="mailto:jpechane@redhat.com">jpechane@redhat.com</a>
 * @see org.switchyard.component.bean.Reference
 */
@Target({TYPE, FIELD })
@Retention(RUNTIME)
@Documented
public @interface Service {

    /**
     * Get the Service Interface for the Service.
     */
    // Class field couldn't be declared so using a literal anyway
    Class<?> value() default Service.class; 
    
    /**
     * Optional Service name.
     */
    String name() default EMPTY;
    
    /**
     * Optional name of the component that implements the service.
     */
    String componentName() default EMPTY;

    /**
     * Constant representing a null (i.e. unassigned) value.
     * Annotations are not allowed to have empty values, so a default
     * representation for an empty value is used.
     */
    public static final String EMPTY = "";
}
