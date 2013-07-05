/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
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
