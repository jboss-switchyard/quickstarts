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

package org.switchyard.component.common.selector.config.model.v1;

import javax.xml.namespace.QName;

import org.switchyard.component.common.selector.config.model.OperationSelectorModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * A version 1 OperationSelectorModel.
 */
public abstract class V1OperationSelectorModel extends BaseModel implements OperationSelectorModel {

    protected V1OperationSelectorModel(String type) {
        this(new QName(SwitchYardModel.DEFAULT_NAMESPACE, type == null ? OperationSelectorModel.OPERATION_SELECTOR : OperationSelectorModel.OPERATION_SELECTOR + '.' + type));
    }

    protected V1OperationSelectorModel(QName qname) {
        super(qname);
    }

    protected V1OperationSelectorModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

}
