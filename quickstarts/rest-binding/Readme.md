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


EAP
----------
1. Start EAP in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the quickstart

        mvn -Dmaven.test.skip=true install -Pdeploy

3. Open a console windows and type  

        mvn exec:java -Dexec.args="new"

4. You should see the following output  
    `<order><orderId>1</orderId></order>`
5. Switch back to server console or log and these messages should be displayed  
```
    Added item 1 with name Hydrogen Atom - No, we are not kidding!
    Added item 2 with name Handcrafted Copper Plate
    Added item 3 with name Einstein's Bust - Talks about your future :)
    Added item 4 with name Time Machine
```
Please note it is normal to see WARN messages as below when adding items as the addItem method has two input parameters
```
    Default RESTEasy Message Composer doesn't handle multiple input parameters.
```
6. Add an item to the order created in step 5  

        mvn exec:java -Dexec.args="add 1 3 10"

7. Switch back to server console or log and these messages should be displayed  
```
    Order after adding items: {OrderId: 1
    Items:{Item: {ItemId: 3, name:Einstein's Bust - Talks about your future :)}, quantity:10},
    }
```
8. Add another item  

        mvn exec:java -Dexec.args="add 1 4 1"

9. Switch back to server console or log and these messages should be displayed  
```
    Order after adding items: {OrderId: 1
    Items:{Item: {ItemId: 3, name:Einstein's Bust - Talks about your future :)}, quantity:10},{Item: {ItemId: 4, name:Time Machine}, quantity:1},
    }
```
10. Delete an item  

        mvn exec:java -Dexec.args="del 1 3"

11. Get the order now  

        mvn exec:java -Dexec.args="get 1"

12. Your output should be like this  
    `<order><orderId>1</orderId><orderItem><item><itemId>4</itemId><name>Time Machine</name></item><quantity>1</quantity></orderItem></order>`

13. Try to get non existing order, add non existing item and so on

        mvn exec:java -Dexec.args="get 10000000"

14. Your output should be like this

    `<api-error><message>Order 10000000 not found!</message></api-error>`

15. Undeploy the quickstart:

        mvn clean -Pdeploy


Wildfly
----------
1. Start Wildfly in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the quickstart

        mvn -Dmaven.test.skip=true install -Pdeploy,wildfly

3. Open a console windows and type  

        mvn exec:java -Dexec.args="new"

4. You should see the following output  
`<order><orderId>1</orderId></order>`
5. Switch back to server console or log and these messages should be displayed  
```
    Added item 1 with name Hydrogen Atom - No, we are not kidding!
    Added item 2 with name Handcrafted Copper Plate
    Added item 3 with name Einstein's Bust - Talks about your future :)
    Added item 4 with name Time Machine
```
Please note it is normal to see WARN messages as below when adding items as the addItem method has two input parameters
```
    Default RESTEasy Message Composer doesn't handle multiple input parameters.
```
6. Add an item to the order created in step 5  

        mvn exec:java -Dexec.args="add 1 3 10"

7. Switch back to server console or log and these messages should be displayed  
```
        Order after adding items: {OrderId: 1
        Items:{Item: {ItemId: 3, name:Einstein's Bust - Talks about your future :)}, quantity:10},
    }  
```
8. Add another item  

        mvn exec:java -Dexec.args="add 1 4 1"

9. Switch back to server console or log and these messages should be displayed  
```
        Order after adding items: {OrderId: 1
        Items:{Item: {ItemId: 3, name:Einstein's Bust - Talks about your future :)}, quantity:10},{Item: {ItemId: 4, name:Time Machine}, quantity:1},
    }
```
10. Delete an item  

        mvn exec:java -Dexec.args="del 1 3"

11. Get the order now  

        mvn exec:java -Dexec.args="get 1"

12. Your output should be like this  
    `<order><orderId>1</orderId><orderItem><item><itemId>4</itemId><name>Time Machine</name></item><quantity>1</quantity></orderItem></order>`

13. Try to get non existing order, add non existing item and so on

        mvn exec:java -Dexec.args="get 10000000"

14. Your output should be like this

    `<api-error><message>Order 10000000 not found!</message></api-error>`

15. Undeploy the quickstart:

        mvn clean -Pdeploy,wildfly


Karaf
----------
1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the rest-binding quickstart :

karaf@root> features:install switchyard-quickstart-rest-binding

3. Open a console windows and type  

        mvn exec:java -Pkaraf -Dexec.args="new"

4. You should see the following output  
`<order><orderId>1</orderId></order>`
5. Switch back to server console or log and these messages should be displayed  
```
Added item 1 with name Hydrogen Atom - No, we are not kidding!
Added item 2 with name Handcrafted Copper Plate
Added item 3 with name Einstein's Bust - Talks about your future :)
Added item 4 with name Time Machine
```
Please note it is normal to see WARN messages as below when adding items as the addItem method has two input parameters
```
Default RESTEasy Message Composer doesn't handle multiple input parameters.
```
6. Add an item to the order created in step 5  

        mvn exec:java -Pkaraf -Dexec.args="add 1 3 10"

7. Switch back to server console or log and these messages should be displayed  
```
Order after adding items: {OrderId: 1
Items:{Item: {ItemId: 3, name:Einstein's Bust - Talks about your future :)}, quantity:10},
}  
```
8. Add another item  

        mvn exec:java -Pkaraf -Dexec.args="add 1 4 1"

9. Switch back to server console or log and these messages should be displayed  
```
Order after adding items: {OrderId: 1
Items:{Item: {ItemId: 3, name:Einstein's Bust - Talks about your future :)}, quantity:10},{Item: {ItemId: 4, name:Time Machine}, quantity:1},
}
```
10. Delete an item  

        mvn exec:java -Pkaraf -Dexec.args="del 1 3"

11. Get the order now  

        mvn exec:java -Pkaraf -Dexec.args="get 1"

12. Your output should be like this  
`<order><orderId>1</orderId><orderItem><item><itemId>4</itemId><name>Time Machine</name></item><quantity>1</quantity></orderItem></order>`

13. Try to get non existing order, add non existing item and so on

        mvn exec:java -Pkaraf -Dexec.args="get 10000000"

14. Your output should be like this

    `<api-error><message>Order 10000000 not found!</message></api-error>`

15. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-rest-binding




## Further Reading

1. [RESTEasy Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/RESTEasy)
