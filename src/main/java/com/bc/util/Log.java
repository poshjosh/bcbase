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
public class Log {

//    public transient static final toLogger ROOT_LOGGER = toLogger.getLogger("");
    
    /**
     * If true, only the annonymous toLogger will be used
     */
    private boolean annonymous;
    
    /**
     * If true, only the root toLogger will be used
     */
    private boolean rootOnly;
    
    private String rootLoggerName;
    
    private static Log instance;
    
    public Log() { }
    
    public static Log getInstance() {
        if(instance == null) {
            instance = new Log();
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
        
//Log.getInstance().log(Level.INFO, "Setting log level to {0} for {1} and handlers", Log.class, newLevel, loggerName);
        
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
    
    /**
     * Transfers the ConsoleHandler from one logger to another
     * @param fromLoggerName
     * @param toLoggerNames
     * @param createIfNonExists
     * @return 
     */
    public ConsoleHandler transferConsoleHandler(
            String fromLoggerName, String [] toLoggerNames, boolean createIfNonExists) {
        
        int i = 0;
        ConsoleHandler consoleHandler = null;
        for(String to:toLoggerNames) {
            if(i == 0) {
                consoleHandler = this.transferConsoleHandler(fromLoggerName, to, createIfNonExists);
            }else{
                if(consoleHandler != null) {
                    Logger logger = logger(to);
                    logger.addHandler(consoleHandler);
                }else{
                    break;
                }
            }
            ++i;
        }
        
        return consoleHandler;
    }

    /**
     * Transfers the ConsoleHandler from one logger to another
     * @param fromLoggerName
     * @param toLoggerName
     * @param createIfNonExists
     * @return 
     */
    public ConsoleHandler transferConsoleHandler(String fromLoggerName, String toLoggerName, boolean createIfNonExists) {
        Logger fromLogger = logger(fromLoggerName);
        Logger toLogger = logger(toLoggerName);
        final Handler [] fromHandlers = fromLogger.getHandlers(); 
        ConsoleHandler transfered = null;
        if(fromHandlers != null) {
            for(Handler handler:fromHandlers) {
                if(handler instanceof ConsoleHandler) {
//System.out.println(this.getClass().getName()+" = = = = = = = = = = = = = Transfering ConsoleHandler from toLogger: "+fromLoggerName+" to toLogger: "+toLoggerName);                        
                    fromLogger.removeHandler(handler);
                    toLogger.addHandler(handler);
                    transfered = (ConsoleHandler)handler;
                    break;
                }
            }
        }
        if(transfered == null && createIfNonExists) {
            ConsoleHandler consoleHandler = new ConsoleHandler();
//System.out.println(this.getClass().getName()+" = = = = = = = = = = = = = Adding new ConsoleHandler to toLogger: "+toLoggerName);                        
            toLogger.addHandler(consoleHandler);
            transfered = consoleHandler;
        }
        return transfered;
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
/**
 * 
    public static final class JavaLoggerNamesFilter implements Filter<String> {
         * Filters all names which start with:
         * <ul>
         *   <li><tt>java.</tt></li>
         *   <li><tt>javax.</tt></li>
         *   <li><tt>com.sun.</tt></li>
         *   <li><tt>com.oracle.</tt></li>
         * </ul>
         * @param loggerName
         * @return 
        @Override
        public boolean accept(String loggerName) {
            return !(loggerName.isEmpty() ||
                    loggerName.startsWith("java") || loggerName.startsWith("javax") ||
                    loggerName.startsWith("com.sun") || loggerName.startsWith("com.oracle"));
        }
    }
    
 * 
 */