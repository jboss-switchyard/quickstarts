package org.switchyard.as7.extension.camel;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import org.apache.camel.impl.DefaultPackageScanClassResolver;
import org.apache.camel.spi.PackageScanFilter;
import org.jboss.logging.Logger;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;
import org.switchyard.as7.extension.ExtensionLogger;

/**
 * VFS-compatible version of DefaultPackageScanClassResolver.
 */
public class JBoss7PackageScanClassResolver extends DefaultPackageScanClassResolver {
    
    private static final Logger LOG = Logger.getLogger(JBoss7PackageScanClassResolver.class);
    
    protected void find(PackageScanFilter test, String packageName, ClassLoader loader, Set<Class<?>> classes) {
        if (LOG.isTraceEnabled()) {
            LOG.tracef("Searching for: %s in package: %s using classloader: %s", 
                    new Object[]{test, packageName, loader.getClass().getName()});
        }

        Enumeration<URL> urls;
        try {
            urls = getResources(loader, packageName);
            if (!urls.hasMoreElements()) {
                LOG.trace("No URLs returned by classloader");
            }
        } catch (IOException ioe) {
            ExtensionLogger.ROOT_LOGGER.cannotReadPackage(packageName, ioe);
            return;
        }

        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            LOG.tracef("URL from classloader: %s", url);
            
            if (url.toString().startsWith("vfs:")) {
                try {
                    VirtualFile vfsDir = VFS.getChild(url);
                    handleDirectory(vfsDir, null, classes, test);
                } catch (URISyntaxException uriEx) {
                    ExtensionLogger.ROOT_LOGGER.failedToParseURL(url.toString(), uriEx);
                }
            }
                
        }
    }
    
    private void handleDirectory(VirtualFile file, 
            String path, 
            Set<Class<?>> classes, 
            PackageScanFilter test) {

        for (VirtualFile child : file.getChildren()) {
            String newPath = (path == null) ? child.getName() : (path + '/' + child.getName());

            if (child.isDirectory()) {
                handleDirectory(child, newPath, classes, test);
            } else {
                handleFile(child, classes, test);
            }
        }
    }

    private void handleFile(VirtualFile file, Set<Class<?>> classes,  PackageScanFilter test) {
        if (file.getName().endsWith(".class")) {
            String fqn = file.getPathName();
            String qn;
            if (fqn.indexOf("jar/") != -1) {
                qn = fqn.substring(fqn.indexOf("jar/") + 4);
            } else {
                qn = fqn.substring(fqn.indexOf("/") + 1);
            }

            addIfMatching(test, qn, classes);
        }
    }
}
