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

package org.switchyard.tools.forge.plugin;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.switchyard.tools.forge.GenericTestForge;

/**
 * Test for {@link SwitchyardFacet}.
 *
 * @author Mario Antollini
 */
public class ForgeSwitchyardTest extends GenericTestForge {

    private static final String SERVICE_TEST = "ForgeServiceTest";

    private static final String ENABLE_TRACING_MSG = "Message tracing has been enabled";

    private static final String DISABLE_TRACING_MSG = "Message tracing has been disabled";

    private static final String SHOW_CONFIG_MSG = "[Public]" 
        + System.getProperty("line.separator") 
        + System.getProperty("line.separator") 
        + "[Private]";

    private static String VERSION_MSG;

    /**
     * Constructor.
     */
    public ForgeSwitchyardTest() {
        VERSION_MSG = "SwitchYard version " + getSwitchyardVersion();
    }
    
    /**
     * The deployment method is where you references to classes, packages, and
     * configuration files are added via  Arquillian.
     * @return the Traditional JAR (Java Archive) structure
     */
    @Deployment
    public static JavaArchive getDeployment() {
        // The deployment method is where references to classes, packages, 
        // and configuration files are added via Arquillian.
        JavaArchive archive = AbstractShellTest.getDeployment();
        archive.addPackages(true, SwitchYardFacet.class.getPackage());
        return archive;
    }
    
    /**
     * The single test containing some test cases.
     */
    @Test
    public void runTest() {
        try {
            createTestService();
            testTraceMessages();
            testGetVersion();
            testShowConfig();
        
        } catch (Exception e) {
            System.out.println(getOutput());
            e.printStackTrace();
        }
    }
    
    /**
     * Tests the creation of a test service. 
     * @throws Exception if a problem occurs during execution
     */
    public void createTestService() throws Exception {
        resetOutputStream();
        getShell().execute("switchyard create-service-test --serviceName " + SERVICE_TEST);
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains("Created unit test " + SERVICE_TEST + "Test.java"));
    }
    
    /**
     * Tests the trace-messages command. 
     * @throws Exception if a problem occurs during execution
     */
    public void testTraceMessages() throws Exception {
        resetOutputStream();
        queueInputLines("Y");
        getShell().execute("switchyard trace-messages");
        Assert.assertTrue(getOutput().contains(ENABLE_TRACING_MSG));
        queueInputLines("n");
        getShell().execute("switchyard trace-messages");
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains(DISABLE_TRACING_MSG));
    }

    /**
     * Tests the get-version command.
     * @throws Exception if a problem occurs during execution
     */
    public void testGetVersion() throws Exception {
        resetOutputStream();
        getShell().execute("switchyard get-version");
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains(VERSION_MSG));
    }

    /**
     * Tests the show-config command.
     * @throws Exception if a problem occurs during execution
     */
    public void testShowConfig() throws Exception {
        resetOutputStream();
        getShell().execute("switchyard show-config");
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains(SHOW_CONFIG_MSG));
    }
    
}
