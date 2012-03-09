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

package org.switchyard.admin.base;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.switchyard.Exchange;
import org.switchyard.ExchangeState;
import org.switchyard.Property;
import org.switchyard.admin.MessageMetrics;
import org.switchyard.runtime.event.ExchangeCompletionEvent;

/**
 * Message metric collection support.  Holds the values of message metrics
 * and provides a method recording metric data from an exchange.
 */
public class MessageMetricsSupport implements MessageMetrics {
    
    private static final String NL = System.getProperty("line.separator");
    private AtomicInteger _successCount = new AtomicInteger();
    private AtomicInteger _faultCount = new AtomicInteger();
    private AtomicInteger _minTimeMS = new AtomicInteger();
    private AtomicInteger _maxTimeMS = new AtomicInteger();
    private AtomicLong _totalTimeMS = new AtomicLong();
    
    /**
     * Update metric data based on the specified exchange.
     * @param exchange an exchange with metrics info
     */
    public synchronized void recordMetrics(Exchange exchange) {
        if (exchange.getState().equals(ExchangeState.FAULT)) {
            _faultCount.incrementAndGet();
        } else {
            _successCount.incrementAndGet();
        }
        
        Property prop = exchange.getContext().getProperty(ExchangeCompletionEvent.EXCHANGE_DURATION);
        if (prop != null) {
            Long duration = (Long)prop.getValue();
            _totalTimeMS.addAndGet(duration);
            // check minTime
            if (_minTimeMS.intValue() == 0 || duration < _minTimeMS.intValue()) {
                _minTimeMS.set(duration.intValue());
            }
            // check maxTime
            if (_maxTimeMS.intValue() == 0 || duration > _maxTimeMS.intValue()) {
                _maxTimeMS.set(duration.intValue());
            }
        }
    }
    
    /**
     * Reset all message metrics.
     */
    public synchronized void reset() {
        _successCount.set(0);
        _faultCount.set(0);
        _minTimeMS.set(0);
        _maxTimeMS.set(0);
        _totalTimeMS.set(0);
    }

    @Override
    public int getTotalCount() {
        return _successCount.get() + _faultCount.get();
    }

    @Override
    public int getFaultCount() {
        return _faultCount.get();
    }

    @Override
    public int getSuccessCount() {
        return _successCount.get();
    }

    @Override
    public long getTotalProcessingTime() {
        return _totalTimeMS.get();
    }

    @Override
    public synchronized double getAverageProcessingTime() {
        if (getTotalCount() == 0) {
            return 0.0;
        }
        return (double)getTotalProcessingTime() / getTotalCount();
    }

    @Override
    public int getMaxProcessingTime() {
        return _maxTimeMS.get();
    }

    @Override
    public int getMinProcessingTime() {
        return _minTimeMS.get();
    }
    
    @Override
    public String toString() {
        return    "Success Count : " + getSuccessCount() + NL
                + "Fault Count   : " + getFaultCount() + NL
                + "Total Count   : " + getTotalCount() + NL
                + "Avg Time MS   : " + getAverageProcessingTime() + NL
                + "Min Time MS   : " + getMinProcessingTime() + NL
                + "Max Time MS   : " + getMaxProcessingTime() + NL
                + "Total Time MS : " + getTotalProcessingTime();
    }
}
