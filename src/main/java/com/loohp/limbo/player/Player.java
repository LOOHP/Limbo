package com.loohp.limbo.player;

import java.io.IOException;
import java.util.UUID;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.loohp.limbo.Limbo;
import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.entity.DataWatcher;
import com.loohp.limbo.entity.DataWatcher.WatchableField;
import com.loohp.limbo.entity.DataWatcher.WatchableObjectType;
import com.loohp.limbo.entity.EntityType;
import com.loohp.limbo.entity.LivingEntity;
import com.loohp.limbo.events.player.PlayerChatEvent;
import com.loohp.limbo.events.player.PlayerTeleportEvent;
import com.loohp.limbo.location.Location;
import com.loohp.limbo.server.ClientConnection;
import com.loohp.limbo.server.packets.PacketPlayOutChat;
import com.loohp.limbo.server.packets.PacketPlayOutGameState;
import com.loohp.limbo.server.packets.PacketPlayOutHeldItemChange;
import com.loohp.limbo.server.packets.PacketPlayOutPlayerListHeaderFooter;
import com.loohp.limbo.server.packets.PacketPlayOutPluginMessaging;
import com.loohp.limbo.server.packets.PacketPlayOutPositionAndLook;
import com.loohp.limbo.server.packets.PacketPlayOutResourcePackSend;
import com.loohp.limbo.server.packets.PacketPlayOutRespawn;
import com.loohp.limbo.utils.GameMode;
import com.loohp.limbo.utils.NamespacedKey;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;

public class Player extends LivingEntity implements CommandSender {
	
	public static final String CHAT_DEFAULT_FORMAT = "<%name%> %message%";
	public static final BaseComponent[] EMPTY_CHAT_COMPONENT = new BaseComponent[] {new TextComponent("")};

	public final ClientConnection clientConnection;
	public final PlayerInteractManager playerInteractManager;

	protected final String username;
	protected GameMode gamemode;
	protected DataWatcher watcher;
	protected byte selectedSlot;
	
	@WatchableField(MetadataIndex = 15, WatchableObjectType = WatchableObjectType.FLOAT) 
	protected float additionalHearts = 0.0F;
	@WatchableField(MetadataIndex = 16, WatchableObjectType = WatchableObjectType.VARINT) 
	protected int score = 0;
	@WatchableField(MetadataIndex = 17, WatchableObjectType = WatchableObjectType.BYTE) 
	protected byte skinLayers = 0;
	@WatchableField(MetadataIndex = 18, WatchableObjectType = WatchableObjectType.BYTE) 
	protected byte mainHand = 1;
	//@WatchableField(MetadataIndex = 19, WatchableObjectType = WatchableObjectType.NBT) 
	//protected Entity leftShoulder = null;
	//@WatchableField(MetadataIndex = 20, WatchableObjectType = WatchableObjectType.NBT) 
	//protected Entity rightShoulder = null;
	
