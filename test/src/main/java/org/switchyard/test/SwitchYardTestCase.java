/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceDomain;
import org.switchyard.deploy.internal.AbstractDeployment;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.InOutService;
import org.switchyard.transform.Transformer;

import javax.xml.namespace.QName;

/**
 * Base class for writing SwitchYard tests.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public abstract class SwitchYardTestCase {

    /**
     * The deployment.
     */
    private AbstractDeployment _deployment;

    /**
     * Create and initialise the deployment.
     * @throws Exception creating the deployment.
     */
    @Before
    public void deploy() throws Exception {
        if (_deployment == null) {
            _deployment = createDeployment();
            _deployment.init();
        }
    }

    /**
     * Undeploy the deployment.
     */
    @After
    public void undeploy() {
        assertDeployed();
        _deployment.destroy();
    }

    /**
     * Create the deployment instance.
     * @return The deployment instance.
     * @throws Exception creating the deployment.
     */
    protected AbstractDeployment createDeployment() throws Exception {
        return new AbstractDeployment() {
            @Override
            public void destroy() {
            }
        };
    }

    /**
     * Get the ServiceDomain.
     * @return The service domain.
     */
    public ServiceDomain getServiceDomain() {
        assertDeployed();
        return _deployment.getDomain();
    }

    /**
     * Register an IN_OUT Service.
     * <p/>
     * Registers a {@link MockHandler} as the service handler.
     *
     * @param serviceName The Service name.
     * @return The {@link MockHandler} service handler.
     */
    protected MockHandler registerInOutService(String serviceName) {
        MockHandler handler = new MockHandler();
        getServiceDomain().registerService(new QName(serviceName), handler, new InOutService());
        return handler;
    }

    /**
     * Register an IN_OUT Service.
     *
     * @param serviceName The Service name.
     * @param serviceHandler The service handler.
     */
    protected void registerInOutService(String serviceName, ExchangeHandler serviceHandler) {
        getServiceDomain().registerService(new QName(serviceName), serviceHandler, new InOutService());
    }

    /**
     * Register an IN_ONLY Service.
     * <p/>
     * Registers a {@link MockHandler} as the fault service handler.
     *
     * @param serviceName The Service name.
     * @return The {@link MockHandler} service fault handler.
     */
    protected MockHandler registerInOnlyService(String serviceName) {
        MockHandler handler = new MockHandler();
        getServiceDomain().registerService(new QName(serviceName), handler, new InOnlyService());
        return handler;
    }

    /**
     * Register an IN_ONLY Service.
     *
     * @param serviceName The Service name.
     * @param serviceHandler The service handler.
     */
    protected void registerInOnlyService(String serviceName, ExchangeHandler serviceHandler) {
        getServiceDomain().registerService(new QName(serviceName), serviceHandler, new InOnlyService());
    }

    /**
     * Add a Transformer instance.
     * @param transformer The transformer instance.
     */
    public void addTransformer(Transformer transformer) {
        getServiceDomain().getTransformerRegistry().addTransformer(transformer);
    }

    /**
     * Create a new {@link Invoker} instance for invoking a Service in the test ServiceDomain.
     * @param serviceName The target Service name.
     * @return The invoker instance.
     */
    protected Invoker newInvoker(QName serviceName) {
        return new Invoker(getServiceDomain(), serviceName);
    }

    /**
     * Create a new {@link Invoker} instance for invoking a Service in the test ServiceDomain.
     * @param serviceName The target Service name.  Can be a serialized {@link QName}.  Can also
     * include the operation name e.g. "OrderManagementService.createOrder".
     * @return The invoker instance.
     */
    protected Invoker newInvoker(String serviceName) {
        return new Invoker(getServiceDomain(), serviceName);
    }

    private void assertDeployed() {
        if (_deployment == null) {
            Assert.fail("TestCase deployment not yet deployed.  You may need to make an explicit call to the deploy() method.");
        }
    }
}
