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
package org.switchyard.transform.internal;

import javax.xml.namespace.QName;

/**
 * Transformer data types.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class TransformerTypes {

    private QName _from;
    private QName _to;

    /**
     * Public constructor.
     *
     * @param from From type.
     * @param to   To type.
     */
    TransformerTypes(QName from, QName to) {
        this._from = from;
        this._to = to;
    }

    /**
     * Get from.
     *
     * @return from.
     */
    public QName getFrom() {
        return _from;
    }

    /**
     * Get to.
     *
     * @return to.
     */
    public QName getTo() {
        return _to;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s [from=%s, to=%s]", getClass().getSimpleName(), getFrom(), getTo());
    }
}
