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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Filter classpath classes based on presence of an annotation.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class IsAnnotationPresentFilter extends AbstractTypeFilter {

    private List<Class<? extends Annotation>> _searchTypes = 
            new LinkedList<Class<? extends Annotation>>();

    /**
     * Public constructor.
     * @param searchType The Java type to search for.
     */
    public IsAnnotationPresentFilter(Class<? extends Annotation> searchType) {
        _searchTypes.add(searchType);
    }
    
    /**
     * Add a search type to this filter.
     * @param type the annotation type to filter for
     * @return a reference to this filter instance
     */
    public IsAnnotationPresentFilter addType(Class<? extends Annotation> type) {
        _searchTypes.add(type);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(Class<?> clazz) {
        // check for class-level annotation
        for (Class<? extends Annotation> annotation : _searchTypes) {
            if (clazz.isAnnotationPresent(annotation)) {
                return true;
            }
        }
        
        // check the fields
        for (Class<? extends Annotation> annotation : _searchTypes) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(annotation)) {
                    return true;
                }
            }
        }
        
        // not found
        return false;
    }

    /**
     * Is the Java method a filter match.
     * @param method The Java method to be checked.
     * @return true if the Java method is a match, otherwise false.
     */
    public boolean matches(Method method) {
        for (Class<? extends Annotation> annotation : _searchTypes) {
            if (method.isAnnotationPresent(annotation)) {
                return true;
            }
        }
        
        // not found
        return false;
    }

}
