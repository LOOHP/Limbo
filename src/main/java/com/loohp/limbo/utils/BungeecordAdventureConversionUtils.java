package com.loohp.limbo.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class BungeecordAdventureConversionUtils {
	
	public static Component toComponent(BaseComponent... components) {
		return GsonComponentSerializer.gson().deserialize(ComponentSerializer.toString(components));
	}
	
	public static BaseComponent[] toComponent(Component component) {
		return ComponentSerializer.parse(GsonComponentSerializer.gson().serialize(component));
	}

}
