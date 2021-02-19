package com.loohp.limbo.World;

import com.loohp.limbo.Entity.DataWatcher;
import com.loohp.limbo.Entity.Entity;

@Deprecated
public class Unsafe {
	
	private Unsafe() {}
	
	@Deprecated
	public void a(World a, Entity b) {
		a.removeEntity(b);
	}
	
	@Deprecated
	public DataWatcher b(World a, Entity b) {
		return a.getDataWatcher(b);
	}
	
}
