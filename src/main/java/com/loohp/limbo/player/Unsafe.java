package com.loohp.limbo.player;

import com.loohp.limbo.location.Location;
import com.loohp.limbo.utils.GameMode;

@Deprecated
public class Unsafe {

    private Unsafe() {
    }

    @Deprecated
    public void a(Player a, GameMode b) {
        a.gamemode = b;
    }

    @Deprecated
    public void a(Player a, int b) {
        a.setEntityId(b);
    }

    @Deprecated
    public void a(Player a, Location b) {
        a.setLocation(b);
    }

    @Deprecated
    public void a(Player a, byte b) {
        a.selectedSlot = b;
    }

}
