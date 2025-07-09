/*
 * This file is part of Limbo.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
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

public class MovingObjectPositionBlock extends MovingObjectPosition {

    private final BlockFace direction;
    private final BlockPosition blockPos;
    private final boolean miss;
    private final boolean inside;
    private final boolean worldBorderHit;

    public static MovingObjectPositionBlock miss(Vector vec3d, BlockFace direction, BlockPosition blockposition) {
        return new MovingObjectPositionBlock(true, vec3d, direction, blockposition, false, false);
    }

    public MovingObjectPositionBlock(Vector vec3d, BlockFace direction, BlockPosition blockposition, boolean flag) {
        this(false, vec3d, direction, blockposition, flag, false);
    }

    public MovingObjectPositionBlock(Vector vec3d, BlockFace direction, BlockPosition blockposition, boolean flag, boolean flag1) {
        this(false, vec3d, direction, blockposition, flag, flag1);
    }

    private MovingObjectPositionBlock(boolean flag, Vector vec3d, BlockFace direction, BlockPosition blockposition, boolean flag1, boolean flag2) {
        super(vec3d);
        this.miss = flag;
        this.direction = direction;
        this.blockPos = blockposition;
        this.inside = flag1;
        this.worldBorderHit = flag2;
    }

    public MovingObjectPositionBlock withDirection(BlockFace direction) {
        return new MovingObjectPositionBlock(this.miss, this.location, direction, this.blockPos, this.inside, this.worldBorderHit);
    }

    public MovingObjectPositionBlock withPosition(BlockPosition blockposition) {
        return new MovingObjectPositionBlock(this.miss, this.location, this.direction, blockposition, this.inside, this.worldBorderHit);
    }

    public MovingObjectPositionBlock hitBorder() {
        return new MovingObjectPositionBlock(this.miss, this.location, this.direction, this.blockPos, this.inside, true);
    }

    public BlockPosition getBlockPos() {
        return this.blockPos;
    }

    public BlockFace getDirection() {
        return this.direction;
    }

    @Override
    public MovingObjectPosition.EnumMovingObjectType getType() {
        return this.miss ? MovingObjectPosition.EnumMovingObjectType.MISS : MovingObjectPosition.EnumMovingObjectType.BLOCK;
    }

    public boolean isInside() {
        return this.inside;
    }

    public boolean isWorldBorderHit() {
        return worldBorderHit;
    }
}
