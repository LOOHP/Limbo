package com.loohp.limbo;

import java.lang.reflect.Constructor;

import com.loohp.limbo.Entity.DataWatcher;
import com.loohp.limbo.Entity.Entity;
import com.loohp.limbo.Location.Location;
import com.loohp.limbo.Player.Player;
import com.loohp.limbo.Utils.GameMode;
import com.loohp.limbo.World.World;

@Deprecated
public class Unsafe {
	
	private com.loohp.limbo.Player.Unsafe playerUnsafe;
	private com.loohp.limbo.World.Unsafe worldUnsafe;
	
	protected Unsafe() {
		try {
			Constructor<com.loohp.limbo.Player.Unsafe> playerConstructor = com.loohp.limbo.Player.Unsafe.class.getDeclaredConstructor();
			playerConstructor.setAccessible(true);
			playerUnsafe = playerConstructor.newInstance();
			playerConstructor.setAccessible(false);
			
			Constructor<com.loohp.limbo.World.Unsafe> worldConstructor = com.loohp.limbo.World.Unsafe.class.getDeclaredConstructor();
			worldConstructor.setAccessible(true);
			worldUnsafe = worldConstructor.newInstance();
			worldConstructor.setAccessible(false);
		} catch (Exception e) {e.printStackTrace();}
	}
	
	@Deprecated
	public void setPlayerGameModeSilently(Player player, GameMode mode) {
		playerUnsafe.a(player, mode);
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
