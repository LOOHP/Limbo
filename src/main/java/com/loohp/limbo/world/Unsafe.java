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

package com.loohp.limbo.world;

import com.loohp.limbo.entity.DataWatcher;
import com.loohp.limbo.entity.Entity;

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
