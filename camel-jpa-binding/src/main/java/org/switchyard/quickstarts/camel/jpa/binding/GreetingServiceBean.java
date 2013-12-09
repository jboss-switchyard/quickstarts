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
package org.switchyard.quickstarts.camel.jpa.binding;

import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.switchyard.component.bean.Service;
import org.switchyard.quickstarts.camel.jpa.binding.domain.Greet;

/**
 * A POJO Service implementation.
 *
 * @author Lukasz Dywicki
 */
@Service(GreetingService.class)
public class GreetingServiceBean implements GreetingService {

    private Logger _logger = Logger.getLogger(GreetingServiceBean.class);

    private static SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm:ss:SSS");

    @Override
    public final void greet(final Greet event) {
        _logger.info("Hey " + event.getReceiver() + " please receive greetings from " + event.getSender() + " sent at "
            + FORMAT.format(event.getCreatedAt().getTime()));
    }

}
