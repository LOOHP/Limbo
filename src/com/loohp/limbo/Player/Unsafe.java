package com.loohp.limbo.Player;

import com.loohp.limbo.Utils.GameMode;

@Deprecated
public class Unsafe {
	
	private Unsafe() {}
	
	@Deprecated
	public void a(Player a, GameMode b) {
		a.gamemode = b;
	}
	
	@Deprecated
	public void a(Player a, int b) {
		a.entityId = b;
	}
	
}
