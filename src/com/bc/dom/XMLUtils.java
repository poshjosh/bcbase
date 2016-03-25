package com.bc.dom;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @(#)XMLUtils.java   23-May-2014 16:33:46
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class XMLUtils {

    private static final transient Logger logger = Logger.getLogger(XMLUtils.class.getName());

    public XMLUtils() { }

    public static Node add(Document document, String rootNodeName, String tagName, String attrName, String attrVal) {
            Element newNode = document.createElement(tagName);
            newNode.setAttribute(attrName, attrVal);
            NodeList list = document.getElementsByTagName(tagName);
            Node added = null;
            if (list == null || list.getLength() == 0) {
                    if (rootNodeName == null)
                            throw new NullPointerException();
                    Node rootNode = document.getElementsByTagName(rootNodeName).item(0);
                    added = rootNode.appendChild(newNode);
            } else {
                    Node refNode = list.item(0);
                    added = refNode.getParentNode().insertBefore(newNode, refNode);
            }
            return added;
    }

    public static boolean contains(Document document, String tagName, String attrName, String val) {
            NodeList list = document.getElementsByTagName(tagName);
            if (list == null)
                    return false;
            for (int i = 0; i < list.getLength(); i++) {
                    Node node = list.item(i);
                    if (matchesAttribute(node, attrName, val))
                            return true;
            }

            return false;
    }

    public static Node remove(Document document, String tagName, String attrName, String val) {
            NodeList list = document.getElementsByTagName(tagName);
            if (list == null)
                    return null;
            Node parent = null;
            for (int i = 0; i < list.getLength(); i++) {
                    Node node = list.item(i);
                    if (parent == null)
                            parent = node.getParentNode();
                    if (matchesAttribute(node, attrName, val)) {
                            Node removed = parent.removeChild(node);
                            return removed;
                    }
            }

            return null;
    }

    public static boolean hasAttribute(Node node, String attrName, String attrVal) {
            NamedNodeMap nodeMap = node.getAttributes();
            String nodeVal = nodeMap.getNamedItem(attrName).getNodeValue();
            return attrVal.equals(nodeVal);
    }

    public static boolean matchesAttribute(Node node, String attrName, String attrVal) {
            NamedNodeMap nodeMap = node.getAttributes();
            String nodeVal = nodeMap.getNamedItem(attrName).getNodeValue();
            return attrVal.contains(nodeVal);
    }

    public static Node getAttribute(Node node, String name) {
            NamedNodeMap nodeMap = node.getAttributes();
            if (nodeMap == null)
                    return null;
            else
                    return nodeMap.getNamedItem(name);
    }

    public static String getAttributeValue(Node node, String name) {
            Node attribute = getAttribute(node, name);
            if (attribute == null)
                    return null;
            else
                    return attribute.getNodeValue();
    }

    public static double getAttributeNumber(Node node, String name) {
            Node attribute = getAttribute(node, name);
            if (attribute == null)
                    return -1D;
            String val = attribute.getNodeValue();
            if (val == null)
                    return -1D;
            else
                    return Double.parseDouble(val);
    }

    public static String[] getAttributeValues(Node node, String name) {
            return getAttributeValues(node, name, ",");
    }

    public static String[] getAttributeValues(Node node, String name, String separatorRegex) {
            Node attribute = getAttribute(node, name);
            if (attribute == null)
                    return null;
            String nodeValue = attribute.getNodeValue();
            if (nodeValue == null)
                    return null;
            else
                    return nodeValue.split(separatorRegex);
    }

    public static void save(Document doc, String path) {
        try{
            // Use a Transformer for output
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            // To preserve the XML document's DOCTYPE setting, it is also necessary to add the following code
            if (doc.getDoctype() != null){
                String systemValue = getFileName(doc.getDoctype().getSystemId());
                transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemValue);
            }

            DOMSource source = new DOMSource(doc);

            FileOutputStream fos = new FileOutputStream(path);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            OutputStreamWriter osw = new OutputStreamWriter(bos, "UTF-8"); // XML encoding
            
            try{
                           
                
                StreamResult result = new StreamResult(osw);

                transformer.transform(source, result);

                osw.flush();
                
            }finally{
                if(osw != null) try{ osw.close(); }catch(IOException e){
                    logger.log(Level.WARNING, "", e);
                }
                if(bos != null) try{ bos.close(); }catch(IOException e){
                    logger.log(Level.WARNING, "", e);
                }
                if(fos != null) try{ fos.close(); }catch(IOException e){
                    logger.log(Level.WARNING, "", e);
                }
            }
        }catch(IOException e) {
            logger.log(Level.WARNING, "", e);
        } catch (TransformerConfigurationException e) {
            logger.log(Level.WARNING, "Transformer Factory Error", e);
        } catch (TransformerException e) {
            logger.log(Level.WARNING, "Transformation Error", e);
        }
    }

    public static Document load(String uri) {
            Document doc = null;
            try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = factory.newDocumentBuilder();
                    doc = docBuilder.parse(uri);
Logger.getLogger(XMLUtils.class.getName()).log(Level.FINE, 
"Doc URL: {0}, Base URI: {1}, Path: {2}", 
new Object[]{doc.getDocumentURI(), doc.getBaseURI(), uri});                    
            }
            catch (SAXException e) {
                    logger.log(Level.WARNING, "Could not parse XML at: "+uri, e);
            }
            catch (IOException e) {
                    // Lighter logging for this, no stack trace
                    logger.log(Level.WARNING, "{0}. Could not read: {1}, reason: {2}", 
                    new Object[]{logger.getName(), uri, e});
            }
            catch (ParserConfigurationException e) {
                    logger.log(Level.SEVERE, "Could not obtain SAX parser", e);
                    throw new RuntimeException("Could not obtain SAX parser");
            }
            return doc;
    }
    
    public static Document load(File file) {
            Document doc = null;
            try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = factory.newDocumentBuilder();
                    doc = docBuilder.parse(file);
Logger.getLogger(XMLUtils.class.getName()).log(Level.FINE, 
"Doc URL: {0}, Base URI: {1}, Path: {2}", 
new Object[]{doc.getDocumentURI(), doc.getBaseURI(), file});                    
            }
            catch (SAXException e) {
                    logger.log(Level.WARNING, "Could not parse XML at: "+file, e);
            }
            catch (IOException e) {
                    // Lighter logging for this, no stack trace
                    logger.log(Level.WARNING, "{0}. Could not read: {1}, reason: {2}", 
                    new Object[]{logger.getName(), file, e});
            }
            catch (ParserConfigurationException e) {
                    logger.log(Level.SEVERE, "Could not obtain SAX parser", e);
                    throw new RuntimeException("Could not obtain SAX parser");
            }
            return doc;
    }

    public static Document load(InputStream in) {
            Document doc = null;
            try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = factory.newDocumentBuilder();
                    doc = docBuilder.parse(in);
Logger.getLogger(XMLUtils.class.getName()).log(Level.FINE, 
"Doc URL: {0}, Base URI: {1}", 
new Object[]{doc.getDocumentURI(), doc.getBaseURI()});                    
            }
            catch (SAXException e) {
                    logger.log(Level.WARNING, "Could not parse XML", e);
            }
            catch (IOException e) {
                    // Lighter logging for this, no stack trace
                    logger.log(Level.WARNING, "{0}. Could not read XML, reason: {2}", 
                    new Object[]{logger.getName(), e});
            }
            catch (ParserConfigurationException e) {
                    logger.log(Level.SEVERE, "Could not obtain SAX parser", e);
                    throw new RuntimeException("Could not obtain SAX parser");
            }
            return doc;
    }
    
    public static StringBuilder stringValue(Node node, int maxLen) {
            StringBuilder builder = (new StringBuilder("Node:")).append(node.getNodeName());
            builder.append(", localName:").append(node.getLocalName()).append(", nameSpaceURI:").append(node.getNamespaceURI());
            builder.append(", nodeType:").append(node.getNodeType()).append(", nodeValue:").append(truncate(maxLen, node.getNodeValue()));
            builder.append(", prefix:").append(node.getPrefix()).append(", textContent:").append(truncate(maxLen, node.getTextContent()));
            NamedNodeMap nodeMap = node.getAttributes();
            if (nodeMap != null && nodeMap.getLength() > 0)
                    builder.append("\nAttibutes:: ").append(stringValue(nodeMap, maxLen));
            return builder;
    }

    public static StringBuilder stringValue(NamedNodeMap map, int maxLen) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < map.getLength(); i++) {
                    Node node = map.item(i);
                    builder.append("\nnode[").append(i).append("]::");
                    builder.append(node.getNodeName()).append("=").append(node.getNodeValue());
            }

            return builder;
    }

    private static String truncate(int maxLen, String val) {
            if (maxLen < 0)
                    return val;
            if (val == null || val.length() <= maxLen)
                    return val;
            else
                    return val.substring(0, maxLen);
    }

    public static String toString(Document doc) {

        StringWriter writer = null;

        try{
            // Use a Transformer for output
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            // To preserve the XML document's DOCTYPE setting, it is also necessary to add the following code
            if (doc.getDoctype() != null){
                String systemValue = getFileName(doc.getDoctype().getSystemId());
                transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemValue);
            }

            DOMSource source = new DOMSource(doc);

            writer = new StringWriter();

            StreamResult result = new StreamResult(writer);

            transformer.transform(source, result);

        } catch (TransformerConfigurationException e) {

            // Error generated by the parser
            Logger.getLogger(XMLUtils.class.getName()).log(Level.WARNING, "Transformer Factory Error", e);

        } catch (TransformerException e) {

            // Error generated by the parser
            Logger.getLogger(XMLUtils.class.getName()).log(Level.WARNING, "Transformation Error", e);
        }

        return writer == null ? null : writer.getBuffer().toString();
    }

    /**
     * Mirrors logic of method {@link java.io.File#getName()}.
     * Use this method if its not necessary to create a new File object.
     * @param path The path to the file whose name is required
     * @return The name of the file at the specified path
     */
    private static String getFileName(String path) {
        String output = getFileName(path, File.separatorChar);
        if(output == null) {
            output = getFileName(path, '/');
            if(output == null) {
                output = getFileName(path, '\\');
            }
        }
        return output;
    }
    
    private static String getFileName(String path, char separatorChar) {
	int index = path.lastIndexOf(separatorChar);
	if (index == -1 || index == 0) return null;
	return path.substring(index + 1);
    }
}
