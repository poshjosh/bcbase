/*
 * Copyright 2017 NUROX Ltd.
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

package com.bc.functions;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Dec 12, 2017 10:32:59 PM
 */
public class GetDateOfAge implements Serializable, BiFunction<Integer, TimeUnit, Date> {

    private transient static final Logger LOG = Logger.getLogger(GetDateOfAge.class.getName());

    public Date get(Long age, TimeUnit timeUnit) {
        return this.apply((int)timeUnit.toDays(age), TimeUnit.DAYS);
    }
    
    @Override
    public Date apply(Integer age, TimeUnit timeUnit) {
        
        final Calendar cal = Calendar.getInstance();
        
        final int calField = this.getCalendarField(timeUnit);
        
        LOG.finer(() -> "To subtract from current date: " + age + ' ' + timeUnit);
        
        cal.add(calField, -age);
        
        final Date date = cal.getTime();
        
        LOG.fine(() -> "Input: " + age + ' ' + timeUnit + ", Output: " + date);

        return date;
    }
    
    public int getCalendarField(TimeUnit timeUnit) {
        switch(timeUnit) {
            case DAYS: return Calendar.DAY_OF_YEAR;
            case HOURS: return Calendar.HOUR_OF_DAY;
            case MINUTES: return Calendar.MINUTE;
            case SECONDS: return Calendar.SECOND;
            case MILLISECONDS: return Calendar.MILLISECOND;
            default: throw new UnsupportedOperationException("TimeUnit: "+timeUnit);
        }
    }
}
