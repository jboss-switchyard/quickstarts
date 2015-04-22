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
package org.switchyard.component.rules.session;

import javax.xml.namespace.QName;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.switchyard.ServiceDomain;
import org.switchyard.component.common.knowledge.runtime.KnowledgeRuntimeManager;
import org.switchyard.component.common.knowledge.runtime.KnowledgeRuntimeManagerFactory;
import org.switchyard.component.common.knowledge.runtime.KnowledgeRuntimeManagerType;
import org.switchyard.component.rules.config.model.RulesComponentImplementationModel;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.deploy.ServiceDomainManager;

/**
 * Tests Rules sessions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class RulesSessionTests {

    private static final String GOOD_RESOURCES = "/org/switchyard/component/rules/session/RulesSessionTests-GoodResources.xml";
    private static final String BAD_RESOURCES = "/org/switchyard/component/rules/session/RulesSessionTests-BadResources.xml";

    private ModelPuller<SwitchYardModel> _puller;

    @Before
    public void before() throws Exception {
        _puller = new ModelPuller<SwitchYardModel>();
    }

    @After
    public void after() throws Exception {
        _puller = null;
    }

    @Test
    public void testGoodResources() throws Exception {
        Throwable t = doTestResources(GOOD_RESOURCES);
        //t.printStackTrace();
        Assert.assertNull(t);
    }

    @Test
    public void testBadResources() throws Exception {
        Throwable t = doTestResources(BAD_RESOURCES);
        //t.printStackTrace();
        Assert.assertNotNull(t);
    }

    private Throwable doTestResources(String xml) {
        try {
            ClassLoader loader = getClass().getClassLoader();
            SwitchYardModel switchyardModel = _puller.pull(xml, loader);
            RulesComponentImplementationModel implementationModel = (RulesComponentImplementationModel)switchyardModel.getComposite().getComponents().get(0).getImplementation();
            ServiceDomain serviceDomain = new ServiceDomainManager().createDomain();
            QName serviceName = new QName("test");
            KnowledgeRuntimeManagerFactory runtimeManagerFactory = new KnowledgeRuntimeManagerFactory(loader, serviceDomain, serviceName, implementationModel);
            KnowledgeRuntimeManager runtimeManager = runtimeManagerFactory.newRuntimeManager(KnowledgeRuntimeManagerType.SINGLETON);
            RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine();
            KieSession session = runtimeEngine.getKieSession();
            session.insert(this);
            session.fireAllRules();
            runtimeManager.disposeRuntimeEngine(runtimeEngine);
            runtimeManager.close();
            return null;
        } catch (Throwable t) {
            return t;
        }
    }

}
