package com.bc.task;

import java.io.Serializable;

public abstract interface StoppableTask
  extends Task, Serializable
{
  public abstract void stop();
  
  public abstract boolean isStopInitiated();
  
  public abstract boolean isStopped();
  
  public abstract String getTaskName();
}
