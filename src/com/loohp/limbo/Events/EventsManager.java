package com.loohp.limbo.Events;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.loohp.limbo.Plugins.LimboPlugin;

public class EventsManager {
	
	private List<ListenerPair> listeners;
	
	public EventsManager() {
		listeners = new ArrayList<>();
	}
	
	public Event callEvent(Event event) {
		for (EventPriority priority : EventPriority.getPrioritiesInOrder()) {
			for (ListenerPair entry : listeners) {
				Listener listener = entry.listener;
				for (Method method : listener.getClass().getMethods()) {
					if (method.isAnnotationPresent(EventHandler.class)) {
						if (method.getAnnotation(EventHandler.class).priority().equals(priority)) {
							if (method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(event.getClass())) {
								try {
									method.invoke(listener, event);
								} catch (Exception e) {
									System.err.println("Error while passing " + event.getClass().getCanonicalName() + " to the plugin \"" + entry.plugin.getName() + "\"");
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
		return event;
	}
	
	public void registerEvents(LimboPlugin plugin, Listener listener) {
		listeners.add(new ListenerPair(plugin, listener));
	}
	
	public void unregisterAllListeners(LimboPlugin plugin) {
		listeners.removeIf(each -> each.plugin.equals(plugin));
	}
	
	protected static class ListenerPair {
		public LimboPlugin plugin;
		public Listener listener;
		
		public ListenerPair(LimboPlugin plugin, Listener listener) {
			this.plugin = plugin;
			this.listener = listener;
		}
	}

}
