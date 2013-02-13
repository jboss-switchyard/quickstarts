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

package org.switchyard.transform.config.model;

import org.switchyard.config.model.transform.TransformModel;

/**
 * A "transform.java" configuration model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface JavaTransformModel extends TransformModel {

    /** The "java" name. */
    public static final String JAVA = "java";

    /** The "class" name. */
    public static final String CLASS = "class";

    /** The "bean" name. */
    public static final String BEAN = "bean";
    
    /**
     * Gets the class attribute.
     * @return the class attribute
     */
    public String getClazz();

    /**
     * Sets the class attribute.
     * @param clazz the class attribute
     * @return this JavaTransformModel (useful for chaining)
     */
    public JavaTransformModel setClazz(String clazz);

    /**
     * Gets the bean attribute.
     * @return the bean attribute
     */
    public String getBean();
    
    /**
     * Sets the bean attribute.
     * @param bean the bean attribute
     * @return this JavaTransformModel (useful for chaining)
     */
    public JavaTransformModel setBean(String bean);
}
