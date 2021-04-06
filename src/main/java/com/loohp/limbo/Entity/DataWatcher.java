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
	
	private Entity entity;
	private Map<Field, WatchableObject> values;
	
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
	public static @interface WatchableField {
		int MetadataIndex();
		WatchableObjectType WatchableObjectType();
		boolean IsOptional() default false;
		boolean IsBitmask() default false;
		int Bitmask() default 0x00;
	}
	
	public static enum WatchableObjectType {
		BYTE(0), 
		VARINT(1, 17), 
		FLOAT(2), 
		STRING(3), 
		CHAT(4, 5), 
		SLOT(6), 
		BOOLEAN(7), 
		ROTATION(8), 
		POSITION(9, 10), 
		DIRECTION(11), 
		UUID(-1, 12), 
		BLOCKID(-1, 13), 
		NBT(14), 
		PARTICLE(15), 
		VILLAGER_DATA(16), 
		POSE(18);
		
		int typeId;
		int optionalTypeId;
		
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
