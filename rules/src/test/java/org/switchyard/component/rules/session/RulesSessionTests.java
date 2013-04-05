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
package org.switchyard.component.rules.session;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.component.common.knowledge.session.KnowledgeSession;
import org.switchyard.component.common.knowledge.session.KnowledgeSessionFactory;
import org.switchyard.component.rules.config.model.RulesComponentImplementationModel;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.exception.SwitchYardException;

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
