package com.bc.util.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Josh
 * @see java.util.concurrent.Executors#defaultThreadFactory() 
 */
public class NamedThreadFactory implements ThreadFactory {

    private final ThreadGroup group;

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private final String namePrefix;
    
    private final boolean daemonThreads;

    public NamedThreadFactory(String poolName) {
        this(poolName, false);
    }
    
    public NamedThreadFactory(String poolName, boolean daemonThreads) {
        SecurityManager s = System.getSecurityManager();
        this.group = (s != null) ? s.getThreadGroup() :
                              Thread.currentThread().getThreadGroup();
        this.namePrefix = poolName + "-thread-";
        this.daemonThreads = daemonThreads;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                              namePrefix + threadNumber.getAndIncrement(),
                              0);
        if (daemonThreads) {
            if(!t.isDaemon()) {
                t.setDaemon(true);
            }
        }else{
            if(t.isDaemon()) {
                t.setDaemon(false);
            }
        }    
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }

    @Override
    public String toString() {
        return "NamedThreadFactory{" + "group=" + (group==null?null:group.getName()) + 
                ", threadNumber=" + threadNumber + ", namePrefix=" + namePrefix + 
                ", daemonThreads=" + daemonThreads + '}';
    }
}
