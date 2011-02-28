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

package org.switchyard.deploy;

import javax.xml.namespace.QName;

import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceReference;
import org.switchyard.config.model.Model;

/**
 * Activators allow components to participate in the deployment lifecycle of
 * a SwitchYard application.  The deployer has a list of activators that map
 * to implementation and binding types.  During deployment, the deployer will 
 * build a list of activators for known types and enlist each activator in the
 * lifecycle of the application.  The deployer takes care of dependency ordering
 * between services, references, and bindings and invokes Activator instances
 * in the appropriate order.
 */
public interface Activator {
    /**
     * Initialize a service or service reference based on the supplied
     * configuration.  Activator instances should attempt to validate 
     * configuration, policy, and any runtime constraints during init and fail
     * fast if there is a problem.
     * @param name name of the service or reference
     * @param config switchyard configuration for the service or reference
     * @return exchange handler to use for the service or reference.  In the case
     * of a reference, the handler will be used as the default callback handler
     * for all exchanges.
     */
    ExchangeHandler init(QName name, Model config);
    /**
     * Start the specified service or reference.
     * @param service service to start
     */
    void start(ServiceReference service);
    /**
     * Stop the specified service or reference.
     * @param service service to stop
     */
    void stop(ServiceReference service);
    /**
     * Destroy the specified service or reference.  Once destroyed, the activator
     * should be capable of launching a clean instance of this service or 
     * reference through an init() call (e.g. application redeployment).
     * @param service service to destroy
     */
    void destroy(ServiceReference service);
}
