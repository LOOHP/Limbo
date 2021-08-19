package com.loohp.limbo.events;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.loohp.limbo.plugins.LimboPlugin;

public class EventsManager {
	
	private List<ListenerPair> listeners;
	private Map<Listener, RegisteredCachedListener> cachedListeners;
	
	public EventsManager() {
		listeners = new ArrayList<>();
		cachedListeners = new ConcurrentHashMap<>();
	}
	
	public <T extends Event> T callEvent(T event) {
		for (EventPriority priority : EventPriority.getPrioritiesInOrder()) {
			for (Entry<Listener, RegisteredCachedListener> entry : cachedListeners.entrySet()) {
				for (Method method : entry.getValue().getListeners(event.getClass(), priority)) {
					try {
						method.invoke(entry.getKey(), event);
					} catch (Exception e) {
						System.err.println("Error while passing " + event.getClass().getCanonicalName() + " to the plugin \"" + entry.getValue().getPlugin().getName() + "\"");
						e.printStackTrace();
					}
				}
			}
		}
		return event;
	}
	
	public void registerEvents(LimboPlugin plugin, Listener listener) {
		listeners.add(new ListenerPair(plugin, listener));
		cachedListeners.put(listener, new RegisteredCachedListener(plugin, listener));
	}
	
	public void unregisterAllListeners(LimboPlugin plugin) {
		listeners.removeIf(each -> {
			if (each.plugin.equals(plugin)) {
				cachedListeners.remove(each.listener);
				return true;
			} else {
				return false;
			}
		});
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
