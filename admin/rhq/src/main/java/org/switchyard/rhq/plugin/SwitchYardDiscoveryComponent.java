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
package org.switchyard.rhq.plugin;

import static org.switchyard.rhq.plugin.SwitchYardConstants.DESCRIPTION_SWITCH_YARD;
import static org.switchyard.rhq.plugin.SwitchYardConstants.KEY_SWITCH_YARD;
import static org.switchyard.rhq.plugin.SwitchYardConstants.NAME_SWITCH_YARD;
import static org.switchyard.rhq.plugin.SwitchYardConstants.INVOCATION_FAILURE_DESCRIPTION;
import static org.switchyard.rhq.plugin.SwitchYardConstants.INVOCATION_OUTCOME;
import static org.switchyard.rhq.plugin.SwitchYardConstants.INVOCATION_RESULT;
import static org.switchyard.rhq.plugin.SwitchYardConstants.OUTCOME_SUCCESS;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.pluginapi.inventory.DiscoveredResourceDetails;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryComponent;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryContext;
import org.rhq.modules.plugins.jbossas7.ASConnection;
import org.rhq.modules.plugins.jbossas7.BaseServerComponent;
import org.rhq.modules.plugins.jbossas7.json.Operation;
import org.switchyard.rhq.plugin.model.GetVersionResult;
import org.switchyard.rhq.plugin.operations.GetVersion;

/**
 * SwitchYard Discovery Component
 */
public class SwitchYardDiscoveryComponent implements ResourceDiscoveryComponent<BaseServerComponent<?>> {
    /**
     * The logger instance.
     */
    private static final Log LOG = LogFactory.getLog(SwitchYardDiscoveryComponent.class);

    /**
     * The JSON object mapper
     */
    private static final ObjectMapper mapper = new ObjectMapper();
    
    /**
     * Discover existing resources.
     * 
     * @param context
     *            The context for the current discovery component.
     * @return The discovered resources.
     */
    @Override
    public Set<DiscoveredResourceDetails> discoverResources(final ResourceDiscoveryContext<BaseServerComponent<?>> context) {

        final HashSet<DiscoveredResourceDetails> discoveredResources = new HashSet<DiscoveredResourceDetails>();

        final GetVersionResult result = execute(context.getParentResourceComponent(), new GetVersion(), GetVersionResult.class);

        if (result != null) {
            final Configuration pluginConfig = context.getDefaultPluginConfiguration();

            final DiscoveredResourceDetails resource = new DiscoveredResourceDetails(
                    context.getResourceType(), KEY_SWITCH_YARD,
                    NAME_SWITCH_YARD, result.getVersion(),
                    DESCRIPTION_SWITCH_YARD, pluginConfig, null);

            discoveredResources.add(resource);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Discovered SwitchYard subsystem for " + context.getPluginContainerName());
            }
        }

        return discoveredResources;
    }

    public static <T> T execute(final BaseServerComponent<?> server,
            final Operation operation, final Class<T> clazz) {

        final ASConnection asConnection = server.getASConnection();

        final JsonNode jsonNode = asConnection.executeRaw(operation);

        if (jsonNode == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Receiver null response to operation " + operation);
            }
            return null;
        }

        final JsonNode outcome = jsonNode.findValue(INVOCATION_OUTCOME);
        if ((outcome == null) || !OUTCOME_SUCCESS.equals(outcome.asText())) {
            if (LOG.isDebugEnabled()) {
                final JsonNode failureDescription = jsonNode.findValue(INVOCATION_FAILURE_DESCRIPTION);
                if (failureDescription != null) {
                    LOG.debug("Invocation failed for operation " + operation + ": " + failureDescription.asText());
                } else {
                    LOG.debug("Invocation failed for operation " + operation);
                }
            }
            return null;
        }
        
        final JsonNode result = jsonNode.findValue(INVOCATION_RESULT);
        if (result == null) {
            return null;
        }
        
        final T response;
        try {
            response = mapper.readValue(result,clazz);
        } catch (final Exception ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Failure during deserialisation of operation " + operation, ex);
            }
            return null;
        }

        return response;
    }
}
