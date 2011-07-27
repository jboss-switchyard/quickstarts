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
package org.switchyard.as7.extension.services;

import org.jboss.as.naming.context.NamespaceContextSelector;
import org.jboss.logging.Logger;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;
import org.switchyard.admin.base.BaseSwitchYard;
import org.switchyard.admin.base.SwitchYardBuilder;
import org.switchyard.as7.extension.deployment.SwitchYardDeployment;

/**
 * The SwitchYard service.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SwitchYardService implements Service<SwitchYardDeployment> {

    private static final Logger LOG = Logger.getLogger("org.switchyard");

    /**
     * Represents a SwitchYard service name.
     */
    public static final ServiceName SERVICE_NAME = ServiceName.of("SwitchYardService");

    private final InjectedValue<NamespaceContextSelector> _namespaceSelector = new InjectedValue<NamespaceContextSelector>();
    private SwitchYardDeployment _switchyardDeployment;
    private SwitchYardBuilder _switchYardBuilder;

    /**
     * Constructs a SwitchYard service.
     * 
     * @param switchyardDeployment the deployment instance
     */
    public SwitchYardService(SwitchYardDeployment switchyardDeployment) {
        _switchyardDeployment = switchyardDeployment;
    }

    @Override
    public SwitchYardDeployment getValue() throws IllegalStateException,
            IllegalArgumentException {
        return null;
    }

    @Override
    public void start(StartContext context) throws StartException {
        try {
            NamespaceContextSelector.pushCurrentSelector(_namespaceSelector.getValue());
            LOG.info("Starting SwitchYard service");

            ServiceController<?> adminService = context.getController().getServiceContainer()
                    .getService(SwitchYardAdminService.SERVICE_NAME);
            if (adminService != null) {
                BaseSwitchYard switchYard = BaseSwitchYard.class.cast(adminService.getValue());
                if (switchYard != null) {
                    _switchYardBuilder = new SwitchYardBuilder(switchYard);
                    _switchyardDeployment.setDeploymentListener(_switchYardBuilder);
                }
            }

            _switchyardDeployment.start();
        } catch (Exception e) {
            try {
                _switchyardDeployment.stop();
            } catch (Exception ex) {
                LOG.error(ex);
            }
            throw new StartException(e);
        } finally {
            NamespaceContextSelector.popCurrentSelector();
        }
    }

    @Override
    public void stop(StopContext context) {
        _switchyardDeployment.stop();
        if (_switchYardBuilder != null) {
            _switchyardDeployment.removeDeploymentListener(_switchYardBuilder);
            _switchYardBuilder = null;
        }
    }

    /**
     * Injection point for NamespaceContextSelector.
     * 
     * @return the NamespaceContextSelector
     */
    public InjectedValue<NamespaceContextSelector> getNamespaceSelector() {
        return _namespaceSelector;
    }

}
