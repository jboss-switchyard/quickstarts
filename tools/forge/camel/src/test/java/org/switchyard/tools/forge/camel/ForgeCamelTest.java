/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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

package org.switchyard.tools.forge.camel;


import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.switchyard.tools.forge.GenericTestForge;
import org.switchyard.tools.forge.bean.BeanFacet;
import org.switchyard.tools.forge.common.CommonFacet;
import org.switchyard.tools.forge.plugin.SwitchYardFacet;

/**
 * Test for {@link CamelFacet}.
 *
 * @author Mario Antollini
 */
public class ForgeCamelTest extends GenericTestForge {

    private static final String CAMEL_SERVICE = "ForgeCamelService";
    
    private static final String BEAN_SERVICE = "ForgeBeanService";
    
    private static final String BEAN_SERVICE_REFERENCEABLE = "ForgeBeanServiceReferenceable"; 
    
    private static final String CAMEL_SRV_SUCCESS_MSG = "Created Camel service " + CAMEL_SERVICE;

    private static final String CAMEL_BINDING_SUCCESS_MSG = "Added binding.camel to service " + BEAN_SERVICE;
    
    private static final String CAMEL_REF_SUCCESS_MSG = "Added binding.camel to reference " + BEAN_SERVICE_REFERENCEABLE;
    
    /**
     * The deployment method is where references to classes, packages, and
     * configuration files are added via  Arquillian.
     * @return the Traditional JAR (Java Archive) structure
     */
    @Deployment
    public static JavaArchive getDeployment() {
        
        // The deployment method is where references to classes, packages, 
        // and configuration files are added via Arquillian.
        JavaArchive archive = AbstractShellTest.getDeployment();
        archive.addPackages(true, SwitchYardFacet.class.getPackage());
        archive.addPackages(true, CommonFacet.class.getPackage());
        archive.addPackages(true, CamelFacet.class.getPackage());
        archive.addPackages(true, BeanFacet.class.getPackage());
        return archive;
    }

    /**
     * The single test containing some test cases.
     */
    @Test
    public void test() throws Exception {
        try {
            //camel-service create
            testCreateCamelService();
            //camel-binding bind-service
            testBindService();
            //camel-binding bind-reference
            testBindReference();
        } catch (Exception e) {
            System.out.println(getOutput());
            throw e;
        }
    }
    
    /**
     * Tests the creation of a camel service.
     * @throws Exception if a problem occurs during execution
     */
    private void testCreateCamelService() throws Exception {
        resetOutputStream();
        getShell().execute("project install-facet switchyard.camel");
        getShell().execute("camel-service create --serviceName " + CAMEL_SERVICE);
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains(CAMEL_SRV_SUCCESS_MSG));
    }

    /**
     * Tests the camel-binding command. 
     * @throws Exception if a problem occurs during execution
     */
    private void testBindService() throws Exception {
        resetOutputStream();
        getShell().execute("project install-facet switchyard.bean");
        getShell().execute("bean-service create --serviceName " + BEAN_SERVICE);
        mavenBuildSkipTest();
        getShell().execute("switchyard promote-service --serviceName " + BEAN_SERVICE);
        getShell().execute("camel-binding bind-service --serviceName " + BEAN_SERVICE + " --configURI 'file://target/input?fileName=test.txt'");
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains(CAMEL_BINDING_SUCCESS_MSG));
    }
    
    /**
     * Tests the bind-reference command.
     * @throws Exception if a problem occurs during execution
     */
    private void testBindReference() throws Exception {
        resetOutputStream();
        getShell().execute("project install-facet switchyard.bean");
        getShell().execute("bean-service create --serviceName " + BEAN_SERVICE_REFERENCEABLE);
        getShell().execute("bean-reference create --beanName " + BEAN_SERVICE + " --referenceName " + BEAN_SERVICE_REFERENCEABLE + " --referenceBeanName " + BEAN_SERVICE_REFERENCEABLE);
        mavenBuildSkipTest();
        getShell().execute("switchyard promote-reference --referenceName " + BEAN_SERVICE_REFERENCEABLE);
        queueInputLines("file://target/input?fileName=test.txt");
        getShell().execute("camel-binding bind-reference --referenceName " + BEAN_SERVICE_REFERENCEABLE);
        getShell().execute("switchyard show-config");
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains(CAMEL_REF_SUCCESS_MSG));
    }

    
}
