package com.bc.task;

import java.io.Serializable;

public interface StoppableTask<R> extends Task<R>, Serializable {
    
  void stop();
  
  long getStartTime();
  
  boolean isStopRequested();
  
  boolean isStopped();
}
