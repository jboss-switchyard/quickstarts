/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.switchyard.component.camel.common.transaction;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.apache.camel.util.URISupport;
import org.springframework.transaction.PlatformTransactionManager;
import org.switchyard.SwitchYardException;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.component.camel.common.CamelConstants;

/**
 * Utility class which encapsulates logic related to operations needed to register
 * Spring {@link PlatformTransactionManager} and transaction policy beans necessary
 * in camel routes.
 */
public final class TransactionHelper {

    private final SwitchYardCamelContext _context;

    /**
     * Private constructor seen only from static method.
     * 
     * @param context Camel context instance.
     */
    private TransactionHelper(SwitchYardCamelContext context) {
        this._context = context;
    }

    private boolean isDefaultJtaTransactionName(final String tmName) {
        return tmName.equals(TransactionManagerFactory.TM);
    }

    private boolean isRegisteredInCamelRegistry(String beanName) {
        return _context.getRegistry().lookup(beanName) != null;
    }

    private void addToCamelRegistry() {
        final PlatformTransactionManager transactionManager = TransactionManagerFactory.getInstance().create();
        // Add the transaction manager
        _context.getWritebleRegistry().put(TransactionManagerFactory.TM, transactionManager);
        // Add a policy ref bean pointing to the transaction manager
        _context.getWritebleRegistry().put(CamelConstants.TRANSACTED_REF, new SpringTransactionPolicy(transactionManager));
    }

    private String getTransactionManagerName(URI uri) {
        try {
            final Map<String, Object> parseParameters = URISupport.parseParameters(uri);
            String name = (String) parseParameters.get("transactionManager");
            if (name != null) {
                name = name.replace("#", "");
            }
            return name;
        } catch (URISyntaxException e) {
            throw new SwitchYardException(e);
        }
    }

    private boolean process(URI endpointUri) {
        String transactionManagerName = getTransactionManagerName(endpointUri);
        if (transactionManagerName == null) {
            return false;
        }
        if (!isRegisteredInCamelRegistry(transactionManagerName) && isDefaultJtaTransactionName(transactionManagerName)) {
            addToCamelRegistry();
        }
        return true;
    }

    /**
     * Process component uri and register default transaction manager when necessary.
     * 
     * @param endpointUri Camel endpoint uri.
     * @param context Camel context instance.
     * @return True if transaction manager is necessary for endpoint.
     */
    public static boolean useTransactionManager(URI endpointUri, SwitchYardCamelContext context) {
        return new TransactionHelper(context).process(endpointUri);
    }

    /**
     * Process component uri and register default transaction manager when necessary.
     * 
     * @param endpointUri String with endpoint uri.
     * @param context Camel context instance.
     * @return True if transaction manager is necessary for endpoint.
     */
    public static boolean useTransactionManager(String endpointUri, SwitchYardCamelContext context) {
        return useTransactionManager(URI.create(endpointUri), context);
    }


}
