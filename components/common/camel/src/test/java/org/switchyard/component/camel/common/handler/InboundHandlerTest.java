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
package org.switchyard.component.camel.common.handler;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

/**
 * Unit test for {@link InboundHandler}.
 * 
 * @author Daniel Bevenius
 */
public class InboundHandlerTest extends InboundHandlerTestBase {

    @Test
    public void reflexive() {
        final InboundHandler<?> x = createInboundHandler("direct://reflexive");
        assertThat(x.equals(x), is(true));
    }

    @Test
    public void symmetric() {
        final InboundHandler<?> x = createInboundHandler("direct://symmetric");
        final InboundHandler<?> y = createInboundHandler("direct://symmetric");

        assertThat(x.equals(y), is(true));
        assertThat(y.equals(x), is(true));
    }

    @Test
    public void nonSymmetric() {
        final InboundHandler<?> x = createInboundHandler("direct://symmetric");
        final InboundHandler<?> y = createInboundHandler("direct://nonsymmetric");

        assertThat(x.equals(y), is(false));
        assertThat(y.equals(x), is(false));
    }

    @Test
    public void transitive() {
        final InboundHandler<?> x = createInboundHandler("direct://transitive");
        final InboundHandler<?> y = createInboundHandler("direct://transitive");
        final InboundHandler<?> z = createInboundHandler("direct://transitive");

        assertThat(x.equals(y), is(true));
        assertThat(y.equals(z), is(true));
        assertThat(x.equals(z), is(true));
    }

    @Test
    public void nonTransitive() {
        final InboundHandler<?> x = createInboundHandler("direct://transitive");
        final InboundHandler<?> y = createInboundHandler("direct://nontransitive");
        final InboundHandler<?> z = createInboundHandler("direct://transitive");

        assertThat(x.equals(y), is(false));
        assertThat(y.equals(z), is(false));
        assertThat(x.equals(z), is(true));
    }

    @Test
    public void verifyEqualHashCode() {
        final InboundHandler<?> x = createInboundHandler("direct://hashcode");
        final InboundHandler<?> y = createInboundHandler("direct://hashcode");
        
        assertThat(x.hashCode(), is(y.hashCode()));
    }

    @Test
    public void verifyUnEqualHashCode() {
        final InboundHandler<?> x = createInboundHandler("direct://hashcode");
        final InboundHandler<?> y = createInboundHandler("direct://unEqualhashcode");

        assertThat(x.hashCode() == y.hashCode(), is(false));
    }

    @Test
    public void nullCheckForEqualsMethod() {
        final InboundHandler<?> x = createInboundHandler("direct://hashcode");
        assertThat(x.equals(null), is(false));
    }

    @Test
    public void hasTransactionManagerConfigured() {
        createInboundHandler("jms://queue?transactionManager=%23jtaTransactionMgr");
        assertThat(_camelContext.getRegistry().lookup("jtaTransactionMgr"), is(nullValue()));
        createInboundHandler("jms://GreetingServiceQueue?connectionFactory=%23&JmsXA&transactionManager=%23jtaTransactionManager");
        assertThat(_camelContext.getRegistry().lookup("jtaTransactionManager"), is(notNullValue()));
    }

    /**
     * Test covering a case when transaction manager is specified - tests route definition.
     * @throws Exception If route startup or shutdown fails.
     */
    @Test
    public void hasTransactionManager() throws Exception {
        mockTransaction("jtaTransactionManager");
        createInboundHandler("transaction://foo?transactionManager=#jtaTransactionManager");
        _camelContext.start();
        _camelContext.stop();
    }

}
