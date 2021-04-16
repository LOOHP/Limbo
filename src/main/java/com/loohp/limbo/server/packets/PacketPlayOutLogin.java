package com.loohp.limbo.server.packets;

import com.loohp.limbo.utils.DataTypeIO;
import com.loohp.limbo.utils.GameMode;
import com.loohp.limbo.utils.NamespacedKey;
import com.loohp.limbo.world.Environment;
import com.loohp.limbo.world.World;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PacketPlayOutLogin extends PacketOut {

    private final int entityId;
    private final boolean isHardcore;
    private final GameMode gamemode;
    private final String[] worldsNames;
    private final CompoundTag dimensionCodec;
    private final Environment dimension;
    private final String worldName;
    private final long hashedSeed;
    private final byte maxPlayers;
    private final int viewDistance;
    private final boolean reducedDebugInfo;
    private final boolean enableRespawnScreen;
    private final boolean isDebug;
    private final boolean isFlat;

    public PacketPlayOutLogin(int entityId, boolean isHardcore, GameMode gamemode,
                              String[] worldsNames, CompoundTag dimensionCodec, World world, long hashedSeed,
                              byte maxPlayers, int viewDistance, boolean reducedDebugInfo, boolean enableRespawnScreen, boolean isDebug,
                              boolean isFlat) {
        this.entityId = entityId;
        this.isHardcore = isHardcore;
        this.gamemode = gamemode;
        this.worldsNames = worldsNames;
        this.dimensionCodec = dimensionCodec;
        this.dimension = world.getEnvironment();
        this.worldName = new NamespacedKey(world.getName()).toString();
        this.hashedSeed = hashedSeed;
        this.maxPlayers = maxPlayers;
        this.viewDistance = viewDistance;
        this.reducedDebugInfo = reducedDebugInfo;
        this.enableRespawnScreen = enableRespawnScreen;
        this.isDebug = isDebug;
        this.isFlat = isFlat;
    }

    public int getEntityId() {
        return entityId;
    }

    public boolean isHardcore() {
        return isHardcore;
    }

    public GameMode getGamemode() {
        return gamemode;
    }

    public String[] getWorldsNames() {
        return worldsNames;
    }

    public CompoundTag getDimensionCodec() {
        return dimensionCodec;
    }

    public Environment getDimension() {
        return dimension;
    }

    public String getWorldName() {
        return worldName;
    }

    public long getHashedSeed() {
        return hashedSeed;
    }

    public byte getMaxPlayers() {
        return maxPlayers;
    }

    public int getViewDistance() {
        return viewDistance;
    }

    public boolean isReducedDebugInfo() {
        return reducedDebugInfo;
    }

    public boolean isEnableRespawnScreen() {
        return enableRespawnScreen;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public boolean isFlat() {
        return isFlat;
    }

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(Packet.getPlayOut().get(getClass()));
        output.writeInt(entityId);
        output.writeBoolean(isHardcore);
        output.writeByte((byte) gamemode.getId());
        output.writeByte((byte) gamemode.getId());
        DataTypeIO.writeVarInt(output, worldsNames.length);
        for (int u = 0; u < worldsNames.length; u++) {
            DataTypeIO.writeString(output, worldsNames[u], StandardCharsets.UTF_8);
        }
        DataTypeIO.writeCompoundTag(output, dimensionCodec);
        CompoundTag tag = null;
        ListTag<CompoundTag> list = dimensionCodec.getCompoundTag("minecraft:dimension_type").getListTag("value").asCompoundTagList();
        for (CompoundTag each : list) {
            if (each.getString("name").equals(dimension.getNamespacedKey().toString())) {
                tag = each.getCompoundTag("element");
                break;
            }
        }
        DataTypeIO.writeCompoundTag(output, tag != null ? tag : list.get(0));
        DataTypeIO.writeString(output, worldName, StandardCharsets.UTF_8);
        output.writeLong(hashedSeed);
        DataTypeIO.writeVarInt(output, maxPlayers);
        DataTypeIO.writeVarInt(output, viewDistance);
        output.writeBoolean(reducedDebugInfo);
        output.writeBoolean(enableRespawnScreen);
        output.writeBoolean(isDebug);
        output.writeBoolean(isFlat);

        return buffer.toByteArray();
    }

}
