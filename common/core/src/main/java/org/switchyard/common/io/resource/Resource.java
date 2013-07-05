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
package org.switchyard.common.io.resource;

import java.net.URL;

/**
 * Represents a resource.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface Resource {

    /**
     * Gets the location of the resource.
     * @return the location of the resource
     */
    public String getLocation();

    /**
     * Gets the URL form of the resource's location.
     * @return the URL form of the resource's location
     */
    public URL getLocationURL();

    /**
     * Gets the URL form of the resource's location.
     * @param caller the caller class so we can check with it's classloader
     * @return the URL form of the resource's location
     */
    public URL getLocationURL(Class<?> caller);

    /**
     * Gets the URL form of the resource's location.
     * @param loader the classloader to check with
     * @return the URL form of the resource's location
     */
    public URL getLocationURL(ClassLoader loader);

    /**
     * Gets the type of the resource.
     * @return the type of the resource
     */
    public ResourceType getType();

}
