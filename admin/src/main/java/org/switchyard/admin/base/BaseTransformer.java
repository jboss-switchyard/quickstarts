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
