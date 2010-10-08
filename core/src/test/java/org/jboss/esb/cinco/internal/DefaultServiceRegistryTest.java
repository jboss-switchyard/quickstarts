package org.jboss.esb.cinco.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.esb.cinco.Service;
import org.junit.Test;

/**
 * Unit test for {@link DefaultServiceRegistry}
 * </p>
 * @author Daniel Bevenius
 *
 */
public class DefaultServiceRegistryTest
{
    @Test
    public void shouldBePossibleToSearchForNonRegisteredService()
    {
        DefaultServiceRegistry registry = new DefaultServiceRegistry();
        List<Service> services = registry.getServices(new QName("unRegisteredService"));
        assertThat(services.size(), is(0));
    }

}
