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
package org.switchyard.quickstarts.bpel.xts.wsba.ws;

import java.io.Serializable;
import java.util.logging.Logger;

import com.arjuna.wst.BusinessAgreementWithCoordinatorCompletionParticipant;
import com.arjuna.wst.FaultedException;
import com.arjuna.wst.SystemException;
import com.arjuna.wst.WrongStateException;

/**
 * 
 * Order participant to prepare and order fly tickets.
 * 
 */
public class OrderParticipant implements BusinessAgreementWithCoordinatorCompletionParticipant, Serializable {

    private static final long serialVersionUID = -6459800278322126331L;

    private static Logger log = Logger.getLogger(OrderParticipant.class.getName());

    private String _txID;
    private String _name;
    private String _fltid;

    /**
     * @param txID transaction identifier
     * @param name username
     * @param fltid flight identifier
     */
    public OrderParticipant(String txID, String name, String fltid) {
        _txID = txID;
        _name = name;
        _fltid = fltid;
    }

    /**
     * Get transaction identifier.
     * @return transaction identifier
     */
    public String getTxID() {
        return _txID;
    }

    @Override
    public void unknown() throws SystemException {

    }

    @Override
    public void error() throws SystemException {

    }

    @Override
    public void cancel() throws FaultedException, WrongStateException, SystemException {
        log.info("\n================================================================================\n"
            + "AirportOrderParticipant " + _fltid + " cancel"
            + "\n================================================================================");
    }

    @Override
    public void close() throws WrongStateException, SystemException {
        log.info("\n================================================================================\n"
            + "AirportOrderParticipant " + _fltid + " close"
            + "\n================================================================================");
    }

    @Override
    public void compensate() throws FaultedException, WrongStateException, SystemException {
        log.info("\n================================================================================\n"
            + "AirportOrderParticipant " + _fltid + " compensate"
            + "\n================================================================================");
    }

    @Override
    public String status() throws SystemException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void complete() throws WrongStateException, SystemException {
        log.info("\n================================================================================\n"
            + "AirportOrderParticipant " + _fltid + " complete"
            + "\n================================================================================");
    }

}
