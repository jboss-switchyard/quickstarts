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

import org.switchyard.admin.Validator;
import org.switchyard.config.model.TypedModel;
import org.switchyard.config.model.validate.ValidateModel;

/**
 * BaseValidator
 * 
 * Base implementation for {@link Validator}.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class BaseValidator implements Validator {

    private final QName _name;
    private String _type;

    /**
     * Create a new BaseTransformer.
     * 
     * @param name the name of type
     * @param type the implementation type (e.g. java)
     */
    public BaseValidator(QName name, String type) {
        _name = name;
        _type = type;
    }

    /**
     * Create a new BaseValidator from a config model.
     * @param config the the validator config model
     */
    public BaseValidator(ValidateModel config) {
        _name = config.getName();
        if (config instanceof TypedModel) {
            _type = ((TypedModel)config).getType();
        }
    }

    @Override
    public QName getName() {
        return _name;
    }

    @Override
    public String getType() {
        return _type;
    }

}
