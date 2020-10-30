package com.loohp.limbo.Player;

import java.io.IOException;
import java.util.UUID;

import com.loohp.limbo.Limbo;
import com.loohp.limbo.Commands.CommandSender;
import com.loohp.limbo.Events.PlayerChatEvent;
import com.loohp.limbo.Events.PlayerTeleportEvent;
import com.loohp.limbo.Location.Location;
import com.loohp.limbo.Server.ClientConnection;
import com.loohp.limbo.Server.Packets.PacketPlayOutChat;
import com.loohp.limbo.Server.Packets.PacketPlayOutGameState;
import com.loohp.limbo.Server.Packets.PacketPlayOutPositionAndLook;
import com.loohp.limbo.Server.Packets.PacketPlayOutRespawn;
import com.loohp.limbo.Utils.GameMode;
import com.loohp.limbo.World.World;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class Player implements CommandSender {

	public final ClientConnection clientConnection;

	private final String username;
	private final UUID uuid;
	protected GameMode gamemode;
	
	protected int entityId;

	private Location location;
	
	public Player(ClientConnection clientConnection, String username, UUID uuid, int entityId, Location location) {
		this.clientConnection = clientConnection;
		this.username = username;
		this.uuid = uuid;
		this.entityId = entityId;
		this.location = location.clone();
	}
	
	public GameMode getGamemode() {
		return gamemode;
	}

	public void setGamemode(GameMode gamemode) {
		if (!this.gamemode.equals(gamemode)) {
			try {
				PacketPlayOutGameState state = new PacketPlayOutGameState(3, gamemode.getId());
				clientConnection.sendPacket(state);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.gamemode = gamemode;
	}

	public World getWorld() {
		return location.clone().getWorld();
	}
	
	public int getEntityId() {
		return entityId;
	}

	public Location getLocation() {
		return location.clone();
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getName() {
		return username;
	}

	public UUID getUUID() {
		return uuid;
	}
	
	public boolean hasPermission(String permission) {
		return Limbo.getInstance().getPermissionsManager().hasPermission(this, permission);
	}

	public void sendMessage(String message) {
		sendMessage(TextComponent.fromLegacyText(message));
	}

	public void sendMessage(BaseComponent component) {
		sendMessage(new BaseComponent[] { component });
	}

	public void teleport(Location location) {
		PlayerTeleportEvent event = Limbo.getInstance().getEventsManager().callEvent(new PlayerTeleportEvent(this, getLocation(), location));
		if (!event.isCancelled()) {
			location = event.getTo();
			try {
				if (!this.location.getWorld().equals(location.getWorld())) {
					PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(location.getWorld(), Limbo.getInstance().getDimensionRegistry().getCodec(), 0, gamemode, false, false, true);
					clientConnection.sendPacket(respawn);
				}
				PacketPlayOutPositionAndLook positionLook = new PacketPlayOutPositionAndLook(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), 1);
				clientConnection.sendPacket(positionLook);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void sendMessage(BaseComponent[] component) {
		try {
			PacketPlayOutChat chat = new PacketPlayOutChat(ComponentSerializer.toString(component), 0, new UUID(0, 0));
			clientConnection.sendPacket(chat);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		disconnect("Disconnected!");
	}
	
	public void disconnect(String reason) {
		disconnect(TextComponent.fromLegacyText(reason));
	}
	
	public void disconnect(BaseComponent reason) {
		disconnect(new BaseComponent[] {reason});
	}
	
	public void disconnect(BaseComponent[] reason) {
		clientConnection.disconnect(reason);
	}
	
	public void chat(String message) {
		String prefix = "<" + username + "> ";
		PlayerChatEvent event = (PlayerChatEvent) Limbo.getInstance().getEventsManager().callEvent(new PlayerChatEvent(this, prefix, message, false));
		if (!event.isCancelled()) {
			String chat = event.getPrefix() + event.getMessage();
			Limbo.getInstance().getConsole().sendMessage(chat);
			for (Player each : Limbo.getInstance().getPlayers()) {
				each.sendMessage(chat);
			}
		}
	}

}
