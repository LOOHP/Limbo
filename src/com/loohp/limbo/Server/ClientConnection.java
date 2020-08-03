package com.loohp.limbo.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.loohp.limbo.Limbo;
import com.loohp.limbo.File.ServerProperties;
import com.loohp.limbo.Location.Location;
import com.loohp.limbo.Server.Packets.Packet;
import com.loohp.limbo.Server.Packets.PacketHandshakingIn;
import com.loohp.limbo.Server.Packets.PacketLoginInLoginStart;
import com.loohp.limbo.Server.Packets.PacketLoginOutLoginSuccess;
import com.loohp.limbo.Server.Packets.PacketPlayInChat;
import com.loohp.limbo.Server.Packets.PacketPlayInKeepAlive;
import com.loohp.limbo.Server.Packets.PacketPlayInPosition;
import com.loohp.limbo.Server.Packets.PacketPlayInPositionAndLook;
import com.loohp.limbo.Server.Packets.PacketPlayOutChat;
import com.loohp.limbo.Server.Packets.PacketPlayOutLogin;
import com.loohp.limbo.Server.Packets.PacketPlayOutMapChunk;
import com.loohp.limbo.Server.Packets.PacketPlayOutPlayerAbilities;
import com.loohp.limbo.Server.Packets.PacketPlayOutPlayerAbilities.PlayerAbilityFlags;
import com.loohp.limbo.Server.Packets.PacketPlayOutPlayerInfo;
import com.loohp.limbo.Server.Packets.PacketPlayOutPlayerInfo.PlayerInfoAction;
import com.loohp.limbo.Server.Packets.PacketPlayOutPlayerInfo.PlayerInfoData;
import com.loohp.limbo.Server.Packets.PacketPlayOutPlayerInfo.PlayerInfoData.PlayerInfoDataAddPlayer.PlayerSkinProperty;
import com.loohp.limbo.Server.Packets.PacketPlayOutPositionAndLook;
import com.loohp.limbo.Server.Packets.PacketPlayOutShowPlayerSkins;
import com.loohp.limbo.Server.Packets.PacketPlayOutSpawnPosition;
import com.loohp.limbo.Server.Packets.PacketPlayOutUpdateViewPosition;
import com.loohp.limbo.Server.Packets.PacketStatusInPing;
import com.loohp.limbo.Server.Packets.PacketStatusInRequest;
import com.loohp.limbo.Server.Packets.PacketStatusOutPong;
import com.loohp.limbo.Server.Packets.PacketStatusOutResponse;
import com.loohp.limbo.Utils.DataTypeIO;
import com.loohp.limbo.Utils.SkinUtils;
import com.loohp.limbo.Utils.SkinUtils.SkinResponse;
import com.loohp.limbo.World.BlockPosition;
import com.loohp.limbo.World.DimensionRegistry;
import com.loohp.limbo.World.World;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.querz.mca.Chunk;

public class ClientConnection extends Thread {
	
	public enum ClientState {
		HANDSHAKE,
		STATUS,
		LOGIN,
		PLAY,
		DISCONNECTED;
	}

    private Socket client_socket;
    private boolean running;
    private ClientState state;
    
    private String username;
	private UUID uuid;
	
	private Location location;
	
	private long lastKeepAlivePayLoad;
	
	public long getLastKeepAlivePayLoad() {
		return lastKeepAlivePayLoad;
	}
	
	public void setLastKeepAlivePayLoad(long payLoad) {
		this.lastKeepAlivePayLoad = payLoad;
	}

	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public String getUsername() {
		return username;
	}

	public UUID getUuid() {
		return uuid;
	}

