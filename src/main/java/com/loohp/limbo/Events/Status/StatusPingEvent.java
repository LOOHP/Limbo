package com.loohp.limbo.Events.Status;

import java.awt.image.BufferedImage;

import com.loohp.limbo.Events.Event;
import com.loohp.limbo.Server.ClientConnection;

import net.md_5.bungee.api.chat.BaseComponent;

public class StatusPingEvent extends Event {

	private ClientConnection connection;
	private String version;
	private int protocol;
	private BaseComponent[] motd;
	private int maxPlayers;
	private int playersOnline;
	private BufferedImage favicon;

	public StatusPingEvent(ClientConnection connection, String version, int protocol, BaseComponent[] motd, int maxPlayers, int playersOnline, BufferedImage favicon) {
		this.connection = connection;
		this.version = version;
		this.protocol = protocol;
		this.motd = motd;
		this.maxPlayers = maxPlayers;
		this.playersOnline = playersOnline;
		this.favicon = favicon;
	}
	
	public ClientConnection getConnection() {
		return connection;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getProtocol() {
		return protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

	public BaseComponent[] getMotd() {
		return motd;
	}

	public void setMotd(BaseComponent[] motd) {
		this.motd = motd;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public int getPlayersOnline() {
		return playersOnline;
	}

	public void setPlayersOnline(int playersOnline) {
		this.playersOnline = playersOnline;
	}

	public BufferedImage getFavicon() {
		return favicon;
	}

	public void setFavicon(BufferedImage favicon) {
		this.favicon = favicon;
	}

}
