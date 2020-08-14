package com.loohp.limbo;

import java.lang.reflect.Constructor;

import com.loohp.limbo.Player.Player;
import com.loohp.limbo.Utils.GameMode;

@Deprecated
public class Unsafe {
	
	private com.loohp.limbo.Player.Unsafe playerUnsafe;
	
	protected Unsafe() {
		try {
			Constructor<com.loohp.limbo.Player.Unsafe> playerConstructor = com.loohp.limbo.Player.Unsafe.class.getDeclaredConstructor();
			playerConstructor.setAccessible(true);
			playerUnsafe = playerConstructor.newInstance();
			playerConstructor.setAccessible(false);
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

}
