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

package com.loohp.limbo.player;

import com.loohp.limbo.location.Location;
import com.loohp.limbo.utils.GameMode;

@Deprecated
public class Unsafe {
	
	private Unsafe() {}
	
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
