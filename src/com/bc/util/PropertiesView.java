package com.bc.util;

import java.io.FilenameFilter;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

/**
 * @author Josh
 */
public interface PropertiesView {

    String[] getArray(String key);

    String[] getArray(String key, String separator);

    String[] getArray(String key, String[] defaultValue);

    String[] getArray(String key, String[] defaultValue, String separator);

    Boolean getBoolean(String key);

    boolean getBoolean(String key, boolean defaultValue);

    Collection<String> getCollection(String key);

    Collection<String> getCollection(String key, Collection defaultValue);

    String getDefaultFilename();

    Double getDouble(String key);

    double getDouble(String key, double defaultValue);

    FilenameFilter getFilter();

    Float getFloat(String key);

    float getFloat(String key, float defaultValue);

    Integer getInt(String key);

    int getInt(String key, int defaultValue);

    Long getLong(String key);

    long getLong(String key, long defaultValue);

    Map getMap(String key, String separator);

    Map getMap(String key, Map defaultValue, String separator);

    String getProperty(String key);

    String getProperty(String key, String defaultValue);

    String getPropertyFor(String filename, String key);

    String getPropertyFor(String filename, String key, String defaultValue);

    Short getShort(String key);

    short getShort(String key, short defaultValue);

    String getString(String key);

    String getString(String key, String defaultValue);

    Calendar getTime(String key, Calendar defaultValue) throws ParseException;

    /**
     * @param key
     * @return The time represented by <tt>key</tt> as contained in the config
     *         document of this object, or null if no such time is specified.
     * @throws java.text.ParseException
     */
    Calendar getTime(String key) throws ParseException;

    String getTimePattern();

    String[] setArray(String key, String[] arr);

    String[] setArray(String key, String[] arr, String separator);

    Boolean setBoolean(String key, boolean b);

    String[] setCollection(String key, Collection arr);

    String[] setCollection(String key, Collection arr, String separator);

    Double setDouble(String key, double d);

    Float setFloat(String key, float i);

    Integer setInt(String key, int i);

    Long setLong(String key, long l);

    Map setMap(String key, Map value, String separator);

    Object setProperty(String key, String value);

    Object setPropertyFor(String filename, String key, String value);

    Short setShort(String key, short i);

    String setString(String key, String value);

    Calendar setTime(String key, Calendar date) throws ParseException;

    /**
     * <b>Note</b>: Using multiple dots (e.g <tt>..</tt>) or dollar signs
     * (e.g <tt>$$</tt>) as separator leads to unexpected results. Best practice
     * is to use a single dot <tt>(.)</tt>, hash <tt>(#)</tt>, underscore
     * <tt>(_)</tt> or dollar sign <tt>($)</tt> as separator.
     * <br/><br/>
     * <b>Generally given inputs:</b><br/>
     * subset_name = name<br/>
     * separator = .
     * <br/><br/>
     * If this object (a Map of <tt>properties</tt>) contains a <tt>properties</tt>
     * instance with the following entries:<br/>
     * <tt>name.first = John</tt><br/>
     * <tt>name.last = Doe</tt>
     * <br/><br/>
     * The following output <tt>properties</tt> instance is returned:
     * <tt>first = John</tt><br/>
     * <tt>last = Doe</tt>
     * @param subset_name
     * @param separator
     * @return The subset properties
     */
    Properties subset(String subset_name, String separator);
    
}
