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
