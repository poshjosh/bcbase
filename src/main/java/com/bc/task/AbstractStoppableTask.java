package com.bc.task;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractStoppableTask<R> implements Serializable, StoppableTask<R>{

  private transient static final Logger LOG = Logger.getLogger(AbstractStoppableTask.class.getName());
    
  private boolean attemptedPre;
  
  private boolean started;
  private boolean stopInitiated;
  private boolean stopped;
  
  private long startTime;
  
  public void reset() {
    this.setStarted(false);
    this.stopInitiated = false;
    this.stopped = false;
    this.attemptedPre = false;
  }
  
  /**
   * Called from within the {@link #call() call()} method.
   * <p>
   * {@link #doCall() doCall()} is called from within {@link #call() call()} method after
   * {@link #pre() pre()} method.
   * </p>
   * <p>Default implementation does nothing</p>
   * @return The result
   * @throws java.lang.Exception
   * @see #call() 
   * @see #pre() 
   */
  protected abstract R doCall() throws Exception;
  
  /**
   * Called from within the {@link #call() call()} method.
   * <p>
   * {@link #pre() pre()} is called from within {@link #call() call()} method before 
   * {@link #doCall() doCall()} method.
   * </p>
   * <p>Default implementation does nothing</p>
   * @see #call() 
   * @see #doCall() 
   */
  protected void pre() {}
  
  /**
   * Called from within the {@link #call() call()} method.
   * <p>Default implementation does nothing</p>
   * @param result The result returned by the {@link #call() call()} method.
   * @see #calln() 
   */
  protected void onSuccess(R result) {  }
  
  /**
   * Called from within the {@link #call() call()} method.
   * <p>Default implementation does nothing</p>
   * @param e The Exception thrown by either the {@link #pre() pre()}, 
   * {@link #call() call()} or {@link #onSuccess(java.lang.Object) onSuccess(Object)} methods.
   * @see #call() 
   */
  protected void onError(Exception e) { 
      onError(e, "#onError(Exception)");
  }
  
  protected void onError(Exception e, String message) {
      LOG.log(Level.WARNING, message, e);
  }
  
  /**
   * Called from within the {@link #call() call()} method.
   * <p>
   * {@link #post() post()} is called from within {@link #call() call()} method after 
   * {@link #doCall() doCall()} method and only if {@link #pre() pre()} method earlier returned successfully. 
   * </p>
   * <p>Default implementation does nothing</p>
   * @see #call() 
   * @see #doCall()
   */
  protected void post() {}
  
  /**
   * Simply calls {@link #call() call()}
   * @see #call() 
   */
  @Override
  public final void run() {
    this.call();
  }
  
  /**
   * Within the {@link #call() call()} method, order of method call is as follows: 
   * <p>
   * {@link #pre() pre()} -> {@link #doCall() doCall()} -> 
   * ({@link #onSuccess(java.lang.Object) onSuccess(Object)} / {@link #onError(java.lang.Exception) onError(Exception)}) 
   * -> {@link #post() post()}
   * </p>
   * @see #doCall() 
   */
  @Override
  public final R call() {
      
    R result = null;  
     
    LOG.log(Level.FINER, "BEFORE #call(). {0}", this);
    
    if(this.isStarted()) {
      throw new IllegalStateException("Cannot call method #call() when: Started == true. Call #reset() before each successive call to #call()");
    }
    
    this.setStarted(true);
    this.stopInitiated = false;
    this.stopped = false;
    
    boolean preCalled = false;
    
    try {
        
      if (!this.attemptedPre) {
          
        this.attemptedPre = true;
        
        pre();
        
        preCalled = true;
      }
      
      result = doCall();

      if(this.isCompleted()) {
          
        onSuccess(result);
      }
    }catch(Exception e) {  
        
      onError(e);  
      
    }finally {
        
      this.stopped = true;
      
      if(preCalled) {
          
        post();
      }
      
      LOG.log(Level.FINER, "AFTER #call(). {0}", this);
    }

    return result;
  }

  protected void setStarted(boolean started) {
    this.started = started;
    if(started) {
      this.startTime = System.currentTimeMillis();
    }else{
      this.startTime = 0L;
    }
  }
  
  protected void setStopped(boolean stopped) {
    this.stopped = stopped;
  }

  @Override
  public void stop() {
    this.stopInitiated = true;
  }
  
  @Override
  public boolean isStopRequested() {
    return this.stopInitiated;
  }
  
  @Override
  public boolean isCompleted() {
    return (this.started) && (this.stopped) && (!this.stopInitiated);
  }
  
  @Override
  public boolean isStarted() {
    return this.started;
  }
  
  @Override
  public boolean isStopped() {
    return this.stopped;
  }
  
  public boolean isTimedout(long timeout) {
    return this.getStartTime() > 0 && timeout > 0 && this.getTimeSpent() > timeout;
  }
    
  public long getTimeSpent() {
    return System.currentTimeMillis() - this.getStartTime();
  }
    
  @Override
  public long getStartTime() {
    return startTime;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    print(builder);
    return builder.toString();
  }
  
  public void print(StringBuilder builder) {
    builder.append(getTaskName());
    builder.append("{Started=").append(this.started);
    if(this.startTime > 0) {
      builder.append(", start time=").append(this.startTime);
    }
    builder.append(", stopInitiated=").append(this.stopInitiated);
    builder.append(", stopped=").append(this.stopped);
    builder.append('}');
  }
}
