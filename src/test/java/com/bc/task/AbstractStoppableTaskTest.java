/*
 * Copyright 2018 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bc.task;

import com.bc.util.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Josh
 */
public class AbstractStoppableTaskTest {
    
    private static class Task extends AbstractStoppableTask {
        private final Object id;
        private final int loopCount;
        public Task(Object id, int loopCount) {
            this.id = Objects.requireNonNull(id);
            this.loopCount = loopCount;
        }
        public void start() {
            System.out.println("Starting: " + this);
            if(!this.isRunning()) {
                if(this.isStarted()) {
                    this.setStarted(false);
                }
                this.call();
            }
        }
        @Override
        protected Object doCall() throws Exception {
            System.out.println("Calling: "+this);
            int i= 0;
            for(; i<loopCount; i++) {
                synchronized(this) {
                    try{
                        this.wait(1000);
                    }catch(InterruptedException e) {
                        e.printStackTrace();
                    }finally{
                        this.notifyAll();
                    }
                    System.out.println(this.getTaskName()+'('+i+')');
                }
            }
            return i; 
        }
        @Override
        public String getTaskName() {
            return "Task_" + id;
        }
    }
    
    public AbstractStoppableTaskTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    public Task getTask(Object id, int loopCount) {
        return new Task(id, loopCount);
    }
    
    @Test
    public void testAll() {
        System.out.println("testAll");
        
        final int loopCount = 5;
        final long maxIdleMillis = 1_000;
        
        final List<Task> taskList = new ArrayList<>();
        
        final Runnable add = () -> {
//            System.out.println("\tTask count: " + instance.size());
            final Integer id = this.generateId();
            final Task task = this.getTask(id, loopCount);
            System.out.println("\tRunning task: " + task);
            task.run();
            taskList.add(task);
        };

        final Runnable read = () -> {
            System.out.println("\tTask count: " + taskList.size());
            taskList.stream().forEach((task) -> {
                final boolean yes = Math.random() >= 0.5;
                if(yes) {
                    if(task.isRunning()) {
                        task.stop();
                    }else{
                        task.start();
                    }
                }
            });
        };
        
        final Runnable [] tasks = {add, add, read};

        final ScheduledExecutorService svc = Executors.newScheduledThreadPool(tasks.length);

        for(Runnable task : tasks) {
            
            svc.scheduleAtFixedRate(task, 1, 1, TimeUnit.SECONDS);
        }
        
        synchronized(this) {
            try{
                this.wait(25_000);
            }catch(InterruptedException ignored) {
            }finally{
                this.notifyAll();
            }
        }
        
        Util.shutdownAndAwaitTermination(svc, 500, TimeUnit.MILLISECONDS);
    }
    
    private int i;
    
    public Integer generateId() {
        return ++i;
    }
}
