/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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
package org.switchyard.quickstarts.rules.interview;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.common.io.Files;
import org.switchyard.common.type.Classes;
import org.switchyard.component.common.knowledge.system.ResourceChangeService;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

/**
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = SwitchYardTestCaseConfig.SWITCHYARD_XML)
public class RulesInterviewTest {

    private static final String NAME = RulesInterviewTest.class.getSimpleName();

    private File pkgFile = null;
    private File pkgFile18 = null;
    private File pkgFile21 = null;

    @ServiceOperation("Interview.verify")
    private Invoker verify;

    @Before
    public void before() throws Exception {
        String pkgUrl = Classes.getResource(InterviewRules.PKG, InterviewRules.class).toExternalForm();
        String pkgPath = pkgUrl.substring(5, pkgUrl.length());
        pkgFile = new File(pkgPath);
        pkgFile18 = new File(pkgPath.substring(0, pkgPath.length() - 4) + "-18.pkg");
        pkgFile21 = new File(pkgPath.substring(0, pkgPath.length() - 4) + "-21.pkg");
        Files.copy(pkgFile18, pkgFile);
        System.setProperty(ResourceChangeService.DROOLS_RESOURCE_SCANNER_INTERVAL, "1");
        ResourceChangeService.start(NAME);
    }

    @After
    public void after() throws Exception {
        ResourceChangeService.stop(NAME);
        System.setProperty(ResourceChangeService.DROOLS_RESOURCE_SCANNER_INTERVAL, "60");
        Files.copy(pkgFile18, pkgFile);
        pkgFile21 = null;
        pkgFile18 = null;
        pkgFile = null;
    }

    @Test
    public void testInterviewRules() throws Exception {
        Applicant twenty = new Applicant("Twenty", 20);
        verify.sendInOnly(twenty);
        Thread.sleep(3000);
        Files.copy(pkgFile21, pkgFile);
        Thread.sleep(6000);
        verify.sendInOnly(twenty);
        Thread.sleep(1000);
    }

}
