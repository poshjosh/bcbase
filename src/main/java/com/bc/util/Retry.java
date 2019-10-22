/*
 * Copyright 2019 NUROX Ltd.
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

package com.bc.util;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 12, 2019 7:00:48 PM
 */
public interface Retry {

    <T> Optional<ScheduledFuture<T>> retryAsyncIf(Callable<T> callable, Predicate<Throwable> test, long delay);

    <T> ScheduledFuture<T> retryAsyncIf(Callable<T> callable, Predicate<Throwable> test, long delay, T outputIfNone);

    <T> Optional<T> retryIf(Callable<T> callable, Predicate<Throwable> test);

    <T> T retryIf(Callable<T> callable, Predicate<Throwable> test, T outputIfNone);

    <E extends Throwable, T> Optional<T> retryOn(Callable<T> callable, Class<E> type);

    <E extends Throwable, T> T retryOn(Callable<T> callable, Class<E> type, T outputIfNone);
}
