package com.loohp.limbo.world;

import com.loohp.limbo.Limbo;
import com.loohp.limbo.entity.ArmorStand;
import com.loohp.limbo.entity.DataWatcher;
import com.loohp.limbo.entity.DataWatcher.WatchableObject;
import com.loohp.limbo.entity.Entity;
import com.loohp.limbo.entity.EntityType;
import com.loohp.limbo.location.Location;
import com.loohp.limbo.player.Player;
import com.loohp.limbo.server.packets.PacketPlayOutEntityDestroy;
import com.loohp.limbo.server.packets.PacketPlayOutEntityMetadata;
import com.loohp.limbo.utils.SchematicConvertionUtils;
import net.querz.mca.Chunk;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class World {

    private final String name;
    private final Environment environment;
    private final Chunk[][] chunks;
    private final int width;
    private final int length;
    private final LightEngineBlock lightEngineBlock;
    private LightEngineSky lightEngineSky;
    private final Map<Entity, DataWatcher> entities;

    public World(String name, int width, int length, Environment environment) {
        this.name = name;
        this.environment = environment;
        this.chunks = new Chunk[(width >> 4) + 1][(length >> 4) + 1];
        this.width = width;
        this.length = length;

        for (int x = 0; x < chunks.length; x++) {
            for (int z = 0; z < chunks[x].length; z++) {
                chunks[x][z] = Chunk.newChunk();
                Chunk chunk = chunks[x][z];
                chunk.cleanupPalettesAndBlockStates();
                CompoundTag heightMap = new CompoundTag();
                heightMap.putLongArray("MOTION_BLOCKING",
                        new long[]{1371773531765642314L, 1389823183635651148L, 1371738278539598925L,
                                1389823183635388492L, 1353688558756731469L, 1389823114781694027L, 1317765589597723213L,
                                1371773531899860042L, 1389823183635651149L, 1371773462911685197L, 1389823183635650636L,
                                1353688626805119565L, 1371773531900123211L, 1335639250618849869L, 1371738278674077258L,
                                1389823114781694028L, 1353723811310638154L, 1371738278674077259L, 1335674228429068364L,
                                1335674228429067338L, 1335674228698027594L, 1317624576693539402L, 1335709481520370249L,
                                1299610178184057417L, 1335638906349064264L, 1299574993811968586L, 1299574924958011464L,
                                1299610178184056904L, 1299574924958011464L, 1299610109330100296L, 1299574924958011464L,
                                1299574924823793736L, 1299574924958011465L, 1281525273222484040L, 1299574924958011464L,
                                1281525273222484040L, 9548107335L});
                chunk.setHeightMaps(heightMap);
                chunk.setBiomes(new int[256]);
                chunk.setTileEntities(new ListTag<>(CompoundTag.class));
            }
        }

        this.lightEngineBlock = new LightEngineBlock(this);
        if (environment.hasSkyLight()) {
            this.lightEngineSky = new LightEngineSky(this);
        }

        this.entities = new LinkedHashMap<>();
    }

    public LightEngineBlock getLightEngineBlock() {
        return lightEngineBlock;
    }

    public LightEngineSky getLightEngineSky() {
        return lightEngineSky;
    }

    public boolean hasSkyLight() {
        return lightEngineSky != null;
    }

    protected void setBlock(int x, int y, int z, String blockdata) {
        Chunk chunk = this.chunks[(x >> 4)][(z >> 4)];
        if (chunk == null) {
            chunk = Chunk.newChunk();
            this.chunks[(x >> 4)][(z >> 4)] = chunk;
        }
        CompoundTag block = SchematicConvertionUtils.toBlockTag(blockdata);
        chunk.setBlockStateAt(x, y, z, block, false);
    }

    public BlockState getBlock(int x, int y, int z) {
        Chunk chunk = this.chunks[(x >> 4)][(z >> 4)];
        if (chunk == null) {
            chunk = Chunk.newChunk();
            this.chunks[(x >> 4)][(z >> 4)] = chunk;
        }

        CompoundTag tag = chunk.getBlockStateAt(x, y, z);
        if (tag == null) {
            tag = new CompoundTag();
            tag.putString("Name", "minecraft:air");
        }
        return new BlockState(tag);
    }

    public void setBlock(int x, int y, int z, BlockState state) {
        Chunk chunk = this.chunks[(x >> 4)][(z >> 4)];
        if (chunk == null) {
            chunk = Chunk.newChunk();
            this.chunks[(x >> 4)][(z >> 4)] = chunk;
        }
        chunk.setBlockStateAt(x % 16, y % 16, z % 16, state.toCompoundTag(), false);
    }

    public Chunk[][] getChunks() {
        return this.chunks;
    }

    public Chunk getChunkAtWorldPos(int x, int z) {
        return this.chunks[(x >> 4)][(z >> 4)];
    }

    public Chunk getChunkAt(int x, int z) {
        if (x < 0 || z < 0 || x >= chunks.length || z >= chunks[x].length) {
            return null;
        }
        return this.chunks[x][z];
    }

    public int getChunkX(Chunk chunk) {
        for (int x = 0; x < chunks.length; x++) {
            for (int z = 0; z < chunks[x].length; z++) {
                Chunk c = getChunkAt(x, z);
                if (c.equals(chunk)) {
                    return x;
                }
            }
        }
        return Integer.MIN_VALUE;
    }

    public int getChunkZ(Chunk chunk) {
        for (int x = 0; x < chunks.length; x++) {
            for (int z = 0; z < chunks[x].length; z++) {
                Chunk c = getChunkAt(x, z);
                if (c.equals(chunk)) {
                    return z;
                }
            }
        }
        return Integer.MIN_VALUE;
    }

    public int[] getChunkXZ(Chunk chunk) {
        for (int x = 0; x < chunks.length; x++) {
            for (int z = 0; z < chunks[x].length; z++) {
                Chunk c = getChunkAt(x, z);
                if (c.equals(chunk)) {
                    return new int[]{x, z};
                }
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public int getChunkWidth() {
        return (width >> 4) + 1;
    }

    public int getChunkLength() {
        return (length >> 4) + 1;
    }

    public Set<Entity> getEntities() {
        return Collections.unmodifiableSet(entities.keySet());
    }

    public Entity spawnEntity(EntityType type, Location location) {
        if (!location.getWorld().equals(this)) {
            throw new IllegalArgumentException("Location not in world.");
        }
        Entity entity;
        switch (type) {
            case ARMOR_STAND:
                entity = new ArmorStand(location);
                break;
            default:
                throw new UnsupportedOperationException("This EntityType cannot be summoned.");
        }
        entities.put(entity, new DataWatcher(entity));
        return entity;
    }

    public Entity addEntity(Entity entity) {
        if (entity.getWorld().equals(this)) {
            entities.put(entity, new DataWatcher(entity));
        } else {
            throw new IllegalArgumentException("Location not in world.");
        }
        return entity;
    }

    public List<Player> getPlayers() {
        return Limbo.getInstance().getPlayers().stream().filter(each -> each.getWorld().equals(this)).collect(Collectors.toList());
    }

    protected void removeEntity(Entity entity) {
        entities.remove(entity);
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entity.getEntityId());
        for (Player player : getPlayers()) {
            try {
                player.clientConnection.sendPacket(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected DataWatcher getDataWatcher(Entity entity) {
        return entities.get(entity);
    }

    public void update() throws IllegalArgumentException, IllegalAccessException {
        for (DataWatcher watcher : entities.values()) {
            if (watcher.getEntity().getWorld().equals(this)) {
                Map<Field, WatchableObject> updated = watcher.update();
                PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(watcher.getEntity(), false, updated.keySet().toArray(new Field[0]));
                for (Player player : getPlayers()) {
                    try {
                        player.clientConnection.sendPacket(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(watcher.getEntity().getEntityId());
                for (Player player : getPlayers()) {
                    try {
                        player.clientConnection.sendPacket(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                entities.remove(watcher.getEntity());
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.deepHashCode(chunks);
        result = prime * result + ((environment == null) ? 0 : environment.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        World other = (World) obj;
        if (!Arrays.deepEquals(chunks, other.chunks)) {
            return false;
        }
        if (environment != other.environment) {
            return false;
        }
        if (name == null) {
            return other.name == null;
        } else return name.equals(other.name);
    }
}
