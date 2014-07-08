Introduction
============
This quickstart demonstrates the usage of HTTP gateway component. It binds two SwitchYard services
over HTTP URLs that can be accessed by any HTTP based client. One of them also acts as a client.
When a message arrives to the Quote service Symbol service will be invoked via reference binding.

```
+-----------------+      +--------------+      +----------------+      +---------------+
| http://         | ---- | QuoteService | ---- | http://        | ---- | SymbolService |
+-----------------+      +--------------+      +----------------+      +---------------+
```

![HTTP Binding Quickstart](Tooling image comming soon)


Running the quickstart
======================

JBoss AS 7
----------
1. Start JBoss AS 7 in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the quickstart

        mvn install -Pdeploy

3. Open a console windows and type

        mvn exec:java -Dexec.args="vineyard"

4. You should see the following output

      Request: http://localhost:8080/http-binding/quote -> vineyard
     Response: 136.5

5. Undeploy the quickstart:

        mvn clean -Pdeploy

## Further Reading

1. [HTTP Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/HTTP)
