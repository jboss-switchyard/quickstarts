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
package org.switchyard.quickstarts.demo.security.propagation.ejb;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.annotation.security.RolesAllowed;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.SecurityDomain;

@Stateless
@EJB(name = "java:global/TestEJBBean", beanInterface = TestEJBBeanLocal.class)
@RolesAllowed({"friend"})
@SecurityDomain("other")
public class TestEJBBean implements TestEJBBeanLocal {

    private static final Logger LOGGER = Logger.getLogger(TestEJBBean.class);
    private static final String MSG_1 = ":: TestEJBBean :: process => %s (caller principal=%s, in roles? 'friend'=%s 'enemy'=%s)";

    @Resource
    private SessionContext ctx;

    @Override
    public String process(String in) {
        String msg1 = String.format(MSG_1, in, ctx.getCallerPrincipal(), ctx.isCallerInRole("friend"), ctx.isCallerInRole("enemy"));
        LOGGER.info(msg1);
        return "Processed by TestEJBBean: " + in;
    }

}
