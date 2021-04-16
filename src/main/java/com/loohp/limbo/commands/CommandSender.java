package com.loohp.limbo.commands;

import net.md_5.bungee.api.chat.BaseComponent;

import java.util.UUID;

public interface CommandSender {

    void sendMessage(BaseComponent[] component, UUID uuid);

    void sendMessage(BaseComponent component, UUID uuid);

    void sendMessage(String message, UUID uuid);

    void sendMessage(BaseComponent[] component);

    void sendMessage(BaseComponent component);

    void sendMessage(String message);

    boolean hasPermission(String permission);

    String getName();

}
