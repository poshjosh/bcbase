package com.bc.sql;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Ref;
import java.sql.Struct;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @(#)SQLUtils.java   24-May-2014 15:08:47
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
public class SQLUtils {

    private transient static final Logger LOG = Logger.getLogger(SQLUtils.class.getName());
    
    public static void removeNullValues(Map paramMap) {
    
        if(paramMap == null || paramMap.isEmpty()) return;
        
        // Collection.remove(null) will only remove the first null value
        // So we use Collection.removeAll(Collection c);
        //
        ArrayList nullList = new ArrayList(1);
        nullList.add(null);

        paramMap.values().removeAll(nullList);
        
        LOG.log(Level.FINER, "AFTER REMOVING null values: {0}", paramMap);
    }
    
    public static Object toSQLType(
            SQLDateTimePatterns dtPatterns, 
            int type, Object val) {
        
        if(val == null) {
            return null;
        }

        switch(type) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.LONGNVARCHAR:
                if(!(val instanceof String)) {
                    val = String.valueOf(val);
                }
                return val;

            case Types.BIT:
            case Types.BOOLEAN:
                if(!(val instanceof Boolean)) {
                    val = Boolean.valueOf(val.toString());
                }
                return val;

            case Types.TINYINT:
                if(!(val instanceof Byte)) {
                    val = Byte.valueOf(val.toString());
                }
                return val;
                
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                if(!(val instanceof byte[])) {
                    val = val.toString().getBytes();
                }
                return val;

            case Types.SMALLINT:
                if(!(val instanceof Short)) {
                    val = Short.valueOf(val.toString());
                }
                return val;
                
            case Types.INTEGER:
                if(!(val instanceof Integer)) {
                    val = Integer.valueOf(val.toString());
                }
                return val;

            case Types.BIGINT:
                if(!(val instanceof Long)) {
                    val = Long.valueOf(val.toString());
                }
                return val;

            case Types.REAL:
                if(!(val instanceof Float)) {
                    val = Float.valueOf(val.toString());
                }
                return val;

            case Types.FLOAT:
            case Types.DOUBLE:
                if(!(val instanceof Double)) {
                    val = Double.valueOf(val.toString());
                }
                return val;

            case Types.DECIMAL:
            case Types.NUMERIC:
                if(!(val instanceof BigDecimal)) {
                    val = new BigDecimal(val.toString());
                }
                return val;

            case Types.DATE:
                return new java.sql.Date(getMillis(dtPatterns, type, val));
            case Types.TIME:
                return new java.sql.Time(getMillis(dtPatterns, type, val));
            case Types.TIMESTAMP:
                return new java.sql.Timestamp(getMillis(dtPatterns, type, val));

            case Types.ARRAY:
            case Types.REF:
            case Types.STRUCT:
            default:
                // This converts enums etc, toString 
                return val.toString();
        }
    }
    
    private static long getMillis(
            SQLDateTimePatterns dtPatterns, int type, Object val) {
        long millis;
        try{
            millis = ((java.util.Date)val).getTime();
        }catch(ClassCastException e) {
            millis = getDate(dtPatterns.getPattern(type), val.toString()).getTime();
        }
        return millis;
    }

    private static java.util.Date getDate(String pattern, String value) {
        SimpleDateFormat fmt = new SimpleDateFormat();
        fmt.applyPattern(pattern);
        java.util.Date date = null;
        try{
            date = fmt.parse(value);
        }catch(ParseException e) {
            LOG.log(Level.WARNING, null, e);
        }
        return date;
    }
    
    public static Class getClass(int type) {
        return getClass(type, Object.class);
    }
    
    public static Class getClass(int type, Class outputIfNone) {
        switch(type) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.LONGNVARCHAR: 
                return String.class;

            case Types.BIT:
            case Types.BOOLEAN:
                return Boolean.class;

            case Types.TINYINT:
                return Byte.class;
                
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                return byte[].class;

            case Types.SMALLINT:
                return Short.class;

            case Types.INTEGER:
                return Integer.class;

            case Types.BIGINT:
                return Long.class;

            case Types.REAL:
                return Float.class;

            case Types.FLOAT:
            case Types.DOUBLE:
                return Double.class;

            case Types.DECIMAL:
            case Types.NUMERIC:
                return BigDecimal.class;

            case Types.DATE:
                return java.sql.Date.class;

            case Types.TIME:
                return java.sql.Time.class;

            case Types.TIMESTAMP:
                return java.sql.Timestamp.class;

            case Types.ARRAY:
                return Array.class;

            case Types.REF:
                return Ref.class;

            case Types.STRUCT:
                return Struct.class;

            default:
                return outputIfNone;
        }
// @todo tests needed to determine if this is recognized in the TableModel Interface
/**
 *
      case Types.ARRAY:
        return Array.class;

      case Types.REF:
        return Ref.class;

      case Types.STRUCT:
        return Struct.class;

      case Types.BLOB:
        return Blob.class;

      case Types.CLOB:
        return Clob.class;

      case Types.NCLOB:
        return NClob.class;
*/
    }

    public static int [] getTypes(Class aClass) {

        if(aClass == String.class) {
            return new int[]{Types.CHAR, Types.VARCHAR, Types.LONGVARCHAR, Types.LONGNVARCHAR};
        }
        if(aClass == Boolean.class || aClass == boolean.class) {
            return new int[]{Types.BIT, Types.BOOLEAN};
        }
        if(aClass == Byte.class || aClass == byte.class) {
            return new int[]{Types.TINYINT};
        }
        if(aClass == Byte[].class || aClass == byte[].class) {
            return new int[]{Types.BINARY, Types.VARBINARY, Types.LONGVARBINARY}; 
        }
        if(aClass == Short.class || aClass == short.class) {
            return new int[]{Types.SMALLINT};
        }
        if(aClass == Integer.class || aClass == int.class) {
            return new int[]{Types.INTEGER};
        }
        if(aClass == Long.class || aClass == long.class) {
            return new int[]{Types.BIGINT};
        }
        if(aClass == Float.class || aClass == float.class) {
            return new int[]{Types.REAL};
        }
        if(aClass == Double.class || aClass == double.class) {
            return new int[]{Types.DOUBLE, Types.FLOAT};
        }
        if(aClass == BigDecimal.class) {
            return new int[]{Types.NUMERIC, Types.DECIMAL};
        }
        if(aClass == java.util.Date.class) { // Note this
            return new int[]{Types.DATE};
        }
        if(aClass == java.sql.Date.class) {
            return new int[]{Types.DATE};
        }
        if(aClass == java.sql.Time.class) {
            return new int[]{Types.TIME};
        }
        if(aClass == java.sql.Timestamp.class) {
            return new int[]{Types.TIMESTAMP};
        }
        if(aClass == Array.class) {
            return new int[]{Types.ARRAY};
        }
        if(aClass == Ref.class) {
            return new int[]{Types.REF};
        }
        if(aClass == Struct.class) {
            return new int[]{Types.STRUCT};
        }
        
        return null;
    }

    public static boolean isDateOrTimeType(int sqlType) {
        boolean dateOrTimeType = sqlType == Types.DATE || sqlType == Types.TIME || sqlType == Types.TIMESTAMP;
        return dateOrTimeType;
    }
    
    public static boolean isStringType(int sqlType) {
        boolean stringType = sqlType == Types.CHAR || sqlType == Types.VARCHAR || sqlType == Types.LONGVARCHAR || sqlType == Types.LONGNVARCHAR;
        return stringType;
    }
}
