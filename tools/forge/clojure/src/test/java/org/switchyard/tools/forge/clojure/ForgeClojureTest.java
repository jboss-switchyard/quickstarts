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
    public void test() throws Exception {
        try {
            //clojure-service create
            testCreateClojureService();
        } catch (Exception e) {
            System.out.println(getOutput());
            throw e;
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
