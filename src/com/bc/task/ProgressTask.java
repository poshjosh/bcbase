package com.bc.task;

public abstract interface ProgressTask
{
  public abstract int getMax();
  
  public abstract int getPos();
  
  public abstract long getStartTime();
  
  public abstract String getMessage();
}
