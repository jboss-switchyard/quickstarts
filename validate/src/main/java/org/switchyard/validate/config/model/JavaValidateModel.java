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

package org.switchyard.validate.config.model;

import org.switchyard.config.model.validate.ValidateModel;

/**
 * A "transform.java" configuration model.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public interface JavaValidateModel extends ValidateModel {

    /** The "java" name. */
    public static final String JAVA = "java";

    /** The "class" name. */
    public static final String CLASS = "class";

    /**
     * Gets the class attribute.
     * @return the class attribute
     */
    public String getClazz();

    /**
     * Sets the class attribute.
     * @param clazz the class attribute
     * @return this JavaValidateModel (useful for chaining)
     */
    public JavaValidateModel setClazz(String clazz);

}
