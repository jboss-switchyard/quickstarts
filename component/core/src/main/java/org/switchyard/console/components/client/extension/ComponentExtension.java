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
package org.switchyard.console.components.client.extension;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ComponentExtension
 * 
 * Marks a class which provides support for a specific SwitchYard component. The
 * class will provide a configuration view for the component, plus service
 * implementation and binding views where applicable.
 * 
 * @author Rob Cernich
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ComponentExtension {

    /**
     * The name of the component to which this extension applies.
     */
    String componentName();

    /**
     * The activation types to which this extension applies.
     */
    String[] activationTypes();

    /**
     * The name to display for this component.
     */
    String displayName();

}
