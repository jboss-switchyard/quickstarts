/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.internal;

import java.util.Collections;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.switchyard.event.EventObserver;
import org.switchyard.event.EventPublisher;

/**
 * Handles registration and publication of events in a ServiceDomain.
 */
public class EventManager implements EventPublisher {

    private static Logger _logger = Logger.getLogger(EventManager.class);
    
    private Map<Class<? extends EventObject>, List<EventObserver>> _observers;
    
    /**
     * Creates a new instance of EventManager.
     */
    public EventManager() {
        _observers = new ConcurrentHashMap<Class<? extends EventObject>, List<EventObserver>>();
    }
    
    @Override
    public void publish(EventObject event) {
       if (_logger.isTraceEnabled()) {
           _logger.trace("Publishing event " + event);
       }

       for (EventObserver observer : getObserversForEvent(event.getClass())) {
           try {
               observer.notify(event);
           } catch (Throwable t) {
               // do not propagate errors on event notifications
               _logger.debug("Observer threw exception on event " + event.getClass(), t);
           }
       }
    }
    
    /**
     * Returns a list of EventObserver instances for a given event type.
     * @param event event type to query for observers
     * @return list of EventObservers for the type or an empty list if none are registered
     */
    public List<EventObserver> getObserversForEvent(Class<? extends EventObject> event) {
        if (_observers.containsKey(event)) {
            return _observers.get(event);
        } else {
            return Collections.emptyList();
        }
    }
    
    /**
     * Synchronizing this method since adding an observer may lead to a 
     * new list being created for an event entry and we don't want a race 
     * condition if multiple threads hit this during deployment/startup.
     * @param observer observer instance to add
     * @param event the event to register against
     * @return a reference to this EventManger for chaining calls
     */
    public synchronized EventManager addObserver(
            EventObserver observer, Class<? extends EventObject> event) {
        
        List<EventObserver> observerList = _observers.get(event);
        if (observerList == null) {
            observerList = new LinkedList<EventObserver>();
            _observers.put(event, observerList);
        }
        
        observerList.add(observer);
        _logger.debug("Observer added for event " + event.getCanonicalName());
        return this;
    }
    
    /**
     * Remove all event registrations for a given EventObserver instance.
     * @param observer the observer to unregister
     */
    public synchronized void removeObserver(EventObserver observer) {
        for (List<EventObserver> observers : _observers.values()) {
            observers.remove(observer);
        }
    }

    /**
     * Remove an EventObserver from a specific event type.
     * @param observer the EventObserver to unregister
     * @param event the event to unregister
     */
    public synchronized void removeObserverForEvent(
            EventObserver observer, Class<? extends EventObject> event) {
        
        List<EventObserver> observers = _observers.get(event);
        if (observers != null) {
            observers.remove(observer);
        }
    }
}
