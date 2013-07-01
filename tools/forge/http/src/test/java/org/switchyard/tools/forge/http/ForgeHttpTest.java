/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.switchyard.tools.forge.http;

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
 * Test for {@link HttpFacet}.
 *
 * @author Mario Antollini
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class ForgeHttpTest extends GenericTestForge {

    private static final String BEAN_SERVICE = "ForgeBeanService";
    
    private static final String BEAN_SERVICE_REFERENCEABLE = "ForgeBeanServiceReferenceable"; 
    
    private static final String HTTP_SUCCESS_MSG = "***SUCCESS*** Installed [switchyard.http] successfully";
    
    private static final String HTTP_BINDING_MSG = "Added binding.http to service " + BEAN_SERVICE;
    
    private static final String HTTP_REFERENCE_MSG = "Added binding.http to reference " + BEAN_SERVICE_REFERENCEABLE;
    
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
        archive.addPackages(true, HttpFacet.class.getPackage());
        archive.addPackages(true, BeanFacet.class.getPackage());
        return archive;
    }

    /**
     * The single test containing some test cases.
     */
    @Test
    public void test() throws Exception {
        try {
            //soap-binding bind-service
            testBindService();
            //camel-binding bind-reference
            testBindReference();
        } catch (Exception e) {
            System.out.println(getOutput());
            throw e;
        }
    }
    
    /**
     * Tests the creation of a rule service. 
     * @throws Exception if a problem occurs during execution
     */
    private void testBindService() throws Exception {
        resetOutputStream();
        getShell().execute("project install-facet switchyard.http");
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains(HTTP_SUCCESS_MSG));
        resetOutputStream();
        
        getShell().execute("project install-facet switchyard.bean");
        queueInputLines(BEAN_SERVICE);
        getShell().execute("bean-service create");

        mavenBuildSkipTest();

        getShell().execute("switchyard promote-service --serviceName " + BEAN_SERVICE);
        getShell().execute("http-binding bind-service --serviceName " + BEAN_SERVICE);
        
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains(HTTP_BINDING_MSG));
    }
    
    /**
     * Tests the "bind-reference" command. 
     * @throws Exception if a problem occurs during execution
     */
    private void testBindReference() throws Exception {
        resetOutputStream();

        getShell().execute("project install-facet switchyard.bean");
        getShell().execute("bean-service create --serviceName " + BEAN_SERVICE_REFERENCEABLE);
        getShell().execute("bean-reference create --beanName " + BEAN_SERVICE + " --referenceName " + BEAN_SERVICE_REFERENCEABLE + " --referenceBeanName " + BEAN_SERVICE_REFERENCEABLE);
        mavenBuildSkipTest();
        getShell().execute("switchyard promote-reference --referenceName " + BEAN_SERVICE_REFERENCEABLE);
        getShell().execute("http-binding bind-reference --address http://localhost:8080/http-binding/hello --referenceName " + BEAN_SERVICE_REFERENCEABLE);
        getShell().execute("switchyard show-config");
        
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains(HTTP_REFERENCE_MSG));
    }
        

}
