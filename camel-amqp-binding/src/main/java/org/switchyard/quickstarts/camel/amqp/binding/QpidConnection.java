package org.switchyard.quickstarts.camel.amqp.binding;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.apache.qpid.client.AMQConnectionFactory;
import org.apache.qpid.url.URLSyntaxException;

public class QpidConnection {

    @Produces
    @Named("qpidConnectionFactory")
    public AMQConnectionFactory createConnectionFactory() throws URLSyntaxException {
        return new AMQConnectionFactory("amqp://guest:guest@/test?brokerlist='tcp://localhost:5672'");
    }

}
