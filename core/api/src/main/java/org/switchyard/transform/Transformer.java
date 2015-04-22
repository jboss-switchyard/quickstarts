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
