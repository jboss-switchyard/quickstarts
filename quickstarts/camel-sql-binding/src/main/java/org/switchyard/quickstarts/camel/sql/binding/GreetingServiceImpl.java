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
package org.switchyard.quickstarts.camel.sql.binding;

import java.util.Random;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;

/**
 * Implementation of greeting service.
 */
@Service(GreetingService.class)
public class GreetingServiceImpl implements GreetingService {

    private static final Logger LOGGER = Logger.getLogger(GreetingConverter.class);
    // some values to populate entity
    private final static String[] NAMES = {
        "Keith", "David", "Brian", "Rob",
        "Tomo", "Lukasz", "Magesh", "Tom"
    };

    @Inject
    @Reference("StoreService")
    private SingleGreetService store;

    @Inject
    @Reference("RemoveService")
    private SingleGreetService remove;

    @Override
    public void generate() {
        store(new Greeting(random(), random()));
    }

    @Override
    public void store(Greeting greeting) {
        store.execute(greeting);
    }

    @Override
    public void consume(Greeting greeting) {
        if (greeting != null) {
            LOGGER.info("Consumed [id: " + greeting.getId() + "] from " + greeting.getSender() + " to " + greeting.getReceiver());
            remove.execute(greeting);
        }
    }

    private String random() {
        Random random = new Random();
        return NAMES[random.nextInt(NAMES.length)];
    }
}
