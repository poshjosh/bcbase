package com.bc.task;

import java.util.concurrent.Callable;

public abstract interface Task<R> extends Callable<R>, Runnable {
    
  public abstract boolean isStarted();
  
  public abstract boolean isCompleted();

  public abstract String getTaskName();
}
