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
package org.switchyard.component.camel.deploy;

import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.component.camel.common.deploy.BaseBindingActivator;
import org.switchyard.component.camel.common.deploy.BaseBindingComponent;
import org.switchyard.component.camel.model.v1.V1CamelImplementationModel;

/**
 * An implementation of Camel component.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class CamelComponent extends BaseBindingComponent {

    /**
     * Default constructor.
     */
    public CamelComponent() {
        super("CamelComponent", V1CamelImplementationModel.CAMEL);
    }

    @Override
    protected BaseBindingActivator createActivator(SwitchYardCamelContext context, String... types) {
        return new CamelActivator(context, types);
    }

}
