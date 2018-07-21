package com.bc.sql;

import java.io.Serializable;
import java.sql.Types;

/**
 * @(#)MySQLDateTimePatterns.java   28-Jul-2014 13:10:48
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
public class MySQLDateTimePatterns implements SQLDateTimePatterns, Serializable {
    @Override
    public String getPattern(int sqlType) {
        switch(sqlType) {
            case Types.DATE: return "yyyy-MM-dd";
            case Types.TIME: return "HH:mm:ss";
            case Types.TIMESTAMP: return "yyyyMMddHHmmss";
//                case Types.DATETIME: return "yyyy-MM-dd HH:mm:ss";    
            default: throw new IllegalArgumentException("Unexpected SQL Type: "+sqlType);    
        }
    }
}
