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

package org.switchyard.tools.forge.bean;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.switchyard.tools.forge.GenericTestForge;
import org.switchyard.tools.forge.plugin.SwitchYardFacet;

/**
 * Test for {@link BeanFacet}.
 *
 * @author Mario Antollini
 */
public class ForgeBeanTest extends GenericTestForge {

    private static final String SERVICE_NAME = "ForgeBeanService";
    
    private static final String FOO_SERVICE_NAME = "FooBeanService";

    private static final String SUCCESS_MSG = "Created service implementation [" + SERVICE_NAME + "Bean.java]";
    
    private static final String REFERENCE_NAME = "RefName";
    
    private static final String REFERENCE_INTERFACE = "com.test.RefIntf";

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
        archive.addPackages(true, BeanFacet.class.getPackage());
        return archive;
    }

    /**
     * The single test containing some test cases.
     */
    @Test
    public void runTest() {
        try {
            testCreateBeanService();
            testAddReferencePositive();
            testAddReferenceNegative();
        } catch (Exception e) {
            System.out.println(getOutput());
            e.printStackTrace();
        }
    }
    
    /**
     * Tests the creation of a bean service. 
     * @throws Exception if a problem occurs during execution
     */
    public void testCreateBeanService() throws Exception {
        resetOutputStream();
        getDeployment().addPackages(true,BeanFacet.class.getPackage());
        getShell().execute("project install-facet switchyard.bean");
        queueInputLines(SERVICE_NAME);
        getShell().execute("bean-service create");
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains(SUCCESS_MSG));
    }
    
    /**
     * Tests creation of a reference. 
     * @throws Exception if a problem occurs during execution
     */
    public void testAddReferencePositive() throws Exception {
        resetOutputStream();
        build();
        getShell().execute("switchyard add-reference --referenceName " 
                + REFERENCE_NAME 
                + " --interfaceType java --interface " 
                + REFERENCE_INTERFACE 
                + "  --componentName " 
                + SERVICE_NAME);
        getShell().execute("switchyard show-config");
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains("Reference " + REFERENCE_NAME + " successfully added to component " + SERVICE_NAME));
        Assert.assertTrue(getOutput().contains("reference: " + REFERENCE_NAME));
        Assert.assertTrue(getOutput().contains("interface: " + REFERENCE_INTERFACE));
    }

    /**
     * Tests that an error is caught when adding a reference to a non-existing component.
     * @throws Exception if a problem occurs during execution
     */
    public void testAddReferenceNegative() throws Exception {
        resetOutputStream();
        getShell().execute("switchyard add-reference --referenceName " 
                + REFERENCE_NAME 
                + " --interfaceType java --interface " 
                + REFERENCE_INTERFACE 
                + "  --componentName " 
                + FOO_SERVICE_NAME);
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains("Component not found: " + FOO_SERVICE_NAME));
    }
    

}
