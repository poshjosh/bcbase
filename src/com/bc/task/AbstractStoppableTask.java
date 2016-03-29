package com.bc.task;

import com.bc.util.XLogger;
import java.util.logging.Level;













public abstract class AbstractStoppableTask
  implements StoppableTask
{
  private boolean started;
  private boolean stopInitiated;
  private boolean stopped;
  
  public void reset()
  {
    this.started = false;
    this.stopInitiated = false;
    this.stopped = false;
  }
  
  protected abstract void doRun();
  
  public void stop() {
    this.stopInitiated = true;
  }
  
  public boolean isStopInitiated() {
    return this.stopInitiated;
  }
  
  public boolean isCompleted() {
    return (this.started) && (this.stopped) && (!this.stopInitiated);
  }
  
  public boolean isStarted() {
    return this.started;
  }
  
  public boolean isStopped() {
    return this.stopped;
  }
  

  @Override
  public void run()
  {
    XLogger.getInstance().log(Level.FINER, "Before doRun(). {0}", getClass(), this);
    

    this.started = true;
    this.stopInitiated = false;
    this.stopped = false;
    
    try
    {
      doRun();
    }
    finally
    {
      this.stopped = true;
      
      XLogger.getInstance().log(Level.FINER, "After doRun(). {0}", getClass(), this);
    }
  }
  
  protected void setStarted(boolean started)
  {
    this.started = started;
  }
  
  protected void setStopped(boolean stopped) {
    this.stopped = stopped;
  }
  
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    print(builder);
    return builder.toString();
  }
  
  public void print(StringBuilder builder) {
    builder.append(getTaskName());
    builder.append(", started: ").append(this.started);
    builder.append(", stopInitiated: ").append(this.stopInitiated);
    builder.append(", stopped: ").append(this.stopped);
  }
}
