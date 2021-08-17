package com.loohp.limbo.server;

import com.loohp.limbo.Limbo;
import com.loohp.limbo.events.player.*;
import com.loohp.limbo.events.status.StatusPingEvent;
import com.loohp.limbo.file.ServerProperties;
import com.loohp.limbo.location.Location;
import com.loohp.limbo.player.Player;
import com.loohp.limbo.player.PlayerInteractManager;
import com.loohp.limbo.server.packets.*;
import com.loohp.limbo.server.packets.PacketPlayOutPlayerAbilities.PlayerAbilityFlags;
import com.loohp.limbo.server.packets.PacketPlayOutPlayerInfo.PlayerInfoAction;
import com.loohp.limbo.server.packets.PacketPlayOutPlayerInfo.PlayerInfoData;
import com.loohp.limbo.server.packets.PacketPlayOutPlayerInfo.PlayerInfoData.PlayerInfoDataAddPlayer.PlayerSkinProperty;
import com.loohp.limbo.server.packets.PacketPlayOutTabComplete.TabCompleteMatches;
import com.loohp.limbo.utils.*;
import com.loohp.limbo.utils.MojangAPIUtils.SkinResponse;
import com.loohp.limbo.world.BlockPosition;
import com.loohp.limbo.world.World;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class ClientConnection extends Thread {
	
	public static enum ClientState {
		LEGACY,
		HANDSHAKE,
		STATUS,
		LOGIN,
		PLAY,
		DISCONNECTED;
	}

	private final Random random = new Random();

    private final Socket client_socket;
    private boolean running;
    private ClientState state;
    
    private Player player;	
	private AtomicLong lastKeepAlivePayLoad;
	
	private DataOutputStream output;
	private DataInputStream input;
	
	private InetAddress inetAddress;
	
	private boolean ready;
	
    public ClientConnection(Socket client_socket) {
        this.client_socket = client_socket;
        this.inetAddress = client_socket.getInetAddress();
        this.lastKeepAlivePayLoad = new AtomicLong();
        this.running = false;
        this.ready = false;
    }
	
	public InetAddress getInetAddress() {
		return inetAddress;
	}

	public long getLastKeepAlivePayLoad() {
		return lastKeepAlivePayLoad.get();
	}
	
	public void setLastKeepAlivePayLoad(long payLoad) {
		this.lastKeepAlivePayLoad.set(payLoad);
	}

	public Player getPlayer() {
		return player;
	}
    
    public ClientState getClientState() {
    	return state;
    }
    
    public Socket getSocket() {
    	return client_socket;
    }
    
	public boolean isRunning() {
		return running;
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public synchronized void sendPacket(PacketOut packet) throws IOException {
		byte[] packetByte = packet.serializePacket();
		DataTypeIO.writeVarInt(output, packetByte.length);
		output.write(packetByte);
		output.flush();
	}
	
	public void disconnect(BaseComponent[] reason) {
		try {
			PacketPlayOutDisconnect packet = new PacketPlayOutDisconnect(ComponentSerializer.toString(reason));
			sendPacket(packet);
		} catch (IOException e) {}
		try {
			client_socket.close();
		} catch (IOException e) {}
	}
	
	private void disconnectDuringLogin(BaseComponent[] reason) {
		try {
			PacketLoginOutDisconnect packet = new PacketLoginOutDisconnect(ComponentSerializer.toString(reason));
			sendPacket(packet);
		} catch (IOException e) {}
		try {
			client_socket.close();
		} catch (IOException e) {}
	}

	@SuppressWarnings("deprecation")
	@Override
    public void run() {
        running = true;
        state = ClientState.HANDSHAKE;
    	try {
    		client_socket.setKeepAlive(true);
    		input = new DataInputStream(client_socket.getInputStream());
    		output = new DataOutputStream(client_socket.getOutputStream());
		    int handShakeSize = DataTypeIO.readVarInt(input);

		    //legacy ping
		    if (handShakeSize == 0xFE) {
		    	state = ClientState.LEGACY;			    
		    	output.writeByte(255);
		    	String str = inetAddress.getHostName() + ":" + client_socket.getPort();
		    	Limbo.getInstance().getConsole().sendMessage("[/" + str + "] <-> Legacy Status has pinged");
		    	ServerProperties p = Limbo.getInstance().getServerProperties();
		    	StatusPingEvent event = Limbo.getInstance().getEventsManager().callEvent(new StatusPingEvent(this, p.getVersionString(), p.getProtocol(), ComponentSerializer.parse(p.getMotdJson()), p.getMaxPlayers(), Limbo.getInstance().getPlayers().size(), p.getFavicon().orElse(null)));
				String response = Limbo.getInstance().buildLegacyPingResponse(event.getVersion(), event.getMotd(), event.getMaxPlayers(), event.getPlayersOnline());
				byte[] bytes = response.getBytes(StandardCharsets.UTF_16BE);
				output.writeShort(response.length());
				output.write(bytes);
				
				client_socket.close();
				state = ClientState.DISCONNECTED;
		    }
		    
		    @SuppressWarnings("unused")
			int handShakeId = DataTypeIO.readVarInt(input);
		    
		    PacketHandshakingIn handshake = new PacketHandshakingIn(input);
		    
		    boolean isBungeecord = Limbo.getInstance().getServerProperties().isBungeecord();
		    boolean isVelocityModern = Limbo.getInstance().getServerProperties().isVelocityModern();
		    String bungeeForwarding = handshake.getServerAddress();
		    UUID bungeeUUID = null;
		    SkinResponse forwardedSkin = null;
		    
		    try {
			    switch (handshake.getHandshakeType()) {
			    case STATUS:
					state = ClientState.STATUS;
					while (client_socket.isConnected()) {
						DataTypeIO.readVarInt(input);
						int packetId = DataTypeIO.readVarInt(input);
						Class<? extends Packet> packetType = Packet.getStatusIn().get(packetId);
						if (packetType == null) {
							//do nothing
						} else if (packetType.equals(PacketStatusInRequest.class)) {
							String str = inetAddress.getHostName() + ":" + client_socket.getPort();
							if (Limbo.getInstance().getServerProperties().handshakeVerboseEnabled()) {
								Limbo.getInstance().getConsole().sendMessage("[/" + str + "] <-> Handshake Status has pinged");
							}
							ServerProperties p = Limbo.getInstance().getServerProperties();		
							StatusPingEvent event = Limbo.getInstance().getEventsManager().callEvent(new StatusPingEvent(this, p.getVersionString(), p.getProtocol(), ComponentSerializer.parse(p.getMotdJson()), p.getMaxPlayers(), Limbo.getInstance().getPlayers().size(), p.getFavicon().orElse(null)));												
							PacketStatusOutResponse packet = new PacketStatusOutResponse(Limbo.getInstance().buildServerListResponseJson(event.getVersion(), event.getProtocol(), event.getMotd(), event.getMaxPlayers(), event.getPlayersOnline(), event.getFavicon()));
							sendPacket(packet);
						} else if (packetType.equals(PacketStatusInPing.class)) {
							PacketStatusInPing ping = new PacketStatusInPing(input);
							PacketStatusOutPong packet = new PacketStatusOutPong(ping.getPayload());
							sendPacket(packet);
							break;
						}
					}
					break;
				case LOGIN:
					state = ClientState.LOGIN;
					
					if (isBungeecord) {
						try {
					    	String[] data = bungeeForwarding.split("\\x00");
					    	//String host = data[0];
					    	String ip = data[1];
					    	
					    	bungeeUUID = UUID.fromString(data[2].replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5"));
					    	inetAddress = InetAddress.getByName(ip);
					    	
					    	if (data.length > 3) {
						    	String skinJson = data[3];
						    	
						    	String skin = skinJson.split("\"value\":\"")[1].split("\"")[0];
					            String signature = skinJson.split("\"signature\":\"")[1].split("\"")[0];
						    	forwardedSkin = new SkinResponse(skin, signature);
					    	}
						} catch (Exception e) {
							Limbo.getInstance().getConsole().sendMessage("If you wish to use bungeecord's IP forwarding, please enable that in your bungeecord config.yml as well!");
							disconnectDuringLogin(new BaseComponent[] {new TextComponent(ChatColor.RED + "Please connect from the proxy!")});
						}
				    }

					int messageId = this.random.nextInt();
					while (client_socket.isConnected()) {
						int size = DataTypeIO.readVarInt(input);
						int packetId = DataTypeIO.readVarInt(input);
						Class<? extends Packet> packetType = Packet.getLoginIn().get(packetId);
						
						if (packetType == null) {
							input.skipBytes(size - DataTypeIO.getVarIntLength(packetId));
						} else if (packetType.equals(PacketLoginInLoginStart.class)) {
							PacketLoginInLoginStart start = new PacketLoginInLoginStart(input);
							String username = start.getUsername();

							if (Limbo.getInstance().getServerProperties().isVelocityModern()) {
								PacketLoginOutPluginMessaging loginPluginRequest = new PacketLoginOutPluginMessaging(messageId, ForwardingUtils.VELOCITY_FORWARDING_CHANNEL);
								sendPacket(loginPluginRequest);
								continue;
							}

							UUID uuid = isBungeecord ? bungeeUUID : UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
							
							PacketLoginOutLoginSuccess success = new PacketLoginOutLoginSuccess(uuid, username);
							sendPacket(success);
							
							state = ClientState.PLAY;
	
							player = new Player(this, username, uuid, Limbo.getInstance().getNextEntityId(), Limbo.getInstance().getServerProperties().getWorldSpawn(), new PlayerInteractManager());
							player.setSkinLayers((byte) (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40));
							Limbo.getInstance().addPlayer(player);
							
							break;
						} else if (packetType.equals(PacketLoginInPluginMessaging.class)) {
							PacketLoginInPluginMessaging response = new PacketLoginInPluginMessaging(input, size, packetId);
							if (response.getMessageId() != messageId) {
								disconnectDuringLogin(TextComponent.fromLegacyText("Internal error, messageId did not match"));
								return;
							}
							if (response.getData() == null) {
								disconnectDuringLogin(TextComponent.fromLegacyText("Unknown login plugin response packet!"));
								return;
							}
							if (!ForwardingUtils.validateVelocityModernResponse(response.getData())) {
								disconnectDuringLogin(TextComponent.fromLegacyText("Invalid playerinfo forwarding!"));
								return;
							}
							ForwardingUtils.VelocityModernForwardingData data = ForwardingUtils.getVelocityDataFrom(response.getData());
							inetAddress = InetAddress.getByName(data.getIpAddress());
							forwardedSkin = data.getSkinResponse();

							PacketLoginOutLoginSuccess success = new PacketLoginOutLoginSuccess(data.getUuid(), data.getUsername());
							sendPacket(success);

							state = ClientState.PLAY;

							player = new Player(this, data.getUsername(), data.getUuid(), Limbo.getInstance().getNextEntityId(), Limbo.getInstance().getServerProperties().getWorldSpawn(), new PlayerInteractManager());
							player.setSkinLayers((byte) (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40));
							Limbo.getInstance().addPlayer(player);

							break;
						} else {
							input.skipBytes(size - DataTypeIO.getVarIntLength(packetId));
						}
		    		}
					
					PlayerLoginEvent event = Limbo.getInstance().getEventsManager().callEvent(new PlayerLoginEvent(this, false));
					if (event.isCancelled()) {
						disconnectDuringLogin(event.getCancelReason());
					}
					
					break;
			    }
		    } catch (Exception e) {
		    	e.printStackTrace();
		    	client_socket.close();
				state = ClientState.DISCONNECTED;
		    }
		    
		    if (state == ClientState.PLAY) {
		    	
		    	TimeUnit.MILLISECONDS.sleep(500);
		    	
				ServerProperties properties = Limbo.getInstance().getServerProperties();
				Location worldSpawn = properties.getWorldSpawn();
				
				PlayerJoinEvent joinEvent = Limbo.getInstance().getEventsManager().callEvent(new PlayerJoinEvent(player, worldSpawn));
				worldSpawn = joinEvent.getSpawnLocation();
				World world = worldSpawn.getWorld();
				
    			PacketPlayOutLogin join = new PacketPlayOutLogin(player.getEntityId(), false, properties.getDefaultGamemode(), Limbo.getInstance().getWorlds().stream().map(each -> new NamespacedKey(each.getName()).toString()).collect(Collectors.toList()).toArray(new String[Limbo.getInstance().getWorlds().size()]), Limbo.getInstance().getDimensionRegistry().getCodec(), world, 0, (byte) properties.getMaxPlayers(), 8, properties.isReducedDebugInfo(), true, false, true);
    			sendPacket(join);
    			Limbo.getInstance().getUnsafe().setPlayerGameModeSilently(player, properties.getDefaultGamemode());
    			
				SkinResponse skinresponce = (isVelocityModern || isBungeecord) && forwardedSkin != null ? forwardedSkin : MojangAPIUtils.getSkinFromMojangServer(player.getName());
				PlayerSkinProperty skin = skinresponce != null ? new PlayerSkinProperty(skinresponce.getSkin(), skinresponce.getSignature()) : null;
				PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(PlayerInfoAction.ADD_PLAYER, player.getUniqueId(), new PlayerInfoData.PlayerInfoDataAddPlayer(player.getName(), Optional.ofNullable(skin), properties.getDefaultGamemode(), 0, false, Optional.empty()));
				sendPacket(info);			
				
				Set<PlayerAbilityFlags> flags = new HashSet<>();
				if (properties.isAllowFlight()) {
					flags.add(PlayerAbilityFlags.FLY);
				}
				if (player.getGamemode().equals(GameMode.CREATIVE)) {
					flags.add(PlayerAbilityFlags.CREATIVE);
				}
				PacketPlayOutPlayerAbilities abilities = new PacketPlayOutPlayerAbilities(0.05F, 0.1F, flags.toArray(new PlayerAbilityFlags[flags.size()]));
				sendPacket(abilities);
				
				String str = inetAddress.getHostName() + ":" + client_socket.getPort() + "|" + player.getName() + "(" + player.getUniqueId() + ")";
				Limbo.getInstance().getConsole().sendMessage("[/" + str + "] <-> Player had connected to the Limbo server!");
				
				PacketPlayOutDeclareCommands declare = DeclareCommands.getDeclareCommandsPacket(player);
				if (declare != null) {
					sendPacket(declare);
				}
				
				player.playerInteractManager.update();
				
				PacketPlayOutSpawnPosition spawnPos = new PacketPlayOutSpawnPosition(BlockPosition.from(worldSpawn), worldSpawn.getPitch());
				sendPacket(spawnPos);
				
				PacketPlayOutPositionAndLook positionLook = new PacketPlayOutPositionAndLook(worldSpawn.getX(), worldSpawn.getY(), worldSpawn.getZ(), worldSpawn.getYaw(), worldSpawn.getPitch(), 1, false);
				Limbo.getInstance().getUnsafe().setPlayerLocationSilently(player, new Location(world, worldSpawn.getX(), worldSpawn.getY(), worldSpawn.getZ(), worldSpawn.getYaw(), worldSpawn.getPitch()));
				sendPacket(positionLook);
				
				player.getDataWatcher().update();
				PacketPlayOutEntityMetadata show = new PacketPlayOutEntityMetadata(player, false, Player.class.getDeclaredField("skinLayers"));
				sendPacket(show);
				
				if (properties.isAllowFlight()) {
					PacketPlayOutGameState state = new PacketPlayOutGameState(3, player.getGamemode().getId());
					sendPacket(state);
				}
				
				ready = true;

				while (client_socket.isConnected()) {
					try {
						int size = DataTypeIO.readVarInt(input);
						int packetId = DataTypeIO.readVarInt(input);
						Class<? extends Packet> packetType = Packet.getPlayIn().get(packetId);
						//Limbo.getInstance().getConsole().sendMessage(packetId + " -> " + packetType);
						CheckedConsumer<PlayerMoveEvent, IOException> processMoveEvent = event -> {
							Location originalTo = event.getTo().clone();
							if (event.isCancelled()) {
								Location returnTo = event.getFrom();
								PacketPlayOutPositionAndLook cancel = new PacketPlayOutPositionAndLook(returnTo.getX(), returnTo.getY(), returnTo.getZ(), returnTo.getYaw(), returnTo.getPitch(), 1, false);
								sendPacket(cancel);
							} else {
								Location to = event.getTo();
								Limbo.getInstance().getUnsafe().setPlayerLocationSilently(player, to);
								// If an event handler used setTo, let's make sure we tell the player about it.
								if (!originalTo.equals(to)) {
									PacketPlayOutPositionAndLook pos = new PacketPlayOutPositionAndLook(to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch(), 1, false);
									sendPacket(pos);
								}
								PacketPlayOutUpdateViewPosition response = new PacketPlayOutUpdateViewPosition((int) player.getLocation().getX() >> 4, (int) player.getLocation().getZ() >> 4);
								sendPacket(response);
							}
						};
						if (packetType == null) {
							input.skipBytes(size - DataTypeIO.getVarIntLength(packetId));
						} else if (packetType.equals(PacketPlayInPositionAndLook.class)) {
							PacketPlayInPositionAndLook pos = new PacketPlayInPositionAndLook(input);
							Location from = player.getLocation();
							Location to = new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ(), pos.getYaw(), pos.getPitch());

							PlayerMoveEvent event = Limbo.getInstance().getEventsManager().callEvent(new PlayerMoveEvent(player, from, to));
							processMoveEvent.consume(event);
						} else if (packetType.equals(PacketPlayInPosition.class)) {
							PacketPlayInPosition pos = new PacketPlayInPosition(input);
							Location from = player.getLocation();
							Location to = new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());

							PlayerMoveEvent event = Limbo.getInstance().getEventsManager().callEvent(new PlayerMoveEvent(player, from, to));
							processMoveEvent.consume(event);
						} else if (packetType.equals(PacketPlayInRotation.class)) {
							PacketPlayInRotation pos = new PacketPlayInRotation(input);
							Location from = player.getLocation();
							Location to = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), pos.getYaw(), pos.getPitch());

							PlayerMoveEvent event = Limbo.getInstance().getEventsManager().callEvent(new PlayerMoveEvent(player, from, to));
							processMoveEvent.consume(event);
						} else if (packetType.equals(PacketPlayInKeepAlive.class)) {
							PacketPlayInKeepAlive alive = new PacketPlayInKeepAlive(input);
							if (alive.getPayload() != getLastKeepAlivePayLoad()) {
								Limbo.getInstance().getConsole().sendMessage("Incorrect Payload recieved in KeepAlive packet for player " + player.getName());
								break;
							}
						} else if (packetType.equals(PacketPlayInTabComplete.class)) {
							PacketPlayInTabComplete request = new PacketPlayInTabComplete(input);
							String[] command = CustomStringUtils.splitStringToArgs(request.getText().substring(1));

							List<TabCompleteMatches> matches = new ArrayList<TabCompleteMatches>();
							
							matches.addAll(Limbo.getInstance().getPluginManager().getTabOptions(player, command).stream().map(each -> new TabCompleteMatches(each)).collect(Collectors.toList()));
							
							int start = CustomStringUtils.getIndexOfArg(request.getText(), command.length - 1) + 1;
							int length = command[command.length - 1].length();
							
							PacketPlayOutTabComplete response = new PacketPlayOutTabComplete(request.getId(), start, length, matches.toArray(new TabCompleteMatches[matches.size()]));
							sendPacket(response);
						} else if (packetType.equals(PacketPlayInChat.class)) {
							PacketPlayInChat chat = new PacketPlayInChat(input);
							if (chat.getMessage().startsWith("/")) {
								Limbo.getInstance().dispatchCommand(player, chat.getMessage());
							} else {
								player.chat(chat.getMessage());
							}
						} else if (packetType.equals(PacketPlayInHeldItemChange.class)) {
							PacketPlayInHeldItemChange change = new PacketPlayInHeldItemChange(input);
							PlayerSelectedSlotChangeEvent event = Limbo.getInstance().getEventsManager().callEvent(new PlayerSelectedSlotChangeEvent(player, (byte) change.getSlot()));
							if (event.isCancelled()) {
								PacketPlayOutHeldItemChange cancelPacket = new PacketPlayOutHeldItemChange(player.getSelectedSlot());
								sendPacket(cancelPacket);
							} else if (change.getSlot() != event.getSlot()) {
								PacketPlayOutHeldItemChange changePacket = new PacketPlayOutHeldItemChange(event.getSlot());
								sendPacket(changePacket);
								Limbo.getInstance().getUnsafe().setSelectedSlotSilently(player, event.getSlot());
							} else {
								Limbo.getInstance().getUnsafe().setSelectedSlotSilently(player, event.getSlot());
							}
						} else {
							input.skipBytes(size - DataTypeIO.getVarIntLength(packetId));
						}

					} catch (Exception e) {
						break;
					}
	    		}

				Limbo.getInstance().getEventsManager().callEvent(new PlayerQuitEvent(player));

		    	str = inetAddress.getHostName() + ":" + client_socket.getPort() + "|" + player.getName();
		    	Limbo.getInstance().getConsole().sendMessage("[/" + str + "] <-> Player had disconnected!");

	    	}

    	} catch (Exception e) {}

    	try {
			client_socket.close();
		} catch (IOException e) {}
    	state = ClientState.DISCONNECTED;

    	if (player != null) {
    		Limbo.getInstance().removePlayer(player);
    	}
    	Limbo.getInstance().getServerConnection().getClients().remove(this);
		running = false;
	}

	@FunctionalInterface
	public interface CheckedConsumer<T, TException extends Throwable> {
		void consume(T t) throws TException;
	}
}
