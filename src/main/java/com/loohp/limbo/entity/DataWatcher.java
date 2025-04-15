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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DataWatcher {
	
	private final Entity entity;
	private final Map<Field, WatchableObject> values;
	
	public DataWatcher(Entity entity) {
		this.entity = entity;
		this.values = new HashMap<>();
		
		Class<?> clazz = entity.getClass();
		while (clazz != null) {
			for (Field field : clazz.getDeclaredFields()) {
				WatchableField a = field.getAnnotation(WatchableField.class);
				if (a != null) {
					field.setAccessible(true);
					try {
						values.put(field, new WatchableObject(field.get(entity), a.MetadataIndex(), a.WatchableObjectType(), a.IsOptional(), a.IsBitmask(), a.Bitmask()));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
			clazz = clazz.getSuperclass();
		}
	}
	
	public Entity getEntity() {
		return entity;
	}
 	
	public boolean isValid() {
		return entity.isValid();
	}
	
	public synchronized Map<Field, WatchableObject> update() throws IllegalArgumentException, IllegalAccessException {
		if (!isValid()) {
			return null;
		}
		Map<Field, WatchableObject> updated = new HashMap<>();
		for (Entry<Field, WatchableObject> entry : values.entrySet()) {
			Field field = entry.getKey();
			WatchableObject watchableObj = entry.getValue();
			field.setAccessible(true);
			Object newValue = field.get(entity);
			if ((newValue == null && watchableObj.getValue() != null) || (newValue != null && watchableObj.getValue() == null) || (newValue != null && watchableObj.getValue() != null && !newValue.equals(watchableObj.getValue()))) {
				watchableObj.setValue(newValue);
				updated.put(field, watchableObj);
			}
		}
		return updated;
	}
	
	public Map<Field, WatchableObject> getWatchableObjects() {
		return Collections.unmodifiableMap(values);
	}
	
	public static class WatchableObject {
		
		private int index;
		private WatchableObjectType type;
		private boolean optional;
		private boolean isBitmask;
		private int bitmask;
		
		private Object value;
		
		public WatchableObject(Object value, int index, WatchableObjectType type, boolean optional, boolean isBitmask, int bitmask) {
			this.index = index;
			this.type = type;
			this.optional = optional;
			this.isBitmask = isBitmask;
			this.bitmask = bitmask;
			this.value = value;
		}
		
		public WatchableObject(Object value, int index, WatchableObjectType type, boolean isBitmask, int bitmask) {
			this(value, index, type, false, isBitmask, bitmask);
		}
		
		public WatchableObject(Object value, int index, WatchableObjectType type, boolean optional) {
			this(value, index, type, optional, false, 0x00);
		}
		
		public WatchableObject(Object value, int index, WatchableObjectType type) {
			this(value, index, type, false, false, 0x00);
		}
		
		public Object getValue() {
			return value;
		}
		
		public void setValue(Object newValue) {
			this.value = newValue;
		}

		public int getIndex() {
			return index;
		}

		public WatchableObjectType getType() {
			return type;
		}

		public boolean isOptional() {
			return optional;
		}

		public boolean isBitmask() {
			return isBitmask;
		}

		public int getBitmask() {
			return bitmask;
		}
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface WatchableField {
		int MetadataIndex();
		WatchableObjectType WatchableObjectType();
		boolean IsOptional() default false;
		boolean IsBitmask() default false;
		int Bitmask() default 0x00;
	}
	
	public enum WatchableObjectType {
		BYTE(0), 
		VARINT(1, 20),
		VARLONG(2, 17),
		FLOAT(3),
		STRING(4),
		CHAT(5, 6),
		SLOT(7),
		BOOLEAN(8),
		ROTATION(9),
		POSITION(10, 11),
		DIRECTION(12),
		UUID(-1, 13),
		BLOCKID(14, 15),
		NBT(16),
		PARTICLE(17),
		PARTICLES(18),
		VILLAGER_DATA(19),
		POSE(21),
		CAT_VARIANT(22),
		WOLF_VARIANT(23),
		FROG_VARIANT(24),
		GLOBAL_POSITION(-1, 25),
		PAINTING_VARIANT(26),
		SNIFFER_STATE(27),
		ARMADILLO_STATE(28),
		VECTOR3(29),
		QUATERNION(30);
		
		private final int typeId;
		private final int optionalTypeId;
		
		WatchableObjectType(int typeId, int optionalTypeId) {
			this.typeId = typeId;
			this.optionalTypeId = optionalTypeId;
		}
		
		WatchableObjectType(int typeId) {
			this(typeId, -1);
		}
		
		public int getTypeId() {
			return typeId;
		}
		
		public int getOptionalTypeId() {
			return optionalTypeId;
		}
	}
	
}
