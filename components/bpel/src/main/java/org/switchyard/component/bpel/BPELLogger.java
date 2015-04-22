package org.switchyard.component.bpel;

import javax.xml.namespace.QName;
import org.jboss.logging.Cause;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
/**
 * <p/>
 * This file is using the subset 30800-31199 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface BPELLogger {
    /**
     * A root logger with the category of the package name.
     */
    BPELLogger ROOT_LOGGER = Logger.getMessageLogger(BPELLogger.class, BPELLogger.class.getPackage().getName());

    /**
     * initBPELComponent method definition.
     */
    @LogMessage(level = Level.INFO)
    @Message(id = 30800, value = "Init BPEL component")
    void initBPELComponent();

    /**
     * destroyBPELComponent method definition.
     */
    @LogMessage(level = Level.INFO)
    @Message(id = 30801, value = "Destroy BPEL component")
    void destroyBPELComponent();

    /**
     * failedToCloseBPELEngine method definition.
     * @param e the e
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 30802, value = "Failed to close BPEL engine")
    void failedToCloseBPELEngine(@Cause Exception e);

    /**
     * failedToObtainDeploymentNameFromURL method definition.
     * @param urlpath the urlpath
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 30803, value = "Failed to obtain deployment name from URL: %s")
    void failedToObtainDeploymentNameFromURL(String urlpath);

    /**
     * unableToLocateDeploymentDescriptorDeployXmlToDeriveDeploymentName method definition.
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 30804, value = "Unable to locate deployment descriptor (deploy.xml) to derive deployment name")
    void unableToLocateDeploymentDescriptorDeployXmlToDeriveDeploymentName();

    /**
     * deploymentNameIs method definition.
     * @param ret the ret
     */
    @LogMessage(level = Level.INFO)
    @Message(id = 30805, value = "Deployment name is: %s")
    void deploymentNameIs(String ret);

    /**
     * unableToResolveTheDeploymentURL method definition.
     * @param t the t
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 30806, value = "Unable to resolve the deployment URL")
    void unableToResolveTheDeploymentURL(@Cause java.lang.NoClassDefFoundError t);

    /**
     * unableToTransformPropertyValueIntoUndeployDelayValue method definition.
     * @param e the e
     * @param delayValue delayValue
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 30807, value = "Unable to transform property value '%s' into undeploy delay value")
    void unableToTransformPropertyValueIntoUndeployDelayValue(@Cause Exception e, String delayValue);

    /**
     * failedToUndeploy method definition.
     * @param serviceName the serviceName
     * @param e the e
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 30808, value = "Failed to undeploy '%s'")
    void failedToUndeploy(QName serviceName, @Cause Exception e);

    /**
     * noServiceReferencesFoundForProcess method definition.
     * @param localProcessName the localProcessName
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 30809, value = "No service references found for process '%s'")
    void noServiceReferencesFoundForProcess(String localProcessName);

    /**
     * noServiceFoundFor method definition.
     * @param serviceName the serviceName
     * @param portName the portName
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 30810, value = "No service found for '%s' (port %s)")
    void noServiceFoundFor(String serviceName, String portName);

    /**
     * noServiceFoundFor method definition.
     * @param serviceName the serviceName
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 30811, value = "No service found for '%s")
    void noServiceFoundFor(String serviceName);

}

