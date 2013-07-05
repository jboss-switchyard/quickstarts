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
package org.switchyard.quickstarts.bpel.service.hello;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;

public final class HornetQClient {
    
    /**
     * The queue to send to.
     */
    private static final String REQUEST_NAME = "HelloRequestQueue";
    private static final String REPLY_NAME = "HelloReplyQueue";
    private static final String USER = "guest";
    private static final String PASSWD = "guestp.1";
    
    private HornetQClient() {
    }
    
    public static void main(final String[] args) throws Exception {

        if (args.length != 1) {
            System.err.println("ERROR: Use -Dexec.args to pass a name value, e.g. -Dexec.args=\"Skippy\"");
            return;
        }
        
        HornetQMixIn hqMixIn = new HornetQMixIn(false)
                                    .setUser(USER)
                                    .setPassword(PASSWD);
        hqMixIn.initialize();

        try {
            Session session = hqMixIn.getJMSSession();
            final MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(REQUEST_NAME));
            producer.send(hqMixIn.createJMSMessage(createPayload(args[0])));
            System.out.println("Message sent. Waiting for reply ...");
         
            final MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(REPLY_NAME));
            Message message = consumer.receive(3000);
            String reply = hqMixIn.readStringFromJMSMessage(message);
            System.out.println("REPLY: \n" + reply);
        } finally {
            hqMixIn.uninitialize();
        }

    }
    
    private static String createPayload(String name) {
        return "<exam:sayHello xmlns:exam=\"http://www.jboss.org/bpel/examples\">"
                + "<exam:input>" + name + "</exam:input>"
                + "</exam:sayHello>";
    }
}
