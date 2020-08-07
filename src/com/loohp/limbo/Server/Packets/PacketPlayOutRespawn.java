package com.loohp.limbo.Server.Packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.loohp.limbo.Utils.DataTypeIO;
import com.loohp.limbo.Utils.GameMode;
import com.loohp.limbo.Utils.NamespacedKey;
import com.loohp.limbo.World.World;

public class PacketPlayOutRespawn extends PacketOut {

	private String dimension;
	private String worldName;
	private long hashedSeed;
	private GameMode gamemode;
	private boolean isDebug;
	private boolean isFlat;
	private boolean copyMetaData;

	public PacketPlayOutRespawn(World world, long hashedSeed, GameMode gamemode, boolean isDebug,
			boolean isFlat, boolean copyMetaData) {
		this.dimension = world.getEnvironment().getNamespacedKey().toString();
		this.worldName = new NamespacedKey(world.getName()).toString();
		this.hashedSeed = hashedSeed;
		this.gamemode = gamemode;
		this.isDebug = isDebug;
		this.isFlat = isFlat;
		this.copyMetaData = copyMetaData;
	}

	public String getDimension() {
		return dimension;
	}

	public String getWorldName() {
		return worldName;
	}

	public long getHashedSeed() {
		return hashedSeed;
	}

	public GameMode getGamemode() {
		return gamemode;
	}

	public boolean isDebug() {
		return isDebug;
	}

	public boolean isFlat() {
		return isFlat;
	}

	public boolean isCopyMetaData() {
		return copyMetaData;
	}

	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		DataTypeIO.writeString(output, dimension, StandardCharsets.UTF_8);
		DataTypeIO.writeString(output, worldName, StandardCharsets.UTF_8);
		output.writeLong(hashedSeed);
        output.writeByte((byte) gamemode.getId());
		output.writeByte((byte) gamemode.getId());
		output.writeBoolean(isDebug);
		output.writeBoolean(isFlat);
		output.writeBoolean(copyMetaData);
		
		return buffer.toByteArray();
	}

}
