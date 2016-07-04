package com.bc.task;

import com.bc.util.XLogger;
import java.util.List;
import java.util.logging.Level;

public abstract class AbstractStoppableTaskList 
    extends AbstractTaskList<StoppableTask>
    implements ProgressTask {
    
  @Override
  public String getMessage() {
      
    StringBuilder message = new StringBuilder("Please wait, running ");
    
    message.append(getMax()).append(" tasks.\n");
    
    for (StoppableTask task : getList()) {
      if (task.isCompleted()) {
        message.append("Completed task: ");
      } else if ((task.isStarted()) && (task.isStopRequested())) {
        if (task.isStopped()) {
          message.append("Stopping task: ");
        } else {
          message.append("Stopped task: ");
        }
      }
      message.append(task).append("\n");
    }
    
    return message.toString();
  }
  
  public int getMax()
  {
    return getTaskCount();
  }
  

  public int getPos()
  {
    int pos = super.getPos();
    
    if (pos == getMax())
    {
      StoppableTask lastTask = (StoppableTask)getList().get(getList().size() - 1);
      
      if (lastTask.isStopped())
      {
        return pos;
      }
    }
    
    return pos - 1;
  }
  
  public void execute(StoppableTask task) {
    try
    {
      if ((task == null) || (task.isStopRequested()) || (task.isCompleted())) {
        return;
      }
      XLogger.getInstance().log(Level.FINER, "Sub process: {0}", getClass(), task);
      
      task.run();

    }
    catch (RuntimeException e)
    {

      XLogger.getInstance().log(Level.WARNING, "Exception encountered while executing task: " + task, getClass(), e);
    }
  }
  

  public void stop(StoppableTask task)
  {
    if ((task != null) && (task.isStarted()) && (!task.isStopped())) {
      task.stop();
    }
  }
  


  public int countRunning()
  {
    return countRunning(true);
  }
  






  public int countRunning(boolean greedy)
  {
    List<StoppableTask> tasks = getList();
    
    int count = 0;
    
    for (StoppableTask task : tasks)
    {
      if ((!task.isStarted()) && (!greedy)) {
        break;
      }
      



      if ((task.isStarted()) && (!task.isStopped()))
      {
        count++;
      }
    }
    
    XLogger.getInstance().log(Level.FINER, "Processes {0}, running: {1}, ", getClass(), Integer.valueOf(tasks.size()), Integer.valueOf(count));
    
    return count;
  }
  


  public int countStopped()
  {
    return countStopped(true);
  }
  






  public int countStopped(boolean greedy)
  {
    List<StoppableTask> tasks = getList();
    
    int count = 0;
    
    for (StoppableTask task : tasks)
    {
      if ((!task.isStarted()) && (!greedy)) {
        break;
      }
      



      if ((task.isStarted()) && (task.isStopped()))
      {
        count++;
      }
    }
    
    XLogger.getInstance().log(Level.FINER, "Processes {0}, stopped: {1}, ", getClass(), Integer.valueOf(tasks.size()), Integer.valueOf(count));
    
    return count;
  }
  

  public boolean isCompleted()
  {
    List<StoppableTask> tasks = getList();
    
    int count = 0;
    
    for (StoppableTask task : tasks)
    {
      if (task.isCompleted()) {
        count++;
      }
    }
    
    return count == getTaskCount();
  }
}
