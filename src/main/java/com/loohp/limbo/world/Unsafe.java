package com.loohp.limbo.world;

import com.loohp.limbo.entity.DataWatcher;
import com.loohp.limbo.entity.Entity;

@Deprecated
public class Unsafe {

    private Unsafe() {
    }

    @Deprecated
    public void a(World a, Entity b) {
        a.removeEntity(b);
    }

    @Deprecated
    public DataWatcher b(World a, Entity b) {
        return a.getDataWatcher(b);
    }

}
