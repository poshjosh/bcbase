package com.bc.task;


/**
 * @(#)ControlledTask.java   30-May-2015 04:09:40
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
public interface ControlledTask {

    long getBatchInterval();

    int getBatchSize();

    int getLastoffset();

    int getMax();

    int getOffset();

    int getTotal();

    boolean isRunning();

    boolean isStopRequested();

    void resume();

    void setBatchInterval(long batchInterval);

    void setBatchSize(int batchSize);

    void setMax(int max);

    void setOffset(int offset);

    void start();

    void startAt(int off);

    boolean stop();

}
