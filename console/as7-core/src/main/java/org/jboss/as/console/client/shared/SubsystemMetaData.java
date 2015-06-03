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

package org.jboss.as.console.client.shared;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.plugins.SubsystemExtension;
import org.jboss.as.console.client.plugins.SubsystemRegistry;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.widgets.nav.Predicate;

import com.google.gwt.core.client.GWT;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 3/29/11
 */
@Deprecated
public class SubsystemMetaData {

    static Map<String, SubsystemGroup> groups = new LinkedHashMap<String, SubsystemGroup>();
    static List<Predicate> runtimeMetricsExtensions;
    static List<Predicate> runtimeOperationsExtensions;

    private static final String CONNECTOR = "Connector";

    private static final String MESSAGING = "Messaging";

    private static final String CORE = "Core";

    private static final String CONTAINER = "Container";

    private static final String OSGI = "OSGi";

    private static final String INFINISPAN = "Infinispan";

    private static final String SECURITY = "Security";

    private static final String WEB = "Web";

    private static final String OTHER = "Other";

    static {

        // specify groups and the order they appear
        groups.put(CORE, new SubsystemGroup(CORE));
        groups.put(CONNECTOR, new SubsystemGroup(CONNECTOR));
        groups.put(MESSAGING, new SubsystemGroup(MESSAGING));
        groups.put(CONTAINER, new SubsystemGroup(CONTAINER));
        groups.put(SECURITY, new SubsystemGroup(SECURITY));
        groups.put(WEB, new SubsystemGroup(WEB));
        groups.put(OSGI, new SubsystemGroup(OSGI));
        groups.put(INFINISPAN, new SubsystemGroup(INFINISPAN));
        groups.put(OTHER, new SubsystemGroup(OTHER));

        // assign actual subsystems
        groups.get(CONNECTOR).getItems().add(new SubsystemGroupItem("JCA", "jca"));
        groups.get(CONNECTOR).getItems().add(new SubsystemGroupItem("Datasources", "datasources"));
        groups.get(CONNECTOR).getItems().add(new SubsystemGroupItem("Resource Adapters", "resource-adapters"));
        groups.get(CONNECTOR).getItems().add(new SubsystemGroupItem("Connector", "connector",Boolean.TRUE));
        groups.get(CONNECTOR).getItems().add(new SubsystemGroupItem("Mail", "mail"));

        groups.get(WEB).getItems().add(new SubsystemGroupItem("Servlet/HTTP", "web"));
        groups.get(WEB).getItems().add(new SubsystemGroupItem("Web Services", "webservices"));
        groups.get(WEB).getItems().add(new SubsystemGroupItem("JAXRS", "jaxrs",Boolean.TRUE));
        groups.get(WEB).getItems().add(new SubsystemGroupItem("mod_cluster", "modcluster", NameTokens.ModclusterPresenter));

        groups.get(MESSAGING).getItems().add(new SubsystemGroupItem("Destinations", "messaging"));

        groups.get(CORE).getItems().add(new SubsystemGroupItem("Threads", "threads", Boolean.TRUE));
        groups.get(CORE).getItems().add(new SubsystemGroupItem("Logging", "logging"));
        groups.get(CORE).getItems().add(new SubsystemGroupItem("Deployment Scanners", "deployment-scanner"));
        groups.get(CORE).getItems().add(new SubsystemGroupItem("Remoting", "remoting",Boolean.TRUE));
        groups.get(CORE).getItems().add(new SubsystemGroupItem("Threads", NameTokens.BoundedQueueThreadPoolPresenter));
        groups.get(CORE).getItems().add(new SubsystemGroupItem("JMX", "jmx"));
        groups.get(CORE).getItems().add(new SubsystemGroupItem("Config Admin", NameTokens.ConfigAdminPresenter));
        groups.get(CORE).getItems().add(new SubsystemGroupItem("JGroups", NameTokens.JGroupsPresenter));

        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("Naming", "naming", !Console.getBootstrapContext().isStandalone()));
        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("EJB 3", "ejb3"));
        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("EE", "ee"));
        //groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("Transactions", "transactions"));
        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("Weld", "weld",Boolean.TRUE));
        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("JPA", "jpa"));
        groups.get(CONTAINER).getItems().add(new SubsystemGroupItem("JacORB", "jacorb"));

        groups.get(SECURITY).getItems().add(new SubsystemGroupItem("Security Subsystem", "security", NameTokens.SecuritySubsystemPresenter));
        groups.get(SECURITY).getItems().add(new SubsystemGroupItem("Security Domains", "security", NameTokens.SecurityDomainsPresenter));

        groups.get(OSGI).getItems().add(new SubsystemGroupItem("Framework", "osgi", NameTokens.OSGiConfigurationPresenter));

        groups.get(INFINISPAN).getItems().add(new SubsystemGroupItem("Cache Containers", NameTokens.Infinispan, NameTokens.CacheContainerPresenter));
        groups.get(INFINISPAN).getItems().add(new SubsystemGroupItem("Local Caches", NameTokens.Infinispan, NameTokens.LocalCachePresenter));
        groups.get(INFINISPAN).getItems().add(new SubsystemGroupItem("Invalidation Caches", NameTokens.Infinispan, NameTokens.InvalidationCachePresenter));
        groups.get(INFINISPAN).getItems().add(new SubsystemGroupItem("Replicated Caches", NameTokens.Infinispan, NameTokens.ReplicatedCachePresenter));
        groups.get(INFINISPAN).getItems().add(new SubsystemGroupItem("Distributed Caches", NameTokens.Infinispan, NameTokens.DistributedCachePresenter));

        groups.get(OTHER).getItems().add(new SubsystemGroupItem("SAR", "sar",Boolean.TRUE));
        groups.get(OTHER).getItems().add(new SubsystemGroupItem("Arquillian", "arquillian",Boolean.TRUE));


        SubsystemExtensionProcessor extensionProcessor = GWT.create(SubsystemExtensionProcessor.class);
        extensionProcessor.processProfileExtensions(groups);
        runtimeMetricsExtensions = extensionProcessor.getRuntimeMetricsExtensions();
        runtimeOperationsExtensions = extensionProcessor.getRuntimeOperationsExtensions();
    }

    public static void bootstrap(SubsystemRegistry registry) {

        List<SubsystemExtension> defaults = new ArrayList<SubsystemExtension>();

        for(String groupName : groups.keySet())
        {
            SubsystemGroup group = groups.get(groupName);
            for(SubsystemGroupItem item : group.getItems())
            {
                if(!item.isDisabled())
                {
                    defaults.add(new SubsystemExtension(
                            item.getName(), item.getPresenter(),
                            group.getName(), item.getKey())
                    );
                }
            }
        }

        registry.getExtensions().addAll(defaults);

    }

    public static Map<String, SubsystemGroup> getGroups() {
        return groups;
    }

    /**
     * @return the runtime extensions
     */
    public static List<Predicate> getRuntimeMetricsExtensions() {
        return runtimeMetricsExtensions;
    }

    /**
     * @return the runtime extensions
     */
    public static List<Predicate> getRuntimeOperationsExtensions() {
        return runtimeOperationsExtensions;
    }

    public static SubsystemGroup getGroupForKey(String subsysKey)
    {
        SubsystemGroup matchingGroup = null;

        for(String groupName : groups.keySet())
        {
            SubsystemGroup group = groups.get(groupName);
            for(SubsystemGroupItem item : group.getItems())
            {
                if(item.getKey().equals(subsysKey)
                        && item.isDisabled() == false)
                {
                    matchingGroup =  group;
                    break;
                }
            }

            if(matchingGroup!=null)
                break;
        }

        // found one?
        if(null==matchingGroup)
            matchingGroup = groups.get(OTHER);

        return matchingGroup;
    }

    public static String[] getDefaultSubsystem(String preferred, List<SubsystemRecord> existing)
    {
        if(existing.isEmpty())
            throw new RuntimeException("No subsystem provided!");

        SubsystemRecord chosen = null;
        for(SubsystemRecord subsys : existing)
        {
            if(subsys.getKey().equals(preferred))
            {
                chosen = subsys;
                break;
            }
        }

        if(null==chosen)
            chosen = existing.get(0);


        return resolveTokens(chosen.getKey());
    }

    public static String[] resolveTokens(String key) {
        String[] token = new String[2];

        final SubsystemRegistry subsystemRegistry = Console.MODULES.getSubsystemRegistry();
        for(SubsystemExtension ext : subsystemRegistry.getExtensions())
        {
            if(ext.getKey().equals(key))
            {
                token[0] = ext.getName();
                token[1] = ext.getToken();
                break;
            }
        }

        /*for(String groupName : groups.keySet())
        {
            SubsystemGroup group = groups.get(groupName);
            for(SubsystemGroupItem item : group.getItems())
            {
                if(item.getKey().equals(key)
                        && item.isDisabled() == false)
                {
                    token[0] = item.getName();
                    token[1] = item.getPresenter();
                    break;
                }
            }
        } */

        return token;
    }
}
