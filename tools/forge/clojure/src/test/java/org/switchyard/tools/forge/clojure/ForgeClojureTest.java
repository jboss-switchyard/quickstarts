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

package org.switchyard.tools.forge.clojure;

import java.io.File;

import junit.framework.Assert;

import org.eclipse.core.runtime.Path;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.switchyard.tools.forge.GenericTestForge;
import org.switchyard.tools.forge.plugin.SwitchYardFacet;

/**
 * Test for {@link ClojureFacet}.
 *
 * @author Mario Antollini
 */
public class ForgeClojureTest extends GenericTestForge {

    private static final String CLOJURE_SERVICE1 = "ForgeClojureService1";
    
    private static final String CLOJURE_SUCCESS_MSG1 = "Created Clojure implementation " + CLOJURE_SERVICE1;
    
    private static final String CLOJURE_SERVICE2 = "ForgeClojureService2";
    
    private static final String CLOJURE_SUCCESS_MSG2 = "Created Clojure implementation " + CLOJURE_SERVICE2;
    
    private static final String CLOJURE_SERVICE3 = "ForgeClojureService3";
    
    private static final String CLOJURE_SUCCESS_MSG3 = "Created Clojure implementation " + CLOJURE_SERVICE3;
    
    private static final String CLOJURE_SERVICE4 = "ForgeClojureService4";
    
    private static final String CLOJURE_SUCCESS_MSG4 = "Created Clojure implementation " + CLOJURE_SERVICE4;
    
    private static final String CLOJURE_SERVICE5 = "ForgeClojureService5";
    
    private static final String CLOJURE_SUCCESS_MSG5 = "Created Clojure implementation " + CLOJURE_SERVICE5;
    
    private static final String FOO_CONFIG_FILE = "src" 
        + Path.SEPARATOR 
        + "main" 
        + Path.SEPARATOR 
        + "resources" 
        + Path.SEPARATOR + "foo_config.txt";

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
        archive.addPackages(true, ClojureFacet.class.getPackage());
        return archive;
    }

    /**
     * The single test containing some test cases.
     */
    @Test
    public void test() {
        try {
            //clojure-service create
            testCreateClojureService();
        } catch (Exception e) {
            System.out.println(getOutput());
            e.printStackTrace();
        }
    }

    /**
     * Tests the creation of a clojure service.
     * @throws Exception if a problem occurs during execution
     */
    private void testCreateClojureService() throws Exception {
        getShell().execute("project install-facet switchyard.clojure");
    
        File configFile = new File("" + FOO_CONFIG_FILE);
        configFile.createNewFile();
        getShell().execute("clojure-service create --serviceName " + CLOJURE_SERVICE1 + " --inlineScript " + FOO_CONFIG_FILE);
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains(CLOJURE_SUCCESS_MSG1));
        
        getShell().execute("clojure-service create --serviceName " + CLOJURE_SERVICE2 + " --emptyInlineScript");
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains(CLOJURE_SUCCESS_MSG2));
        
        getShell().execute("clojure-service create --serviceName " + CLOJURE_SERVICE3 + " --externalScriptPath file.config");
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains(CLOJURE_SUCCESS_MSG3));
        
        getShell().execute("clojure-service create --serviceName " + CLOJURE_SERVICE4 + " --emptyExternalScriptPath");
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains(CLOJURE_SUCCESS_MSG4));
        
        getShell().execute("clojure-service create --serviceName " + CLOJURE_SERVICE5 + " --externalScriptPath file.config --injectExchange");
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains(CLOJURE_SUCCESS_MSG5));
        
    }

}
