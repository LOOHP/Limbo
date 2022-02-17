/*
 * This file is part of Limbo.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
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

package com.loohp.limbo.events;

public enum EventPriority {
	
	LOWEST(0),
	LOW(1),
	NORMAL(2),
	HIGH(3),
	HIGHEST(4),
	MONITOR(5);
	
	int order;
	
	EventPriority(int order) {
		this.order = order;
	}
	
	public int getOrder() {
		return order;
	}
	
	public static EventPriority getByOrder(int order) {
		for (EventPriority each : EventPriority.values()) {
			if (each.getOrder() == order) {
				return each;
			}
		}
		return null;
	}
	
	public static EventPriority[] getPrioritiesInOrder() {
		EventPriority[] array = new EventPriority[EventPriority.values().length];
		for (int i = 0; i < array.length; i++) {
			array[i] = EventPriority.getByOrder(i);
		}
		return array;
	}
	
}