/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.camel.core.deploy;

import org.switchyard.component.camel.common.deploy.BaseBindingComponent;
import org.switchyard.component.camel.core.model.direct.v1.V1CamelDirectBindingModel;
import org.switchyard.component.camel.core.model.mock.v1.V1CamelMockBindingModel;
import org.switchyard.component.camel.core.model.seda.v1.V1CamelSedaBindingModel;
import org.switchyard.component.camel.core.model.timer.v1.V1CamelTimerBindingModel;
import org.switchyard.component.camel.core.model.v1.V1CamelBindingModel;

/**
 * Camel core component. Supports camel-core bindings.
 */
public class CamelCoreComponent extends BaseBindingComponent {

    /**
     * Creates new core binding component.
     */
    public CamelCoreComponent() {
        super("CamelCoreComponent",
            V1CamelBindingModel.URI,
            V1CamelDirectBindingModel.DIRECT,
            V1CamelSedaBindingModel.SEDA,
            V1CamelTimerBindingModel.TIMER,
            V1CamelMockBindingModel.MOCK
        );
    }

}
