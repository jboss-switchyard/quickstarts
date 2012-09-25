Introduction
============
This quickstart demonstrates the usage of HTTP gateway component. It binds two SwitchYard services
over HTTP URLs that can be accessed by any HTTP based client. One of them also acts as a client.
When a message arrives to the Quote service Symbol service will be invoked via reference binding.

<pre>
+-----------------+      +--------------+      +----------------+      +---------------+
| http://         | ---- | QuoteService | ---- | http://        | ---- | SymbolService |
+-----------------+      +--------------+      +----------------+      +---------------+
</pre>

![HTTP Binding Quickstart](Tooling image comming soon)


Running the quickstart
======================

JBoss AS 7
----------
1. Build the quickstart:
<pre>
    mvn clean install
</pre>
2. Start JBoss AS 7 in standalone-preview mode:
<pre>
    ${AS}/bin/standalone.sh --server-config=standalone.xml
</pre>
3. Deploy the quickstart
<pre>
    mvn jboss-as:deploy
</pre>
4. Open a console windows and type  
<pre>
    mvn exec:java -Dexec.args="vineyard"
</pre>
5. You should see the following output  
    `136.5

## Further Reading

1. [HTTP Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/HTTP+Bindings)
