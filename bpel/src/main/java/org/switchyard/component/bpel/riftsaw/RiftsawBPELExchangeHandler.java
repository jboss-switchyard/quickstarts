/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.bpel.riftsaw;

import java.util.jar.JarEntry;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPFault;

import org.apache.log4j.Logger;
import org.riftsaw.engine.BPELEngine;
import org.riftsaw.engine.DeploymentUnit;
import org.riftsaw.engine.Fault;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.ServiceReference;
import org.switchyard.component.bpel.BPELFault;
import org.switchyard.component.bpel.config.model.BPELComponentImplementationModel;
import org.switchyard.component.bpel.exchange.BPELExchangeHandler;
import org.switchyard.exception.SwitchYardException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A Riftsaw implementation of a BPEL ExchangeHandler.
 *
 */
public class RiftsawBPELExchangeHandler extends BaseHandler implements BPELExchangeHandler {

    private static final int JAR_SUFFIX_LEN = 4;

    private static final String VFS_CONTENT = "vfs:/content/";

    private static final String DEPLOY_XML = "deploy.xml";

    private static final Logger LOG =
            Logger.getLogger(RiftsawBPELExchangeHandler.class);

    private BPELEngine _engine=null;
    private QName _serviceName=null;
    private javax.wsdl.Definition _wsdl=null;
    private javax.wsdl.PortType _portType=null;
    private String _version=null;
    private static java.util.Map<QName, QName> _serviceRefToCompositeMap=
                new java.util.HashMap<QName, QName>();
    private static java.util.Map<QName, DeploymentUnit> _deployed=
                        new java.util.HashMap<QName, DeploymentUnit>();

    /**
     * Constructs a new RiftSaw BPEL ExchangeHandler within the specified ServiceDomain.
     *
     */
    public RiftsawBPELExchangeHandler() {
    }

    /**
     * {@inheritDoc}
     */
    public void init(QName qname, BPELComponentImplementationModel model,
                 String intf, BPELEngine engine) {

        _engine = engine;
        _version = model.getVersion();

        _wsdl = WSDLHelper.getWSDLDefinition(intf);

        _portType = WSDLHelper.getPortType(intf, _wsdl);

        javax.wsdl.Service service =
                WSDLHelper.getServiceForPortType(_portType, _wsdl);

        _serviceName = service.getQName();

        // Check if composite is already been initialized for BPEL processes
        QName compositeName = model.getComponent().getComposite().getQName();

        if (!_serviceRefToCompositeMap.containsValue(compositeName)) {

            try {
                java.io.File deployFile =
                            getDeploymentDescriptor();

                DeploymentUnit bdu = new DeploymentUnit(qname.getLocalPart(),
                        _version, deployFile.lastModified(), deployFile);

                // Deploy the process
                engine.deploy(bdu);

                _deployed.put(qname, bdu);
            } catch (Exception e) {
                throw new SwitchYardException(e);
            }
        }

        _serviceRefToCompositeMap.put(qname, compositeName);
    }

