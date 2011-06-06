/*
 * See LICENSE.TXT.
 */
package org.apachextras.camel.jboss;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import org.apache.camel.impl.DefaultPackageScanClassResolver;
import org.apache.camel.spi.PackageScanFilter;

import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;
import org.jboss.vfs.VisitorAttributes;
import org.jboss.vfs.util.AbstractVirtualFileVisitor;

/**
 * JBoss specific package scan classloader to be used when Camel is running
 * inside JBoss Application Server.
 */
public class JBossPackageScanClassResolver extends DefaultPackageScanClassResolver {

    @Override
    protected void find(PackageScanFilter test, String packageName, ClassLoader loader, Set<Class<?>> classes) {
        if (log.isTraceEnabled()) {
            log.trace("Searching for: " + test + " in package: " + packageName + " using classloader: "
                    + loader.getClass().getName());
        }

        Enumeration<URL> urls;
        try {
            urls = getResources(loader, packageName);
            if (!urls.hasMoreElements()) {
                log.trace("No URLs returned by classloader");
            }
        } catch (IOException ioe) {
            log.warn("Cannot read package: " + packageName, ioe);
            return;
        }

        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();

            if (log.isTraceEnabled()) {
                log.trace("URL from classloader: " + url);
            }

            if (url.toString().startsWith("vfs:")) {
                try {
                    VirtualFile packageNode = VFS.getChild(url.toURI());
                    packageNode.visit(new MatchingClassVisitor(test, classes));
                } catch (IOException ioe) {
                    log.warn("Could not read entries in url: " + url, ioe);
                } catch (URISyntaxException use) {
                    log.warn("Could not read entries in url: " + url, use);
                }
            }
        }
    }

    private final class MatchingClassVisitor extends AbstractVirtualFileVisitor {
        private PackageScanFilter _filter;
        private Set<Class<?>> _classes;

        private MatchingClassVisitor(PackageScanFilter filter, Set<Class<?>> classes) {
            super(VisitorAttributes.RECURSE_LEAVES_ONLY);
            this._filter = filter;
            this._classes = classes;
        }

        public void visit(VirtualFile file) {
            if (file.getName().endsWith(".class")) {
                String fqn = file.getPathName();
                String qn;
                if (fqn.indexOf("jar/") != -1) {
                    qn = fqn.substring(fqn.indexOf("jar/") + 4);
                } else {
                    qn = fqn.substring(fqn.indexOf("/") + 1);
                }

                addIfMatching(_filter, qn, _classes);
            }
        }
    }

}
