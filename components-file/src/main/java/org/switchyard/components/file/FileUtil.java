/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.components.file;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.CharBuffer;

public class FileUtil {
	
	/**
	 * Returns the content of a file as a string.  This is a just a temp
	 * example and definitely not recommended as a general purpose message
	 * processing approach.
	 * @param file file to read
	 * @return string containing files content
	 * @throws java.io.IOException kaboom!
	 */
	public static String readContent(File file) throws java.io.IOException {
		
		FileReader rfile = null;
		String content = null;
		
		try {
			rfile = new FileReader(file);
			CharBuffer buff = CharBuffer.allocate((int)file.length());
			rfile.read(buff);
			content = buff.toString();
		}
		finally {
			if (rfile != null) {
				rfile.close();
			}
		}
		
		return content;
	}
	
	public static void writeContent(String content, File dest) 
	throws IOException {
		FileWriter wfile = null;
		
		try {
			wfile = new FileWriter(dest);
			wfile.write(content);
		}
		finally {
			if (wfile != null) {
				wfile.close();
			}
		}
	}
}
