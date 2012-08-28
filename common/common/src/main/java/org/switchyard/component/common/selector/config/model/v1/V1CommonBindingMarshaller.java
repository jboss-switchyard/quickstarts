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

import org.switchyard.component.common.selector.config.model.JavaOperationSelectorModel;
import org.switchyard.component.common.selector.config.model.OperationSelectorModel;
import org.switchyard.component.common.selector.config.model.RegexOperationSelectorModel;
import org.switchyard.component.common.selector.config.model.XPathOperationSelectorModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseMarshaller;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;

/**
 * A common marshaller which supports operationSelector.
 */
public class V1CommonBindingMarshaller extends BaseMarshaller {

    private static final String SELECTOR_STATIC = OperationSelectorModel.OPERATION_SELECTOR;
    private static final String SELECTOR_XPATH = OperationSelectorModel.OPERATION_SELECTOR + "." + XPathOperationSelectorModel.XPATH;
    private static final String SELECTOR_REGEX = OperationSelectorModel.OPERATION_SELECTOR + "." + RegexOperationSelectorModel.REGEX;
    private static final String SELECTOR_JAVA = OperationSelectorModel.OPERATION_SELECTOR + "." + JavaOperationSelectorModel.JAVA;

    /**
     * Constructs a new V1OperationSelectorMarshaller with the specified Descriptor.
     * @param desc the Descriptor
     */
    public V1CommonBindingMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Model read(Configuration config) {
        String name = config.getName();
        Descriptor desc = getDescriptor();

        if (name.equals(SELECTOR_STATIC)) {
            return new V1StaticOperationSelectorModel(config, desc);
        } else if (name.equals(SELECTOR_XPATH)) {
            return new V1XPathOperationSelectorModel(config, desc);
        } else if (name.equals(SELECTOR_REGEX)) {
            return new V1RegexOperationSelectorModel(config, desc);
        } else if (name.equals(SELECTOR_JAVA)) {
            return new V1JavaOperationSelectorModel(config, desc);
        }

        return null;
    }

}
