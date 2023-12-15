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

package com.loohp.limbo.network.protocol.packets;

import com.loohp.limbo.entity.DataWatcher.WatchableObject;
import com.loohp.limbo.entity.DataWatcher.WatchableObjectType;
import com.loohp.limbo.entity.Entity;
import com.loohp.limbo.entity.Pose;
import com.loohp.limbo.utils.DataTypeIO;
import com.loohp.limbo.utils.Rotation3f;
import com.loohp.limbo.world.BlockPosition;
import net.kyori.adventure.text.Component;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class PacketPlayOutEntityMetadata extends PacketOut {
	
	public static final int END_OFF_METADATA = 0xff;
	
	private Entity entity;
	public boolean allFields;
	public Field[] fields;
	
	public PacketPlayOutEntityMetadata(Entity entity, boolean allFields, Field... fields) {
		this.entity = entity;
		this.allFields = allFields;
		this.fields = fields;
	}
	
	public PacketPlayOutEntityMetadata(Entity entity) {
		this(entity, true);
	}

	public Entity getEntity() {
		return entity;
	}

	@Override
	public byte[] serializePacket() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		DataOutputStream output = new DataOutputStream(buffer);
		output.writeByte(Packet.getPlayOut().get(getClass()));
		DataTypeIO.writeVarInt(output, entity.getEntityId());
		Collection<WatchableObject> watches;
		if (allFields) {
			watches = new HashSet<>(entity.getDataWatcher().getWatchableObjects().values());
		} else {
			watches = new HashSet<>();
			Map<Field, WatchableObject> entries = entity.getDataWatcher().getWatchableObjects();
			for (Field field : fields) {
				WatchableObject watch = entries.get(field);
				if (watch != null) {
					watches.add(watch);
				}
			}
		}
		
		Map<Integer, Integer> bitmasks = new HashMap<>();
		Iterator<WatchableObject> itr = watches.iterator();
		while (itr.hasNext()) {
			WatchableObject watch = itr.next();
			if (watch.isBitmask()) {
				itr.remove();
				Integer bitmask = bitmasks.get(watch.getIndex());
				if (bitmask == null) {
					bitmask = 0;
				}
				if ((boolean) watch.getValue()) {
					bitmask |= watch.getBitmask();
				} else {
					bitmask &= ~watch.getBitmask();
				}
				bitmasks.put(watch.getIndex(), bitmask);
			}
		}
		for (Entry<Integer, Integer> entry : bitmasks.entrySet()) {
			watches.add(new WatchableObject(entry.getValue().byteValue(), entry.getKey(), WatchableObjectType.BYTE));
		}
		
		for (WatchableObject watch : watches) {
			output.writeByte(watch.getIndex());
			if (watch.isOptional()) {
				DataTypeIO.writeVarInt(output, watch.getType().getOptionalTypeId());
				output.writeBoolean(watch.getValue() != null);
			} else {
				DataTypeIO.writeVarInt(output, watch.getType().getTypeId());
			}
			if (!watch.isOptional() || watch.getValue() != null) {
				switch (watch.getType()) {
				//case BLOCKID:
				//	break;
				case POSITION:
					DataTypeIO.writeBlockPosition(output, (BlockPosition) watch.getValue());
					break;
				case BOOLEAN:
					output.writeBoolean((boolean) watch.getValue());
					break;
				case BYTE:
					output.writeByte((byte) watch.getValue());
					break;
				case CHAT:
					DataTypeIO.writeComponent(output, (Component) watch.getValue());
					break;
				//case DIRECTION:
				//	break;
				case FLOAT:
					output.writeFloat((float) watch.getValue());
					break;
				//case NBT:
				//	break;
				//case PARTICLE:
				//	break;
				case POSE:
					DataTypeIO.writeVarInt(output, ((Pose) watch.getValue()).getId());
					break;
				case ROTATION:
					Rotation3f rotation = (Rotation3f) watch.getValue();
					output.writeFloat((float) rotation.getX());
					output.writeFloat((float) rotation.getY());
					output.writeFloat((float) rotation.getZ());
					break;
				//case SLOT:
				//	break;
				case STRING:
					DataTypeIO.writeString(output, watch.getValue().toString(), StandardCharsets.UTF_8);
					break;
				case UUID:
					DataTypeIO.writeUUID(output, (UUID) watch.getValue());
					break;
				case VARINT:
					DataTypeIO.writeVarInt(output, (int) watch.getValue());
					break;
				//case VILLAGER_DATA:
				//	break;
				default:
					break;
				}
			}
		}
		output.writeByte(END_OFF_METADATA);
		
		return buffer.toByteArray();
	}

}
