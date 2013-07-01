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

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.component.common.knowledge.session.KnowledgeSession;
import org.switchyard.component.common.knowledge.session.KnowledgeSessionFactory;
import org.switchyard.component.rules.config.model.RulesComponentImplementationModel;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.SwitchYardException;

/**
 * Tests Rules sessions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class RulesSessionTests {

    private static final String GOOD_RESOURCES = "/org/switchyard/component/rules/session/RulesSessionTests-GoodResources.xml";
    private static final String BAD_RESOURCES = "/org/switchyard/component/rules/session/RulesSessionTests-BadResources.xml";

    private ModelPuller<RulesComponentImplementationModel> _puller;

    @Before
    public void before() throws Exception {
        _puller = new ModelPuller<RulesComponentImplementationModel>();
    }

    @After
    public void after() throws Exception {
        _puller = null;
    }

    @Test
    public void testGoodResources() throws Exception {
        Throwable t = doTestResources(GOOD_RESOURCES);
        Assert.assertNull(t);
    }

    @Test
    public void testBadResources() throws Exception {
        Throwable t = doTestResources(BAD_RESOURCES);
        //System.err.println(t.getMessage());
        Assert.assertTrue(t instanceof SwitchYardException);
    }

    private Throwable doTestResources(String xml) {
        try {
            ClassLoader loader = getClass().getClassLoader();
            RulesComponentImplementationModel model = _puller.pull(xml, loader);
            KnowledgeSessionFactory factory = KnowledgeSessionFactory.newSessionFactory(model, loader, null, null);
            KnowledgeSession session = factory.newStatelessSession();
            session.getStateless().execute(this);
            return null;
        } catch (Throwable t) {
            return t;
        }
    }

}