    /**
     * This method returns the file associated with the BPEL deployment
     * descriptor.
     *
     * @return The deployment descriptor
     * @throws Exception Failed
     */
    private java.io.File getDeploymentDescriptor() throws Exception {
        java.io.File ret = null;

        java.net.URL url = Thread.currentThread().
                getContextClassLoader().getResource(DEPLOY_XML);
        int index = url.toString().indexOf(".jar");

        // Check if url contains a jar
        if (index != -1) {

            // TO-DO: Hopefully short term hack to overcome problem
            // that ODE needs jar unpacked to access artifacts -
            // hopefully switchyard can be configurable to expand jar
            // depending on the container. (RIFTSAW-435)
            if (url.toString().startsWith(VFS_CONTENT)) {
                // AS7 deployment
                String jarName = url.toString().substring(
                            VFS_CONTENT.length(),
                            index + JAR_SUFFIX_LEN);
                String jarFilePath =
                   System.getProperty("jboss.server.deploy.dir")
                              + "/../../deployments/" + jarName;

                String destPath =
                    System.getProperty("jboss.server.temp.dir")
                       + java.io.File.separatorChar
                       + "riftsaw"
                       + java.io.File.separatorChar
                       + jarName;
                java.io.File tmpdir =
                            new java.io.File(destPath);

                // Recursive delete in case already exists
                delete(tmpdir);

                tmpdir.mkdirs();

                java.util.jar.JarFile jarFile =
                        new java.util.jar.JarFile(jarFilePath);
                java.util.Enumeration<JarEntry> iter =
                        jarFile.entries();

                while (iter.hasMoreElements()) {
                    JarEntry entry = iter.nextElement();

                    String entryPath = destPath
                            + java.io.File.separatorChar;
                    entryPath += entry.getName();
                    java.io.File entryFile =
                            new java.io.File(entryPath);

                    if (entry.isDirectory()) {
                        entryFile.mkdirs();
                    } else {
                        java.io.InputStream is =
                          jarFile.getInputStream(entry);

                        java.io.FileOutputStream fos =
                            new java.io.FileOutputStream(entryFile);

                        byte[] b =
                            new byte[is.available()];
                        is.read(b);

                        fos.write(b);

                        fos.flush();
                        fos.close();
                        is.close();

                        if (entry.getName().equals(
                                "deploy.xml")) {
                            ret = entryFile;
                        }
                    }
                }

                jarFile.close();
            } else {
                throw new SwitchYardException(
                        "Unknown deployment environment");
            }
        } else {
            ret = new java.io.File(url.toURI());
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Deployment descriptor=" + ret);
        }

        return (ret);
    }

    /**
     * This method deletes the supplied file. If it
     * represents a directory, then the operation will
     * be performed recursively.
     *
     * @param file The file or directory to be deleted
     */
    private void delete(final java.io.File file) {
        if (file.isDirectory()) {
            for (java.io.File f : file.listFiles()) {
                delete(f);
            }
        }
        file.delete();
    }

    /**
     * {@inheritDoc}
     */
    public void start(ServiceReference serviceRef) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("START: " + serviceRef);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void handleMessage(final Exchange exchange) throws HandlerException {
        Node request = exchange.getMessage().getContent(Node.class);
        
        java.util.Map<String, Object> headers =
                  new java.util.HashMap<String, Object>();
        
        try {
            // Find part name associated with operation on port type
            javax.wsdl.Operation operation =
                    _portType.getOperation(exchange.getContract().
                            getServiceOperation().getName(),
                                null, null);

            Element newreq =
                    WSDLHelper.wrapRequestMessagePart((Element) request,
                                    operation);

            // Invoke the operation on the BPEL process
            Element response = _engine.invoke(_serviceName, null,
                    exchange.getContract().
                        getServiceOperation().getName(),
                            newreq, headers);

            if (exchange.getContract().getServiceOperation().
                    getExchangePattern().equals(ExchangePattern.IN_OUT)) {

                Message message = exchange.createMessage();

                // Strip off wrapper and part to just return
                // the part contents
                message.setContent(WSDLHelper.unwrapMessagePart(response));

                exchange.send(message);
            }
        } catch (Fault f) {
            SOAPFault fault = null;

            try {
                fault = javax.xml.soap.SOAPFactory.newInstance().
                        createFault("", f.getFaultName());

                Detail detail=fault.addDetail();
                Node cloned=detail.getOwnerDocument().importNode(WSDLHelper.unwrapMessagePart(f.getFaultMessage()), true);                    
                detail.appendChild(cloned);
        
            } catch (Exception e) {
                throw new HandlerException(e);
            }
            
            throw new BPELFault(fault);
            
        } catch (Exception e) {
            throw new HandlerException(e);
        }
    }

     /**
     * {@inheritDoc}
     */
    public void stop(ServiceReference serviceRef) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("STOP: "+serviceRef);
        }
        
        DeploymentUnit bdu=_deployed.get(serviceRef.getName());
        
        if (bdu != null) {            
            _engine.undeploy(bdu);
        }
        
        _serviceRefToCompositeMap.remove(serviceRef.getName());
    }

    /**
     * {@inheritDoc}
     */
    public void destroy(ServiceReference serviceRef) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("DESTROY: "+serviceRef);
        }
    }

}
