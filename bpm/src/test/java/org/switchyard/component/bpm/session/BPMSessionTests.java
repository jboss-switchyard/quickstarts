/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
