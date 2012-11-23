/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.quickstarts.camel.jpa.binding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.transaction.TransactionMixIn;
import org.switchyard.quickstarts.camel.jpa.binding.domain.Greet;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;

@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML, 
    mixins = {CDIMixIn.class, TransactionMixIn.class},
    scanners = BeanSwitchYardScanner.class)
@RunWith(SwitchYardRunner.class)
public class CamelJpaBindingStoreTest extends CamelJpaBindingTest {

    private SwitchYardTestKit _testKit;

    @Test
    public void storeEntity() throws Exception {
        Calendar createdAt = Calendar.getInstance();

        Greet event = new Greet();
        event.setReceiver(RECEIVER);
        event.setSender(SENDER);
        event.setCreatedAt(createdAt);

        _testKit.newInvoker("StoreService").sendInOnly(event);

        PreparedStatement statement = connection.prepareStatement("select createdAt, sender, receiver from events");
        ResultSet resultSet = statement.executeQuery();

        assertTrue(resultSet.next());
        assertEquals(SENDER, resultSet.getString("sender"));
        assertEquals(RECEIVER, resultSet.getString("receiver"));
        assertEquals(createdAt.getTimeInMillis(), resultSet.getTimestamp("createdAt").getTime());
    }

}
