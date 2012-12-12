package org.jboss.as.console.client.domain.runtime;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.hosts.ServerPicker;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.SubsystemMetaData;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.widgets.nav.Predicate;
import org.jboss.ballroom.client.layout.LHSNavTree;
import org.jboss.ballroom.client.layout.LHSNavTreeItem;
import org.jboss.ballroom.client.widgets.stack.DisclosureStackPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/2/11
 */
class DomainRuntimeNavigation {

    private VerticalPanel stack;
    private VerticalPanel layout;

    private ServerPicker serverPicker;

    private List<Predicate> metricPredicates = new ArrayList<Predicate>();
    private List<Predicate> runtimePredicates = new ArrayList<Predicate>();

    private LHSNavTree metricTree;
    private LHSNavTree runtimeTree;

    private ScrollPanel scroll;

    public Widget asWidget()
    {
        layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        stack = new VerticalPanel();
        stack.setStyleName("fill-layout-width");


        // ----------------------------------------------------

        serverPicker = new ServerPicker();
        stack.add(serverPicker.asWidget());

        // ----------------------------------------------------


        Tree statusTree = new LHSNavTree("domain-runtime");

        LHSNavTreeItem serverInstances=
                new LHSNavTreeItem(Console.CONSTANTS.common_label_serverInstances(), NameTokens.InstancesPresenter);


        LHSNavTreeItem jvm = new LHSNavTreeItem("JVM Status", NameTokens.HostVMMetricPresenter);

        statusTree.addItem(serverInstances);
        statusTree.addItem(jvm);

        DisclosurePanel statusPanel  = new DisclosureStackPanel("Domain Status").asWidget();
        statusPanel.setContent(statusTree);
        stack.add(statusPanel);

        // -------------

        metricTree = new LHSNavTree("domain-runtime");

        LHSNavTreeItem datasources = new LHSNavTreeItem("Datasources", NameTokens.DataSourceMetricPresenter);
        LHSNavTreeItem jmsQueues = new LHSNavTreeItem("JMS Destinations", NameTokens.JmsMetricPresenter);
        LHSNavTreeItem web = new LHSNavTreeItem("Web", NameTokens.WebMetricPresenter);
        LHSNavTreeItem tx = new LHSNavTreeItem("Transactions", NameTokens.TXMetrics);
        LHSNavTreeItem jpa = new LHSNavTreeItem("JPA", NameTokens.JPAMetricPresenter);

        metricPredicates.add(new Predicate("datasources", datasources));
        metricPredicates.add(new Predicate("messaging", jmsQueues));
        metricPredicates.add(new Predicate("web", web));
        metricPredicates.add(new Predicate("transactions", tx));
        metricPredicates.add(new Predicate("jpa", jpa));
        
        metricPredicates.addAll(SubsystemMetaData.getRuntimeMetricsExtensions());

        DisclosurePanel metricPanel  = new DisclosureStackPanel("Subsystem Metrics").asWidget();
        metricPanel.setContent(metricTree);
        stack.add(metricPanel);

        // ---

        runtimeTree = new LHSNavTree("domain-runtime");


        LHSNavTreeItem osgi = new LHSNavTreeItem("OSGi", NameTokens.OSGiRuntimePresenter);

        runtimePredicates.add(new Predicate("osgi", osgi));

        runtimePredicates.addAll(SubsystemMetaData.getRuntimeOperationsExtensions());

        DisclosurePanel runtimeOpPanel = new DisclosureStackPanel("Runtime Operations").asWidget();
        runtimeOpPanel.setContent(runtimeTree);
        stack.add(runtimeOpPanel);


        // ----------------------------------------------------

        Tree deploymentTree = new LHSNavTree("domain-runtime");
        DisclosurePanel deploymentPanel  = new DisclosureStackPanel("Deployments").asWidget();
        deploymentPanel.setContent(deploymentTree);

        deploymentTree.addItem(new LHSNavTreeItem("Manage Deployments", NameTokens.DeploymentsPresenter));
        deploymentTree.addItem(new LHSNavTreeItem("Webservices", NameTokens.WebServiceRuntimePresenter));

        stack.add(deploymentPanel);

        layout.add(stack);


        scroll = new ScrollPanel(layout);

        return scroll;
    }

    public void setHosts(List<Host> hosts) {

        serverPicker.setHosts(hosts);

    }

    public void setServer(List<ServerInstance> server) {

        serverPicker.setServers(server);
    }

    public void setSubsystems(List<SubsystemRecord> subsystems) {


        metricTree.removeItems();
        runtimeTree.removeItems();

        // match subsystems
        for(SubsystemRecord subsys : subsystems)
        {

            //System.out.println(subsys.getKey());

            for(Predicate predicate : metricPredicates)
            {
                if(predicate.matches(subsys.getKey()))
                    metricTree.addItem(predicate.getNavItem());
            }

            for(Predicate predicate : runtimePredicates)
            {
                if(predicate.matches(subsys.getKey()))
                    runtimeTree.addItem(predicate.getNavItem());
            }
        }

    }


    public void clearSelection() {
        serverPicker.clearSelection();
    }

    public void setSelectedServer(String hostName, ServerInstance server) {
        serverPicker.setSelected(server, true);
    }

}
