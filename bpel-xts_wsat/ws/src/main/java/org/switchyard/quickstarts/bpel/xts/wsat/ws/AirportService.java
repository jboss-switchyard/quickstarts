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
package org.switchyard.quickstarts.bpel.xts.wsat.ws;

import static javax.jws.soap.SOAPBinding.Style.DOCUMENT;
import static javax.jws.soap.SOAPBinding.Use.LITERAL;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.arjuna.mw.wst11.TransactionManagerFactory;
import com.arjuna.mw.wst11.UserTransaction;
import com.arjuna.mw.wst11.UserTransactionFactory;

/**
 * Airport service to get flight identifier and order flight.
 */
@Stateless
@WebService(name = "AirportService", serviceName = "AirportService", targetNamespace = "http://www.jboss.org/bpel/examples/airport")
@SOAPBinding(style = DOCUMENT, use = LITERAL)
@HandlerChain(file = "/context-handlers.xml")
public class AirportService {

    private static Logger log = Logger.getLogger(AirportService.class.getName());

    /**
     * Get flight identifier for the parameters.
     * @param from place (city)
     * @param to place (city)
     * @param date of flight
     * @return format [from/to/month/day]
     */
    @WebMethod
    @WebResult(name = "fltid")
    public String getFLTID(@WebParam(name = "from") String from, @WebParam(name = "to") String to,
        @WebParam(name = "date") Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return from + "/" + to + "/" + String.valueOf(c.get(Calendar.MONTH) + 1) + "/"
            + String.valueOf(c.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Order flight ticket defined by fltid for a user identified by name.
     * @param name username
     * @param fltid flight identifier
     */
    @WebMethod
    public void order(@WebParam(name = "name") String name, @WebParam(name = "fltid") String fltid) {
        log.info("AirportService:order");

        UserTransaction transactionId = UserTransactionFactory.userTransaction();

        if (transactionId != null) {
            log.info("Transaction ID = " + transactionId.toString());
            if (transactionId.toString().compareTo("Unknown") == 0) {
                log.info("JBoss AS is badly configured. (Enable XTS)");
                return;
            }

            // Create order participant (fly ticket)
            OrderParticipant op = new OrderParticipant(transactionId.toString(), name, fltid);

            try {
                // Enlist order participant to the transaction
                TransactionManagerFactory.transactionManager().enlistForDurableTwoPhase(op,
                    "org.switchyard.quickstarts.bpel.xts.wsat.ws:AirportService:" + name + ":" + fltid);
            } catch (Exception e) {
                log.log(Level.SEVERE, e.getMessage());
                e.printStackTrace();
            }
        }
    }

}
