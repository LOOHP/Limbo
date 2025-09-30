/*
 * This file is part of Limbo.
 *
 * Copyright (C) 2025. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2025. Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.loohp.limbo.location;

import com.loohp.limbo.world.BlockPosition;
import com.loohp.limbo.world.World;

public class GlobalPos {

    private final World world;
    private final BlockPosition pos;

    public GlobalPos(World world, BlockPosition pos) {
        this.world = world;
        this.pos = pos;
    }

    public World getWorld() {
        return world;
    }

    public BlockPosition getPos() {
        return pos;
    }

    public static GlobalPos from(Location location) {
        return new GlobalPos(location.getWorld(), BlockPosition.from(location));
    }
}
