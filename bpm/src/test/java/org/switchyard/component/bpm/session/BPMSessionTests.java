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
package org.switchyard.component.bpm.session;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.component.bpm.config.model.BPMComponentImplementationModel;
import org.switchyard.component.common.knowledge.session.KnowledgeSession;
import org.switchyard.component.common.knowledge.session.KnowledgeSessionFactory;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.SwitchYardException;

/**
 * Tests BPM sessions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class BPMSessionTests {

    private static final String GOOD_RESOURCES = "/org/switchyard/component/bpm/session/BPMSessionTests-GoodResources.xml";
    private static final String BAD_RESOURCES = "/org/switchyard/component/bpm/session/BPMSessionTests-BadResources.xml";

    private ModelPuller<BPMComponentImplementationModel> _puller;

    @Before
    public void before() throws Exception {
        _puller = new ModelPuller<BPMComponentImplementationModel>();
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
            BPMComponentImplementationModel model = _puller.pull(xml, loader);
            KnowledgeSessionFactory factory = KnowledgeSessionFactory.newSessionFactory(model, loader, null, null);
            KnowledgeSession session = factory.newStatefulSession(null);
            session.getStateful().startProcess("TestProcess");
            return null;
        } catch (Throwable t) {
            return t;
        }
    }

}
