/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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

import java.util.Calendar;
import java.util.Random;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;
import org.switchyard.quickstarts.camel.jpa.binding.domain.Greet;

/**
 * Implementation of scheduler service.
 */
@Service(PeriodicService.class)
public class PeriodicServiceImpl implements PeriodicService {

    // some values to populate entity
    private final static String[] NAMES = {
        "Keith", "David", "Brian", "Rob",
        "Tomo", "Lukasz", "Magesh", "Tom"
    };

    private Logger _logger = LoggerFactory.getLogger(PeriodicServiceImpl.class);

    @Inject
    @Reference("StoreReference")
    private StoreService _store;

    @Override
    public void execute() {
        Greet greet = createRandomGreet();
        _logger.info("Sending {} to JPA _store", greet);
        _store.storeGreeting(greet);
    }

    private Greet createRandomGreet() {
        Random random = new Random(System.currentTimeMillis());

        Greet event = new Greet();
        event.setReceiver(NAMES[random.nextInt(NAMES.length)]);
        event.setSender(NAMES[random.nextInt(NAMES.length)]);
        event.setCreatedAt(Calendar.getInstance());
        return event;
    }

}
