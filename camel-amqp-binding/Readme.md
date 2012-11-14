Introduction
============
This quickstart demonstrates the usage of the Camel Component and it's binding feature, by binding 
to a JMS/AMQP Queue deployed on Apache QPid broker. When a message arrives in this queue the service will be invoked.


Running the quickstart
======================

The AMQP quickstart works with an embedded Qpid broker inside the unit test, so running the quickstart
is simply a matter of building the project module.

JBoss AS 7
----------
1. Build the quickstart:
    mvn clean install

## Further Reading

1. [Camel Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Camel+Bindings)

