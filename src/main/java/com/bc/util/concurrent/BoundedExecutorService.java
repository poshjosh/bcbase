package com.bc.util.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * <p>
 * <b>Note:</b>
 * A SynchronousQueue is used if the queueCapacity <= 0. A SynchronousQueue is 
 * essentially a queue with 0 capacity, hence the associated ExecutorService 
 * will only accept new tasks if there is an idle thread available. If all 
 * threads are busy, new task will be rejected immediately and will never wait. 
 * </p>
 * @author Josh
 */
public class BoundedExecutorService extends ThreadPoolExecutor implements RejectedExecutionHandler {

    private static final Logger LOG = Logger.getLogger(BoundedExecutorService.class.getName());

    public BoundedExecutorService(String threadPoolName) {
        this(threadPoolName, false);
    }
    
    public BoundedExecutorService(String threadPoolName, boolean daemonThreads) {
        this(threadPoolName, Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(), daemonThreads);
    }

    public BoundedExecutorService(String threadPoolName, int poolSize, int queueCapacity) {
        
        this(threadPoolName, poolSize, queueCapacity, false);
    }
    
    public BoundedExecutorService(String threadPoolName, int poolSize, int queueCapacity, boolean daemonThreads) {
        
        this(threadPoolName, poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, queueCapacity, daemonThreads);
    }

    public BoundedExecutorService(String threadPoolName,
            int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit timeUnit, int queueCapacity) {

        this(threadPoolName, corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, queueCapacity, false);
    }
    
    public BoundedExecutorService(String threadPoolName,
            int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit timeUnit, int queueCapacity, boolean daemonThreads) {
        
        super(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, 
                queueCapacity <= 0 ? new SynchronousQueue<>() : new LinkedBlockingQueue<>(queueCapacity), 
                new NamedThreadFactory(threadPoolName, daemonThreads));

        BoundedExecutorService.this.setRejectedExecutionHandler(BoundedExecutorService.this);
    }
    
    /**
     * Does nothing, which has the effect of discarding task r.
     *
     * @param r the runnable task requested to be executed
     * @param executor the executor attempting to execute this task
     */
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
//        LOG.warning(() -> "EXECUTION REJECTED. Runnable type: " + r.getClass().getName());
        LOG.fine(() -> "Runnable: " + r + "\nExecutor: " + executor + 
                        "\nExecutor's ThreadFactory: " + executor.getThreadFactory());
    }
}
