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

package org.switchyard.validate;

import javax.xml.namespace.QName;

/**
 * Handles validation for message content to ensure type and structure is
 * suitable for consumer. A Validator instance can be directly attached to
 * an exchange or it can be registered in the ValidatorRegistry and loaded
 * dynamically based on the message name.
 *
 * @param <T> Java type representing the content to be validated
 */
public interface Validator<T> {

    /**
     * Validates the content.
     * <code>T</code>.
     * @param content the content to be validated
     * @return true if the content is valid, otherwise false
     */
    ValidationResult validate(T content);

    /**
     * Set the name of the subject for validation.
     * @param name name of the subject for validation.
     * @return a reference to the current Validator.
     */
    Validator<T> setName(QName name);

    /**
     * Return the name of the subject for validation.
     * @return from message
     */
    QName getName();

    /**
     * Return the Java type of the content to be validated.
     * @return class representing the Java type for the content to be validated
     */
    Class<T> getType();
}