    public ClientConnection(Socket client_socket) {
        this.client_socket = client_socket;
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
	
	public int getEntityId() {
		return Limbo.getInstance().getServerConnection().getClients().indexOf(this);
	}
	
	public void sendMessage(String message) {
		sendMessage(TextComponent.fromLegacyText(message));
	}
	
	public void sendMessage(BaseComponent component) {
		sendMessage(new BaseComponent[] {component});
	}
	
	public void sendMessage(BaseComponent[] component) {
		try {
			DataOutputStream output = new DataOutputStream(client_socket.getOutputStream());
			PacketPlayOutChat chat = new PacketPlayOutChat(ComponentSerializer.toString(component), 0, new UUID(0, 0));
			byte[] packetByte = chat.getBytes();
			DataTypeIO.writeVarInt(output, packetByte.length);
			output.write(packetByte);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
    public void run() {
        running = true;
        state = ClientState.HANDSHAKE;
    	try {
    		client_socket.setKeepAlive(true);
    		DataInputStream input = new DataInputStream(client_socket.getInputStream());
    		DataOutputStream output = new DataOutputStream(client_socket.getOutputStream());
		    DataTypeIO.readVarInt(input);
		    int handShakeId = DataTypeIO.readVarInt(input);
		    PacketHandshakingIn handshake = (PacketHandshakingIn) Packet.getHandshakeIn().get(handShakeId).getConstructor(DataInputStream.class).newInstance(input);
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
						String str = client_socket.getInetAddress().getHostName() + ":" + client_socket.getPort();
						System.out.println("[/" + str + "] <-> InitialHandler has pinged");
						PacketStatusOutResponse packet = new PacketStatusOutResponse(Limbo.getInstance().getServerListResponseJson());
						byte[] packetByte = packet.getBytes();
						DataTypeIO.writeVarInt(output, packetByte.length);
						output.write(packetByte);
					} else if (packetType.equals(PacketStatusInPing.class)) {
						PacketStatusInPing ping = (PacketStatusInPing) packetType.getConstructor(DataInputStream.class).newInstance(input);
						PacketStatusOutPong packet = new PacketStatusOutPong(ping.getPayload());
						byte[] packetByte = packet.getBytes();
						DataTypeIO.writeVarInt(output, packetByte.length);
						output.write(packetByte);
						break;
					}
				}
				break;
			case LOGIN:
				state = ClientState.LOGIN;
				while (client_socket.isConnected()) {
					int size = DataTypeIO.readVarInt(input);
					int packetId = DataTypeIO.readVarInt(input);
					Class<? extends Packet> packetType = Packet.getLoginIn().get(packetId);
					
					if (packetType == null) {
						input.skipBytes(size - DataTypeIO.getVarIntLength(packetId));
					} else if (packetType.equals(PacketLoginInLoginStart.class)) {
						PacketLoginInLoginStart start = (PacketLoginInLoginStart) packetType.getConstructor(DataInputStream.class).newInstance(input);
						username = start.getUsername();
						uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
						PacketLoginOutLoginSuccess success = new PacketLoginOutLoginSuccess(uuid, start.getUsername());
						byte[] packetByte = success.getBytes();
						DataTypeIO.writeVarInt(output, packetByte.length);
						output.write(packetByte);
						
						state = ClientState.PLAY;
						break;
					}
	    		}
				break;
		    }
		    
		    if (state == ClientState.PLAY) {
		    	
		    	TimeUnit.MILLISECONDS.sleep(500);
		    	
		    	ServerProperties p = Limbo.getInstance().getServerProperties();
    			PacketPlayOutLogin join = new PacketPlayOutLogin(getEntityId(), false, p.getDefaultGamemode(), new String[] {p.getLevelName().toString()}, DimensionRegistry.getCodec(), p.getLevelDimension().toString(), p.getLevelName().toString(), 0, (byte) p.getMaxPlayers(), 8, p.isReducedDebugInfo(), true, false, false);
    			byte[] packetByte = join.getBytes();
				DataTypeIO.writeVarInt(output, packetByte.length);
				output.write(packetByte);
				
				Location s = p.getWorldSpawn();
				
				//PacketPlayOutKeepAlive alive = new PacketPlayOutKeepAlive((long) (Math.random() * Long.MAX_VALUE));
				
				World world = s.getWorld();
				
				for (int x = 0; x < world.getChunks().length; x++) {
					for (int z = 0; z < world.getChunks()[x].length; z++) {
						Chunk chunk = world.getChunks()[x][z];
						if (chunk != null) {
							PacketPlayOutMapChunk chunkdata = new PacketPlayOutMapChunk(x, z, chunk);
							packetByte = chunkdata.getBytes();
							DataTypeIO.writeVarInt(output, packetByte.length);
							output.write(packetByte);
							//System.out.println(x + ", " + z);
						}
					}
				}
				
				PacketPlayOutSpawnPosition spawnPos = new PacketPlayOutSpawnPosition(BlockPosition.from(s));
				packetByte = spawnPos.getBytes();
				DataTypeIO.writeVarInt(output, packetByte.length);
				output.write(packetByte);
				PacketPlayOutPositionAndLook positionLook = new PacketPlayOutPositionAndLook(s.getX(), s.getY(), s.getZ(), s.getYaw(), s.getPitch(), 1);
				location = new Location(world, s.getX(), s.getY(), s.getZ(), s.getYaw(), s.getPitch());
				packetByte = positionLook.getBytes();
				DataTypeIO.writeVarInt(output, packetByte.length);
				output.write(packetByte);
				
				SkinResponse skinresponce = SkinUtils.getSkinFromMojangServer(username);
				PlayerSkinProperty skin = skinresponce != null ? new PlayerSkinProperty(skinresponce.getSkin(), skinresponce.getSignature()) : null;
				PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(PlayerInfoAction.ADD_PLAYER, uuid, new PlayerInfoData.PlayerInfoDataAddPlayer(username, Optional.ofNullable(skin), p.getDefaultGamemode(), 0, false, Optional.empty()));
				packetByte = info.getBytes();
				DataTypeIO.writeVarInt(output, packetByte.length);
				output.write(packetByte);
				/*
				for (ClientConnection client : Limbo.getInstance().getServerConnection().getClients()) {
					DataOutputStream other = new DataOutputStream(client.getSocket().getOutputStream());
					DataTypeIO.writeVarInt(other, packetByte.length);
					other.write(packetByte);
				}
				*/
				
				PacketPlayOutShowPlayerSkins show = new PacketPlayOutShowPlayerSkins(getEntityId());
				packetByte = show.getBytes();
				DataTypeIO.writeVarInt(output, packetByte.length);
				output.write(packetByte);
				
				PacketPlayOutPlayerAbilities abilities;
				if (p.isAllowFlight()) {
					abilities = new PacketPlayOutPlayerAbilities(0.05F, 0.1F, PlayerAbilityFlags.ALLOW_FLYING);
				} else {
					abilities = new PacketPlayOutPlayerAbilities(0.05F, 0.1F);
				}
				packetByte = abilities.getBytes();
				DataTypeIO.writeVarInt(output, packetByte.length);
				output.write(packetByte);
				
				String str = client_socket.getInetAddress().getHostName() + ":" + client_socket.getPort() + "|" + username;
				System.out.println("[/" + str + "] <-> Player had connected to the Limbo server!");
				
				while (client_socket.isConnected()) {
					try {						
						int size = DataTypeIO.readVarInt(input);
						int packetId = DataTypeIO.readVarInt(input);
						Class<? extends Packet> packetType = Packet.getPlayIn().get(packetId);
						//System.out.println(packetId);
						
						if (packetType == null) {
							input.skipBytes(size - DataTypeIO.getVarIntLength(packetId));
						} else if (packetType.equals(PacketPlayInPositionAndLook.class)) {					
							PacketPlayInPositionAndLook pos = new PacketPlayInPositionAndLook(input);
							location = new Location(location.getWorld(), pos.getX(), pos.getY(), pos.getZ(), pos.getYaw(), pos.getPitch());
							
							PacketPlayOutUpdateViewPosition response = new PacketPlayOutUpdateViewPosition((int) location.getX() >> 4, (int) location.getZ() >> 4);
							packetByte = response.getBytes();
							DataTypeIO.writeVarInt(output, packetByte.length);
							output.write(packetByte);
						} else if (packetType.equals(PacketPlayInPosition.class)) {
							PacketPlayInPosition pos = new PacketPlayInPosition(input);
							location = new Location(location.getWorld(), pos.getX(), pos.getY(), pos.getZ(), location.getYaw(), location.getPitch());
							
							PacketPlayOutUpdateViewPosition response = new PacketPlayOutUpdateViewPosition((int) location.getX() >> 4, (int) location.getZ() >> 4);
							packetByte = response.getBytes();
							DataTypeIO.writeVarInt(output, packetByte.length);
							output.write(packetByte);
						} else if (packetType.equals(PacketPlayInKeepAlive.class)) {
							PacketPlayInKeepAlive alive = new PacketPlayInKeepAlive(input);
							if (alive.getPayload() != lastKeepAlivePayLoad) {
								System.out.println("Incorrect Payload recieved in KeepAlive packet for player " + username);
								break;
							}
						} else if (packetType.equals(PacketPlayInChat.class)) {
							PacketPlayInChat chat = new PacketPlayInChat(input);
							if (chat.getMessage().startsWith("/")) {
								//TO-DO COMMANDS
							} else {
								String message = "<" + username + "> " + chat.getMessage();
								Limbo.getInstance().getConsole().sendMessage(message);
								for (ClientConnection client : Limbo.getInstance().getServerConnection().getClients()) {
									client.sendMessage(message);
								}
							}
						}
						
					} catch (Exception e) {
						break;
					}
	    		}
				str = client_socket.getInetAddress().getHostName() + ":" + client_socket.getPort() + "|" + username;
				System.out.println("[/" + str + "] <-> Player had disconnected!");
	    	}
		    
    	} catch (Exception e) {}
    	
    	try {
			client_socket.close();
		} catch (IOException e) {}
    	state = ClientState.DISCONNECTED;
    	Limbo.getInstance().getServerConnection().getClients().remove(this);
		running = false;
	}
}
