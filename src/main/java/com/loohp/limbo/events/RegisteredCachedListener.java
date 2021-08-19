package com.loohp.limbo.events;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.loohp.limbo.plugins.LimboPlugin;

public class RegisteredCachedListener {
	
	private LimboPlugin plugin;
	private Map<Class<? extends Event>, Map<EventPriority, List<Method>>> listeners;
	
	@SuppressWarnings("unchecked")
	public RegisteredCachedListener(LimboPlugin plugin, Listener listener) {
		this.plugin = plugin;
		this.listeners = new ConcurrentHashMap<>();
		for (Method method : listener.getClass().getMethods()) {
			if (method.isAnnotationPresent(EventHandler.class) && method.getParameterCount() == 1 && Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
				Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];
				listeners.putIfAbsent(eventClass, new ConcurrentHashMap<>());
				Map<EventPriority, List<Method>> mapping = listeners.get(eventClass);
				EventPriority priority = method.getAnnotation(EventHandler.class).priority();
				mapping.putIfAbsent(priority, new ArrayList<>());
				List<Method> list = mapping.get(priority);
				list.add(method);
			}
		}
	}
	
	public LimboPlugin getPlugin() {
		return plugin;
	}
	
	public List<Method> getListeners(Class<? extends Event> eventClass, EventPriority priority) {
		Map<EventPriority, List<Method>> mapping = listeners.get(eventClass);
		if (mapping == null) {
			return Collections.emptyList();
		}
		List<Method> list = mapping.get(priority);
		return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
	}

}
