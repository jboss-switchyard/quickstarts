/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.test.mixins.jca;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jboss.jca.core.spi.rar.ResourceAdapterRepository;
import org.jboss.jca.deployers.fungal.AbstractFungalDeployment;
import org.jboss.jca.deployers.fungal.RAActivator;
import org.jboss.jca.embedded.Embedded;
import org.jboss.jca.embedded.EmbeddedFactory;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.arjuna.ats.jta.common.JTAEnvironmentBean;
import com.github.fungal.api.Kernel;
import com.github.fungal.api.util.FileUtil;

/**
 * Handle IronJacamar embedded and fungal kernel.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class SwitchYardIronJacamarHandler {
    private static final String TEMPLATE_RA_XML = "jcamixin-ironjacamar-template-ra.xml";
    private static final String TEMP_OUT_DIR = System.getProperty("java.io.tmpdir")
                                                + File.separator + "switchyard.iron.jacamar";
    private static final String KERNEL_BEAN_NAME = "Kernel";
    private static final String RESOURCE_ADAPTER_REPOSITORY_BEAN_NAME = "ResourceAdapterRepository";
    private static final String RA_ACTIVATOR_BEAN_NAME = "RAActivator";
    
    private Embedded _embedded;
    private Kernel _kernel;
    private ResourceAdapterRepository _resourceAdapterRepository;
    private List<URL> _manualDeployments = new ArrayList<URL>();
    
    /**
     * Start the IronJacamar embedded.
     * 
     * @throws Throwable when it fails to start
     */
    public void startup() throws Throwable {
        _embedded = EmbeddedFactory.create();
        try {
            _embedded.startup();
        } catch (Throwable t) {
            // avoid SWITCHYARD-874 ...
            System.gc();
            _embedded.startup();
        }
        _kernel = _embedded.lookup(KERNEL_BEAN_NAME, Kernel.class);
    }

    /**
     * Deploy a {@link ResourceAdapterArchive}.
     * 
     * @param raa {@link ResourceAdapterArchive} to deploy
     * @param connDefs a list of connection defintions
     * @throws Throwable failed to deploy
     */
    public void deploy(ResourceAdapterArchive raa, Map<String, String> connDefs) throws Throwable {
        File outdir = new File(TEMP_OUT_DIR);
        if (!outdir.exists()) {
            if (!outdir.mkdir()) {
                throw new RuntimeException("Failed to create directory: " + TEMP_OUT_DIR);
            }
        }
        
        if (connDefs != null && connDefs.size() != 0) {
            URL raxmlUrl = createRaXml(raa.getName(), connDefs);
            RAActivator activator = _embedded.lookup(RA_ACTIVATOR_BEAN_NAME, RAActivator.class);
            activator.setEnabled(false);
            _embedded.deploy(raa);
            _embedded.deploy(raxmlUrl);
            _manualDeployments.add(raxmlUrl);
            activator.setEnabled(true);
        } else {
            _embedded.deploy(raa);
        }
    }
    
    /**
     * Shutdown the IronJacamar embedded.
     * 
     * @throws Throwable when it fails to shutdown
     */
    public void shutdown() throws Throwable {
        try {
            for (URL d : _manualDeployments) {
                try {
                    _embedded.undeploy(d);
                    new File(d.toURI()).delete();
                } catch (Throwable t) {
                    t.getMessage(); // ignore
                }
            }
            _embedded.shutdown();
        } finally {
            new FileUtil().delete(new File(TEMP_OUT_DIR));
            _kernel = null;
            _embedded = null;
        }
    }
    
    /**
     * Get {@link Embedded}.
     * 
     * @return {@link Embedded} instance
     */
    public Embedded getEmbeddedInstance() {
        return _embedded;
    }
    
    /**
     * Get {@link ResourceAdapterRepository}.
     * 
     * @return {@link ResourceAdapterRepository} instance
     * @throws Throwable when it fails to acquire
     */
    public ResourceAdapterRepository getResourceAdapterRepository() throws Throwable {
        if (_resourceAdapterRepository != null) {
            return _resourceAdapterRepository;
        }
        
        _resourceAdapterRepository = _embedded.lookup(RESOURCE_ADAPTER_REPOSITORY_BEAN_NAME, ResourceAdapterRepository.class);
        return _resourceAdapterRepository;
    }

    /**
     * Get {@link JTAEnvironmentBean}.
     * 
     * @return Instance of environment bean.
     * @throws Throwable When lookup of JTAEnvironmentBean fails.
     */
    public JTAEnvironmentBean getEnvironmentBean() throws Throwable {
        return _embedded.lookup("JTAEnvironmentBean", JTAEnvironmentBean.class);
    }

    /**
     * Get ResourceAdapter uniqueId.
     * 
     * @param raName ResourceAdapter name to find
     * @return uniqueId
     * @throws Exception failed to acquire
     */
    public String getResourceAdapterIdentifier(String raName) throws Exception {
        List<?> deployments = getFieldValue(_kernel, List.class, "deployments");
        for (Object deploy : deployments) {
            if (deploy instanceof AbstractFungalDeployment) {
                if (getFieldValue(deploy, boolean.class, "activator")
                        && getFieldValue(deploy, String.class, "deploymentName").equals(raName)) {
                    return getFieldValue(deploy, String.class, "raKey");
                }
            }
        }
        return null;
    }
    
    /**
     * Create *-ra.xml from the template to bind ConnectionFactory to JNDI.
     */
    private URL createRaXml(String raName, Map<String, String> connDefs) throws Exception {
        // TODO support multiple connection definition
        String cfJndi = connDefs.keySet().toArray(new String[0])[0];
        String cfClass = connDefs.get(cfJndi);
        
        InputStream template = Thread.currentThread().getContextClassLoader().getResourceAsStream(TEMPLATE_RA_XML);
        Document doc = DocumentBuilderFactory.newInstance()
                                                .newDocumentBuilder()
                                                .parse(template);

        Node archive = doc.getElementsByTagName("archive").item(0);
        archive.setTextContent(raName);
        Node connection = doc.getElementsByTagName("connection-definition").item(0);
        NamedNodeMap attributes = connection.getAttributes();
        attributes.getNamedItem("class-name").setNodeValue(cfClass);
        attributes.getNamedItem("jndi-name").setNodeValue(cfJndi);
        
        String raxml = stripDotRarSuffix(raName);
        if (!raxml.endsWith("-ra")) {
            raxml += "-ra";
        }
        raxml += ".xml";
        File outFile = new File(TEMP_OUT_DIR, raxml);
        
        Transformer t = TransformerFactory.newInstance().newTransformer();
        Source src = new DOMSource(doc);
        Result res = new StreamResult(outFile);
        t.transform(src, res);

        return outFile.toURI().toURL();
    }
    
    private <T> T getFieldValue(Object target, Class<T> type, String name) throws Exception {
        Class<?> targetClass = target.getClass();
        while (targetClass != Object.class) {
            try {
                Field f = targetClass.getDeclaredField(name);
            if (type == Object.class || f.getType().isAssignableFrom(type)) {
                f.setAccessible(true);
                @SuppressWarnings("unchecked")
                T val = (T) f.get(target);
                f.setAccessible(false);
                return val;
            }
            } catch (Exception e) {
                e.getMessage(); // ignore and check the super class
            }
            targetClass = targetClass.getSuperclass();
        }

        return null;
    }
    
    private String stripDotRarSuffix(final String raName) {
        if (raName == null) {
            return null;
        }
        // See RaDeploymentParsingProcessor
        if (raName.endsWith(".rar")) {
            return raName.substring(0, raName.indexOf(".rar"));
        }
        return raName;
    }
}
