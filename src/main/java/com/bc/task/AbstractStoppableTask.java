package com.bc.task;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractStoppableTask<R> implements Serializable, StoppableTask<R>{

  private transient static final Logger LOG = Logger.getLogger(AbstractStoppableTask.class.getName());
    
  private boolean attemptedPre;
  
  private boolean stopRequested;
  
  private long startTime;
  private long stopTime;
  
  public void reset() {
    this.setStarted(false);
    this.stopRequested = false;
    this.setStopped(false);
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
   * {@link #doCall() doCall()} method.
   * </p>
   * <p>Default implementation does nothing</p>
   * @param preSuccessful This value is a reflection of whether {@link #pre()} returned successfully.
   * @see #call() 
   * @see #doCall()
   */
  protected void post(boolean preSuccessful) {}
  
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
    this.stopRequested = false;
    this.setStopped(false);
    
    boolean preSuccessful = false;
    
    try {
        
      if (!this.attemptedPre) {
          
        this.attemptedPre = true;
        
        pre();
        
        preSuccessful = true;
      }
      
      result = doCall();

      if(this.isCompleted()) {
          
        onSuccess(result);
      }
    }catch(Exception e) {  
        
      onError(e);  
      
    }finally {
        
      this.setStopped(true);
      
      post(preSuccessful);
      
      LOG.log(Level.FINER, "AFTER #call(). {0}", this);
    }

    return result;
  }

  protected void setStarted(boolean started) {
    if(started) {
      this.startTime = System.currentTimeMillis();
    }else{
      this.startTime = 0L;
    }
  }
  
  protected void setStopped(boolean stopped) {
    if(stopped) {
      this.stopTime = System.currentTimeMillis();
    }else{
      this.stopTime = 0L;
    }
  }

  @Override
  public void stop() {
    this.stopRequested = true;
  }
  
  @Override
  public boolean isRunning() {
    return this.isStarted() && !this.isCompleted() && !this.isStopped();
  }

  @Override
  public boolean isTimedout(long timeout) {
    return this.getStartTime() > 0 && timeout > 0 && this.getTimeSpent() > timeout;
  }

  @Override
  public long getTimeSpent() {
    final long result;
    if(this.isStarted()) {
      result = this.isStopped() ? this.getStopTime() - this.getStartTime() :
          System.currentTimeMillis() - this.getStartTime();
    }else{
      result = 0L;
    }
    return result;
  }

  @Override
  public boolean isStopRequested() {
    return this.stopRequested;
  }
  
  @Override
  public boolean isCompleted() {
    return this.isStarted() && this.isStopped() && !this.stopRequested;
  }
  
  @Override
  public boolean isStarted() {
    return this.startTime > 0;
  }
  
  @Override
  public boolean isStopped() {
    return this.stopTime > 0;
  }
  
  @Override
  public long getStartTime() {
    return startTime;
  }

  @Override
  public long getStopTime() {
    return stopTime;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(super.toString());
    appendFields(builder);
    return builder.toString();
  }
  
  public void appendFields(StringBuilder builder) {
    builder.append("{Started=").append(this.isStarted());
    if(this.isStarted()) {
      builder.append(", startTime=").append(this.startTime);
      builder.append(", timeSpent=").append(this.getTimeSpent());
    }
    builder.append(", stopInitiated=").append(this.stopRequested);
    builder.append(", stopped=").append(this.isStopped());
    builder.append('}');
  }
}
