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

package org.switchyard.tools.forge.common;

import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.switchyard.config.model.composer.ContextMapperModel;
import org.switchyard.config.model.composer.MessageComposerModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.composite.v1.V1BindingModel;
import org.switchyard.config.model.composite.v1.V1CompositeServiceModel;
import org.switchyard.tools.forge.GenericTestForge;
import org.switchyard.tools.forge.bean.BeanFacet;
import org.switchyard.tools.forge.plugin.SwitchYardFacet;

/**
 * Test for {@link CommonFacet}.
 */
public class ForgeCommonTest extends GenericTestForge {

    private static final String BEAN_SERVICE = "ForgeBeanService";
    
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
        archive.addPackages(true, BeanFacet.class.getPackage());
        return archive;
    }

    /**
     * The single test containing some test cases.
     */
    @Test
    public void test() throws Exception {
        try {
            resetOutputStream();
            SwitchYardFacet switchYard = getProject().getFacet(SwitchYardFacet.class);
            CompositeServiceModel service = new V1CompositeServiceModel();
            service.setName(BEAN_SERVICE);
            service.addBinding(new V1BindingModel("forge"){});
            switchYard.getSwitchYardConfig().getComposite().addService(service);
            switchYard.saveConfig();
            getShell().execute("project install-facet switchyard.common");
            
            testAddContextMapper();
            testAddMessageComposer();
            System.out.println(getOutput());
        } catch (Exception e) {
            System.out.println(getOutput());
            throw e;
        }
    }
    
    public void testAddContextMapper() throws Exception {
        SwitchYardFacet switchYard = getProject().getFacet(SwitchYardFacet.class);

        String className = this.getClass().getName();
        String includes = "includes*";
        String excludes = "excludes*";
        String includeNamespaces = "urn:includes*:0.1.0";
        String excludeNamespaces = "urn:excludes*:0.1.0";
        queueInputLines("1");
        getShell().execute("common add-context-mapper --serviceName " + BEAN_SERVICE
                 + " --className " + className
                 + " --includes " + includes
                 + " --excludes " + excludes
                 + " --includeNamespaces " + includeNamespaces
                 + " --excludeNamespaces " + excludeNamespaces);
        
        ContextMapperModel mapper = switchYard.getCompositeService(BEAN_SERVICE).getBindings().get(0).getContextMapper();
        Assert.assertEquals(className, mapper.getClazz());
        Assert.assertEquals(includes, mapper.getIncludes());
        Assert.assertEquals(excludes, mapper.getExcludes());
        Assert.assertEquals(includeNamespaces, mapper.getIncludeNamespaces());
        Assert.assertEquals(excludeNamespaces, mapper.getExcludeNamespaces());
    }
    
    public void testAddMessageComposer() throws Exception {
        SwitchYardFacet switchYard = getProject().getFacet(SwitchYardFacet.class);

        String className = this.getClass().getName();
        queueInputLines("1");
        getShell().execute("common add-message-composer --serviceName " + BEAN_SERVICE + " --className " + className);
        
        MessageComposerModel composer = switchYard.getCompositeService(BEAN_SERVICE).getBindings().get(0).getMessageComposer();
        Assert.assertEquals(className, composer.getClazz());
    }

}
