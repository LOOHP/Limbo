/*
 * This file is part of Limbo.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.loohp.limbo.player;

import com.loohp.limbo.Limbo;
import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.entity.DataWatcher;
import com.loohp.limbo.entity.DataWatcher.WatchableField;
import com.loohp.limbo.entity.DataWatcher.WatchableObjectType;
import com.loohp.limbo.entity.EntityEquipment;
import com.loohp.limbo.entity.EntityType;
import com.loohp.limbo.entity.LivingEntity;
import com.loohp.limbo.events.inventory.InventoryCloseEvent;
import com.loohp.limbo.events.inventory.InventoryOpenEvent;
import com.loohp.limbo.events.player.PlayerChatEvent;
import com.loohp.limbo.events.player.PlayerTeleportEvent;
import com.loohp.limbo.inventory.Inventory;
import com.loohp.limbo.inventory.InventoryHolder;
import com.loohp.limbo.inventory.InventoryView;
import com.loohp.limbo.inventory.ItemStack;
import com.loohp.limbo.inventory.TitledInventory;
import com.loohp.limbo.location.Location;
import com.loohp.limbo.network.ClientConnection;
import com.loohp.limbo.network.protocol.packets.ClientboundClearTitlesPacket;
import com.loohp.limbo.network.protocol.packets.ClientboundOpenBookPacket;
import com.loohp.limbo.network.protocol.packets.ClientboundSetActionBarTextPacket;
import com.loohp.limbo.network.protocol.packets.ClientboundSetSubtitleTextPacket;
import com.loohp.limbo.network.protocol.packets.ClientboundSetTitleTextPacket;
import com.loohp.limbo.network.protocol.packets.ClientboundSetTitlesAnimationPacket;
import com.loohp.limbo.network.protocol.packets.ClientboundSystemChatPacket;
import com.loohp.limbo.network.protocol.packets.PacketOut;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutCloseWindow;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutGameState;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutHeldItemChange;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutNamedSoundEffect;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutOpenWindow;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutPlayerListHeaderFooter;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutPositionAndLook;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutResourcePackSend;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutRespawn;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutSetSlot;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutStopSound;
import com.loohp.limbo.sounds.SoundEffect;
import com.loohp.limbo.utils.BungeecordAdventureConversionUtils;
import com.loohp.limbo.utils.GameMode;
import com.loohp.limbo.utils.MessageSignature;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Emitter;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import net.kyori.adventure.title.TitlePart;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Player extends LivingEntity implements CommandSender, InventoryHolder {

	public static final String CHAT_DEFAULT_FORMAT = "<%name%> %message%";

	public final ClientConnection clientConnection;
	public final PlayerInteractManager playerInteractManager;

	protected final String username;
	protected GameMode gamemode;
	protected DataWatcher watcher;
	protected byte selectedSlot;
	protected final PlayerInventory playerInventory;
	protected final InventoryView inventoryView;
	private final AtomicInteger containerIdCounter;

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
		this.containerIdCounter = new AtomicInteger(1);
		this.playerInventory = new PlayerInventory(this);
		this.inventoryView = new InventoryView(this, null, null, playerInventory);
		this.playerInteractManager = playerInteractManager;
		this.playerInteractManager.setPlayer(this);
		this.watcher = new DataWatcher(this);
		this.watcher.update();
	}

	protected int nextContainerId() {
		return containerIdCounter.updateAndGet(i -> ++i > Byte.MAX_VALUE ? 1 : i);
	}

	public byte getSelectedSlot() {
		return selectedSlot;
	}

	public void setSelectedSlot(byte slot) {
		if (slot == selectedSlot) {
			return;
		}
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

	public void sendPluginMessage(Key channel, byte[] data) throws IOException {
		sendPluginMessage(channel.toString(), data);
	}

	@Deprecated
	public void sendPluginMessage(String channel, byte[] data) throws IOException {
		clientConnection.sendPluginMessage(channel, data);
	}

	public void sendMessage(String message, UUID uuid) {
		sendMessage(Identity.identity(uuid), LegacyComponentSerializer.legacySection().deserialize(message));
	}

	@Deprecated
	public void sendMessage(BaseComponent component, UUID uuid) {
		sendMessage(new BaseComponent[] {component}, uuid);
	}

	@Deprecated
	@Override
	public void sendMessage(BaseComponent[] component, UUID uuid) {
		sendMessage(Identity.identity(uuid), BungeecordAdventureConversionUtils.toComponent(component));
	}

	public void sendMessage(String message) {
		sendMessage(LegacyComponentSerializer.legacySection().deserialize(message));
	}

	@Deprecated
	public void sendMessage(BaseComponent component) {
		sendMessage(new BaseComponent[] {component});
	}

	@Deprecated
	@Override
	public void sendMessage(BaseComponent[] component) {
		sendMessage(BungeecordAdventureConversionUtils.toComponent(component));
	}

	public void disconnect() {
		disconnect(Component.translatable("multiplayer.disconnect.kicked"));
	}

	public void disconnect(String reason) {
		disconnect(LegacyComponentSerializer.legacySection().deserialize(reason));
	}

	public void disconnect(Component reason) {
		clientConnection.disconnect(reason);
	}

	@Deprecated
	public void disconnect(BaseComponent reason) {
		disconnect(new BaseComponent[] {reason});
	}

	@Deprecated
	public void disconnect(BaseComponent[] reason) {
		disconnect(BungeecordAdventureConversionUtils.toComponent(reason));
	}

	public void chat(String message) {
		chat(message, false);
	}

	public void chat(String message, boolean verbose) {
		chat(message, verbose, null, Instant.now());
	}

	public void chat(String message, boolean verbose, MessageSignature saltSignature, Instant time) {
		if (Limbo.getInstance().getServerProperties().isAllowChat()) {
			PlayerChatEvent event = Limbo.getInstance().getEventsManager().callEvent(new PlayerChatEvent(this, CHAT_DEFAULT_FORMAT, message, false));
			if (!event.isCancelled()) {
				if (hasPermission("limboserver.chat")) {
					String chat = event.getFormat().replace("%name%", username).replace("%message%", event.getMessage());
					Limbo.getInstance().getConsole().sendMessage(chat);
					if (event.getFormat().equals(CHAT_DEFAULT_FORMAT)) {
						for (Player each : Limbo.getInstance().getPlayers()) {
							each.sendMessage(Identity.identity(uuid), Component.translatable("chat.type.text").args(Component.text(this.getName()), Component.text(event.getMessage())), MessageType.CHAT, saltSignature, time);
						}
					} else {
						for (Player each : Limbo.getInstance().getPlayers()) {
							each.sendMessage(Identity.identity(uuid), Component.text(chat), MessageType.SYSTEM, saltSignature, time);
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

	@Deprecated
	public void setResourcePack(String url, String hash, boolean forced, BaseComponent promptmessage) {
		setResourcePack(url, hash, forced, promptmessage == null ? null : new BaseComponent[] {promptmessage});
	}

	@Deprecated
	public void setResourcePack(String url, String hash, boolean forced, BaseComponent[] promptmessage) {
		setResourcePack(url, hash, forced, promptmessage == null ? null : BungeecordAdventureConversionUtils.toComponent(promptmessage));
	}

	public void setResourcePack(String url, String hash, boolean forced, Component promptmessage) {
		try {
			PacketPlayOutResourcePackSend packsend = new PacketPlayOutResourcePackSend(url, hash, forced, promptmessage != null, promptmessage);
			clientConnection.sendPacket(packsend);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Deprecated
	public void setPlayerListHeaderFooter(BaseComponent[] header, BaseComponent[] footer) {
		sendPlayerListHeaderAndFooter(header == null ? Component.empty() : BungeecordAdventureConversionUtils.toComponent(header), footer == null ? Component.empty() : BungeecordAdventureConversionUtils.toComponent(footer));
	}

	@Deprecated
	public void setPlayerListHeaderFooter(BaseComponent header, BaseComponent footer) {
		sendPlayerListHeaderAndFooter(header == null ? Component.empty() : BungeecordAdventureConversionUtils.toComponent(header), footer == null ? Component.empty() : BungeecordAdventureConversionUtils.toComponent(footer));
	}

	public void setPlayerListHeaderFooter(String header, String footer) {
		sendPlayerListHeaderAndFooter(header == null ? Component.empty() : LegacyComponentSerializer.legacySection().deserialize(header), footer == null ? Component.empty() : LegacyComponentSerializer.legacySection().deserialize(footer));
	}

	@Deprecated
	public void setTitle(BaseComponent[] title) {
		sendTitlePart(TitlePart.TITLE, BungeecordAdventureConversionUtils.toComponent(title));
	}

	@Deprecated
	public void setTitle(BaseComponent title) {
		sendTitlePart(TitlePart.TITLE, BungeecordAdventureConversionUtils.toComponent(title));
	}

	public void setTitle(String title) {
		sendTitlePart(TitlePart.TITLE, LegacyComponentSerializer.legacySection().deserialize(title));
	}

	@Deprecated
	public void setSubTitle(BaseComponent[] subTitle) {
		sendTitlePart(TitlePart.SUBTITLE, BungeecordAdventureConversionUtils.toComponent(subTitle));
	}

	@Deprecated
	public void setSubTitle(BaseComponent subTitle) {
		sendTitlePart(TitlePart.SUBTITLE, BungeecordAdventureConversionUtils.toComponent(subTitle));
	}

	public void setSubTitle(String subTitle) {
		sendTitlePart(TitlePart.SUBTITLE, LegacyComponentSerializer.legacySection().deserialize(subTitle));
	}

	public void setTitleTimer(int fadeIn, int stay, int fadeOut) {
		sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofMillis(fadeIn * 50), Duration.ofMillis(stay * 50), Duration.ofMillis(fadeOut * 50)));
	}

	@Deprecated
	public void setTitleSubTitle(BaseComponent[] title, BaseComponent[] subTitle, int fadeIn, int stay, int fadeOut) {
		setTitleTimer(fadeIn, stay, fadeOut);
		setTitle(title);
		setSubTitle(subTitle);
	}

	@Deprecated
	public void setTitleSubTitle(BaseComponent title, BaseComponent subTitle, int fadeIn, int stay, int fadeOut) {
		setTitleSubTitle(new BaseComponent[] {title}, new BaseComponent[] {subTitle}, fadeIn, stay, fadeOut);
	}

	public void setTitleSubTitle(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
		sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofMillis(fadeIn * 50), Duration.ofMillis(stay * 50), Duration.ofMillis(fadeOut * 50)));
		sendTitlePart(TitlePart.SUBTITLE, LegacyComponentSerializer.legacySection().deserialize(subTitle));
		sendTitlePart(TitlePart.TITLE, LegacyComponentSerializer.legacySection().deserialize(title));
	}

	@Override
	public void sendMessage(Identity source, Component message, MessageType type) {
		sendMessage(source, message, type, null, Instant.now());
	}

	public void sendMessage(Identity source, Component message, MessageType type, MessageSignature signature, Instant time) {
		try {
			PacketOut chat;
			switch (type) {
				case CHAT:
					/*
					if (signature == null) {
						chat = new ClientboundPlayerChatPacket(Component.empty(), Optional.of(message), 0, uuid, time, SignatureData.NONE);
					} else {
						chat = new ClientboundPlayerChatPacket(message, Optional.of(message), 0, uuid, time, signature);
					}
					break;
					*/
				case SYSTEM:
				default:
					chat = new ClientboundSystemChatPacket(message, false);
					break;
			}
			clientConnection.sendPacket(chat);
		} catch (IOException ignored) {}
	}

	@Override
	public void openBook(Book book) {
		ItemStack item = new ItemStack(Key.key("minecraft:written_book"));
		CompoundTag nbt = new CompoundTag();
		nbt.putString("title", PlainTextComponentSerializer.plainText().serialize(book.title()));
		nbt.putString("author", PlainTextComponentSerializer.plainText().serialize(book.author()));
		ListTag<StringTag> pages = new ListTag<>(StringTag.class);
		for (Component page : book.pages()) {
			pages.add(new StringTag(PlainTextComponentSerializer.plainText().serialize(page)));
		}
		nbt.put("pages", pages);
		item.nbt(nbt);
		PacketPlayOutSetSlot setSlot = new PacketPlayOutSetSlot(0, 0,0, item);
		try {
			clientConnection.sendPacket(setSlot);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ClientboundOpenBookPacket openBook = new ClientboundOpenBookPacket(0);
		try {
			clientConnection.sendPacket(openBook);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PacketPlayOutSetSlot resetSlot = new PacketPlayOutSetSlot(0, 0,0, getInventory().getItemInMainHand());
		try {
			clientConnection.sendPacket(resetSlot);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stopSound(SoundStop stop) {
		PacketPlayOutStopSound stopSound = new PacketPlayOutStopSound(stop.sound(), stop.source());
		try {
			clientConnection.sendPacket(stopSound);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void playSound(Sound sound, Emitter emitter) {
		throw new UnsupportedOperationException("This function has not been implemented yet.");
	}

	@Override
	public void playSound(Sound sound, double x, double y, double z) {
		PacketPlayOutNamedSoundEffect namedSoundEffect = new PacketPlayOutNamedSoundEffect(
				SoundEffect.createVariableRangeEvent(sound.name()),
				sound.source(), x, y, z, sound.volume(), sound.pitch(), sound.seed().orElse(ThreadLocalRandom.current().nextLong())
		);
		try {
			clientConnection.sendPacket(namedSoundEffect);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void playSound(Sound sound) {
		playSound(sound, x, y, z);
	}

	@Override
	public void sendActionBar(Component message) {
		try {
			ClientboundSetActionBarTextPacket setActionBar = new ClientboundSetActionBarTextPacket(message);
			clientConnection.sendPacket(setActionBar);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendPlayerListHeaderAndFooter(Component header, Component footer) {
		try {
			PacketPlayOutPlayerListHeaderFooter listHeaderFooter = new PacketPlayOutPlayerListHeaderFooter(header, footer);
			clientConnection.sendPacket(listHeaderFooter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public <T> void sendTitlePart(TitlePart<T> part, T value) {
		if (part.equals(TitlePart.TITLE)) {
			try {
				ClientboundSetTitleTextPacket setTitle = new ClientboundSetTitleTextPacket((Component) value);
				clientConnection.sendPacket(setTitle);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (part.equals(TitlePart.SUBTITLE)) {
			try {
				ClientboundSetSubtitleTextPacket setSubTitle = new ClientboundSetSubtitleTextPacket((Component) value);
				clientConnection.sendPacket(setSubTitle);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (part.equals(TitlePart.TIMES)) {
			try {
				Title.Times times = (Times) value;
				ClientboundSetTitlesAnimationPacket setSubTitle = new ClientboundSetTitlesAnimationPacket((int) (times.fadeIn().toMillis() / 50), (int) (times.stay().toMillis() / 50), (int) (times.fadeOut().toMillis() / 50));
				clientConnection.sendPacket(setSubTitle);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void clearTitle() {
		try {
			ClientboundClearTitlesPacket clearTitle = new ClientboundClearTitlesPacket(false);
			clientConnection.sendPacket(clearTitle);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void resetTitle() {
		try {
			ClientboundClearTitlesPacket clearTitle = new ClientboundClearTitlesPacket(true);
			clientConnection.sendPacket(clearTitle);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Use {@link com.loohp.limbo.bossbar.KeyedBossBar#showPlayer(Player)} instead
	 */
	@Override
	@Deprecated
	public void showBossBar(BossBar bar) {
		Limbo.getInstance().getBossBars().values().stream().filter(each -> each.getProperties() == bar).findFirst().ifPresent(each -> each.showPlayer(this));
	}

	/**
	 * Use {@link com.loohp.limbo.bossbar.KeyedBossBar#hidePlayer(Player)} instead
	 */
	@Override
	@Deprecated
	public void hideBossBar(BossBar bar) {
		Limbo.getInstance().getBossBars().values().stream().filter(each -> each.getProperties() == bar).findFirst().ifPresent(each -> each.hidePlayer(this));
	}

	@Override
	public PlayerInventory getInventory() {
		return playerInventory;
	}

	public InventoryView getInventoryView() {
		return inventoryView;
	}

	public void updateInventory() {
		playerInventory.updateInventory(this);
	}

	public void openInventory(Inventory inventory) {
		Component title = inventory instanceof TitledInventory ? ((TitledInventory) inventory).getTitle() : Component.translatable("container.chest");
		inventoryView.getUnsafe().a(inventory, title);
		int id = nextContainerId();
		inventory.getUnsafe().c().put(this, id);
		InventoryOpenEvent event = Limbo.getInstance().getEventsManager().callEvent(new InventoryOpenEvent(inventoryView));
		if (event.isCancelled()) {
			inventoryView.getUnsafe().a(null, null);
			inventory.getUnsafe().c().remove(this);
		} else {
			PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(id, inventory.getType().getRawType(inventory.getSize()), title);
			try {
				clientConnection.sendPacket(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			inventoryView.updateView();
		}
	}

	public void closeInventory() {
		Inventory inventory = inventoryView.getTopInventory();
		if (inventory != null) {
			Integer id = inventory.getUnsafe().c().get(this);
			if (id != null) {
				Limbo.getInstance().getEventsManager().callEvent(new InventoryCloseEvent(inventoryView));
				inventoryView.getUnsafe().a(null, null);
				inventory.getUnsafe().c().remove(this);
				PacketPlayOutCloseWindow packet = new PacketPlayOutCloseWindow(id);
				try {
					clientConnection.sendPacket(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public EntityEquipment getEquipment() {
		return playerInventory;
	}

	@Override
	public InventoryHolder getHolder() {
		return this;
	}
}
