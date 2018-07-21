package com.bc.task;

import java.io.Serializable;

public interface StoppableTask<R> extends Task<R>, Serializable {

    default boolean isRunning() {
        return this.isStarted() && !this.isCompleted() && !this.isStopped();
    }
    
    void stop();
  
    long getStartTime();
  
    boolean isStopRequested();
  
    boolean isStopped();
}
