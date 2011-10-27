/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.config.model.validate;

import javax.xml.namespace.QName;

import org.switchyard.config.model.Model;

/**
 * The "validate" configuration model.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public interface ValidateModel extends Model {

    /** The default "validate" namespace. */
    public static final String DEFAULT_NAMESPACE = "urn:switchyard-config:validate:1.0";

    /** The "validate" name. */
    public static final String VALIDATE = "validate";

    /** The "name" name. */
    public static final String NAME = "name";

    /**
     * Gets the parent validates model.
     * @return the parent validates model.
     */
    public ValidatesModel getValidates();

    /**
     * Gets the name attribute.
     * @return the name attribute
     */
    public QName getName();

    /**
     * Sets the name attribute.
     * @param name the name attribute
     * @return this ValidateModel (useful for chaining)
     */
    public ValidateModel setName(QName name);

}
