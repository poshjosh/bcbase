package temp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

/**
 * @author Josh
 */
public class ObjectPoolTest {
    
    private final int maxUseTimeMillis = 5_000;
    private final int maxIntervalMillis = 4_000;
    
    @Test
    public void testObjectPool() {
        
        final int maxConnections = 5;
        
        final ObjectPool<PoolItem> objectPool = new ObjectPoolImpl(2,  maxConnections);
        
        final ExecutorService es = Executors.newCachedThreadPool();
        
        final int testRate = maxConnections * 3;
        
        final long waitForPoolItemTimeout = this.maxUseTimeMillis * 5;
        
        for(int i=0; i<testRate; i++) {
            
            es.submit(new Runnable(){
                @Override
                public void run() {
                    try{
System.out.println("BEFORE borrow:: "+objectPool);                        
                        final long startTime = System.currentTimeMillis();
                        long timeSpent = 0;
                        PoolItem poolItem;
                        do{
                            poolItem = objectPool.borrowObject();
                            timeSpent = System.currentTimeMillis() - startTime;
                        }while(poolItem == null && timeSpent < waitForPoolItemTimeout);
System.out.println("Borrowed:: "+poolItem);
System.out.println("AFTER borrow:: "+objectPool); 
                        if(poolItem == null) {
                            throw new RuntimeException("Exiting task in thread: "+Thread.currentThread().getName()+" as no pool-item available for use, even after waiting for "+timeSpent+" milliseconds.");
                        }
                        try{
                            poolItem.use();
                        }finally{
                            poolItem.close();
                        }
                    }catch(RuntimeException e) {
                        e.printStackTrace();
                    }
                }
            });
            
            this.randomTimeSleep(this.maxIntervalMillis);
        }
    }
    
    private void randomTimeSleep(int seed) {
        try{
            final int sleepTimeMillis = (int)(Math.random() * seed);
            Thread.sleep(sleepTimeMillis);
        }catch(InterruptedException e) {
            this.handleException(e);
        }
    }
    
    private void handleException(InterruptedException e) {
        System.err.println("Interrupted, thread: "+Thread.currentThread().getName());
        e.printStackTrace();
        Thread.currentThread().interrupt();
    }
    
    private static AtomicInteger poolItemsCreated = new AtomicInteger(0);
    private class PoolItem {
        private boolean inUse;
        private boolean closed;
        private Integer id;
        private PoolItem() {
            id = poolItemsCreated.incrementAndGet();
        }
        public void use() {
            if(inUse) {
                throw new IllegalStateException("Cannot use an object which is already");
            }
            try{
                inUse = true;
                System.out.println("Using:: "+this);
                ObjectPoolTest.this.randomTimeSleep(maxUseTimeMillis);
            }finally{
                System.out.println("Done Using:: "+this);
                inUse = false;
            }
        }
        public void close() {
            System.out.println("Closing:: "+this);
            closed = true;
            inUse = false;
        }
        public boolean isInUse() {
            return inUse;
        }
        public boolean isClosed() {
            return closed;
        }
        @Override
        public String toString() {
            return this.getClass().getSimpleName()+"{ID: "+id+", in use: "+inUse+", closed: "+closed+"}";
        }
    }
    
    private class ObjectPoolImpl extends ObjectPool<PoolItem> {
        
        public ObjectPoolImpl(int minIdle, int maxConnections) {
            super(minIdle, maxConnections);
        }

        public ObjectPoolImpl(int minIdle, int maxIdle, long validationInterval, int maxConnections) {
            super(minIdle, maxIdle, validationInterval, maxConnections);
        }

        @Override
        protected PoolItem create() {
            return new PoolItem();
        }

        @Override
        protected void close(PoolItem object) {
            object.close();
        }
    }
}
