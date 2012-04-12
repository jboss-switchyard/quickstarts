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
package org.switchyard.tools.maven.plugins.mergeassembly;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Assembly Merge plugin.
 * <p/>
 * Merges multiple assembly.xml files based on &tl;include&gt; directives found in the
 * assembly files.  Supports a hierarchy of assembly files.
 *
 * @goal mergeassembly
 * @phase generate-sources
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class AssemblyMergeMojo extends AbstractMojo {

    /**
     * @parameter
     */
    private File baseAssembly;
    /**
     * @parameter
     */
    private File finalAssembly;

    private static DocumentBuilderFactory docBuilderFactory;
    private static TransformerFactory _transformerFactory = TransformerFactory.newInstance();

    static {
        docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        try {
            _transformerFactory.setAttribute("indent-number", Integer.valueOf(4));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setBaseAssembly(File baseAssembly) {
        this.baseAssembly = baseAssembly;
    }

    public void setFinalAssembly(File finalAssembly) {
        this.finalAssembly = finalAssembly;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (baseAssembly == null || !baseAssembly.isFile()) {
            throw new MojoExecutionException("'baseAssembly' must be defined.");
        }
        if (finalAssembly.exists() && !finalAssembly.isFile()) {
            throw new MojoExecutionException("'finalAssembly' configuration references a file path that cannot be overwritten.");
        }

        Document finalAssemblyDoc = processAssembly(baseAssembly);
        sort(finalAssemblyDoc);
        serialize(finalAssemblyDoc);
    }

    private Document processAssembly(File assemblyFile) throws MojoFailureException, MojoExecutionException {
        Document assemblyDoc = parseAssembly(assemblyFile);
        Element rootElement = assemblyDoc.getDocumentElement();
        NodeList childElements = rootElement.getChildNodes();
        List<IncludeAssembly> includes = new ArrayList<IncludeAssembly>();

        // Process all the <include> directives in the document (off the root element only)....
        int childCount = childElements.getLength();
        for (int i = 0; i < childCount; i++) {
            Node child = childElements.item(i);

            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) child;

                if (element.getLocalName().equals("include")) {
                    String nestedAssembly = element.getAttribute("assembly");

                    if (nestedAssembly == null || nestedAssembly.equals("")) {
                        throw new MojoExecutionException("Assembly file '" + assemblyFile.getAbsolutePath() + "' contains an invalid assembly <include> directive.  It doesn't define an 'assembly' attribute.");
                    }

                    // Recurse down...
                    Document includeAssemblyDoc = processAssembly(new File(assemblyFile.getParent(), nestedAssembly));
                    includes.add(new IncludeAssembly(element, includeAssemblyDoc));
                }
            }
        }

        // Merge in all <include> directives documents....
        for (IncludeAssembly include : includes) {
            processInclude(include);
        }

        return assemblyDoc;
    }

    private void processInclude(IncludeAssembly include) {
        Document includeAssemblyDoc = include.includeAssemblyDoc;
        NodeList assemblyNodes = includeAssemblyDoc.getDocumentElement().getChildNodes();
        int nodeCount = assemblyNodes.getLength();

        // Import the included doc nodes into the parent doc, inserting before the <include>...
        Element includeNode = include.includeNode;
        Node parentNode = includeNode.getParentNode();
        for (int i = 0; i < nodeCount; i++) {
            Node child = assemblyNodes.item(i);

            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element elementToMerge = (Element) child;
                if (mergeableElements.contains(elementToMerge.getLocalName())) {
                    mergeElementToParent(elementToMerge, parentNode, includeNode);
                }
            }
        }

        // Now zap the <include> element itself...
        parentNode.removeChild(includeNode);
    }

    private void mergeElementToParent(Element elementToMerge, Node parentNode, Element includeNode) {
        NodeList parentChildElement = parentNode.getChildNodes();
        int childCount = parentChildElement.getLength();

        // Look for a child element of the parent that matches the elementToMerge...
        for (int i = 0; i < childCount; i++) {
            Node target = parentChildElement.item(i);

            if (target.getNodeType() == Node.ELEMENT_NODE) {
                if (target.getLocalName().equals(elementToMerge.getLocalName())) {
                    // Found a match ("target").  Copy the contents of the elementToMerge
                    // into the target, before it's existing content...

                    NodeList mergeNodes = elementToMerge.getChildNodes();
                    int mergeNodesCount = mergeNodes.getLength();
                    Node insertPoint = target.getFirstChild();

                    for (int ii = 0; ii < mergeNodesCount; ii++) {
                        Node mergeNode = mergeNodes.item(ii);

                        mergeNode = parentNode.getOwnerDocument().importNode(mergeNode, true);
                        if (insertPoint != null) {
                            target.insertBefore(mergeNode, insertPoint);
                        } else {
                            target.appendChild(mergeNode);
                        }
                    }

                    return;
                }
            }
        }

        // There's no matching node in the parent, so add it before the insert node...
        Node target = parentNode.getOwnerDocument().importNode(elementToMerge, true);
        parentNode.insertBefore(target, includeNode);
    }

    private static final List<String> mergeableElements = Arrays.asList("moduleSets", "fileSets", "files", "dependencySets", "repositories", "componentDescriptors");

    private void sort(Document finalAssemblyDoc) {
        // The sort is not strictly necessary according to the schema.
        // Sort by simply removing and re-adding the elements in the order we want...
        for (String mergeableElement : mergeableElements) {
            readdElements(mergeableElement, finalAssemblyDoc);
        }
    }

    private void readdElements(String elementName, Document finalAssemblyDoc) {
        Element documentElement = finalAssemblyDoc.getDocumentElement();
        NodeList theElements = documentElement.getElementsByTagName(elementName);

        if (theElements.getLength() > 0) {
            List<Element> elementsList = new ArrayList<Element>();

            // Copy to a list so we don't get a concurrent mod...
            for (int i = 0; i < theElements.getLength(); i++) {
                elementsList.add((Element) theElements.item(i));
            }

            // Now remove and re-add them...
            for (Element element : elementsList) {
                if (element.getParentNode() == documentElement) {
                    documentElement.removeChild(element);
                    documentElement.appendChild(element);
                }
            }
        }
    }

    private Document parseAssembly(File assemblyFile) throws MojoFailureException, MojoExecutionException {
        if (!assemblyFile.isFile()) {
            throw new MojoExecutionException("'" + assemblyFile.getAbsolutePath() + "' is not a valid assembly file.");
        }

        try {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            return docBuilder.parse(assemblyFile);
        } catch (Exception e) {
            throw new MojoFailureException("Error parsing assembly document '" + assemblyFile.getAbsolutePath() + "'.", e);
        }
    }

    private void serialize(Document document) throws MojoFailureException {
        Transformer transformer;

        try {
            transformer = _transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new MojoFailureException("Unexpected exception creating JDK Transformer instance.", e);
        }

        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "4");

        Writer writer;
        try {
            File finalAssemblyDir = finalAssembly.getParentFile();
            if (!finalAssemblyDir.exists()) {
                finalAssemblyDir.mkdirs();
            }
            writer = new FileWriter(finalAssembly);
        } catch (IOException e) {
            throw new MojoFailureException("Error creating Writer instance to final assembly file '" + finalAssembly.getAbsolutePath() + "'.", e);
        }

        try {
            transformer.transform(new DOMSource(document), new StreamResult(writer));
        } catch (TransformerException e) {
            throw new MojoFailureException("Error serializing DOM document.", e);
        } finally {
            try {
                try {
                    writer.flush();
                } catch (IOException e) {
                    throw new MojoFailureException("Error flushing output Document.", e);
                }
            } finally {
                try {
                    writer.close();
                } catch (IOException e) {
                    throw new MojoFailureException("Error closing output Document.", e);
                }
            }
        }
    }

    private class IncludeAssembly {
        private Element includeNode;
        private Document includeAssemblyDoc;

        private IncludeAssembly(Element includeNode, Document includeAssemblyDoc) {
            this.includeNode = includeNode;
            this.includeAssemblyDoc = includeAssemblyDoc;
        }
    }
}
