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
 * Filter classpath classes besed on Java Package.
 */
public class PackageFilter extends AbstractTypeFilter {

    private List<Package> _includes = new LinkedList<Package>();
    private List<Package> _excludes = new LinkedList<Package>();

    /**
     * Public constructor.
     * @param includes packages to be included
     */
    public PackageFilter(Package... includes) {
        _includes.addAll(Arrays.asList(includes));
    }
    
    /**
     * Add a Package to be included.
     * @param pkg a Package to include
     * @return a reference to this filter instance
     */
    public PackageFilter addInclude(Package pkg) {
        _includes.add(pkg);
        return this;
    }

    /**
     * Add a Package to be excluded.
     * @param pkg a Package to exclude
     * @return a reference to this filter instance
     */
    public PackageFilter addExclude(Package pkg) {
        _excludes.add(pkg);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(Class<?> clazz) {
        Package pkg = clazz.getPackage();
        if (_includes.isEmpty()) {
            return !_excludes.contains(pkg);
        } else {
            return _includes.contains(pkg) && !_excludes.contains(pkg);
        }
    }
}
