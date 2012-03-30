/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.test.mixins.jca;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jboss.jca.core.spi.rar.ResourceAdapterRepository;
import org.jboss.jca.deployers.fungal.AbstractFungalDeployment;
import org.jboss.jca.deployers.fungal.RAConfiguration;
import org.jboss.jca.deployers.fungal.RADeployer;
import org.jboss.jca.embedded.Embedded;
import org.jboss.jca.embedded.EmbeddedFactory;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;

import com.github.fungal.api.Kernel;
import com.github.fungal.api.deployer.MainDeployer;
import com.github.fungal.spi.deployers.Deployer;
/**
 * Handle dirty hack against IronJacamar embedded and fungal kernel.
 * TODO eliminate this class if IronJacamar embedded gets APIs for these stuffs
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class SwitchYardIronJacamarHandler {
    private final Embedded _embedded;
    private Kernel _kernel;
    private MainDeployer _mainDeployer;
    private ResourceAdapterRepository _resourceAdapterRepository;
    private List<ResourceAdapterArchive> _deployments = new ArrayList<ResourceAdapterArchive>();
    
    /**
     * Constructor.
     * 
     * @throws Exception when it fails to instantiate the {@link Embedded}
     */
    public SwitchYardIronJacamarHandler() throws Exception {
        _embedded = EmbeddedFactory.create();
    }

    /**
     * Start the IronJacamar embedded.
     * 
     * @throws Throwable when it fails to start
     */
    public void startup() throws Throwable {
        _embedded.startup();
        _kernel = getFieldValue(_embedded, Kernel.class, "kernel");
        _mainDeployer = _kernel.getMainDeployer();
    }

    /**
     * Deploy a {@link ResourceAdapterArchive}.
     * 
     * @param raa {@link ResourceAdapterArchive} to deploy.
     * @throws Throwable failed to deploy
     */
    public void deploy(ResourceAdapterArchive raa) throws Throwable {
        _embedded.deploy(raa);
        _deployments.add(raa);
    }
    
    /**
     * Shutdown the IronJacamar embedded.
     * 
     * @throws Throwable when it fails to shutdown
     */
    public void shutdown() throws Throwable {
        for (ResourceAdapterArchive raa : _deployments) {
            _embedded.undeploy(raa);
        }
        
        _embedded.shutdown();
        _kernel = null;
        _mainDeployer = null;
    }
    
    /**
     * Get {@link Embedded}.
     * 
     * @return {@link Embedded} instance
     */
    public Embedded getEmbeddedInstance() {
        return _embedded;
    }
    
    /**
     * Get {@link ResourceAdapterRepository}.
     * 
     * @return {@link ResourceAdapterRepository} instance
     * @throws Exception when it fails to acquire
     */
    public ResourceAdapterRepository getResourceAdapterRepository() throws Exception {
        if (_resourceAdapterRepository != null) {
            return _resourceAdapterRepository;
        }
        
        Object deployers = getFieldValue(_mainDeployer, Object.class, "deployers");
        Method getDeployersMethod = deployers.getClass().getDeclaredMethod("getDeployers", new Class[0]);
        getDeployersMethod.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<Deployer> deployerList = (List<Deployer>) getDeployersMethod.invoke(deployers, new Object[0]);
        getDeployersMethod.setAccessible(false);
        
        RADeployer radeployer = null;
        for (Deployer deployer : deployerList) {
            if (deployer instanceof RADeployer) {
                radeployer = RADeployer.class.cast(deployer);
                break;
            }
        }

        _resourceAdapterRepository = ((RAConfiguration)radeployer.getConfiguration()).getResourceAdapterRepository();
        return _resourceAdapterRepository;
    }
    
    /**
     * Get ResourceAdapter uniqueId.
     * 
     * @param raName ResourceAdapter name to find
     * @return uniqueId
     * @throws Exception failed to acquire
     */
    public String getResourceAdapterIdentifier(String raName) throws Exception {
        List<?> deployments = getFieldValue(_kernel, List.class, "deployments");
        for (Object deploy : deployments) {
            if (deploy instanceof AbstractFungalDeployment) {
                if (getFieldValue(deploy, boolean.class, "activator")
                        && getFieldValue(deploy, String.class, "deploymentName").equals(raName)) {
                    return getFieldValue(deploy, String.class, "raKey");
                }
            }
        }
        return null;
    }
    
    private <T> T getFieldValue(Object target, Class<T> type, String name) throws Exception {
        Class<?> targetClass = target.getClass();
        while (targetClass != Object.class) {
            try {
                Field f = targetClass.getDeclaredField(name);
            if (type == Object.class || f.getType().isAssignableFrom(type)) {
                f.setAccessible(true);
                @SuppressWarnings("unchecked")
                T val = (T) f.get(target);
                f.setAccessible(false);
                return val;
            }
            } catch (Exception e) {
                e.getMessage(); // ignore and check the super class
            }
            targetClass = targetClass.getSuperclass();
        }

        return null;
    }
    
}
