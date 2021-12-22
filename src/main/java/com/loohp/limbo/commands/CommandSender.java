package com.loohp.limbo.commands;

import java.util.UUID;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import net.md_5.bungee.api.chat.BaseComponent;

public interface CommandSender extends Audience {
	
	public void sendMessage(BaseComponent[] component, UUID uuid);
	
	public void sendMessage(BaseComponent component, UUID uuid);
	
	public void sendMessage(String message, UUID uuid);
	
	public void sendMessage(BaseComponent[] component);
	
	public void sendMessage(BaseComponent component);
	
	public void sendMessage(String message);
	
	public boolean hasPermission(String permission);
	
	public String getName();
	
	public void sendMessage(Identity source, Component message, MessageType type);
	
	public void openBook(Book book);
	
	public void stopSound(SoundStop stop);
	
	public void playSound(Sound sound, Sound.Emitter emitter);
	
	public void playSound(Sound sound, double x, double y, double z);
	
	public void playSound(Sound sound);
	
	public void sendActionBar(Component message);
	
	public void sendPlayerListHeaderAndFooter(Component header, Component footer);
	
	public <T> void sendTitlePart(TitlePart<T> part, T value);
	
	public void clearTitle();
	
	public void resetTitle();
	  
	public void showBossBar(BossBar bar);

	public void hideBossBar(BossBar bar);
	
}
