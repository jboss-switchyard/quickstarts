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
package org.switchyard.component.test.mixins.transaction;

import java.util.Iterator;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.switchyard.SwitchYardException;
import org.switchyard.test.MixInDependencies;
import org.switchyard.test.TestMixIn;
import org.switchyard.test.mixins.AbstractTestMixIn;
import org.switchyard.component.test.mixins.cdi.CDIMixInParticipant;
import org.switchyard.component.test.mixins.naming.NamingMixIn;

import com.arjuna.ats.jta.common.JTAEnvironmentBean;
import com.arjuna.ats.jta.common.jtaPropertyManager;

/**
 * Mixin which bounds JTA beans in JNDI tree.
 * 
 * @author Lukasz Dywicki
 */
@MixInDependencies(required={NamingMixIn.class})
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
            Iterator<TestMixIn> mixins = getTestKit().getMixIns().iterator();
            while (_jtaEnvironmentBean == null && mixins.hasNext()) {
                TestMixIn testMixIn = mixins.next();
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
                _jtaEnvironmentBean = jtaPropertyManager.getJTAEnvironmentBean();
                _jtaEnvironmentBean.setTransactionManagerClassName(com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionManagerImple.class.getName());
                _jtaEnvironmentBean.setUserTransactionClassName(com.arjuna.ats.internal.jta.transaction.arjunacore.UserTransactionImple.class.getName());
                _jtaEnvironmentBean.setTransactionSynchronizationRegistryClassName(com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionSynchronizationRegistryImple.class.getName());

                InitialContext initialContext = new InitialContext();
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
