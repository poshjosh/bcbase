package com.bc.util;

import java.io.FilenameFilter;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 *
 * @author Josh
 */
public class DefaultPropertiesView 
        extends HashMap<String, Properties> 
        implements PropertiesView, Serializable {

    public DefaultPropertiesView() { }
    
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
    @Override
    public Properties subset(String subset_name, String separator) {
        
        Properties output = null;
        
        Set<String> keys = this.keySet();
        
        final String prefix = subset_name + separator;
        
        StringBuilder reused = new StringBuilder(30);
        
        final String regex = Pattern.matches("\\p{Punct}", separator) ? "["+separator+"]" : separator;
        
        for(String key:keys) {
            Properties prop = this.get(key);
            Set<String> prop_names = prop.stringPropertyNames();
XLogger.getInstance().log(Level.FINE, "Subset name: {0}, parsing: {1} which contains property names: {2}", 
this.getClass(), subset_name, key, prop_names);
            for(String prop_name:prop_names) {
                if(prop_name.startsWith(prefix)) {
                    String [] parts = prop_name.split(regex); 
//XLogger.getInstance().log(Level.FINER, "\"{0}\".split(\"{1}\") = {2}", 
//this.getClass(), prop_name, regex, parts==null?null:Arrays.toString(parts));
                    if(parts != null && parts.length > 1) {
                        reused.setLength(0);
                        int start = 1; // ignore the first part, start at 1 rather than 0
                        for(int i=start; i<parts.length; i++) {
                            reused.append(parts[i]);
                            if(i < parts.length -1) {
                                reused.append(separator);
                            }
                        }
                    }
XLogger.getInstance().log(Level.FINER, "For subset_name: {0}, updating: {1} to {2} from {3}", 
this.getClass(), subset_name, prop_name, reused, key);
                    if(reused.length() > 0) {
                        if(output == null) {
                            output = new Properties();
                        }
                        output.setProperty(reused.toString(), prop.getProperty(prop_name));
                    }
                }
            }
        }
XLogger.getInstance().log(Level.FINE, "Subset has {0} properties", this.getClass(), output==null?null:output.size());
        return output;
    }
    
    @Override
    public String getDefaultFilename() {
        return "app_properties";
    }
    
    @Override
    public String getTimePattern() {
        throw new UnsupportedOperationException();
    }

    @Override
    public FilenameFilter getFilter() {
        return null;
    }
    
    @Override
    public Boolean setBoolean(String key, boolean b) {
        Object obj = this.setPropertyFor(this.getDefaultFilename(), key, ""+b);
        return (obj == null) ? null : Boolean.parseBoolean(obj.toString().trim());
    }

    @Override
    public Boolean getBoolean(String key) {
        String s = this.getProperty(key);
        return s == null ? null : Boolean.valueOf(s.trim());
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        String s = this.getProperty(key);
        return s == null ? defaultValue : Boolean.parseBoolean(s.trim());
    }

    @Override
    public Short setShort(String key, short i) {
        Long l = setLong(key, i);
        return l == null ? null : l.shortValue();
    }

    @Override
    public Short getShort(String key) {
        Long l = getLong(key);
        return l == null ? null : l.shortValue();
    }

    @Override
    public short getShort(String key, short defaultValue) {
        return (short)getLong(key, defaultValue);
    }

    @Override
    public Integer setInt(String key, int i) {
        Long l = setLong(key, i);
        return l == null ? null : l.intValue();
    }

    @Override
    public Integer getInt(String key) {
        Long l = getLong(key);
        return l == null ? null : l.intValue();
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return (int)getLong(key, defaultValue);
    }

    @Override
    public Long setLong(String key, long l) {
        Object obj = this.setProperty(key, ""+l);
        return (obj == null) ? null : Long.parseLong(obj.toString().trim());
    }

    @Override
    public Long getLong(String key) {
        String s = this.getProperty(key);
        return (s == null) ? null : Long.valueOf(s);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        String s = this.getProperty(key);
        return s == null ? defaultValue : Long.parseLong(s.trim());
    }

    @Override
    public Float setFloat(String key, float i) {
        Double d = setDouble(key, i);
        return (d == null) ? null : d.floatValue();
    }

    @Override
    public Float getFloat(String key) {
        Double d = getDouble(key);
        return d == null ? null : d.floatValue();
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return (float)getDouble(key, defaultValue);
    }

    @Override
    public Double setDouble(String key, double d) {
        Object obj = this.setProperty(key, ""+d);
        return (obj == null) ? null : Double.parseDouble(obj.toString().trim()+"d");
    }

    @Override
    public Double getDouble(String key) {
        String prop = this.getProperty(key);
        return prop == null ? null : Double.valueOf(prop.trim()+"d");
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        String s = this.getProperty(key);
        return (s == null) ? defaultValue : Double.parseDouble(s.trim()+"d");
    }

    @Override
    public String setString(String key, String value) {
        Object obj = this.setProperty(key, value);
        return (obj != null) ? obj.toString() : null;
    }

    @Override
    public String getString(String key) {
        return this.getProperty(key);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return this.getProperty(key, defaultValue);
    }

    @Override
    public Map setMap(String key, Map value, String separator) {
        QueryParametersConverter c = new QueryParametersConverter(separator);
        Object obj = this.setProperty(key, c.convert(value));
        return (obj == null) ? null : c.reverse(obj.toString());
    }

    @Override
    public Map getMap(String key, String separator) {
        String s = this.getProperty(key);
        return s == null ? null : new QueryParametersConverter(separator).reverse(s);
    }

    @Override
    public Map getMap(String key, Map defaultValue, String separator) {
        String  s = this.getProperty(key);
        return s == null ? defaultValue : new QueryParametersConverter(separator).reverse(s);

    }
    
    @Override
    public Calendar setTime(String key, Calendar date) throws ParseException {

        SimpleDateFormat sdf = (SimpleDateFormat)DateFormat.getDateInstance();
        sdf.applyPattern(this.getTimePattern());

        String dateStr = sdf.format(date.getTime());

        Object obj = this.setProperty(key, dateStr);

        return (obj != null) ? parseTime(obj.toString(), this.getTimePattern()) : null;
    }

    @Override
    public Calendar getTime(String key, Calendar defaultValue) throws ParseException {
        Calendar cal = getTime(key);
        return (cal == null) ? defaultValue : cal;
    }

    /**
     * @param key
     * @return The time represented by <tt>key</tt> as contained in the config
     *         document of this object, or null if no such time is specified.
     * @throws java.text.ParseException
     */
    @Override
    public Calendar getTime(String key) throws ParseException {

        String timeStr = getProperty(key);
XLogger.getInstance().log(Level.FINER, "Time designation: {0}, Time : {1}", this.getClass(), key, timeStr);

        return parseTime(timeStr, this.getTimePattern());
    }

    public static Calendar parseTime(String timeStr, String pattern) throws ParseException {

        SimpleDateFormat sdf = (SimpleDateFormat)DateFormat.getDateInstance();

        sdf.applyPattern(pattern);

        if (timeStr == null || timeStr.equals("")) {
            return null;
        }else {
            if(timeStr.length()>3 && !timeStr.contains(":")) {
                timeStr+=" 00:00:00"; // Add time part
            }

            Calendar time = Calendar.getInstance(); // Today
            Date date = sdf.parse(timeStr);
            time.setTime(date);

            return time;
        }
    }

    @Override
    public String [] setArray(String key, String [] arr) {
        return setArray(key, arr, ",");
    }
    
    @Override
    public String [] setArray(String key, String [] arr, String separator) {
        String value = "";
        for(int i=0; i<arr.length; i++) {
            if(i < arr.length-1) {
                value += (arr[i] + separator);
            }else{
                value += arr[i];
            }
        }

        if(value.length() < 1) return null;

        Object obj = this.setProperty(key, value);

        return (obj != null) ? obj.toString().split(separator) : null;
    }
    
    @Override
    public String [] getArray(String key) {
        return getArray(key, ",");
    }
    
    @Override
    public String [] getArray(String key, String separator) {
        String val = this.getProperty(key);
        return val == null ? null : val.split(separator);
    }

    @Override
    public String [] getArray(String key, String [] defaultValue) {
        return getArray(key, defaultValue, ",");
    }
    
    @Override
    public String [] getArray(String key, String [] defaultValue, String separator) {
        String value = this.getProperty(key);
        return (value == null || value.length() < 1) ? defaultValue : value.split(separator);
    }

    @Override
    public String [] setCollection(String key, Collection arr) {
        return setCollection(key, arr, ",");
    }
    
    @Override
    public String [] setCollection(String key, Collection arr, String separator) {
        String value = "";
        Iterator iter = arr.iterator();
        while(iter.hasNext()) {
            value += (iter.next() + separator);
        }

        if(value.length() < 1) return null;

        Object obj = this.setProperty(key, value);

        return (obj != null) ? obj.toString().split(separator) : null;
    }

    @Override
    public Collection<String> getCollection(String key) {
        return Arrays.asList(this.getArray(key));
    }
    
    @Override
    public Collection<String> getCollection(String key, Collection defaultValue) {
        String value = this.getProperty(key);
        return (value == null || value.length() < 1) ? defaultValue : getCollection(key);
    }

    @Override
    public String getProperty(String key) {
        return this.getPropertyFor(this.getDefaultFilename(), key);
    }

    @Override
    public String getPropertyFor(String filename, String key) {
        Properties properties = this.get(filename);
        return properties.getProperty(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return this.getPropertyFor(this.getDefaultFilename(), key, defaultValue);
    }
    
    @Override
    public String getPropertyFor(String filename, String key, String defaultValue) {
        Properties properties = this.get(filename);
        return properties.getProperty(key, defaultValue);
    }

    @Override
    public Object setProperty(String key, String value) {
        return this.setPropertyFor(this.getDefaultFilename(), key, value);
    }
    
    @Override
    public Object setPropertyFor(String filename, String key, String value) {
        Properties properties = this.get(filename);
        return properties.setProperty(key, value);
    }
    
    @Override
    public String toString() {
        return this == null ? this.getClass().getName() : 
                this.getClass().getName() + ": " + this.keySet();
    }
}

