package com.bc.task;

import com.bc.util.XLogger;
import java.util.logging.Level;

public abstract class AbstractStoppableTask implements StoppableTask {
    
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
  
  protected abstract void doRun();
  
  public long getTimeout() {
    return 0L;
  }
  
  protected void pre() {}
  
  protected void post() {}
  
  @Override
  public final void run() {
     
    XLogger.getInstance().log(Level.FINER, "Before doRun(). {0}", getClass(), this);
    
    if(this.isStarted()) {
      throw new IllegalStateException("Cannot call method #run() when: Started == true. Call #reset() before each successive call to #run");
    }
    
    this.setStarted(true);
    this.stopInitiated = false;
    this.stopped = false;
    
    try {
        
      if (!this.attemptedPre) {
          
        this.attemptedPre = true;
        
        pre();
      }
      
      doRun();
      
    }finally {
      this.stopped = true;
      XLogger.getInstance().log(Level.FINER, "After doRun(). {0}", getClass(), this);
      if(this.isCompleted()) {
        post();
      }
    }
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
  
  public boolean isRunning() {
    return this.started && !this.stopped;
  }
    
  public boolean isTimedout() {
    return this.getStartTime() > 0 && this.getTimeout() > 0 && this.getTimeSpent() > this.getTimeout();
  }
    
  public long getTimeSpent() {
    return System.currentTimeMillis() - this.getStartTime();
  }
    
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
    builder.append(", started: ").append(this.started);
    if(this.startTime > 0) {
      builder.append(", start time: ").append(this.startTime);
    }
    builder.append(", stopInitiated: ").append(this.stopInitiated);
    builder.append(", stopped: ").append(this.stopped);
  }
}
