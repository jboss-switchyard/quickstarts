# SwitchYard Test Utilities
This module contains a test utilities for SwitchYard. Things like MockHandlers, TestRunners and TestMixins are
all contained in this module.

----

## MinInx
### HornetQMixIn
The HornetQ MinIn enables tests to be run with a Embedded HornetQ server. This embedded server will be started once
per test and has static configuration files. But you also have the options to programmatically add/remove/query queues
if required.

#### Example of using the HornetQMixIn
    @RunWith(SwitchYardRunner.class)
    @SwitchYardTestCaseConfig(config = "switchyard.xml", mixins = {CDIMixIn.class, HornetQMixIn.class})
    public class HornetQExampleTest  {
    
        private SwitchYardTestKit _testKit;

        @Test
        public void testHornetQServiceBinding() throws HornetQException {
            final MockHandler mockHandler = _testKit.registerInOnlyService("HornetQService");
            final HornetQMixIn mixIn = _testKit.getMixIn(HornetQMixIn.class);
            final ClientProducer producer = mixIn.getClientSession().createProducer("jms.queue.consumer");
            producer.send(mixIn.createMessage("payload"));
        
            mockHandler.waitForOKMessage();
            final Object content = mockHandler.getMessages().poll().getMessage().getContent();
            final String string = new String((byte[])content);
            assertThat(string, is(equalTo("payload")));
        
            HornetQUtil.closeClientProducer(producer);
        }
    }
The HornetQMixIn will read its configuration from the files _hornetq-configuration.xml_ and _hornetq-jms.xml_ which should
be places in the root of you test classpath. This will most often be _src/test/resources/_. You can add queues any of these 
files and use them in test. In the example above we places the queue _jms.queue.consumer_ in hornetq-jms.xml like this:

    <queue name="consumer">
      <entry name="consumer"/>
    </queue>
    
If you'd rather create queues in your test then you can do this by using the ClientSession that the HornetQMixIn creates. 
For example, to create a queue for your test programatically:

    final ClientSession clientSession = hornetQMixIn.getClientSession();
    final SimpleString queueName = new SimpleString("testQueue");
    clientSession.createQueue(queueName, queueName);    
----  
    

