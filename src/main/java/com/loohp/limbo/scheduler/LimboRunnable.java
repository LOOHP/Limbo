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

package com.loohp.limbo.scheduler;

import com.loohp.limbo.Limbo;
import com.loohp.limbo.plugins.LimboPlugin;

public abstract class LimboRunnable implements LimboTask {
	
	private volatile boolean registered = false;
	protected volatile int taskId = -1;
	
	public void cancel() {
		synchronized (this) {
			if (registered && taskId >= 0) {
				Limbo.getInstance().getScheduler().cancelTask(taskId);
			}
		}
	}
	
	public int getTaskId() {
		if (registered && taskId >= 0) {
			return taskId;
		} else {
			throw new IllegalStateException("LimboRunnable not yet scheduled");
		}
	}
	
	public LimboRunnable runTask(LimboPlugin plugin) {
		synchronized (this) {
			if (!registered) {
				taskId = Limbo.getInstance().getScheduler().runTask(plugin, this);
				registered = true;
				return this;
			} else {
				throw new IllegalStateException("LimboRunnable already scheduled");
			}
		}
	}
	
	public LimboRunnable runTaskLater(LimboPlugin plugin, long delay) {
		synchronized (this) {
			if (!registered) {
				taskId = Limbo.getInstance().getScheduler().runTaskLater(plugin, this, delay);
				registered = true;
				return this;
			} else {
				throw new IllegalStateException("LimboRunnable already scheduled");
			}
		}
	}
	
	public LimboRunnable runTaskAsync(LimboPlugin plugin) {
		synchronized (this) {
			if (!registered) {
				taskId = Limbo.getInstance().getScheduler().runTaskAsync(plugin, this);
				registered = true;
				return this;
			} else {
				throw new IllegalStateException("LimboRunnable already scheduled");
			}
		}
	}
	
	public LimboRunnable runTaskLaterAsync(LimboPlugin plugin, long delay) {
		synchronized (this) {
			if (!registered) {
				taskId = Limbo.getInstance().getScheduler().runTaskLaterAsync(plugin, this, delay);
				registered = true;
				return this;
			} else {
				throw new IllegalStateException("LimboRunnable already scheduled");
			}
		}
	}
	
	public LimboRunnable runTaskTimer(LimboPlugin plugin, long delay, long period) {
		synchronized (this) {
			if (!registered) {
				taskId = Limbo.getInstance().getScheduler().runTaskTimer(plugin, this, delay, period);
				registered = true;
				return this;
			} else {
				throw new IllegalStateException("LimboRunnable already scheduled");
			}
		}
	}
	
	public LimboRunnable runTaskTimerAsync(LimboPlugin plugin, long delay, long period) {
		synchronized (this) {
			if (!registered) {
				taskId = Limbo.getInstance().getScheduler().runTaskTimerAsync(plugin, this, delay, period);
				registered = true;
				return this;
			} else {
				throw new IllegalStateException("LimboRunnable already scheduled");
			}
		}
	}

}