	public Player(ClientConnection clientConnection, String username, UUID uuid, int entityId, Location location, PlayerInteractManager playerInteractManager) throws IllegalArgumentException, IllegalAccessException {
		super(EntityType.PLAYER, entityId, uuid, location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		this.clientConnection = clientConnection;
		this.username = username;
		this.entityId = entityId;
		this.playerInteractManager = playerInteractManager;
		this.playerInteractManager.setPlayer(this);
		this.watcher = new DataWatcher(this);
		this.watcher.update();
	}

	public byte getSelectedSlot() {
		return selectedSlot;
	}

	public void setSelectedSlot(byte slot) {
		if(slot == selectedSlot)
			return;
		try {
			PacketPlayOutHeldItemChange state = new PacketPlayOutHeldItemChange(slot);
			clientConnection.sendPacket(state);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.selectedSlot = slot;
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
	
	@Deprecated
	protected void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	public float getAdditionalHearts() {
		return additionalHearts;
	}

	public void setAdditionalHearts(float additionalHearts) {
		this.additionalHearts = additionalHearts;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public byte getSkinLayers() {
		return skinLayers;
	}

	public void setSkinLayers(byte skinLayers) {
		this.skinLayers = skinLayers;
	}

	public byte getMainHand() {
		return mainHand;
	}

	public void setMainHand(byte mainHand) {
		this.mainHand = mainHand;
	}
	
	@Override
	public DataWatcher getDataWatcher() {
		return watcher;
	}
	
	@Override
	public boolean isValid() {
		return Limbo.getInstance().getPlayers().contains(this);
	}
	
	@Override
	public void remove() {
		
	}
	
	/*
	public Entity getLeftShoulder() {
		return leftShoulder;
	}

	public void setLeftShoulder(Entity leftShoulder) {
		this.leftShoulder = leftShoulder;
	}

	public Entity getRightShoulder() {
		return rightShoulder;
	}

	public void setRightShoulder(Entity rightShoulder) {
		this.rightShoulder = rightShoulder;
	}
	*/

	@Override
	public String getName() {
		return username;
	}
	
	@Override
	public boolean hasPermission(String permission) {
		return Limbo.getInstance().getPermissionsManager().hasPermission(this, permission);
	}

	@Override
	public void teleport(Location location) {
		PlayerTeleportEvent event = Limbo.getInstance().getEventsManager().callEvent(new PlayerTeleportEvent(this, getLocation(), location));
		if (!event.isCancelled()) {
			location = event.getTo();
			super.teleport(location);
			try {
				if (!world.equals(location.getWorld())) {
					PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(location.getWorld(), Limbo.getInstance().getDimensionRegistry().getCodec(), 0, gamemode, false, false, true);
					clientConnection.sendPacket(respawn);
				}
				PacketPlayOutPositionAndLook positionLook = new PacketPlayOutPositionAndLook(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), 1, false);
				clientConnection.sendPacket(positionLook);
			} catch (IOException e) {}
		}
	}
	
	protected void setLocation(Location location) {
		super.teleport(location);
	}
	
	public void sendMessage(String message, UUID uuid) {
		sendMessage(new TextComponent(message), uuid);
	}

	public void sendMessage(BaseComponent component, UUID uuid) {
		sendMessage(new BaseComponent[] {component}, uuid);
	}

	@Override
	public void sendMessage(BaseComponent[] component, UUID uuid) {
		try {
			PacketPlayOutChat chat = new PacketPlayOutChat(component, 0, uuid);
			clientConnection.sendPacket(chat);
		} catch (IOException e) {}
	}
	
	public void sendMessage(String message) {
		sendMessage(new TextComponent(message));
	}

	public void sendMessage(BaseComponent component) {
		sendMessage(new BaseComponent[] {component});
	}

	@Override
	public void sendMessage(BaseComponent[] component) {
		try {
			PacketPlayOutChat chat = new PacketPlayOutChat(component, 0, new UUID(0, 0));
			clientConnection.sendPacket(chat);
		} catch (IOException e) {}
	}
	
	public void disconnect() {
		disconnect(new TranslatableComponent("multiplayer.disconnect.kicked"));
	}
	
	public void disconnect(String reason) {
		disconnect(new TextComponent(reason));
	}
	
	public void disconnect(BaseComponent reason) {
		disconnect(new BaseComponent[] {reason});
	}
	
	public void disconnect(BaseComponent[] reason) {
		clientConnection.disconnect(reason);
	}
	
	public void chat(String message) {
		chat(message, false);
	}
	
	public void chat(String message, boolean verbose) {
		if (Limbo.getInstance().getServerProperties().isAllowChat()) {
			PlayerChatEvent event = (PlayerChatEvent) Limbo.getInstance().getEventsManager().callEvent(new PlayerChatEvent(this, CHAT_DEFAULT_FORMAT, message, false));
			if (!event.isCancelled()) {
				if (hasPermission("limboserver.chat")) {
					String chat = event.getFormat().replace("%name%", username).replace("%message%", event.getMessage());
					Limbo.getInstance().getConsole().sendMessage(chat);
					if (event.getFormat().equals(CHAT_DEFAULT_FORMAT)) {
						TranslatableComponent translatable = new TranslatableComponent("chat.type.text", username, event.getMessage());
						for (Player each : Limbo.getInstance().getPlayers()) {
							each.sendMessage(translatable, uuid);
						}
					} else {
						for (Player each : Limbo.getInstance().getPlayers()) {
							each.sendMessage(chat, uuid);
						}
					}
				} else if (verbose) {
					sendMessage(ChatColor.RED + "You do not have permission to chat!");
				}
			}
		}
	}
	
	public void setResourcePack(String url, String hash, boolean forced) {
		setResourcePack(url, hash, forced, (BaseComponent[]) null);
	}
	
	public void setResourcePack(String url, String hash, boolean forced, BaseComponent promptmessage) {
		setResourcePack(url, hash, forced, new BaseComponent[] {promptmessage});
	}

	public void setResourcePack(String url, String hash, boolean forced, BaseComponent[] promptmessage) {
		try {
			PacketPlayOutResourcePackSend packsend = new PacketPlayOutResourcePackSend(url, hash, forced, promptmessage != null, promptmessage);
			clientConnection.sendPacket(packsend);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setPlayerListHeaderFooter(BaseComponent[] header, BaseComponent[] footer) {
		try {
			PacketPlayOutPlayerListHeaderFooter packsend = new PacketPlayOutPlayerListHeaderFooter(header == null ? EMPTY_CHAT_COMPONENT : header, footer == null ? EMPTY_CHAT_COMPONENT : footer);
			clientConnection.sendPacket(packsend);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setPlayerListHeaderFooter(BaseComponent header, BaseComponent footer) {
		setPlayerListHeaderFooter(header == null ? EMPTY_CHAT_COMPONENT : new BaseComponent[] {header}, footer == null ? EMPTY_CHAT_COMPONENT : new BaseComponent[] {footer});
	}
	
	public void setPlayerListHeaderFooter(String header, String footer) {
		setPlayerListHeaderFooter(header == null ? EMPTY_CHAT_COMPONENT : new BaseComponent[] {new TextComponent(header)}, footer == null ? EMPTY_CHAT_COMPONENT : new BaseComponent[] {new TextComponent(footer)});
	}

	
	public void sendBungeeMessage(String subChannel, String argument) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(subChannel);
            out.writeUTF(argument);
            PacketPlayOutPluginMessaging packet = new PacketPlayOutPluginMessaging(new NamespacedKey("bungeecord", "main"), out.toByteArray());
            try {clientConnection.sendPacket(packet);
            } catch (IOException e) {e.printStackTrace();}
    }
	
	public void sendToServer(String server) {
            sendBungeeMessage("Connect", server);
    }
	
}
