/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
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
package org.switchyard.quickstarts.demo.policy.security.basic;

import static org.switchyard.policy.SecurityPolicy.AUTHORIZATION;
import static org.switchyard.policy.SecurityPolicy.CLIENT_AUTHENTICATION;
import static org.switchyard.policy.SecurityPolicy.CONFIDENTIALITY;

import org.apache.log4j.Logger;
import org.switchyard.annotations.Requires;
import org.switchyard.component.bean.Service;

@Requires(security = { CONFIDENTIALITY, CLIENT_AUTHENTICATION, AUTHORIZATION })
@Service(WorkService.class)
public class WorkServiceBean implements WorkService {

    private static final Logger LOGGER = Logger.getLogger(WorkServiceBean.class);

    @Override
    public WorkAck doWork(Work work) {
        String cmd = work.getCommand();
        LOGGER.info(":: WorkService :: Received work command => " + cmd);
        return new WorkAck().setCommand(cmd).setReceived(true);
    }

}
