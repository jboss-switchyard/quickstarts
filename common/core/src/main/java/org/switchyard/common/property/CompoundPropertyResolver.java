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
package org.switchyard.common.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Resolves properties from multiple sources.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class CompoundPropertyResolver implements PropertyResolver {

    private List<PropertyResolver> _resolvers = new ArrayList<PropertyResolver>();

    /**
     * Construction with an array of property resolvers.
     * @param resolvers the property resolvers
     */
    protected CompoundPropertyResolver(PropertyResolver... resolvers) {
        if (resolvers != null) {
            for (PropertyResolver resolver : resolvers) {
                addResolver(resolver);
            }
        }
    }

    /**
     * Construction with a collection of property resolvers.
     * @param resolvers the property resolvers
     */
    protected CompoundPropertyResolver(Collection<PropertyResolver> resolvers) {
        if (resolvers != null) {
            for (PropertyResolver resolver : resolvers) {
                addResolver(resolver);
            }
        }
    }

    private void addResolver(PropertyResolver resolver) {
        if (resolver != null && !_resolvers.contains(resolver) && resolver != this) {
            if (resolver instanceof CompoundPropertyResolver) {
                for (PropertyResolver pr : ((CompoundPropertyResolver)resolver)._resolvers) {
                    addResolver(pr);
                }
            } else {
                _resolvers.add(resolver);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object resolveProperty(String key) {
        Object value = null;
        if (key != null) {
            for (PropertyResolver resolver : _resolvers) {
                value = resolver.resolveProperty(key);
                if (value != null) {
                    break;
                }
            }
        }
        return value;
    }

    /**
     * Creates a compact property resolver given an array of property resolvers, weeding out nulls and only compounding when necessary.
     * @param resolvers the property resolvers
     * @return the always not-null compact property resolver
     */
    public static PropertyResolver compact(PropertyResolver... resolvers) {
        CompoundPropertyResolver compound = new CompoundPropertyResolver(resolvers);
        return compound._resolvers.size() == 1 ? compound._resolvers.get(0) : compound;
    }

    /**
     * Creates a compact property resolver given a collection of property resolvers, weeding out nulls and only compounding when necessary.
     * @param resolvers the property resolvers
     * @return the always not-null compact property resolver
     */
    public static PropertyResolver compact(Collection<PropertyResolver> resolvers) {
        CompoundPropertyResolver compound = new CompoundPropertyResolver(resolvers);
        return compound._resolvers.size() == 1 ? compound._resolvers.get(0) : compound;
    }

}
