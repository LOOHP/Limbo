package com.loohp.limbo;

import com.loohp.limbo.entity.DataWatcher;
import com.loohp.limbo.entity.Entity;
import com.loohp.limbo.location.Location;
import com.loohp.limbo.player.Player;
import com.loohp.limbo.utils.GameMode;
import com.loohp.limbo.world.World;

import java.lang.reflect.Constructor;

@Deprecated
public class Unsafe {

    private com.loohp.limbo.player.Unsafe playerUnsafe;
    private com.loohp.limbo.world.Unsafe worldUnsafe;

    protected Unsafe() {
        try {
            Constructor<com.loohp.limbo.player.Unsafe> playerConstructor = com.loohp.limbo.player.Unsafe.class.getDeclaredConstructor();
            playerConstructor.setAccessible(true);
            playerUnsafe = playerConstructor.newInstance();
            playerConstructor.setAccessible(false);

            Constructor<com.loohp.limbo.world.Unsafe> worldConstructor = com.loohp.limbo.world.Unsafe.class.getDeclaredConstructor();
            worldConstructor.setAccessible(true);
            worldUnsafe = worldConstructor.newInstance();
            worldConstructor.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public void setPlayerGameModeSilently(Player player, GameMode mode) {
        playerUnsafe.a(player, mode);
    }

    @Deprecated
    public void setSelectedSlotSilently(Player player, byte slot) {
        playerUnsafe.a(player, slot);
    }

    @Deprecated
    public void setPlayerEntityId(Player player, int entityId) {
        playerUnsafe.a(player, entityId);
    }

    @Deprecated
    public void removeEntity(World world, Entity entity) {
        worldUnsafe.a(world, entity);
    }

    @Deprecated
    public DataWatcher getDataWatcher(World world, Entity entity) {
        return worldUnsafe.b(world, entity);
    }

    @Deprecated
    public void setPlayerLocationSilently(Player player, Location location) {
        playerUnsafe.a(player, location);
    }

}
