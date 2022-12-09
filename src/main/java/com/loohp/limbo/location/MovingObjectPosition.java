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

import com.loohp.limbo.entity.Entity;

public abstract class MovingObjectPosition {

    protected final Vector location;

    protected MovingObjectPosition(Vector vec3d) {
        this.location = vec3d;
    }

    public double distanceTo(Entity entity) {
        double d0 = this.location.x - entity.getX();
        double d1 = this.location.y - entity.getY();
        double d2 = this.location.z - entity.getZ();

        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public abstract MovingObjectPosition.EnumMovingObjectType getType();

    public Vector getLocation() {
        return this.location;
    }

    public enum EnumMovingObjectType {

        MISS, BLOCK, ENTITY;

    }
}