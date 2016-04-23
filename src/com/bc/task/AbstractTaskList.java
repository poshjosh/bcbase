package com.bc.task;

import com.bc.util.XLogger;
import java.util.List;
import java.util.logging.Level;
























public abstract class AbstractTaskList<E>
  extends AbstractStoppableTask
{
  private boolean attemptedPre;
  private int pos;
  
  protected abstract List<E> getList();
  
  public abstract void execute(E paramE);
  
  public abstract void stop(E paramE);
  
  protected void pre() {}
  
  protected void post() {}
  
  public void reset()
  {
    super.reset();
    this.attemptedPre = false;
    this.pos = 0;
  }
  

  public void stop()
  {
    super.stop();
    

    E current = getCurrent();
    
    if (current != null)
    {
      stop(current);
    }
    





    List<E> tasks = getList();
    
    for (E task : tasks) {
      stop(task);
    }
  }
  

  public void run()
  {
    try
    {
      if (!this.attemptedPre)
      {
        this.attemptedPre = true;
        
        pre();
      }
      
      super.run();
    }
    finally
    {
      if (isCompleted())
      {
        post();
      }
    }
  }
  

  protected void doRun()
  {
    XLogger.getInstance().log(Level.FINE, "Running process: {0} with {1} sub processes, starting at offset: {2}", getClass(), getClass().getSimpleName(), Integer.valueOf(getTaskCount()), Integer.valueOf(this.pos));
    

    while (this.pos < getTaskCount())
    {
      E current = getCurrent();
      
      XLogger.getInstance().log(Level.FINER, "Running process {0} of {1}: {2}", getClass(), Integer.valueOf(this.pos + 1), Integer.valueOf(getTaskCount()), current);
      

      if (isStopRequested()) {
        break;
      }
      

      execute(current);
      
      this.pos += 1;
    }
    
    XLogger.getInstance().log(Level.FINER, "DONE Running process: {0}. pos: {1}, size: {2}", getClass(), getClass().getSimpleName(), Integer.valueOf(this.pos), Integer.valueOf(getTaskCount()));
  }
  
  public E getCurrent()
  {
    List<E> processes = getList();
    if ((processes == null) || (processes.isEmpty())) {
      return null;
    }
    




    if (this.pos > getTaskCount() - 1) {
      return null;
    }
    
    return (E)processes.get(this.pos);
  }
  
  public int getTaskCount() {
    List<E> processes = getList();
    return processes == null ? 0 : processes.size();
  }
  
  public int getPos() {
    return this.pos;
  }
  
  public void print(StringBuilder builder)
  {
    super.print(builder);
    builder.append(", attemptedPre: ").append(this.attemptedPre);
    builder.append(", pos: ").append(this.pos);
    builder.append(", size: ").append(getTaskCount());
  }
}
