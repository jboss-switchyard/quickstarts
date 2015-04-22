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
package org.switchyard.admin.base;

import javax.xml.namespace.QName;

import org.switchyard.admin.Transformer;
import org.switchyard.config.model.TypedModel;
import org.switchyard.config.model.transform.TransformModel;

/**
 * BaseTransformer
 * 
 * Base implementation for {@link Transformer}.
 * 
 * @author Rob Cernich
 */
public class BaseTransformer implements Transformer {

    private final QName _from;
    private final QName _to;
    private String _type;

    /**
     * Create a new BaseTransformer.
     * 
     * @param from the from type
     * @param to the to type
     * @param type the implementation type (e.g. java)
     */
    public BaseTransformer(QName from, QName to, String type) {
        _from = from;
        _to = to;
        _type = type;
    }
    
    /**
     * Create a new BaseTransformer from a config model.
     * @param config the transformer confing model
     */
    public BaseTransformer(TransformModel config) {
        _from = config.getFrom();
        _to = config.getTo();
        if (config instanceof TypedModel) {
            _type = ((TypedModel)config).getType();
        }
    }

    @Override
    public QName getFrom() {
        return _from;
    }

    @Override
    public QName getTo() {
        return _to;
    }

    @Override
    public String getType() {
        return _type;
    }

}
