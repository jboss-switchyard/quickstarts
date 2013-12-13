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
package org.switchyard.component.bpel.riftsaw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPFault;

import org.jboss.logging.Logger;
import org.riftsaw.engine.BPELEngine;
import org.riftsaw.engine.DeploymentRef;
import org.riftsaw.engine.Fault;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.ServiceDomain;
import org.switchyard.component.bpel.BPELMessages;
import org.switchyard.component.bpel.exchange.BPELExchangeHandler;
import org.switchyard.component.common.label.EndpointLabel;
import org.switchyard.component.bpel.BPELLogger;
import org.switchyard.config.model.implementation.bpel.BPELComponentImplementationModel;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.SwitchYardException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A Riftsaw implementation of a BPEL ExchangeHandler.
 *
 */
public class RiftsawBPELExchangeHandler extends BaseServiceHandler implements BPELExchangeHandler {
    
    private static final int UNDEPLOY_DELAY = 10000;

    private static final String VFS_SCHEME = "vfs";

    private static final String DEPLOY_XML = "deploy.xml";

    private static final Logger LOG =
            Logger.getLogger(RiftsawBPELExchangeHandler.class);

    private BPELEngine _engine = null;
    private QName _serviceName = null;
    private QName _processName = null;
    private javax.wsdl.Definition _wsdl = null;
    private javax.wsdl.PortType _portType = null;
    private long _undeployDelay=UNDEPLOY_DELAY;
    private BPELComponentImplementationModel _model;
    private Properties _config;
    
    private static Map<QName, QName> _serviceRefToCompositeMap=
                new HashMap<QName, QName>();
    private static Map<QName, DeploymentRef> _deployed=
                        new HashMap<QName, DeploymentRef>();
    private static Timer _timer=new Timer();
    private static List<QName> _undeployed=new ArrayList<QName>();

    /**
     * Constructs a new RiftSaw BPEL ExchangeHandler within the specified ServiceDomain.
     * @param domain the service domain.
     */
    public RiftsawBPELExchangeHandler(final ServiceDomain domain) {
        super(domain);
    }

    /**
     * {@inheritDoc}
     */
    public void init(QName qname, BPELComponentImplementationModel model,
                 String intf, BPELEngine engine, Properties config) {

        _engine = engine;

        _wsdl = WSDLHelper.getWSDLDefinition(intf);

        _portType = WSDLHelper.getPortType(intf, _wsdl);

        _serviceName = qname;
        
        _processName = model.getProcessQName();
        
        _model = model;
        
        _config = config;
    }

    /**
     * This method returns the deployment name associated with the
     * deployed app.
     * 
     * @return The deployment name
     * @throws Exception Failed to obtain the deployment name
     */
    private String getDeploymentName() throws Exception {
        String ret=null;
        
        java.net.URL url = Thread.currentThread().
                getContextClassLoader().getResource(DEPLOY_XML);
        
        if (url != null) {
            String urlpath=url.toString();
            
            // Remove deploy.xml from end of path, removing an
            // extra character for the path separator
            urlpath = urlpath.substring(0, urlpath.length()-DEPLOY_XML.length()-1);
            
            int fileSeparatorIndex = urlpath.lastIndexOf('/');

            if (fileSeparatorIndex != -1) {
                ret = urlpath.substring(fileSeparatorIndex+1);
                
                int suffixIndex = ret.lastIndexOf('.');
                
                if (suffixIndex != -1) {
                    ret = ret.substring(0, suffixIndex);
                }
            } else {
                BPELLogger.ROOT_LOGGER.failedToObtainDeploymentNameFromURL(urlpath);
            }
        } else {
            BPELLogger.ROOT_LOGGER.unableToLocateDeploymentDescriptorDeployXmlToDeriveDeploymentName();
        }
        
        if (LOG.isDebugEnabled()) {
            BPELLogger.ROOT_LOGGER.deploymentNameIs(ret);
        }
        
        return (ret);
    }
    
