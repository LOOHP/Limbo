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

package com.loohp.limbo.entity;

public enum Pose {
	
	STANDING(0),
	FALL_FLYING(1),
	SLEEPING(2),
	SWIMMING(3),
	SPIN_ATTACK(4),
	SNEAKING(5),
	DYING(6);
	
	private static final Pose[] VALUES = values();
	
	private int id;
	
	Pose(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public static Pose fromId(int id) {
		for (Pose pose : VALUES) {
			if (id == pose.id) {
				return pose;
			}
		}
		return null;
	}

}
