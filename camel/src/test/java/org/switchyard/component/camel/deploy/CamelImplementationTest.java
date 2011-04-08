/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.deploy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.switchyard.test.SwitchYardTestCase;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.CDIMixIn;

/**
 * Test for {@link CamelActivator}.
 * 
 * @author Daniel Bevenius
 * 
 */
@SwitchYardTestCaseConfig(config = "switchyard-activator-impl.xml", mixins = CDIMixIn.class)
public class CamelImplementationTest extends SwitchYardTestCase {
    
    @Test
    public void sendOneWayMessageThroughCamelToSwitchYardService() throws Exception {
        final String title = newInvoker("OrderService").operation("getTitleForItem").sendInOut("10").getContent(String.class);
        assertThat(title, is(equalTo("Fletch")));
    }
}
