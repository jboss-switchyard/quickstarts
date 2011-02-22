/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
     * The Java type of the source format.
     * @return class representing the Java type for the from format.
     */
    Class<F> getFromType();

    /**
     * The Java type of the target format.
     * @return class representing the Java type for the from format.
     */
    Class<T> getToType();

    /**
     * The name of the from, or source, message.
     * @return from message
     */
    QName getFrom();

    /**
     * The name of the to, or target, message.
     * @return to message
     */
    QName getTo();
}
