package com.bc.task;

public abstract interface Task
  extends Runnable
{
  public abstract boolean isStarted();
  
  public abstract boolean isCompleted();
}
