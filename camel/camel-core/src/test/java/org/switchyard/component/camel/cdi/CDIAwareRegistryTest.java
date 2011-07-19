/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.component.camel.cdi;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.component.camel.config.model.RouteScanner;
import org.switchyard.test.SwitchYardTestCase;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.CDIMixIn;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@SwitchYardTestCaseConfig(
        config = "/switchyard-configs/cdireg-switchyard.xml",
        mixins = CDIMixIn.class)
public class CDIAwareRegistryTest extends SwitchYardTestCase {

    private static final String TEST_MESSAGE = "\n"
      + "bob: Hello there!\n"
      + "sally: I like cheese\n"
      + "fred: Math makes me sleepy\n"
      + "bob: E pluribus unum\n"
      + "sally: And milk too\n"
      + "bob: Four score and seven years\n"
      + "sally: Actually, any kind of dairy is OK in my book\n";

    @Test
    public void testCamelRoute() {
        newInvoker("JavaDSL.acceptMessage").sendInOnly(TEST_MESSAGE);

        SallyMuncherBean sallyMuncher = getMixIn(CDIMixIn.class).getObject(SallyMuncherBean.class);
        Assert.assertEquals(3, sallyMuncher.getMunched().size());
        Assert.assertEquals("sally: I like cheese", sallyMuncher.getMunched().get(0));
        Assert.assertEquals("sally: And milk too", sallyMuncher.getMunched().get(1));
        Assert.assertEquals("sally: Actually, any kind of dairy is OK in my book", sallyMuncher.getMunched().get(2));
    }
}
