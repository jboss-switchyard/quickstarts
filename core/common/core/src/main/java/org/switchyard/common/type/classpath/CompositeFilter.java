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

package org.switchyard.common.type.classpath;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Composite Filter holds multiple classpath filters.
 */
public class CompositeFilter extends AbstractTypeFilter {

    private List<AbstractTypeFilter> _filters = new LinkedList<AbstractTypeFilter>();

    /**
     * Public constructor.
     * @param filters filters to be held
     */
    public CompositeFilter(AbstractTypeFilter... filters) {
        _filters.addAll(Arrays.asList(filters));
    }
    
    /**
     * Add a filter.
     * @param filter a filter to add
     * @return a reference to this filter instance
     */
    public CompositeFilter addFilter(AbstractTypeFilter filter) {
        _filters.add(filter);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(Class<?> clazz) {
        for (AbstractTypeFilter filter : _filters) {
            if (!filter.matches(clazz)) {
                return false;
            }
        }
        return true;
    }
}
