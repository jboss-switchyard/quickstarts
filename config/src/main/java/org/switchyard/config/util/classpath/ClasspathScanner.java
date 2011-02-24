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
package org.switchyard.config.util.classpath;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Classpath scanner.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ClasspathScanner {

    private static Logger _logger = Logger.getLogger(ClasspathScanner.class);
    private Filter _filter;

    /**
     * Public contractor.
     * @param filter Filter instance.
     */
    public ClasspathScanner(Filter filter) {
        this._filter = filter;
    }

    /**
     * Scan the specified URL.
     * <p/>
     * The URL can be a folder or an Archive file.
     * @param url URL to scan.
     * @throws IOException Error reading from URL target.
     */
    public void scan(URL url) throws IOException {
        String urlPath = url.getFile();

        urlPath = URLDecoder.decode(urlPath, "UTF-8");
        if (urlPath.startsWith("file:")) {
            urlPath = urlPath.substring(5);
        }

        if (urlPath.indexOf('!') > 0) {
            urlPath = urlPath.substring(0, urlPath.indexOf('!'));
        }

        File file = new File(urlPath);
        if (file.exists()) {
            if (file.isDirectory()) {
                handleDirectory(file, null);
            } else {
                handleArchive(file);
            }
        } else {
            _logger.warn("Unknown Classpath URL File '" +  file.getAbsolutePath() + "'.");
        }
    }

    private void handleArchive(File file) throws IOException {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Scanning archive: " + file.getAbsolutePath());
        }

        ZipFile zip = new ZipFile(file);
        Enumeration<? extends ZipEntry> entries = zip.entries();

        while (entries.hasMoreElements()) {
            if (!_filter.continueScanning()) {
                break;
            }

            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            _filter.filter(name);
        }
    }

    private void handleDirectory(File file, String path) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Scanning directory: " + file.getAbsolutePath());
        }

        for (File child : file.listFiles()) {
            if (!_filter.continueScanning()) {
                break;
            }

            String newPath = path == null ? child.getName() : path + '/' + child.getName();

            if (child.isDirectory()) {
                handleDirectory(child, newPath);
            } else {
                _filter.filter(newPath);
            }
        }
    }

}
