/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.switchyard.quickstarts.camel.jpa.binding.dummy;

import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.camel.component.jpa.JpaComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.switchyard.component.bean.Reference;
import org.switchyard.quickstarts.camel.jpa.binding.StoreService;
import org.switchyard.quickstarts.camel.jpa.binding.domain.Greet;

/**
 * Instance of this class produces test data for polling the data using JPA
 * producer binding. In normal use cases you will not need it.
 */
@ApplicationScoped
public class DummyDataFactory {

    // some values to populate entity
    private final static String[] NAMES = {
        "Keith", "David", "Brian", "Rob",
        "Tomo", "Lukasz", "Magesh", "Tom"
    };

    private Logger _logger = LoggerFactory.getLogger(DummyDataFactory.class);

    @Inject
    @Reference
    private StoreService _service;

    private AtomicBoolean _run = new AtomicBoolean(false);

    private Thread _thread;

    /**
     * It's not necessary to produce component in this way however it allows
     * this bean to be discovered by CDI runtime. Without it test thread will
     * not be started.
     * 
     * @return JPA component instance.
     */
    @Produces @Named("jpa")
    public JpaComponent createComponent() {
        return new JpaComponent();
    }

    /**
     * Initialize producer thread.
     */
    @PostConstruct
    public void startThread() {
        // run only if we are running inside AS
        if (System.getenv("JBOSS_HOME") == null) {
            _logger.info("Skipping producer thread");
            return;
        }
        _logger.info("Starting producer thread");
        _run.compareAndSet(false, true);

        _thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Random random = new Random(System.currentTimeMillis());
                while (_run.get()) {
                    int loopCount = 0;
                    int recordCount = random.nextInt(NAMES.length);
                    _logger.info("Sending {} dummy records from thread", recordCount);

                    while (loopCount++ < recordCount) {
                        Greet event = new Greet();
                        event.setReceiver(NAMES[random.nextInt(NAMES.length)]);
                        event.setSender(NAMES[random.nextInt(NAMES.length)]);
                        event.setCreatedAt(Calendar.getInstance());
                        _service.storeGreeting(event);
                    }

                    try {
                        // sleep a bit to don't overflow database
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.getMessage(); // Are you happy, Mr. Checkstyle?
                    }
                }
            }
        });
        _thread.setName("producer thread");
        _thread.setDaemon(true);
        _thread.start();
    }

    /**
     * Shutdown thread, if it was started.
     */
    @PreDestroy
    public void stopThread() {
        if (_thread != null) {
            _logger.info("Stopping producer thread");
            _run.compareAndSet(true, false);
            try {
                _thread.join(600);
            } catch (InterruptedException e) {
                e.getMessage(); // Are you happy checkstyle? ;)
            }
        }
    }

}
