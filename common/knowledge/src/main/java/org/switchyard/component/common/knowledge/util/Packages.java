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
package org.switchyard.component.common.knowledge.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;

import org.drools.common.DroolsObjectOutputStream;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.definition.KnowledgePackage;
import org.switchyard.common.io.resource.Resource;
import org.switchyard.common.io.resource.SimpleResource;
import org.switchyard.common.type.Classes;

/**
 * Drools Package Utilities.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class Packages {

    /**
     * Creates a Drools KnowledgePackage from a File.
     * @param src the File
     * @return the KnowledgePackage
     * @throws IOException oops
     */
    public static KnowledgePackage create(File src) throws IOException {
        return create(new SimpleResource(src.toURI().toURL().toString()));
    }

    /**
     * Creates a Drools KnowledgePackage from a Resource.
     * @param res the Resource
     * @return the KnowledgePackage
     */
    public static KnowledgePackage create(Resource res) {
        return create(res, Classes.getClassLoader(Packages.class));
    }

    /**
     * Creates a Drools KnowledgePackage from a Resource.
     * @param res the Resource
     * @param loader the ClassLoader to use
     * @return the KnowledgePackage
     */
    public static KnowledgePackage create(Resource res, ClassLoader loader) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Resources.addResource(res, loader, kbuilder);
        Collection<KnowledgePackage> kpkgs = kbuilder.getKnowledgePackages();
        KnowledgeBaseFactory.newKnowledgeBase().addKnowledgePackages(kpkgs);
        return kpkgs.iterator().next();
    }

    /**
     * Creates a Drools KnowledgePackage from a File and writes it to another.
     * @param src the source File
     * @param dest the destination File
     * @return the KnowledgePackage
     * @throws IOException oops
     */
    public static KnowledgePackage write(File src, File dest) throws IOException {
        KnowledgePackage kpkg = create(src);
        write(kpkg, dest);
        return kpkg;
    }

    /**
     * Creates a Drools KnowledgePackage from a Resource and writes it to File.
     * @param res the Resource
     * @param dest the File
     * @return the KnowledgePackage
     * @throws IOException oops
     */
    public static KnowledgePackage write(Resource res, File dest) throws IOException {
        return write(res, dest, Classes.getClassLoader(Packages.class));
    }

    /**
     * Creates a Drools KnowledgePackage from a Resource and writes it to File.
     * @param res the Resource
     * @param dest the File
     * @param loader the ClassLoader to use
     * @return the KnowledgePackage
     * @throws IOException oops
     */
    public static KnowledgePackage write(Resource res, File dest, ClassLoader loader) throws IOException {
        KnowledgePackage kpkg = create(res, loader);
        write(kpkg, dest);
        return kpkg;
    }

    /**
     * Writes a Drools KnowledgePackage to File.
     * @param kpkg the KnowledgePackage
     * @param dest the File
     * @throws IOException oops
     */
    public static void write(KnowledgePackage kpkg, File dest) throws IOException {
        ObjectOutputStream oos = null;
        try {
            oos = new DroolsObjectOutputStream(new BufferedOutputStream(new FileOutputStream(dest)));
            oos.writeObject(kpkg);
        } finally {
            if (oos != null) {
                try {
                    oos.flush();
                    oos.close();
                } catch (Throwable t) {
                    // just to keep checkstyle happy
                    t.getMessage();
                }
            }
        }
    }

    /**
     * Runs this class as a utility application.
     * @param args The command ("write"), the source (probably .drl) file, and the destination package file.
     * @throws Exception oops
     */
    public static void main(String... args) throws Exception {
        if (args.length != 3) {
            throwUsage();
        }
        String cmd = args[0].trim().toLowerCase();
        if ("write".equals(cmd)) {
            File src = new File(args[1]);
            File dest = new File(args[2]);
            System.out.printf("%s: Creating KnowledgePackage from [%s] and writing to [%s].", Packages.class.getSimpleName(), src.getPath(), dest.getPath());
            write(src, dest);
        } else {
            throwUsage();
        }
    }

    private static void throwUsage() {
        String usage = String.format("Usage: %s write <src .drl> <dest .pkg>", Packages.class.getName());
        throw new IllegalArgumentException(usage);
    }

    private Packages() {}

}
