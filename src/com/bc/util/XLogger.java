package com.bc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.LoggingMXBean;

/**
 * @(#)XLogger.getInstance().java   28-Feb-2013 13:47:09
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
public class XLogger {

//    public transient static final logger ROOT_LOGGER = logger.getLogger("");
    
    /**
     * If true, only the annonymous logger will be used
     */
    private boolean annonymous;
    
    /**
     * If true, only the root logger will be used
     */
    private boolean rootOnly;
    
    private String rootLoggerName;
    
    private static XLogger instance;
    
    public XLogger() { }
    
    public static XLogger getInstance() {
        if(instance == null) {
            instance = new XLogger();
        }
        return instance;
    }

    public boolean isAnnonymous() {
        return annonymous;
    }

    public void setAnnonymous(boolean annonymous) {
        this.annonymous = annonymous;
    }
    
    public Logger logger(Class aClass) {
        return logger(aClass.getName());
    }
  
    public Logger logger(String name) {
        if (isAnnonymous()) {
            return Logger.getAnonymousLogger();
        }    
        if (isRootOnly()) {
            name = this.rootLoggerName == null ? "" : this.rootLoggerName;
        }
        return Logger.getLogger(name);
    }
  
    public boolean isLoggable(Level level, Class aClass) {
        return logger(aClass).isLoggable(level);
    }
    
    /**
     * Same as calling:<br/>
     * Logger.getLogger(aClass.getName()).logp(Level,aClass.getName(), "", msg, new Object[]{o1,o2,o3,o4})
     */
    public void log(Level level, String msg, Class aClass, Object o1, Object o2, Object o3, Object o4) {
        Logger logger = logger(aClass);
        if(!logger.isLoggable(level)) return;
        logger.logp(level, aClass.getName(), "", msg, new Object[]{o1, o2, o3, o4});
    }
    
    /**
     * Same as calling:<br/>
     * Logger.getLogger(aClass.getName()).logp(Level,aClass.getName(), "", msg, new Object[]{o1,o2,o3})
     */
    public void log(Level level, String msg, Class aClass, Object o1, Object o2, Object o3) {
        Logger logger = logger(aClass);
        if(!logger.isLoggable(level)) return;
        logger.logp(level, aClass.getName(), "", msg, new Object[]{o1, o2, o3});
    }
    
    /**
     * Same as calling:<br/>
     * Logger.getLogger(aClass.getName()).logp(Level,aClass.getName(), "", msg, new Object[]{o1,o2})
     */
    public void log(Level level, String msg, Class aClass, Object o1, Object o2) {
        Logger logger = logger(aClass);
        if(!logger.isLoggable(level)) return;
        logger.logp(level, aClass.getName(), "", msg, new Object[]{o1, o2});
    }

    /**
     * Same as calling:<br/>
     * Logger.getLogger(aClass.getName()).logp(Level,aClass.getName(), "", msg, o1)
     */
    public void log(Level level, String msg, Class aClass, Object o1) {
        Logger logger = logger(aClass);
        if(!logger.isLoggable(level)) return;
        logger.logp(level, aClass.getName(), "", msg, o1);
    }

    /**
     * Same as calling:<br/>
     * Logger.getLogger(aClass.getName()).logp(Level,aClass.getName(),"",msg)
     */
    public void log(Level level, String msg, Class aClass) {
        Logger logger = logger(aClass);
        if(!logger.isLoggable(level)) return;
        logger.logp(level, aClass.getName(), "", msg);
    }
    
    /**
     * Same as calling:<br/>
     * Logger.getLogger(aClass.getName()).logp(Level,aClass.getName(),"",msg,Throwable);
     */
    public void log(Level level, String msg, Class aClass, Throwable t) {
        Logger logger = logger(aClass);
        if(!logger.isLoggable(level)) return;
        logger.logp(level, aClass.getName(), "", msg, t);
    }
    
    /**
     * Same as calling:<br/>
     * Logger.getLogger(aClass.getName()).logp(Level,aClass.getName(),"",Throwable.toString());
     */
    public void logSimple(Level level, Class aClass, Throwable t)
    {
        Logger logger = logger(aClass);
        if (!logger.isLoggable(level)) return;
        logger.logp(level, aClass.getName(), "", t.toString());
    }
  
    public void entering(Class aClass, String srcMethod, Object o1) {
        if (!isLoggable(Level.FINER, aClass)) return;
        logger(aClass).entering(aClass.getName(), srcMethod, o1);
    }
  
    public void exiting(Class aClass, String srcMethod, Object o1) {
        if (!isLoggable(Level.FINER, aClass)) return;
        logger(aClass).exiting(aClass.getName(), srcMethod, o1);
    }
  
    public void throwing(Class aClass, String srcMethod, Throwable t) {
        if (!isLoggable(Level.FINER, aClass)) return;
        logger(aClass).throwing(aClass.getName(), srcMethod, t);
    }
    
    /**
     * Sets the Log level of the Logger with the specified name and all its
     * registered Handlers to the specified Level.
     * @param loggerName
     * @param newLevel
     * @return The old Loggers Log level before this operation
     */
    public Level setLogLevel(String loggerName, Level newLevel) {
        
        final Logger mLogger = logger(loggerName);
        final Level oldLevel = mLogger.getLevel();
        
XLogger.getInstance().log(Level.INFO, "Setting log level to {0} for {1} and handlers", XLogger.class, newLevel, loggerName);
        
        mLogger.setLevel(newLevel);
        
        if(newLevel != null) {
            Handler [] handlers = mLogger.getHandlers();
            if(handlers != null) {    
                synchronized(mLogger) {
                    for(Handler handler:handlers) {
                        handler.setLevel(newLevel);
                    }
                }
            }
        }
        
        return oldLevel;
    }
    
    public boolean addConsoleHandler(String fromLogger, String toLogger, boolean createIfNonExists) {
        Logger logger = XLogger.getInstance().logger(toLogger);
        Handler [] handlers = XLogger.getInstance().logger(fromLogger).getHandlers(); // ROOT Logger
        boolean added = false;
        if(handlers != null) {
            for(Handler handler:handlers) {
                if(handler instanceof ConsoleHandler) {
//System.out.println("Adding ConsoleHandler from logger: "+fromLogger+" to logger: "+toLogger);                        
                    logger.addHandler(handler);
                    added = true;
                    break;
                }
            }
        }
        if(!added && createIfNonExists) {
            ConsoleHandler consoleHandler = new ConsoleHandler();
//System.out.println("Adding new ConsoleHandler to logger: "+toLogger);                        
            logger.addHandler(consoleHandler);
            added = true;
        }
        return added;
    }

    public List<String> getLoggerNames(Filter<String> loggerNameFilter) {
// Iterating over this often throws ConcurrentModificationException
//        
//        Enumeration<String> loggerNames = LogManager.getLogManager().getLoggerNames();
        final LoggingMXBean logManager = LogManager.getLoggingMXBean();
        final List<String> loggerNames = logManager.getLoggerNames();
        List<String> output;
        if(loggerNameFilter != null) {
            output = new ArrayList<>(loggerNames.size());
            for(String loggerName:loggerNames) {
                if(loggerNameFilter.accept(loggerName)) {
                    output.add(loggerName);
                }
            }
        }else{
            output = new ArrayList(loggerNames);
        }
//System.out.println(this.getClass().getName()+". Logger names: "+output);        
        return output;
    }
    
    /**
     * Filters all names which start with:
     * <ul>
     *   <li><tt>java.</tt></li>
     *   <li><tt>javax.</tt></li>
     *   <li><tt>com.sun.</tt></li>
     *   <li><tt>com.oracle.</tt></li>
     * </ul>
     */
    public static class JavaLoggerNamesFilter implements Filter<String> {
        /**
         * Filters all names which start with:
         * <ul>
         *   <li><tt>java.</tt></li>
         *   <li><tt>javax.</tt></li>
         *   <li><tt>com.sun.</tt></li>
         *   <li><tt>com.oracle.</tt></li>
         * </ul>
         * @param loggerName
         * @return 
         */
        @Override
        public boolean accept(String loggerName) {
            return !(loggerName.isEmpty() ||
                    loggerName.startsWith("java") || loggerName.startsWith("javax") ||
                    loggerName.startsWith("com.sun") || loggerName.startsWith("com.oracle"));
        }
    }
    
    public static interface Filter<E> {
        boolean accept(E e); 
    }
    
    public boolean isRootOnly() {
        return rootLoggerName != null;
    }

    public String getRootLoggerName() {
        return rootLoggerName;
    }

    public void setRootLoggerName(String rootLoggerName) {
        this.rootLoggerName = rootLoggerName;
    }
}
