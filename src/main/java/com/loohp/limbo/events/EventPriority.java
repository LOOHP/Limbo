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