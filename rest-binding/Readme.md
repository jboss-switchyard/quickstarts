Introduction
============
This quickstart demonstrates the usage of RESTEasy Component's REST binding features. It binds two
SwitchYard services over REST URLs that can be accessed by any REST based client. One of them also
acts as a client. When a message arrives to the Order endpoint OrderService will be invoked.
This SwitchYard service internally invokes another RESTEasy endpoint (WarehouseService) via reference binding.

Experiment with your favourite REST client to see how this QS behaves and send us your feedback.

<pre>
+-----------------+      +--------------+      +----------------+      +------------------+
| http://         | ---- | OrderService | ---- | http://        | ---- | WarehouseService |
+-----------------+      +--------------+      +----------------+      +------------------+
</pre>

![RESTEasy Rest Binding Quickstart](Tooling image comming soon)


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
    mvn exec:java -Dexec.args="new"
</pre>
5. You should see the following output  
    `<order><orderId>1</orderId></order>`
6. Swicth back to server console or log and these messages should be displayed  
<pre>
    Added item 1 with name Hydrogen Atom - No, we are not kidding!
    Added item 2 with name Handcrafted Copper Plate
    Added item 3 with name Einstein's Bust - Talks about your future :)
    Added item 4 with name Time Machine
</pre>
7. Add an item to the order created in step 5  
<pre>
    mvn exec:java -Dexec.args="add 1 3 10"
</pre>
8. Swicth back to server console or log and these messages should be displayed  
<pre>
    Order after adding items: {OrderId: 1
    Items:{Item: {ItemId: 3, name:Einstein's Bust - Talks about your future :)}, quantity:10},
    }
</pre>
9. Add another item  
<pre>
    mvn exec:java -Dexec.args="add 1 4 1"
</pre>
10. Swicth back to server console or log and these messages should be displayed  
<pre>
    Order after adding items: {OrderId: 1
    Items:{Item: {ItemId: 3, name:Einstein's Bust - Talks about your future :)}, quantity:10},{Item: {ItemId: 4, name:Time Machine}, quantity:1},
    }
</pre>
11. Delete an item  
<pre>
    mvn exec:java -Dexec.args="del 1 3"
</pre>
12. Get the order now  
<pre>
    mvn exec:java -Dexec.args="get 1"
</pre>
13. Your output should be like this  
    `<order><orderId>1</orderId><orderItem><item><itemId>4</itemId><name>Time Machine</name></item><quantity>1</quantity></orderItem></order>`

## Further Reading

1. [RESTEasy Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/RESTEasy+Bindings)
