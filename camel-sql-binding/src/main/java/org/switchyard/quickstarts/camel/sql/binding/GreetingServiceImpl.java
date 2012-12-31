/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.quickstarts.camel.sql.binding;

import java.util.Random;

import javax.inject.Inject;

import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;

/**
 * Implementation of greeting service.
 */
@Service(GreetingService.class)
public class GreetingServiceImpl implements GreetingService {

    // some values to populate entity
    private final static String[] NAMES = {
        "Keith", "David", "Brian", "Rob",
        "Tomo", "Lukasz", "Magesh", "Tom"
    };

    @Inject @Reference("StoreService")
    private SingleGreetService store;

    @Inject @Reference("RemoveService")
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
    public void consume(Greeting[] greetings) {
        for (Greeting greeting : greetings) {
            System.out.println("Consumed [id: " + greeting.getId() + "] from " + greeting.getSender() + " to " + greeting.getReceiver());
            remove.execute(greeting);
        }
    }

    private String random() {
        Random random = new Random();
        return NAMES[random.nextInt(NAMES.length)];
    }
}
