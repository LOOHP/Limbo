/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.loohp.limbo.events.impl;

import com.loohp.limbo.events.api.EventPriority;
import com.loohp.limbo.events.api.Event;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

class ArrayBackedEvent<T> extends Event<T>
{
    private final Function<T[], T> invokerFactory;
    private final Lock lock = new ReentrantLock();
    private T[] handlers;

    private final Class<? super T> invokerType;
    private InvokerWrapper<T>[] handlersWrapped;

    @SuppressWarnings("unchecked")
    ArrayBackedEvent(Class<? super T> type, Function<T[], T> invokerFactory) {
        this.invokerType = type;
        this.invokerFactory = invokerFactory;
        this.handlers = (T[]) Array.newInstance(type, 0);
        this.handlersWrapped = new InvokerWrapper[0];
        update();
    }

    void update() {
        this.invoker = invokerFactory.apply(handlers);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void register(EventPriority eventPriority, T listener) {
        Objects.requireNonNull(listener, "Tried to register a null listener!");

        lock.lock();

        try {
            handlersWrapped = Arrays.copyOf(handlersWrapped, handlersWrapped.length + 1);
            handlersWrapped[handlersWrapped.length - 1] = new InvokerWrapper<>(eventPriority, listener);
            Arrays.sort(handlersWrapped, Comparator.comparing(wrapper -> wrapper.priority));

            handlers = Arrays.stream(handlersWrapped).map(InvokerWrapper::getInvoker).toArray(size -> (T[]) Array.newInstance(this.invokerType, size));
            update();
        } finally {
            lock.unlock();
        }
    }

    static class InvokerWrapper<T> {

        public EventPriority priority;
        public T invoker;

        public InvokerWrapper(EventPriority priority, T invoker) {
            this.priority = priority;
            this.invoker = invoker;
        }

        public T getInvoker() {
            return this.invoker;
        }
    }
}