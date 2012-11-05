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

package org.switchyard.tools.forge.resteasy;

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
 * Test for {@link RESTEasyFacet}.
 *
 * @author Mario Antollini
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class ForgeRESTEasyTest extends GenericTestForge {

    private static final String BEAN_SERVICE = "ForgeBeanService";
    
    private static final String BEAN_SERVICE_REFERENCEABLE = "ForgeBeanServiceReferenceable"; 
    
    private static final String REST_SUCCESS_MSG = "***SUCCESS*** Installed [switchyard.rest] successfully";
    
    private static final String REST_BINDING_MSG = "Added binding.rest to service " + BEAN_SERVICE;
    
    private static final String REST_REFERENCE_MSG = "Added binding.rest to reference " + BEAN_SERVICE_REFERENCEABLE;
    
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
        archive.addPackages(true, RESTEasyFacet.class.getPackage());
        archive.addPackages(true, BeanFacet.class.getPackage());
        return archive;
    }

    /**
     * The single test containing some test cases.
     */
    @Test
    public void test() throws Exception {
        try {
            //rest-binding bind-service
            testBindService();
            //rest-binding bind-reference
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
        getShell().execute("project install-facet switchyard.rest");
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains(REST_SUCCESS_MSG));
        resetOutputStream();
        
        getShell().execute("project install-facet switchyard.bean");
        queueInputLines(BEAN_SERVICE);
        getShell().execute("bean-service create");

        mavenBuildSkipTest();

        getShell().execute("switchyard promote-service --serviceName " + BEAN_SERVICE);
        queueInputLines("dummy.TestResource");
        getShell().execute("rest-binding bind-service --serviceName " + BEAN_SERVICE);
        
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains(REST_BINDING_MSG));
    }
    
    /**
     * Tests the "bind-reference" command. 
     * @throws Exception if a problem occurs during execution
     */
    private void testBindReference() throws Exception {
        resetOutputStream();

        getShell().execute("bean-service create --serviceName " + BEAN_SERVICE_REFERENCEABLE);
        getShell().execute("bean-reference create --beanName " + BEAN_SERVICE + " --referenceName " + BEAN_SERVICE_REFERENCEABLE + " --referenceBeanName " + BEAN_SERVICE_REFERENCEABLE);
        mavenBuildSkipTest();
        getShell().execute("switchyard promote-reference --referenceName " + BEAN_SERVICE_REFERENCEABLE);
        queueInputLines("dummy.TestResource");
        getShell().execute("rest-binding bind-reference --referenceName " + BEAN_SERVICE_REFERENCEABLE);
        getShell().execute("switchyard show-config");
        
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains(REST_REFERENCE_MSG));
    }
        

}
