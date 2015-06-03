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
