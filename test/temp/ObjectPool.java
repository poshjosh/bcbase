package temp;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
* @author ashwinrayaprolu
*
* @param <T>
*/

public abstract class ObjectPool<T> {

  private Queue<T> pool;

  /**
  * Stores number of connections that are being used
  */

  private final AtomicInteger usageCount = new AtomicInteger(0);

  // Maximum number of connections that can be open. Defaulted to 20

  private int maxConnections = 20;

  private ScheduledExecutorService executorService;

  /**

  * Creates the pool.

  *

  * @param minIdle

  * minimum number of objects residing in the pool

  */

  public ObjectPool(final int minIdle,final int maxConnections) {

    // initialize pool

    this.maxConnections = maxConnections;

    initialize(minIdle);

  }

  /**

  * Creates the pool.

  *

  * @param minIdle

  * minimum number of objects residing in the pool

  * @param maxIdle

  * maximum number of objects residing in the pool

  * @param validationInterval

  * time in seconds for periodical checking of minIdle / maxIdle

  * conditions in a separate thread.

  * When the number of objects is less than minIdle, missing

  * instances will be created.

  * When the number of objects is greater than maxIdle, too many

  * instances will be removed.

  */

  public ObjectPool(final int minIdle, final int maxIdle, final long validationInterval,final int maxConnections) {

    this.maxConnections = maxConnections;

    // initialize pool

    initialize(minIdle);

    // check pool conditions in a separate thread

    executorService = Executors.newSingleThreadScheduledExecutor();

    executorService.scheduleWithFixedDelay(new Runnable() {

    @Override

    public void run() {

      int size = pool.size();

      if (size < minIdle) {

        if(usageCount.compareAndSet(maxConnections, maxConnections)){

          return;

        }

        int sizeToBeAdded = minIdle - size;

        for (int i = 0; i < sizeToBeAdded; i++) {

          System.out.println("Background Thread Creating Objects");

          pool.add(create());

        }

      } else if (size > maxIdle) {

        int sizeToBeRemoved = size - maxIdle;

        for (int i = 0; i < sizeToBeRemoved; i++) {

          System.out.println("Background Thread dumping Objects");

          pool.poll();

        }

      }

    }

    }, validationInterval, validationInterval, TimeUnit.SECONDS);

  }

  /**

  * Gets the next free object from the pool. If the pool doesn't contain any

  * objects,

  * a new object will be created and given to the caller of this method back.

  *

  * @return T borrowed object

  */

  public T borrowObject() {

    T object;

    if(usageCount.compareAndSet(maxConnections, maxConnections)){

      return null;

    }

    int preBorrowCount = usageCount.get();

    if ((object = pool.poll()) == null) {

      object = create();

    }

    while (usageCount.compareAndSet(preBorrowCount, preBorrowCount+1));

    return object;

  }

  /**

  * Returns object back to the pool.

  *

  * @param object

  * object to be returned

  */

  public void returnObject(T object) {

    if (object == null) {

    return;

    }

    int preReturnCount = usageCount.get();

    this.pool.offer(object);

    while (usageCount.compareAndSet(preReturnCount, preReturnCount-1));

  }

  /**

  * Shutdown this pool.

  */

  public void shutdown() {

    if (executorService != null) {

      executorService.shutdown();

    }

  }

  /**

  * Creates a new object.

  *

  * @return T new object

  */

  protected abstract T create();

  protected abstract void close(T object);

  private void initialize(final int minIdle) {

    pool = new ConcurrentLinkedQueue<>();

    for (int i = 0; i < minIdle; i++) {

      pool.add(create());

    }

  }

  @Override
  public String toString() {
    return "ObjectPool{" + "Pool size=" + pool.size() + ", maxConnections=" + maxConnections + ", usageCount=" + usageCount.get() + '}';
  }
}