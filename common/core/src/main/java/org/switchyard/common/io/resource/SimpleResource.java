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

/**
 * Simple Resource class.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class SimpleResource extends BaseResource {

    private String _location;
    private ResourceType _type;

    /**
     * Constructs a new BaseResource.
     */
    public SimpleResource() {}

    /**
     * Constructs a new BaseResource with the specified resource location.
     * @param location the resource location
     */
    public SimpleResource(String location) {
        setLocation(location);
    }

    /**
     * Constructs a new BaseResource with the specified resource location and type.
     * @param location the resource location
     * @param type the resource type
     */
    public SimpleResource(String location, ResourceType type) {
        setLocation(location);
        setType(type);
    }

    /**
     * Constructs a new BaseResource with the specified resource location and type.
     * @param location the resource location
     * @param type the resource type
     */
    public SimpleResource(String location, String type) {
        setLocation(location);
        setType(ResourceType.valueOf(type));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocation() {
        return _location;
    }

    /**
     * Sets the resource location.
     * <p><i>If not already set, the resource type will be deduced from the extension of the location, and set.</i></p>
     * @param location the resource location
     * @return this BaseResource (useful for chaining)
     */
    public SimpleResource setLocation(String location) {
        if (location != null && getType() == null) {
            setType(ResourceType.forLocation(location));
        }
        _location = location;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceType getType() {
        return _type;
    }

    /**
     * Sets the resource type.
     * @param type the resource type
     * @return this BaseResource (useful for chaining)
     */
    public SimpleResource setType(ResourceType type) {
        _type = type;
        return this;
    }

}
