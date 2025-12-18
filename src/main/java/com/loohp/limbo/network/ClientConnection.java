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

package com.loohp.limbo.network;

import com.loohp.limbo.Limbo;
import com.loohp.limbo.entity.EntityEquipment;
import com.loohp.limbo.events.connection.ConnectionEstablishedEvent;
import com.loohp.limbo.events.inventory.AnvilRenameInputEvent;
import com.loohp.limbo.events.inventory.InventoryCloseEvent;
import com.loohp.limbo.events.inventory.InventoryCreativeEvent;
import com.loohp.limbo.events.player.PlayerInteractEvent;
import com.loohp.limbo.events.player.PlayerJoinEvent;
import com.loohp.limbo.events.player.PlayerLoginEvent;
import com.loohp.limbo.events.player.PlayerMoveEvent;
import com.loohp.limbo.events.player.PlayerQuitEvent;
import com.loohp.limbo.events.player.PlayerResourcePackStatusEvent;
import com.loohp.limbo.events.player.PlayerSelectedSlotChangeEvent;
import com.loohp.limbo.events.player.PlayerSpawnEvent;
import com.loohp.limbo.events.player.PlayerSwapHandItemsEvent;
import com.loohp.limbo.events.player.PluginMessageEvent;
import com.loohp.limbo.events.status.StatusPingEvent;
import com.loohp.limbo.file.ServerProperties;
import com.loohp.limbo.inventory.AnvilInventory;
import com.loohp.limbo.inventory.Inventory;
import com.loohp.limbo.inventory.ItemStack;
import com.loohp.limbo.location.GlobalPos;
import com.loohp.limbo.location.Location;
import com.loohp.limbo.network.protocol.packets.ClientboundFinishConfigurationPacket;
import com.loohp.limbo.network.protocol.packets.ClientboundRegistryDataPacket;
import com.loohp.limbo.network.protocol.packets.PacketHandshakingIn;
import com.loohp.limbo.network.protocol.packets.PacketIn;
import com.loohp.limbo.network.protocol.packets.PacketLoginInLoginStart;
import com.loohp.limbo.network.protocol.packets.PacketLoginInPluginMessaging;
import com.loohp.limbo.network.protocol.packets.PacketLoginOutDisconnect;
import com.loohp.limbo.network.protocol.packets.PacketLoginOutLoginSuccess;
import com.loohp.limbo.network.protocol.packets.PacketLoginOutPluginMessaging;
import com.loohp.limbo.network.protocol.packets.PacketOut;
import com.loohp.limbo.network.protocol.packets.PacketPlayInBlockDig;
import com.loohp.limbo.network.protocol.packets.PacketPlayInBlockPlace;
import com.loohp.limbo.network.protocol.packets.PacketPlayInChat;
import com.loohp.limbo.network.protocol.packets.PacketPlayInCloseWindow;
import com.loohp.limbo.network.protocol.packets.PacketPlayInHeldItemChange;
import com.loohp.limbo.network.protocol.packets.PacketPlayInItemName;
import com.loohp.limbo.network.protocol.packets.PacketPlayInKeepAlive;
import com.loohp.limbo.network.protocol.packets.PacketPlayInPickItem;
import com.loohp.limbo.network.protocol.packets.PacketPlayInPluginMessaging;
import com.loohp.limbo.network.protocol.packets.PacketPlayInPosition;
import com.loohp.limbo.network.protocol.packets.PacketPlayInPositionAndLook;
import com.loohp.limbo.network.protocol.packets.PacketPlayInRotation;
import com.loohp.limbo.network.protocol.packets.PacketPlayInSetCreativeSlot;
import com.loohp.limbo.network.protocol.packets.PacketPlayInTabComplete;
import com.loohp.limbo.network.protocol.packets.PacketPlayInUseItem;
import com.loohp.limbo.network.protocol.packets.PacketPlayInWindowClick;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutDeclareCommands;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutDisconnect;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutEntityMetadata;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutGameStateChange;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutHeldItemChange;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutKeepAlive;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutLogin;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutPlayerAbilities;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutPlayerAbilities.PlayerAbilityFlags;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutPlayerInfo;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutPlayerInfo.PlayerInfoAction;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutPlayerInfo.PlayerInfoData;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutPlayerInfo.PlayerInfoData.PlayerInfoDataAddPlayer.PlayerSkinProperty;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutPluginMessaging;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutPositionAndLook;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutSpawnPosition;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutTabComplete;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutTabComplete.TabCompleteMatches;
import com.loohp.limbo.network.protocol.packets.PacketPlayOutUpdateViewPosition;
import com.loohp.limbo.network.protocol.packets.PacketStatusInPing;
import com.loohp.limbo.network.protocol.packets.PacketStatusInRequest;
import com.loohp.limbo.network.protocol.packets.PacketStatusOutPong;
import com.loohp.limbo.network.protocol.packets.PacketStatusOutResponse;
import com.loohp.limbo.network.protocol.packets.ServerboundChatCommandPacket;
import com.loohp.limbo.network.protocol.packets.ServerboundFinishConfigurationPacket;
import com.loohp.limbo.network.protocol.packets.ServerboundLoginAcknowledgedPacket;
import com.loohp.limbo.network.protocol.packets.ServerboundResourcePackPacket;
import com.loohp.limbo.network.protocol.packets.ServerboundResourcePackPacket.Action;
import com.loohp.limbo.player.Player;
import com.loohp.limbo.player.PlayerInteractManager;
import com.loohp.limbo.player.PlayerInventory;
import com.loohp.limbo.registry.PacketRegistry;
import com.loohp.limbo.registry.RegistryCustom;
import com.loohp.limbo.utils.BungeecordAdventureConversionUtils;
import com.loohp.limbo.utils.CheckedBiConsumer;
import com.loohp.limbo.utils.CustomStringUtils;
import com.loohp.limbo.utils.DataTypeIO;
import com.loohp.limbo.utils.DeclareCommands;
import com.loohp.limbo.utils.ForwardingUtils;
import com.loohp.limbo.utils.GameMode;
import com.loohp.limbo.utils.InventoryClickUtils;
import com.loohp.limbo.utils.MojangAPIUtils;
import com.loohp.limbo.utils.MojangAPIUtils.SkinResponse;
import com.loohp.limbo.world.BlockState;
import com.loohp.limbo.world.World;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class ClientConnection extends Thread {

    private static final Key DEFAULT_HANDLER_NAMESPACE = Key.key("default");
    private static final String BRAND_ANNOUNCE_CHANNEL = Key.key("brand").toString();
    private final AtomicLong lastKeepAliveResponse = new AtomicLong(System.currentTimeMillis());

    private final Random random = new Random();
    private final Socket clientSocket;
    protected Channel channel;
    private boolean running;
    private volatile ClientState state;

    private Player player;
    private TimerTask keepAliveTask;
    private AtomicLong lastPacketTimestamp;
    private AtomicLong lastKeepAlivePayLoad;
    private InetAddress inetAddress;
    private boolean ready;

    public ClientConnection(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.inetAddress = clientSocket.getInetAddress();
        this.lastPacketTimestamp = new AtomicLong(-1);
        this.lastKeepAlivePayLoad = new AtomicLong(-1);
        this.channel = null;
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

    public long getLastPacketTimestamp() {
        return lastPacketTimestamp.get();
    }

    public void setLastPacketTimestamp(long payLoad) {
        this.lastPacketTimestamp.set(payLoad);
    }

    public TimerTask getKeepAliveTask() {
        return this.keepAliveTask;
    }

    public Player getPlayer() {
        return player;
    }

    public ClientState getClientState() {
        return state;
    }

    public Socket getSocket() {
        return clientSocket;
    }

    public Channel getChannel() {
        return channel;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isReady() {
        return ready;
    }

    public void sendPluginMessage(String channel, byte[] data) throws IOException {
        PacketPlayOutPluginMessaging packet = new PacketPlayOutPluginMessaging(channel, data);
        sendPacket(packet);
    }

    public synchronized void sendPacket(PacketOut packet) throws IOException {
        if (channel.writePacket(packet)) {
            setLastPacketTimestamp(System.currentTimeMillis());
        }
    }

    public void disconnect(BaseComponent[] reason) {
        disconnect(BungeecordAdventureConversionUtils.toComponent(reason));
    }

    public void disconnect(Component reason) {
        try {
            PacketPlayOutDisconnect packet = new PacketPlayOutDisconnect(reason);
            sendPacket(packet);
        } catch (IOException ignored) {
        }
        try {
            ServerProperties properties = Limbo.getInstance().getServerProperties();
            String str = (properties.isLogPlayerIPAddresses() ? inetAddress.getHostName() : "<ip address withheld>") + ":" + clientSocket.getPort();
            Limbo.getInstance().getConsole().sendMessage("[/" + str + "] <-> Player disconnected with the reason " + PlainTextComponentSerializer.plainText().serialize(reason));
            clientSocket.close();
        } catch (IOException ignored) {
        }
    }

    private void disconnectDuringLogin(BaseComponent[] reason) {
        disconnectDuringLogin(BungeecordAdventureConversionUtils.toComponent(reason));
    }

    private void disconnectDuringLogin(Component reason) {
        ServerProperties properties = Limbo.getInstance().getServerProperties();
        if (!properties.isReducedDebugInfo()) {
            String str = (properties.isLogPlayerIPAddresses() ? inetAddress.getHostName() : "<ip address withheld>") + ":" + clientSocket.getPort();
            Limbo.getInstance().getConsole().sendMessage("[/" + str + "] <-> Player disconnected with the reason " + PlainTextComponentSerializer.plainText().serialize(reason));
        }
        try {
            PacketLoginOutDisconnect packet = new PacketLoginOutDisconnect(reason);
            sendPacket(packet);
        } catch (IOException ignored) {
        }
        try {
            clientSocket.close();
        } catch (IOException ignored) {
        }
    }

    private void setChannel(DataInputStream input, DataOutputStream output) {
        this.channel = new Channel(this, input, output);

        this.channel.addHandlerBefore(DEFAULT_HANDLER_NAMESPACE, new ChannelPacketHandler() {
            @Override
            public ChannelPacketRead read(ChannelPacketRead read) {
                if (read.hasReadPacket()) {
                    return super.read(read);
                }
                try {
                    DataInput input = read.getDataInput();
                    int size = read.getSize();
                    int packetId = read.getPacketId();
                    Class<? extends PacketIn> packetType = PacketRegistry.getPacketClass(packetId, PacketRegistry.NetworkPhase.fromClientState(state), PacketRegistry.PacketBound.SERVERBOUND);
                    if (packetType == null) {
                        input.skipBytes(size - DataTypeIO.getVarIntLength(packetId));
                        return null;
                    }
                    Constructor<?>[] constructors = packetType.getConstructors();
                    Constructor<?> constructor = Arrays.stream(constructors).filter(each -> each.getParameterCount() > 0 && each.getParameterTypes()[0].equals(DataInputStream.class)).findFirst().orElse(null);
                    if (constructor == null) {
                        throw new NoSuchMethodException(packetType + " has no valid constructors!");
                    } else if (constructor.getParameterCount() == 1) {
                        read.setPacket((PacketIn) constructor.newInstance(input));
                    } else if (constructor.getParameterCount() == 3) {
                        read.setPacket((PacketIn) constructor.newInstance(input, size, packetId));
                    } else {
                        throw new NoSuchMethodException(packetType + " has no valid constructors!");
                    }
                    return super.read(read);
                } catch (Exception e) {
                    throw new RuntimeException("Unable to read packet", e);
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        running = true;
        state = ClientState.HANDSHAKE;
        try {
            clientSocket.setKeepAlive(true);
            setChannel(new DataInputStream(clientSocket.getInputStream()), new DataOutputStream(clientSocket.getOutputStream()));

            Limbo.getInstance().getEventsManager().callEvent(new ConnectionEstablishedEvent(this));

            int handShakeSize = DataTypeIO.readVarInt(channel.input);

            //legacy ping
            if (handShakeSize == 0xFE) {
                ServerProperties properties = Limbo.getInstance().getServerProperties();

                state = ClientState.LEGACY;
                channel.output.writeByte(255);
                String str = (properties.isLogPlayerIPAddresses() ? inetAddress.getHostName() : "<ip address withheld>") + ":" + clientSocket.getPort();
                Limbo.getInstance().getConsole().sendMessage("[/" + str + "] <-> Legacy Status has pinged");
                ServerProperties p = Limbo.getInstance().getServerProperties();
                StatusPingEvent event = Limbo.getInstance().getEventsManager().callEvent(new StatusPingEvent(this, p.getVersionString(), p.getProtocol(), p.getMotd(), p.getMaxPlayers(), Limbo.getInstance().getPlayers().size(), p.getFavicon().orElse(null)));
                String response = Limbo.getInstance().buildLegacyPingResponse(event.getVersion(), event.getMotd(), event.getMaxPlayers(), event.getPlayersOnline());
                byte[] bytes = response.getBytes(StandardCharsets.UTF_16BE);
                channel.output.writeShort(response.length());
                channel.output.write(bytes);

                channel.close();
                clientSocket.close();
                state = ClientState.DISCONNECTED;
            }

            PacketHandshakingIn handshake = (PacketHandshakingIn) channel.readPacket(handShakeSize);

            boolean isBungeecord = Limbo.getInstance().getServerProperties().isBungeecord();
            boolean isBungeeGuard = Limbo.getInstance().getServerProperties().isBungeeGuard();
            boolean isVelocityModern = Limbo.getInstance().getServerProperties().isVelocityModern();
            String bungeeForwarding = handshake.getServerAddress();
            UUID bungeeUUID = null;
            SkinResponse forwardedSkin = null;

            try {
                switch (handshake.getHandshakeType()) {
                case STATUS:
                    state = ClientState.STATUS;
                    while (clientSocket.isConnected()) {
                        PacketIn packetIn = channel.readPacket();
                        if (packetIn instanceof PacketStatusInRequest) {
                            ServerProperties properties = Limbo.getInstance().getServerProperties();

                            String str = (properties.isLogPlayerIPAddresses() ? inetAddress.getHostName() : "<ip address withheld>") + ":" + clientSocket.getPort();
                            if (Limbo.getInstance().getServerProperties().handshakeVerboseEnabled()) {
                                Limbo.getInstance().getConsole().sendMessage("[/" + str + "] <-> Handshake Status has pinged");
                            }
                            ServerProperties p = Limbo.getInstance().getServerProperties();
                            StatusPingEvent event = Limbo.getInstance().getEventsManager().callEvent(new StatusPingEvent(this, p.getVersionString(), p.getProtocol(), p.getMotd(), p.getMaxPlayers(), Limbo.getInstance().getPlayers().size(), p.getFavicon().orElse(null)));
                            PacketStatusOutResponse response = new PacketStatusOutResponse(Limbo.getInstance().buildServerListResponseJson(event.getVersion(), event.getProtocol(), event.getMotd(), event.getMaxPlayers(), event.getPlayersOnline(), event.getFavicon()));
                            sendPacket(response);
                        } else if (packetIn instanceof PacketStatusInPing) {
                            PacketStatusInPing ping = (PacketStatusInPing) packetIn;
                            PacketStatusOutPong pong = new PacketStatusOutPong(ping.getPayload());
                            sendPacket(pong);
                            break;
                        }
                    }
                    break;
                case LOGIN:
                case TRANSFER:
                    state = ClientState.LOGIN;
                    ServerProperties properties = Limbo.getInstance().getServerProperties();

                    if (isBungeecord || isBungeeGuard) {
                        try {
                            String[] data = bungeeForwarding.split("\\x00");
                            String host = "";
                            String floodgate = "";
                            String clientIp = "";
                            String bungee = "";
                            String skinData = "";
                            int state = 0;
                            for (int i = 0; i < data.length; i++) {
                                if (!properties.isReducedDebugInfo()) {
                                    Limbo.getInstance().getConsole().sendMessage(i + ": " + data[i]);
                                }

                                switch (state) {
                                default:
                                    Limbo.getInstance().getConsole().sendMessage(i + ": ignore data: State: " + state);
                                    break;
                                case 0:
                                    host = data[i];
                                    state = 1;
                                    break;
                                case 1:
                                    if (data[i].startsWith("^Floodgate^")) {
                                        floodgate = data[i];
                                        state = 2;
                                        break;
                                    }
                                    /* fallthrough */
                                case 2:
                                    clientIp = data[i];
                                    state = 3;
                                    break;
                                case 3:
                                    bungee = data[i];
                                    state = 4;
                                    break;
                                case 4:
                                    skinData = data[i];
                                    state = 6;
                                    break;
                                }
                            }
                            if (state != 6) {
                                throw new IllegalStateException("Illegal bungee state: " + state);
                            }

                            if (!properties.isReducedDebugInfo()) {
                                Limbo.getInstance().getConsole().sendMessage("Host: " + host);
                                Limbo.getInstance().getConsole().sendMessage("Floodgate: " + floodgate);
                                Limbo.getInstance().getConsole().sendMessage("clientIp: " + clientIp);
                                Limbo.getInstance().getConsole().sendMessage("bungee: " + bungee);
                                Limbo.getInstance().getConsole().sendMessage("skinData: " + skinData);
                            }

                            bungeeUUID = UUID.fromString(bungee.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5"));
                            inetAddress = InetAddress.getByName(clientIp);

                            boolean bungeeGuardFound = false;

                            if (!skinData.equals("")) {
                                JSONArray skinJson = (JSONArray) new JSONParser().parse(skinData);

                                for (Object obj : skinJson) {
                                    JSONObject property = (JSONObject) obj;
                                    if (property.get("name").toString().equals("textures")) {
                                        String skin = property.get("value").toString();
                                        String signature = property.get("signature").toString();
                                        forwardedSkin = new SkinResponse(skin, signature);
                                    } else if (isBungeeGuard && property.get("name").toString().equals("bungeeguard-token")) {
                                        String token = property.get("value").toString();
                                        bungeeGuardFound = Limbo.getInstance().getServerProperties().getForwardingSecrets().contains(token);
                                    }
                                }
                            }

                            if (isBungeeGuard && !bungeeGuardFound) {
                                disconnectDuringLogin(TextComponent.fromLegacyText("Invalid information forwarding"));
                                break;
                            }
                        } catch (Exception e) {
                            if (!properties.isReducedDebugInfo()) {
                                StringWriter sw = new StringWriter();
                                PrintWriter pw = new PrintWriter(sw);
                                e.printStackTrace(pw);
                                Limbo.getInstance().getConsole().sendMessage(sw.toString());
                            }
                            Limbo.getInstance().getConsole().sendMessage("If you wish to use bungeecord's IP forwarding, please enable that in your bungeecord config.yml as well!");
                            disconnectDuringLogin(new BaseComponent[] {new TextComponent(ChatColor.RED + "Please connect from the proxy!")});
                        }
                    }
                    int messageId = this.random.nextInt();
                    while (clientSocket.isConnected()) {
                        PacketIn packetIn = channel.readPacket();

                        if (packetIn instanceof PacketLoginInLoginStart) {
                            PacketLoginInLoginStart start = (PacketLoginInLoginStart) packetIn;
                            String username = start.getUsername();

                            if (Limbo.getInstance().getServerProperties().isVelocityModern()) {
                                PacketLoginOutPluginMessaging loginPluginRequest = new PacketLoginOutPluginMessaging(messageId, ForwardingUtils.VELOCITY_FORWARDING_CHANNEL);
                                sendPacket(loginPluginRequest);
                                continue;
                            }

                            UUID uuid = isBungeecord || isBungeeGuard ? bungeeUUID : start.getUniqueId();
                            if (uuid == null) {
                                uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
                            }

                            if (!properties.enforceWhitelist() && properties.uuidWhitelisted(uuid)) {
                                disconnectDuringLogin(TextComponent.fromLegacyText("You are not whitelisted on the server"));
                                break;
                            }

                            PacketLoginOutLoginSuccess success = new PacketLoginOutLoginSuccess(uuid, username);
                            sendPacket(success);

                            player = new Player(this, username, uuid, Limbo.getInstance().getNextEntityId(), Limbo.getInstance().getServerProperties().getWorldSpawn(), new PlayerInteractManager());
                            player.setSkinLayers((byte) (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40));
                        } else if (packetIn instanceof PacketLoginInPluginMessaging) {
                            PacketLoginInPluginMessaging response = (PacketLoginInPluginMessaging) packetIn;
                            if (response.getMessageId() != messageId) {
                                disconnectDuringLogin(TextComponent.fromLegacyText("Internal error, messageId did not match"));
                                break;
                            }
                            if (!response.getData().isPresent()) {
                                disconnectDuringLogin(TextComponent.fromLegacyText("Unknown login plugin response packet!"));
                                break;
                            }
                            byte[] responseData = response.getData().get();
                            if (!ForwardingUtils.validateVelocityModernResponse(responseData)) {
                                disconnectDuringLogin(TextComponent.fromLegacyText("Invalid playerinfo forwarding!"));
                                break;
                            }
                            ForwardingUtils.VelocityModernForwardingData data = ForwardingUtils.getVelocityDataFrom(responseData);
                            inetAddress = InetAddress.getByName(data.getIpAddress());
                            forwardedSkin = data.getSkinResponse();

                            PacketLoginOutLoginSuccess success = new PacketLoginOutLoginSuccess(data.getUuid(), data.getUsername());
                            sendPacket(success);

                            player = new Player(this, data.getUsername(), data.getUuid(), Limbo.getInstance().getNextEntityId(), Limbo.getInstance().getServerProperties().getWorldSpawn(), new PlayerInteractManager());
                            player.setSkinLayers((byte) (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40));
                        } else if (packetIn instanceof ServerboundLoginAcknowledgedPacket) {
                            state = ClientState.CONFIGURATION;
                            break;
                        }
                    }

                    PlayerLoginEvent event = Limbo.getInstance().getEventsManager().callEvent(new PlayerLoginEvent(this, false, Component.empty()));
                    if (event.isCancelled()) {
                        disconnectDuringLogin(event.getCancelReason());
                    }

                    break;
                }
            } catch (Exception ignored) {
                channel.close();
                clientSocket.close();
                state = ClientState.DISCONNECTED;
            }

            if (state == ClientState.CONFIGURATION) {

                TimeUnit.MILLISECONDS.sleep(500);

                for (RegistryCustom registryCustom : RegistryCustom.getRegistries()) {
                    ClientboundRegistryDataPacket registryDataPacket = new ClientboundRegistryDataPacket(registryCustom);
                    sendPacket(registryDataPacket);
                }

                ClientboundFinishConfigurationPacket clientboundFinishConfigurationPacket = new ClientboundFinishConfigurationPacket();
                sendPacket(clientboundFinishConfigurationPacket);

                ServerboundFinishConfigurationPacket serverboundFinishConfigurationPacket = (ServerboundFinishConfigurationPacket) channel.readPacket();

                state = ClientState.PLAY;
                Limbo.getInstance().getUnsafe().a(player);

                TimeUnit.MILLISECONDS.sleep(500);

                ServerProperties properties = Limbo.getInstance().getServerProperties();
                Location worldSpawn = properties.getWorldSpawn();

                PlayerSpawnEvent spawnEvent = Limbo.getInstance().getEventsManager().callEvent(new PlayerSpawnEvent(player, worldSpawn));
                worldSpawn = spawnEvent.getSpawnLocation();
                World world = worldSpawn.getWorld();

                PacketPlayOutLogin join = new PacketPlayOutLogin(player.getEntityId(), false, Limbo.getInstance().getWorlds(), properties.getMaxPlayers(), 8, 8, properties.isReducedDebugInfo(), true, false, world.getEnvironment(), world, 0, properties.getDefaultGamemode(), false, true, 0, 0, false);
                sendPacket(join);
                Limbo.getInstance().getUnsafe().a(player, properties.getDefaultGamemode());

                ByteArrayOutputStream brandOut = new ByteArrayOutputStream();
                DataTypeIO.writeString(new DataOutputStream(brandOut), properties.getServerModName(), StandardCharsets.UTF_8);
                sendPluginMessage(BRAND_ANNOUNCE_CHANNEL, brandOut.toByteArray());

                SkinResponse skinresponce = (isVelocityModern || isBungeeGuard || isBungeecord) && forwardedSkin != null ? forwardedSkin : MojangAPIUtils.getSkinFromMojangServer(player.getName());
                PlayerSkinProperty skin = skinresponce != null ? new PlayerSkinProperty(skinresponce.getSkin(), skinresponce.getSignature()) : null;
                PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(EnumSet.of(PlayerInfoAction.ADD_PLAYER, PlayerInfoAction.UPDATE_GAME_MODE, PlayerInfoAction.UPDATE_LISTED, PlayerInfoAction.UPDATE_LATENCY, PlayerInfoAction.UPDATE_DISPLAY_NAME), player.getUniqueId(), new PlayerInfoData.PlayerInfoDataAddPlayer(player.getName(), true, Optional.ofNullable(skin), properties.getDefaultGamemode(), 0, false, Optional.empty()));
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

                String str = (properties.isLogPlayerIPAddresses() ? inetAddress.getHostName() : "<ip address withheld>") + ":" + clientSocket.getPort() + "|" + player.getName() + "(" + player.getUniqueId() + ")";
                Limbo.getInstance().getConsole().sendMessage("[/" + str + "] <-> Player had connected to the Limbo server!");

                PacketPlayOutGameStateChange gameEvent = new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.GameStateChangeEvent.LEVEL_CHUNKS_LOAD_START, 0);
                sendPacket(gameEvent);

                player.playerInteractManager.update();

                PacketPlayOutDeclareCommands declare = DeclareCommands.getDeclareCommandsPacket(player);
                if (declare != null) {
                    sendPacket(declare);
                }

                PacketPlayOutSpawnPosition spawnPos = new PacketPlayOutSpawnPosition(GlobalPos.from(worldSpawn), worldSpawn.getYaw(), worldSpawn.getPitch());
                sendPacket(spawnPos);

                PacketPlayOutPositionAndLook positionLook = new PacketPlayOutPositionAndLook(worldSpawn.getX(), worldSpawn.getY(), worldSpawn.getZ(), worldSpawn.getYaw(), worldSpawn.getPitch(), 1);
                Limbo.getInstance().getUnsafe().a(player, new Location(world, worldSpawn.getX(), worldSpawn.getY(), worldSpawn.getZ(), worldSpawn.getYaw(), worldSpawn.getPitch()));
                sendPacket(positionLook);

                player.getDataWatcher().update();
                PacketPlayOutEntityMetadata show = new PacketPlayOutEntityMetadata(player, false, Player.class.getDeclaredField("skinLayers"));
                sendPacket(show);

                Limbo.getInstance().getEventsManager().callEvent(new PlayerJoinEvent(player));
                
                if (properties.isAllowFlight()) {
                    PacketPlayOutGameStateChange state = new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.GameStateChangeEvent.CHANGE_GAME_MODE, player.getGamemode().getId());
                    sendPacket(state);
                }
                
                // RESOURCEPACK CODE CONRIBUTED BY GAMERDUCK123
                if (!properties.getResourcePackLink().equalsIgnoreCase("")) {
                    if (!properties.getResourcePackSHA1().equalsIgnoreCase("")) {
                        //SEND RESOURCEPACK
                        player.setResourcePack(properties.getResourcePackLink(), properties.getResourcePackSHA1(), properties.getResourcePackRequired(), properties.getResourcePackPrompt());
                    } else {
                        //NO SHA
                        Limbo.getInstance().getConsole().sendMessage("ResourcePacks require SHA1s");
                    }
                } else {
                    //RESOURCEPACK NOT ENABLED
                }
                
                // PLAYER LIST HEADER AND FOOTER CODE CONRIBUTED BY GAMERDUCK123
                player.sendPlayerListHeaderAndFooter(properties.getTabHeader(), properties.getTabFooter());
                
                ready = true;

                keepAliveTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (state != ClientState.PLAY || !ready) {
                            this.cancel();
                        }

                        long now = System.currentTimeMillis();
                        PacketPlayOutKeepAlive keepAlive = new PacketPlayOutKeepAlive(now);
                        try {
                            sendPacket(keepAlive);
                            setLastKeepAlivePayLoad(now);
                        } catch (IOException e) {
                            cancel();
                        }
                    }
                };
                new Timer().schedule(keepAliveTask, 0, 10000);

                while (clientSocket.isConnected()) {
                    try {
                        CheckedBiConsumer<PlayerMoveEvent, Location, IOException> processMoveEvent = (event, originalTo) -> {
                            if (event.isCancelled()) {
                                Location returnTo = event.getFrom();
                                PacketPlayOutPositionAndLook cancel = new PacketPlayOutPositionAndLook(returnTo.getX(), returnTo.getY(), returnTo.getZ(), returnTo.getYaw(), returnTo.getPitch(), 1);
                                sendPacket(cancel);
                            } else {
                                Location to = event.getTo();
                                Limbo.getInstance().getUnsafe().a(player, to);
                                // If an event handler used setTo, let's make sure we tell the player about it.
                                if (!originalTo.equals(to)) {
                                    PacketPlayOutPositionAndLook pos = new PacketPlayOutPositionAndLook(to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch(), 1);
                                    sendPacket(pos);
                                }
                                PacketPlayOutUpdateViewPosition response = new PacketPlayOutUpdateViewPosition((int) player.getLocation().getX() >> 4, (int) player.getLocation().getZ() >> 4);
                                sendPacket(response);
                            }
                        };

                        PacketIn packetIn = channel.readPacket();

                        if (packetIn instanceof PacketPlayInPositionAndLook) {
                            PacketPlayInPositionAndLook pos = (PacketPlayInPositionAndLook) packetIn;
                            Location from = player.getLocation();
                            Location to = new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ(), pos.getYaw(), pos.getPitch());

                            if (!from.equals(to)) {
                                PlayerMoveEvent event = Limbo.getInstance().getEventsManager().callEvent(new PlayerMoveEvent(player, from, to));
                                processMoveEvent.consume(event, to);
                            }
                        } else if (packetIn instanceof PacketPlayInPosition) {
                            PacketPlayInPosition pos = (PacketPlayInPosition) packetIn;
                            Location from = player.getLocation();
                            Location to = new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());

                            if (!from.equals(to)) {
                                PlayerMoveEvent event = Limbo.getInstance().getEventsManager().callEvent(new PlayerMoveEvent(player, from, to));
                                processMoveEvent.consume(event, to);
                            }
                        } else if (packetIn instanceof PacketPlayInRotation) {
                            PacketPlayInRotation pos = (PacketPlayInRotation) packetIn;
                            Location from = player.getLocation();
                            Location to = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), pos.getYaw(), pos.getPitch());

                            if (!from.equals(to)) {
                                PlayerMoveEvent event = Limbo.getInstance().getEventsManager().callEvent(new PlayerMoveEvent(player, from, to));
                                processMoveEvent.consume(event, to);
                            }
                        } else if (packetIn instanceof PacketPlayInKeepAlive) {
                            PacketPlayInKeepAlive alive = (PacketPlayInKeepAlive) packetIn;

                            if (alive.getPayload() == getLastKeepAlivePayLoad()) {
                                lastKeepAliveResponse.set(System.currentTimeMillis());
                            } else {
                                disconnect(Component.text("Bad Keepalive Payload"));
                                break;
                            }
                        } else if (packetIn instanceof PacketPlayInTabComplete) {
                            PacketPlayInTabComplete request = (PacketPlayInTabComplete) packetIn;
                            String[] command = CustomStringUtils.splitStringToArgs(request.getText().substring(1));

                            List<TabCompleteMatches> matches = new ArrayList<>(Limbo.getInstance().getPluginManager().getTabOptions(player, command).stream().map(each -> new TabCompleteMatches(each)).collect(Collectors.toList()));

                            int start = CustomStringUtils.getIndexOfArg(request.getText(), command.length - 1) + 1;
                            int length = command[command.length - 1].length();

                            PacketPlayOutTabComplete response = new PacketPlayOutTabComplete(request.getId(), start, length, matches.toArray(new TabCompleteMatches[matches.size()]));
                            sendPacket(response);
                        } else if (packetIn instanceof PacketPlayInChat) {
                            PacketPlayInChat chat = (PacketPlayInChat) packetIn;
                            player.chat(chat.getMessage(), true, chat.getSignature(), chat.getTime());
                        } else if (packetIn instanceof ServerboundChatCommandPacket) {
                            ServerboundChatCommandPacket command = (ServerboundChatCommandPacket) packetIn;
                            Limbo.getInstance().dispatchCommand(player, "/" + command.getCommand());
                        } else if (packetIn instanceof PacketPlayInHeldItemChange) {
                            PacketPlayInHeldItemChange change = (PacketPlayInHeldItemChange) packetIn;
                            PlayerSelectedSlotChangeEvent event = Limbo.getInstance().getEventsManager().callEvent(new PlayerSelectedSlotChangeEvent(player, (byte) change.getSlot()));
                            if (event.isCancelled()) {
                                PacketPlayOutHeldItemChange cancelPacket = new PacketPlayOutHeldItemChange(player.getSelectedSlot());
                                sendPacket(cancelPacket);
                            } else if (change.getSlot() != event.getSlot()) {
                                PacketPlayOutHeldItemChange changePacket = new PacketPlayOutHeldItemChange(event.getSlot());
                                sendPacket(changePacket);
                                Limbo.getInstance().getUnsafe().a(player, event.getSlot());
                            } else {
                                Limbo.getInstance().getUnsafe().a(player, event.getSlot());
                            }

                        } else if (packetIn instanceof ServerboundResourcePackPacket) {
                            ServerboundResourcePackPacket rpcheck = (ServerboundResourcePackPacket) packetIn;
                            // Pass on result to the events
                            Limbo.getInstance().getEventsManager().callEvent(new PlayerResourcePackStatusEvent(player, rpcheck.getAction()));
                            if (rpcheck.getAction().equals(Action.DECLINED) && properties.getResourcePackRequired()) {
                                player.disconnect(new TranslatableComponent("multiplayer.requiredTexturePrompt.disconnect"));
                            }
                        } else if (packetIn instanceof PacketPlayInPluginMessaging) {
                            PacketPlayInPluginMessaging inPluginMessaging = (PacketPlayInPluginMessaging) packetIn;
                            Limbo.getInstance().getEventsManager().callEvent(new PluginMessageEvent(player, inPluginMessaging.getChannel(), inPluginMessaging.getData()));
                        } else if (packetIn instanceof PacketPlayInBlockPlace) {
                            PacketPlayInBlockPlace packet = (PacketPlayInBlockPlace) packetIn;
                            Limbo.getInstance().getEventsManager().callEvent(new PlayerInteractEvent(player, PlayerInteractEvent.Action.RIGHT_CLICK_AIR, player.getEquipment().getItem(packet.getHand()), null, null, packet.getHand()));
                        } else if (packetIn instanceof PacketPlayInUseItem) {
                            PacketPlayInUseItem packet = (PacketPlayInUseItem) packetIn;
                            BlockState block = player.getWorld().getBlock(packet.getBlockHit().getBlockPos());
                            Limbo.getInstance().getEventsManager().callEvent(new PlayerInteractEvent(player, PlayerInteractEvent.Action.RIGHT_CLICK_AIR, player.getEquipment().getItem(packet.getHand()), block, packet.getBlockHit().getDirection(), packet.getHand()));
                        } else if (packetIn instanceof PacketPlayInSetCreativeSlot) {
                            PacketPlayInSetCreativeSlot packet = (PacketPlayInSetCreativeSlot) packetIn;
                            InventoryCreativeEvent event = Limbo.getInstance().getEventsManager().callEvent(new InventoryCreativeEvent(player.getInventoryView(), player.getInventory().getUnsafe().b().applyAsInt(packet.getSlotNumber()), packet.getItemStack()));
                            if (event.isCancelled()) {
                                player.updateInventory();
                            } else {
                                player.getInventory().setItem(event.getSlot(), event.getNewItem());
                            }
                        } else if (packetIn instanceof PacketPlayInWindowClick) {
                            PacketPlayInWindowClick packet = (PacketPlayInWindowClick) packetIn;
                            try {
                                InventoryClickUtils.handle(player, packet);
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        } else if (packetIn instanceof PacketPlayInCloseWindow) {
                            PacketPlayInCloseWindow packet = (PacketPlayInCloseWindow) packetIn;
                            Inventory inventory = player.getInventoryView().getTopInventory();
                            if (inventory != null) {
                                Integer id = inventory.getUnsafe().c().get(player);
                                if (id != null) {
                                    Limbo.getInstance().getEventsManager().callEvent(new InventoryCloseEvent(player.getInventoryView()));
                                    player.getInventoryView().getUnsafe().a(null, null);
                                    inventory.getUnsafe().c().remove(player);
                                }
                            }
                        } else if (packetIn instanceof PacketPlayInBlockDig) {
                            PacketPlayInBlockDig packet = (PacketPlayInBlockDig) packetIn;
                            //noinspection SwitchStatementWithTooFewBranches
                            switch (packet.getAction()) {
                                case SWAP_ITEM_WITH_OFFHAND: {
                                    EntityEquipment equipment = player.getEquipment();
                                    PlayerSwapHandItemsEvent event = Limbo.getInstance().getEventsManager().callEvent(new PlayerSwapHandItemsEvent(player, equipment.getItemInOffHand(), equipment.getItemInMainHand()));
                                    if (!event.isCancelled()) {
                                        equipment.setItemInMainHand(event.getMainHandItem());
                                        equipment.setItemInOffHand(event.getOffHandItem());
                                    }
                                    break;
                                }
                            }
                        } else if (packetIn instanceof PacketPlayInPickItem) {
                            PacketPlayInPickItem packet = (PacketPlayInPickItem) packetIn;
                            PlayerInventory inventory = player.getInventory();
                            int slot = inventory.getUnsafe().b().applyAsInt(packet.getSlot());
                            int i = player.getSelectedSlot();
                            byte selectedSlot = -1;
                            boolean firstRun = true;
                            while (selectedSlot < 0 || (!firstRun && i == player.getSelectedSlot())) {
                                ItemStack itemStack = inventory.getItem(i);
                                if (itemStack == null) {
                                    selectedSlot = (byte) i;
                                    break;
                                }
                                if (++i >= 9) {
                                    i = 0;
                                }
                            }
                            if (selectedSlot < 0) {
                                selectedSlot = player.getSelectedSlot();
                            }
                            ItemStack leavingHotbar = inventory.getItem(selectedSlot);
                            inventory.setItem(selectedSlot, inventory.getItem(slot));
                            inventory.setItem(slot, leavingHotbar);
                            player.setSelectedSlot(selectedSlot);
                        } else if (packetIn instanceof PacketPlayInItemName) {
                            PacketPlayInItemName packet = (PacketPlayInItemName) packetIn;
                            if (player.getInventoryView().getTopInventory() instanceof AnvilInventory) {
                                AnvilRenameInputEvent event = Limbo.getInstance().getEventsManager().callEvent(new AnvilRenameInputEvent(player.getInventoryView(), packet.getName()));
                                if (!event.isCancelled()) {
                                    AnvilInventory anvilInventory = (AnvilInventory) player.getInventoryView().getTopInventory();
                                    ItemStack result = anvilInventory.getItem(2);
                                    if (result != null) {
                                        result.displayName(LegacyComponentSerializer.legacySection().deserialize(event.getInput()));
                                    }
                                }
                            }
                        }
                    } catch (Exception ignored) {
                        break;
                    }
                }

                Limbo.getInstance().getEventsManager().callEvent(new PlayerQuitEvent(player));

                str = (properties.isLogPlayerIPAddresses() ? inetAddress.getHostName() : "<ip address withheld>") + ":" + clientSocket.getPort() + "|" + player.getName();
                Limbo.getInstance().getConsole().sendMessage("[/" + str + "] <-> Player had disconnected!");
            }
        } catch (Exception ignored) {
        }

        try {
            channel.close();
            clientSocket.close();
        } catch (Exception ignored) {
        }
        state = ClientState.DISCONNECTED;

        if (player != null) {
            Limbo.getInstance().getUnsafe().b(player);
        }
        Limbo.getInstance().getServerConnection().getClients().remove(this);
        running = false;
    }

    public enum ClientState {
        LEGACY,
        HANDSHAKE,
        STATUS,
        LOGIN,
        CONFIGURATION,
        PLAY,
        DISCONNECTED;
    }

}
