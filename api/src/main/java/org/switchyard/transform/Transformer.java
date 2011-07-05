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

package org.switchyard.transform;

import javax.xml.namespace.QName;

/**
 * Handles transformation between message content types (e.g. Object -> XML) to
 * reconcile the type systems used by a service consumer and provider.  A
 * Transformer instance can be directly attached to an exchange or it can be
 * registered in the TransformerRegistry and loaded dynamically based on the
 * from and to message names.
 *
 * @param <F> Java type representing the from, or source, format
 * @param <T> Java type representing the to, or target, format
 */
public interface Transformer<F, T> {

    /**
     * Transforms the source format <code>F</code> to the target format
     * <code>T</code>.
     * @param from the object to be transformed
     * @return object in target format
     */
    T transform(F from);

    /**
     * Set the name of the from, or source, message type.
     * @param fromType From type.
     * @return a reference to the current Transformer.
     */
    Transformer<F, T> setFrom(QName fromType);

    /**
     * The name of the from, or source, message.
     * @return from message
     */
    QName getFrom();

    /**
     * Set the name of the to, or target, message type.
     * @param toType To type.
     * @return a reference to the current Transformer.
     */
    Transformer<F, T> setTo(QName toType);

    /**
     * The name of the to, or target, message.
     * @return to message
     */
    QName getTo();
    
    /**
     * The Java type of the source format.
     * @return class representing the Java type for the from format.
     */
    Class<F> getFromType();

    /**
     * The Java type of the target format.
     * @return class representing the Java type for the from format.
     */
    Class<T> getToType();
}
