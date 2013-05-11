Introduction
============
This quickstart demonstrates the usage of RESTEasy Component's REST binding features. It binds two
SwitchYard services over REST URLs that can be accessed by any REST based client. One of them also
acts as a client. When a message arrives to the Order endpoint OrderService will be invoked.
This SwitchYard service internally invokes another RESTEasy endpoint (WarehouseService) via reference binding.

Experiment with your favourite REST client to see how this QS behaves and send us your feedback.

```
+-----------------+      +--------------+      +----------------+      +------------------+
| http://         | ---- | OrderService | ---- | http://        | ---- | WarehouseService |
+-----------------+      +--------------+      +----------------+      +------------------+
```

![RESTEasy Rest Binding Quickstart](Tooling image coming soon)


Running the quickstart
======================

JBoss AS 7
----------
1. Build the quickstart:

        mvn clean install

2. Start JBoss AS 7 in standalone mode:

        ${AS}/bin/standalone.sh

3. Deploy the quickstart

        mvn jboss-as:deploy

4. Open a console windows and type  

        mvn exec:java -Dexec.args="new"

5. You should see the following output  
    `<order><orderId>1</orderId></order>`
6. Switch back to server console or log and these messages should be displayed  
```
    Added item 1 with name Hydrogen Atom - No, we are not kidding!
    Added item 2 with name Handcrafted Copper Plate
    Added item 3 with name Einstein's Bust - Talks about your future :)
    Added item 4 with name Time Machine
```
7. Add an item to the order created in step 5  

        mvn exec:java -Dexec.args="add 1 3 10"

8. Switch back to server console or log and these messages should be displayed  
```
    Order after adding items: {OrderId: 1
    Items:{Item: {ItemId: 3, name:Einstein's Bust - Talks about your future :)}, quantity:10},
    }
```
9. Add another item  

        mvn exec:java -Dexec.args="add 1 4 1"

10. Switch back to server console or log and these messages should be displayed  
```
    Order after adding items: {OrderId: 1
    Items:{Item: {ItemId: 3, name:Einstein's Bust - Talks about your future :)}, quantity:10},{Item: {ItemId: 4, name:Time Machine}, quantity:1},
    }
```
11. Delete an item  

        mvn exec:java -Dexec.args="del 1 3"

12. Get the order now  

        mvn exec:java -Dexec.args="get 1"

13. Your output should be like this  
    `<order><orderId>1</orderId><orderItem><item><itemId>4</itemId><name>Time Machine</name></item><quantity>1</quantity></orderItem></order>`

## Further Reading

1. [RESTEasy Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/RESTEasy+Bindings)
