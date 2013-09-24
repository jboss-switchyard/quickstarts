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
package org.switchyard.common.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.switchyard.common.CommonCoreMessages;

/**
 * File utilities.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class Files {

    /**
     * Copies one file on top of the other.
     * @param source the source file
     * @param target the destination file
     * @throws IOException something went wanky
     */
    public static final void copy(File source, File target) throws IOException {
        copy(source, target, Buffers.DEFAULT_SIZE);
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

    /**
     * Deletes a file or folder. If a folder all contents will be deleted recursively.
     * @param source the source file
     * @throws IOException something went wanky
     */
    public static final void delete(File source) throws IOException {
        if ((source != null) && (source.exists())) {
            if (source.isDirectory()) {
                for (File f : source.listFiles()) {
                    delete(f);
                }
            }
            if (!source.delete()) {
                throw CommonCoreMessages.MESSAGES.couldNotDeleteFile(source.getName());
            }
        }
    }

    private Files() {}

}
