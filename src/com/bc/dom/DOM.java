package com.bc.dom;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author poshjosh
 */
public interface DOM {

    Node add(String tagName, String attrName, String attrVal);

    boolean contains(String tagName, String attrName, String val);

    NodeList get(String tagName);

    Document getDocument();

    Node getRootNode();

    String getRootNodeName();

    boolean isAutoSaveChanges();

    boolean isEmpty();

    Node remove(String tagName, String attrName, String val);

    boolean save();
}
