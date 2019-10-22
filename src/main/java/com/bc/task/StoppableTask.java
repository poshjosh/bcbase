package com.bc.task;

import java.io.Serializable;

public interface StoppableTask<R> extends Task<R>, Serializable {

    boolean isRunning();
    
    boolean isTimedout(long timeout);
    
    long getTimeSpent();
    
    void stop();
  
    long getStartTime();
    
    long getStopTime();
  
    boolean isStopRequested();
  
    boolean isStopped();
}
