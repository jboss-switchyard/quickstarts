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
package org.switchyard.component.test.mixins.transaction;

import java.util.Iterator;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.test.MixInDependencies;
import org.switchyard.test.TestMixIn;
import org.switchyard.test.mixins.AbstractTestMixIn;
import org.switchyard.component.test.mixins.cdi.CDIMixInParticipant;
import org.switchyard.component.test.mixins.jca.JCAMixIn;
import org.switchyard.component.test.mixins.naming.NamingMixIn;

import com.arjuna.ats.jta.common.JTAEnvironmentBean;

/**
 * Mixin which bounds JTA beans in JNDI tree.
 * 
 * @author Lukasz Dywicki
 */
@MixInDependencies(required={NamingMixIn.class}, optional={JCAMixIn.class})
public class TransactionMixIn extends AbstractTestMixIn 
    implements CDIMixInParticipant {

    /**
     * Location of persistent store for tx logs.
     */
    private String _storeDir = "target/tx-store";
    private JTAEnvironmentBean _jtaEnvironmentBean;
    private Logger _logger = Logger.getLogger(TransactionMixIn.class);

    @Override
    public void initialize() {
        if (getTestKit() != null) {
            Iterator<TestMixIn> dependencies = getTestKit().getOptionalDependencies(this).iterator();
            while (_jtaEnvironmentBean == null && dependencies.hasNext()) {
                TestMixIn testMixIn = dependencies.next();
                if (testMixIn instanceof TransactionMixInParticipant) {
                    try {
                        _logger.debug("Trying to locate JTA environment using " + testMixIn);
                        _jtaEnvironmentBean = ((TransactionMixInParticipant) testMixIn).locateEnvironmentBean();
                    } catch (Throwable e) {
                        throw new SwitchYardException("Exception during locating arjuna environment bean", e);
                    }
                }
            }
        }

        try {
            if (_jtaEnvironmentBean == null) {
                System.setProperty("ObjectStoreEnvironmentBean.objectStoreDir", _storeDir);
                System.setProperty("com.arjuna.ats.arjuna.objectstore.objectStoreDir", _storeDir);
                InitialContext initialContext = new InitialContext();
                _jtaEnvironmentBean = new JTAEnvironmentBean();
                initialContext.bind("java:jboss/TransactionManager", _jtaEnvironmentBean.getTransactionManager());
                initialContext.bind("java:jboss/UserTransaction", _jtaEnvironmentBean.getUserTransaction());
                initialContext.bind("java:jboss/TransactionSynchronizationRegistry", _jtaEnvironmentBean.getTransactionSynchronizationRegistry());
            }
        } catch (NamingException e) {
            throw new SwitchYardException("Unable to bind transaction manager in JNDI", e);
        }
    }

    /**
     * Returns an instance of UserTransaction.
     * @return UserTransaction
     */
    public UserTransaction getUserTransaction() {
        return _jtaEnvironmentBean.getUserTransaction();
    }

    /**
     * Returns the TransactionManager used by the TransactionMixIn.
     * @return TransactionManager instance
     */
    public TransactionManager getTransactionManager() {
       return _jtaEnvironmentBean.getTransactionManager();
    }

    /**
     * Returns the SynchronizationRegistry used by the TransactionMixIn.
     * @return SynchronizationRegistry instance
     */
    public TransactionSynchronizationRegistry getTransactionSynchronizationRegistry() {
        return _jtaEnvironmentBean.getTransactionSynchronizationRegistry();
    }

    /**
     * Returns JTAEnvironmentBean used by the TransactionMixIn.
     * @return JTAEnvironmentBean instance
     */
    public JTAEnvironmentBean getEnvironmentBean() {
        return _jtaEnvironmentBean;
    }

    @Override
    public void participate(Deployment deployment) throws Exception {
        deployment.getServices().add(org.jboss.weld.transaction.spi.TransactionServices.class,
            new LocalArjunaTransactionServices(_jtaEnvironmentBean));
    }


}
