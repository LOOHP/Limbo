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

package com.loohp.limbo.sounds;

import com.loohp.limbo.utils.NamespacedKey;

import java.util.Optional;

public class SoundEffect {

    public static SoundEffect createVariableRangeEvent(NamespacedKey namespacedKey) {
        return new SoundEffect(namespacedKey, 16.0F, false);
    }

    public static SoundEffect createFixedRangeEvent(NamespacedKey namespacedKey, float range) {
        return new SoundEffect(namespacedKey, range, true);
    }

    private final NamespacedKey sound;
    private final float range;
    private final boolean newSystem;

    private SoundEffect(NamespacedKey sound, float range, boolean newSystem) {
        this.sound = sound;
        this.range = range;
        this.newSystem = newSystem;
    }

    public NamespacedKey getSound() {
        return sound;
    }

    public float getRange() {
        return range;
    }

    public boolean isNewSystem() {
        return newSystem;
    }

    public Optional<Float> fixedRange() {
        return this.newSystem ? Optional.of(this.range) : Optional.empty();
    }
}
