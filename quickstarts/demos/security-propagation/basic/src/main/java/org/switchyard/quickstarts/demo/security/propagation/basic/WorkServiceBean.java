/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.switchyard.quickstarts.demo.security.propagation.basic;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.ExchangeSecurity;
import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;
import org.switchyard.quickstarts.demo.security.propagation.ejb.TestEJBBeanLocal;

@Service(WorkService.class)
public class WorkServiceBean implements WorkService {

    private static final Logger LOGGER = Logger.getLogger(WorkServiceBean.class);
    private static final String MSG_1 = ":: WorkService :: Received work command => %s (caller principal=%s, in roles? 'friend'=%s 'enemy'=%s)";
    private static final String MSG_2 = ":: WorkService :: returned from BackEndService => %s";
    private static final String MSG_3 = ":: WorkService :: returned from TestEJBBean => %s";

    @Inject
    private Exchange exchange;

    @Inject
    @Reference
    private BackEndService backEndService;

    @EJB(lookup="java:global/switchyard-demo-security-propagation-ejb/TestEJBBean")
    private TestEJBBeanLocal testEjb;

    @Override
    public WorkAck doWork(Work work) {
        String cmd = work.getCommand();
        ExchangeSecurity es = exchange.getSecurity();
        String msg1 = String.format(MSG_1, cmd, es.getCallerPrincipal(), es.isCallerInRole("friend"), es.isCallerInRole("enemy"));
        LOGGER.info(msg1);
        // BackEndService invocation
        String back = backEndService.process(cmd);
        String msg2 = String.format(MSG_2, back);
        LOGGER.info(msg2);
        // TestEJBBean invocation
        String ejbret = testEjb.process(cmd);
        String msg3 = String.format(MSG_3, ejbret);
        LOGGER.info(msg3);
        return new WorkAck().setCommand(cmd).setReceived(true);
    }

}
