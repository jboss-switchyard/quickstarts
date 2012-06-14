/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.common.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * File utilities.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class Files {

    /**
     * Default buffer size for Files operations.
     */
    public static final int DEFAULT_BUFFER_SIZE = 1024;

    /**
     * Copies one file on top of the other.
     * @param source the source file
     * @param target the destination file
     * @throws IOException something went wanky
     */
    public static final void copy(File source, File target) throws IOException {
        copy(source, target, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Copies one file on top of the other.
     * @param source the source file
     * @param target the destination file
     * @param bufferSize the buffer size to use
     * @throws IOException something went wanky
     */
    public static final void copy(File source, File target, int bufferSize) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(source));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target));
        byte[] buff = new byte[bufferSize];
        int read;
        try {
           while ((read = bis.read(buff)) != -1) {
              bos.write(buff, 0, read);
           }
        } finally {
            bos.flush();
            bos.close();
            bis.close();
        }
    }

    private Files() {}

}
