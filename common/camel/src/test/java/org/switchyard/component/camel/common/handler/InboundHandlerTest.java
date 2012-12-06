/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.common.handler;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

import java.net.URI;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Unit test for {@link InboundHandler}.
 * 
 * @author Daniel Bevenius
 */
public class InboundHandlerTest {

    final SwitchYardCamelContext _camelContext = new SwitchYardCamelContext();
    final Configuration _configuration = mock(Configuration.class);

    @Test
    public void reflexive() {
        final InboundHandler x = createInboundHandler("direct://reflexive");
        assertThat(x.equals(x), is(true));
    }

    @Test
    public void symmetric() {
        final InboundHandler x = createInboundHandler("direct://symmetric");
        final InboundHandler y = createInboundHandler("direct://symmetric");
        assertThat(x.equals(y), is(true));
        assertThat(y.equals(x), is(true));
    }

    @Test
    public void nonSymmetric() {
        final InboundHandler x = createInboundHandler("direct://symmetric");
        final InboundHandler y = createInboundHandler("direct://nonsymmetric");
        
        assertThat(x.equals(y), is(false));
        assertThat(y.equals(x), is(false));
    }

    @Test
    public void transitive() {
        final InboundHandler x = createInboundHandler("direct://transitive");
        final InboundHandler y = createInboundHandler("direct://transitive");
        final InboundHandler z = createInboundHandler("direct://transitive");

        assertThat(x.equals(y), is(true));
        assertThat(y.equals(z), is(true));
        assertThat(x.equals(z), is(true));
    }

    @Test
    public void nonTransitive() {
        final InboundHandler x = createInboundHandler("direct://transitive");
        final InboundHandler y = createInboundHandler("direct://nontransitive");
        final InboundHandler z = createInboundHandler("direct://transitive");

        assertThat(x.equals(y), is(false));
        assertThat(y.equals(z), is(false));
        assertThat(x.equals(z), is(true));
    }

    @Test
    public void verifyEqualHashCode() {
        final InboundHandler x = createInboundHandler("direct://hashcode");
        final InboundHandler y = createInboundHandler("direct://hashcode");
        
        assertThat(x.hashCode(), is(y.hashCode()));
    }

    @Test
    public void verifyUnEqualHashCode() {
        final InboundHandler x = createInboundHandler("direct://hashcode");
        final InboundHandler y = createInboundHandler("direct://unEqualhashcode");

        assertThat(x.hashCode() == y.hashCode(), is(false));
    }

    @Test
    public void nullCheckForEqualsMethod() {
        final InboundHandler x = createInboundHandler("direct://hashcode");
        assertThat(x.equals(null), is(false));
    }

    @Test
    public void hasTransactionManagerConfigured() {
        final InboundHandler handler = createInboundHandler("direct://dummy");
        String transactedRef = handler.getTransactionManagerName(URI.create("jms://queue?transactionManager=%23jtaTransactionMgr"));
        assertThat(transactedRef, is(equalTo("jtaTransactionMgr")));
        transactedRef = handler.getTransactionManagerName(URI.create("jms://GreetingServiceQueue?connectionFactory=%23&JmsXA&transactionManager=%23jtaTransactionManager"));
        assertThat(transactedRef, is(equalTo("jtaTransactionManager")));
    }

    private InboundHandler createInboundHandler(final String uri) {
        final V1BaseCamelBindingModel camelBindingModel = new V1BaseCamelBindingModel(_configuration, new Descriptor()) {
            @Override
            public URI getComponentURI() {
                return URI.create(uri);
            }
        };
        return new InboundHandler(camelBindingModel, _camelContext, new QName("dummyService"));
    }

}
