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

package com.loohp.limbo.inventory;

public enum EquipmentSlot {
	
	MAINHAND,
	OFFHAND,
	HELMET,
	CHESTPLATE,
	LEGGINGS,
	BOOTS;

	public boolean isHandSlot() {
		switch (this) {
			case MAINHAND:
			case OFFHAND:
				return true;
		}
		return false;
	}

	public boolean isArmorSlot() {
		switch (this) {
			case HELMET:
			case CHESTPLATE:
			case LEGGINGS:
			case BOOTS:
				return true;
		}
		return false;
	}

}
