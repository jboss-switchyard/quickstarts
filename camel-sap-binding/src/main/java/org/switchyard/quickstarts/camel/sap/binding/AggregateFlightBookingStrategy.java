/**
 * Copyright 2013 Red Hat, Inc.
 * 
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 */
package org.switchyard.quickstarts.camel.sap.binding;

import javax.inject.Named;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.switchyard.quickstarts.camel.sap.binding.bean.FlightConnectionInfo;
import org.switchyard.quickstarts.camel.sap.binding.bean.FlightCustomerInfo;
import org.switchyard.quickstarts.camel.sap.binding.bean.FlightTripRequestInfo;
import org.switchyard.quickstarts.camel.sap.binding.bean.PassengerInfo;

/**
 * @author William Collins <punkhornsw@gmail.com>
 *
 */
@Named("aggregateFlightBookingStrategy")
public class AggregateFlightBookingStrategy implements AggregationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(AggregateFlightBookingStrategy.class);

    /**
     * Merges the message headers of sub-routes.
     * <p>{@inheritDoc}
     */
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Exchange answer = oldExchange == null ? newExchange : oldExchange;
        FlightTripRequestInfo flightTripRequestInfo;
        Object payload = answer.getIn().getBody();
        if (payload instanceof FlightTripRequestInfo) {
            flightTripRequestInfo = FlightTripRequestInfo.class.cast(payload);
        } else {
            flightTripRequestInfo = new FlightTripRequestInfo();
        }
        
        String to = newExchange.getProperty(Exchange.TO_ENDPOINT, String.class);
        if (LOG.isDebugEnabled()) {
            LOG.debug("To endpoint = {}", to);
        }
        if (to.contains("FlightConnectionInfo")) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Adding Flight Connection Info to exchange.");
            }
           flightTripRequestInfo.setFlightConnectionInfo(newExchange.getIn().getBody(FlightConnectionInfo.class));
        } else if (to.contains("FlightCustomerInfo")){
            if (LOG.isDebugEnabled()) {
                LOG.debug("Adding Flight Customer Info to exchange.");
            }
            flightTripRequestInfo.setFlightCustomerInfo(newExchange.getIn().getBody(FlightCustomerInfo.class));
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Adding Passenger Info to exchange.");
            }
            flightTripRequestInfo.setPassengerInfo(newExchange.getIn().getBody(PassengerInfo.class));
        }
        answer.getIn().setBody(flightTripRequestInfo);
        return answer;
    }

}
