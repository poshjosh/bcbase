package com.bc.task;

import java.util.concurrent.Callable;

public interface Task<R> extends Callable<R>, Runnable {
    
    boolean isStarted();
  
    boolean isCompleted();

    String getTaskName();
}
