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

import com.loohp.limbo.events.api.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


// As required by the Apache License, Version 2.0 we are required to
// mention that we decided to delete the second method in this class.
// It was considered unnecessary code by us and thus remove, (this also influences the imports)
public final class EventFactoryImpl {
    private static final List<ArrayBackedEvent<?>> ARRAY_BACKED_EVENTS = new ArrayList<>();

    private EventFactoryImpl() { }

    public static void invalidate() {
        ARRAY_BACKED_EVENTS.forEach(ArrayBackedEvent::update);
    }

    public static <T> Event<T> createArrayBacked(Class<? super T> type, Function<T[], T> invokerFactory) {
        ArrayBackedEvent<T> event = new ArrayBackedEvent<>(type, invokerFactory);
        ARRAY_BACKED_EVENTS.add(event);
        return event;
    }
}