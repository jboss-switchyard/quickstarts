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
package org.switchyard.quickstarts.camel.sap.binding.bean;

/**
 * A composite object contains FlightConnectionInfo, FlightCustomerInfo and PassengerInfo.
 * Those 3 objects are needed to book a trip.
 */
public class FlightTripRequestInfo {
    FlightConnectionInfo _flightConnectionInfo;
    FlightCustomerInfo _flightCustomerInfo;
    PassengerInfo _passengerInfo;

    public FlightConnectionInfo getFlightConnectionInfo() {
        return _flightConnectionInfo;
    }

    public FlightCustomerInfo getFlightCustomerInfo() {
        return _flightCustomerInfo;
    }

    public PassengerInfo getPassengerInfo() {
        return _passengerInfo;
    }

    public void setFlightConnectionInfo(FlightConnectionInfo _flightConnectionInfo) {
        this._flightConnectionInfo = _flightConnectionInfo;
    }

    public void setFlightCustomerInfo(FlightCustomerInfo _flightCustomerInfo) {
        this._flightCustomerInfo = _flightCustomerInfo;
    }

    public void setPassengerInfo(PassengerInfo _passengerInfo) {
        this._passengerInfo = _passengerInfo;
    }

}
