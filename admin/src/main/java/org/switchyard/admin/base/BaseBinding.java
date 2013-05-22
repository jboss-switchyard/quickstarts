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

import org.switchyard.admin.Binding;
import org.switchyard.deploy.Lifecycle;

/**
 * BaseBinding
 * 
 * Base implementation for {@link Binding}.
 * 
 * @author Rob Cernich
 */
public class BaseBinding extends BaseMessageMetricsAware implements Binding {

    private final BaseApplication _application;
    private final QName _serviceName;
    private final String _type;
    private final String _name;
    private final String _configuration;

    /**
     * Create a new BaseBinding.
     * 
     * @param application the containing application
     * @param serviceName the name of the service or reference providing this
     *            binding
     * @param type the binding's type (e.g. soap)
     * @param name the binding's name
     * @param configuration the binding's raw configuration
     */
    public BaseBinding(BaseApplication application, QName serviceName, String type, String name, String configuration) {
        _application = application;
        _serviceName = serviceName;
        _type = type;
        _name = name;
        _configuration = configuration;
    }

    @Override
    public String getType() {
        return _type;
    }

    @Override
    public String getConfiguration() {
        return _configuration;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public void start() {
        /*
         * TODO: should we verify that we could retrieve the service handler?
         * should we check the state first?
         */
        getGatewayLifecycle().start();
    }

    @Override
    public void stop() {
        /*
         * TODO: should we verify that we could retrieve the service handler?
         * should we check the state first?
         */
        getGatewayLifecycle().stop();
    }

    @Override
    public State getState() {
        final Lifecycle lifecycle = getGatewayLifecycle();
        return lifecycle == null ? State.NONE : lifecycle.getState();
    }

    private Lifecycle getGatewayLifecycle() {
        return _application.getDeployment().getGatwayLifecycle(_serviceName, _name);
    }
}
