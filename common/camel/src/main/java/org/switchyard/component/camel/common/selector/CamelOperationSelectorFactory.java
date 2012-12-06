/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.common.selector;

import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.common.selector.OperationSelectorFactory;
import org.switchyard.selector.OperationSelector;

/**
 * A OperationSelectorFactory implementation for camel Message.
 */
public class CamelOperationSelectorFactory extends OperationSelectorFactory<CamelBindingData> {

    @Override
    public Class<CamelBindingData> getTargetClass() {
        return CamelBindingData.class;
    }

    @Override
    public Class<? extends OperationSelector<CamelBindingData>> getDefaultOperationSelectorClass() {
        return CamelOperationSelector.class;
    }

}
