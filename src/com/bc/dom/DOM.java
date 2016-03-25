package com.bc.dom;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @(#)DOM.java   23-May-2014 16:33:09
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
public abstract class DOM {
    
    /**
     * Determines if changes made to {@linkplain #document} will be
     * immediately propagated to the local file system.
     */
    private boolean saveChanges;

    private String uri;
    private transient Document document;

    public DOM() { }

    public DOM(File file) {
        DOM.this.setFile(file);
    }
    
    public DOM(URI uri) {
        DOM.this.setURI(uri);
    }

    public DOM(Document doc) {
        DOM.this.setDocument(doc);
    }
    
    public abstract String getRootNodeName();

    public synchronized boolean isEmpty() {
        return this.document == null || !this.document.hasChildNodes();
    }

    public synchronized Node getRootNode() {
        
        NodeList list;
        
        if(this.getRootNodeName() != null) {
            list = this.get(this.getRootNodeName());
            if(list != null && list.getLength() > 0) {
                return list.item(0);
            }
        }
        
        list = this.document.getChildNodes();
        if(list == null || list.getLength() == 0) {
            throw new UnsupportedOperationException("Could not find any root node in document");
        }
        
//<?xml version="1.0" encoding="UTF-8"?>
//<root>
// ADD CONTENT HERE        
//</root>        
//        
// The <?xml node is not included in the NodeList        
        return list.item(0);
    }
    
    public synchronized Node add(String tagName, String attrName, String attrVal) {
        Node added = XMLUtils.add(document, getRootNodeName(), tagName, attrName, attrVal);
        if (saveChanges) save();
        return added;
    }

    public synchronized NodeList get(String tagName) {
        if(this.document == null) throw new NullPointerException("Config document is null");
        return document.getElementsByTagName(tagName);
    }

    public synchronized boolean contains(String tagName, String attrName, String val) {
        return XMLUtils.contains(document, tagName, attrName, val);
    }

    public synchronized Node remove(String tagName, String attrName, String val) {
        Node removed = XMLUtils.remove(document, tagName, attrName, val);
        if (saveChanges && removed != null)  save();
        return removed;
    }

    public synchronized void save() {
        XMLUtils.save(document, getURI());
    }

    public synchronized String getURI() {
        return uri == null ? document.getDocumentURI() : uri;
    }

    public synchronized void setFile(File file) {
        this.document = XMLUtils.load(file);
        this.uri = file.toURI().toString();
    }

    public synchronized void setURI(URI uri) {
        this.document = XMLUtils.load(uri.toString());
        this.uri = uri.toString();
    }
    
    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
        this.uri = this.document.getDocumentURI();
    }

    public boolean isSaveChanges() {
        return saveChanges;
    }

    public void setSaveChanges(boolean saveChanges) {
        this.saveChanges = saveChanges;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append(", encoding: ").append(this.document.getXmlEncoding());
        builder.append(", doctype: ").append(this.document.getDoctype());
        builder.append(", uri: ").append(this.document.getDocumentURI());
        return builder.toString();
    }
}