    /**
     * This method returns the file associated with the BPEL deployment
     * archive or root folder.
     *
     * @return The deployment
     * @throws Exception Failed to obtain deployment folder/archive
     */
    private java.io.File getDeployment() throws Exception {
        java.io.File ret = null;

        java.net.URL url = Thread.currentThread().
                getContextClassLoader().getResource(DEPLOY_XML);
        int index = url.toString().indexOf(".jar");

        // Check if url contains a jar
        if (index != -1) {

            if (url.getProtocol().equals(VFS_SCHEME)) {
                // AS7 deployment
                try {
                    org.jboss.vfs.VirtualFile vfile=org.jboss.vfs.VFS.getChild(url.toURI());
                    
                    // Recursively get all files
                    List<org.jboss.vfs.VirtualFile> children=vfile.getParent().getChildrenRecursively();
                    for (org.jboss.vfs.VirtualFile child : children) {
                        // Need to request the physical file to have it expanded
                        // on the file system
                        child.getPhysicalFile();
                    }                   
                    
                    // Virtual file is for the deployment descriptor, so we need
                    // the parent file which represents the root of the deployment
                    ret = vfile.getPhysicalFile().getParentFile();
                    
                } catch (java.lang.NoClassDefFoundError t) {
                    BPELLogger.ROOT_LOGGER.unableToResolveTheDeploymentURL(t);
                }
                
            } else {
                throw BPELMessages.MESSAGES.unknownDeploymentEnvironment();
            }
        } else {
            // Retrieve parent folder of deployment descriptor
            ret = new java.io.File(url.toURI()).getParentFile();
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Deployment=" + ret);
        }

        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    protected void doStart() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("START: " + _serviceName);
        }
        // Setup configuration
        if (_config.containsKey("bpel.undeploy.delay")) {
            try {
                _undeployDelay = Long.parseLong(_config.getProperty("bpel.undeploy.delay"));
                
            } catch (Exception e) {
                BPELLogger.ROOT_LOGGER.unableToTransformPropertyValueIntoUndeployDelayValue(e, _config.getProperty("bpel.undeploy.delay"));
            }
        }
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("Undeployment delay="+_undeployDelay+"ms");
        }

        // Check if composite is already been initialized for BPEL processes
        QName compositeName = _model.getComponent().getComposite().getQName();

        if (!_serviceRefToCompositeMap.containsValue(compositeName)) {
            try {
                java.io.File deployFile=getDeployment();
    
                DeploymentRef ref=_engine.deploy(getDeploymentName(), deployFile);
    
                _deployed.put(_serviceName, ref);
                
                // Remove, in case marked for undeployment as part
                // of replacing an existing deployed jar
                _undeployed.remove(_serviceName);
            } catch (Exception e) {
                throw new SwitchYardException(e);
            }
        }

        SwitchYardPropertyFunction.setPropertyResolver(_processName, _model.getModelConfiguration().getPropertyResolver());
        _serviceRefToCompositeMap.put(_serviceName, compositeName);
    }

    /**
     * {@inheritDoc}
     */
    public void handleMessage(final Exchange exchange) throws HandlerException {
        Message message = exchange.getMessage();
        Node request = message.getContent(Node.class);

        Map<String, Object> headers = new HashMap<String, Object>();
        for (Property p : exchange.getContext().getProperties()) {
            if (p.hasLabel(EndpointLabel.SOAP.label())) {
                headers.put(p.getName(), p.getValue());
            }
        }

        try {
            // Find part name associated with operation on port type
            javax.wsdl.Operation operation =
                    _portType.getOperation(exchange.getContract().
                            getProviderOperation().getName(),
                                null, null);

            Element newreq =
                    WSDLHelper.wrapRequestMessagePart((Element) request,
                                    operation);

            // Invoke the operation on the BPEL process
            Element response = _engine.invoke(_serviceName, null,
                    exchange.getContract().
                    getProviderOperation().getName(),
                            newreq, headers);

            if (exchange.getContract().getProviderOperation().
                    getExchangePattern().equals(ExchangePattern.IN_OUT)) {

                Message reply = exchange.createMessage();

                // Strip off wrapper and part to just return
                // the part contents
                reply.setContent(WSDLHelper.unwrapMessagePart(response));
                
                // Set header parts for a response message
                for (Map.Entry<String, Object> e : headers.entrySet()) {
                    exchange.getContext(reply).setProperty(e.getKey(),
                            headers.get(e.getKey())).addLabels(EndpointLabel.SOAP.label());
                }                

                exchange.send(reply);
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
            
            Message msg = exchange.createMessage().setContent(fault);
            exchange.sendFault(msg);
            
        } catch (Exception e) {
            throw new HandlerException(e);
        }
    }

     /**
     * {@inheritDoc}
     */
    @Override
    protected void doStop() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("STOP: " + _serviceName);
        }
        
        _undeployed.add(_serviceName);
        
        if (_undeployDelay > 0) {
            _timer.schedule(new TimerTask() {
                public void run() {
                    undeploy();
                }
                
            }, _undeployDelay);
        } else {
            undeploy();
        }
        
        _serviceRefToCompositeMap.remove(_serviceName);
    }
    
    private void undeploy() {
        synchronized (_undeployed) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Checking whether to undeploy '"
                            +_serviceName+"'");
            }
            
            if (_undeployed.contains(_serviceName)
                        && _deployed.containsKey(_serviceName)) {
                DeploymentRef ref=_deployed.get(_serviceName);
                
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Undeploy '"
                            +_serviceName+"' with ref: "+ref);
                }

                if (ref != null) {  
                    try {
                        _engine.undeploy(ref);
                    } catch (Exception e) {
                        BPELLogger.ROOT_LOGGER.failedToUndeploy(_serviceName, e);
                    }
                }
                
                _deployed.remove(_serviceName);
                _undeployed.remove(_serviceName);
            }
            SwitchYardPropertyFunction.removePropertyResolver(_processName);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void destroy() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("DESTROY: " + _serviceName);
        }
    }

}
