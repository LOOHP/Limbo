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

package com.loohp.limbo.utils;

import com.google.gson.JsonElement;
import com.loohp.limbo.inventory.ItemStack;
import com.loohp.limbo.location.BlockFace;
import com.loohp.limbo.location.MovingObjectPositionBlock;
import com.loohp.limbo.location.Vector;
import com.loohp.limbo.registry.BuiltInRegistries;
import com.loohp.limbo.registry.DataComponentType;
import com.loohp.limbo.world.BlockPosition;
import com.loohp.limbo.world.ChunkPosition;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.io.NBTOutputStream;
import net.querz.nbt.tag.EndTag;
import net.querz.nbt.tag.Tag;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataTypeIO {

	public static void writeItemStack(DataOutputStream out, ItemStack itemstack) throws IOException {
		if (itemstack == null || itemstack.isSimilar(ItemStack.AIR) || itemstack.amount() == 0) {
			DataTypeIO.writeVarInt(out, 0);
		} else {
			DataTypeIO.writeVarInt(out, itemstack.amount());
			writeVarInt(out, BuiltInRegistries.ITEM_REGISTRY.getId(itemstack.type()));
			Map<Key, Tag<?>> components = itemstack.components();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			DataOutputStream componentOut = new DataOutputStream(buffer);
			int componentSize = 0;
			for (Map.Entry<Key, Tag<?>> entry : components.entrySet()) {
				Key componentKey = entry.getKey();
				int typeId = BuiltInRegistries.DATA_COMPONENT_TYPE.getId(componentKey);
				if (typeId >= 0 && DataComponentType.isKnownType(componentKey)) {
					DataTypeIO.writeVarInt(componentOut, typeId);
					DataTypeIO.writeTag(componentOut, entry.getValue());
					componentSize++;
				}
			}
			DataTypeIO.writeVarInt(out, componentSize);
			DataTypeIO.writeVarInt(out, 0);
			out.write(buffer.toByteArray());
		}
	}

	public static ItemStack readUntrustedItemStack(DataInputStream in) throws IOException {
		int amount = DataTypeIO.readVarInt(in);
		if (amount <= 0) {
			return ItemStack.AIR;
		} else {
			Key key = BuiltInRegistries.ITEM_REGISTRY.fromId(readVarInt(in));
			int size = DataTypeIO.readVarInt(in);
			int removeSize = DataTypeIO.readVarInt(in);
			Map<Key, Tag<?>> components = new HashMap<>();
			for (int i = 0; i < size; i++) {
				Key componentKey = BuiltInRegistries.DATA_COMPONENT_TYPE.fromId(DataTypeIO.readVarInt(in));
				DataTypeIO.readVarInt(in);
				Tag<?> component = readTag(in, Tag.class);
				if (componentKey != null && DataComponentType.isKnownType(componentKey)) {
					components.put(componentKey, component);
				}
			}
			for (int i = 0; i < removeSize; i++) {
				DataTypeIO.readVarInt(in);
			}
			return new ItemStack(key, amount, components);
		}
	}

    public static ItemStack readItemStack(DataInputStream in) throws IOException {
		int amount = DataTypeIO.readVarInt(in);
		if (amount <= 0) {
			return ItemStack.AIR;
		} else {
			Key key = BuiltInRegistries.ITEM_REGISTRY.fromId(readVarInt(in));
			int size = DataTypeIO.readVarInt(in);
			int removeSize = DataTypeIO.readVarInt(in);
			Map<Key, Tag<?>> components = new HashMap<>();
			for (int i = 0; i < size; i++) {
				Key componentKey = BuiltInRegistries.DATA_COMPONENT_TYPE.fromId(DataTypeIO.readVarInt(in));
				Tag<?> component = readTag(in, Tag.class);
				if (componentKey != null && DataComponentType.isKnownType(componentKey)) {
					components.put(componentKey, component);
				}
			}
			for (int i = 0; i < removeSize; i++) {
				DataTypeIO.readVarInt(in);
			}
			return new ItemStack(key, amount, components);
		}
	}

	public static void writeBlockHitResult(DataOutputStream out, MovingObjectPositionBlock movingobjectpositionblock) throws IOException {
		BlockPosition blockposition = movingobjectpositionblock.getBlockPos();

		writeBlockPosition(out, blockposition);
		writeVarInt(out, movingobjectpositionblock.getDirection().ordinal());
		Vector vector = movingobjectpositionblock.getLocation();

		out.writeFloat((float) (vector.getX() - (double) blockposition.getX()));
		out.writeFloat((float) (vector.getY() - (double) blockposition.getY()));
		out.writeFloat((float) (vector.getZ() - (double) blockposition.getZ()));
		out.writeBoolean(movingobjectpositionblock.isInside());
		out.writeBoolean(movingobjectpositionblock.isWorldBorderHit());
	}

	public static MovingObjectPositionBlock readBlockHitResult(DataInputStream in) throws IOException {
		BlockPosition blockposition = readBlockPosition(in);
		BlockFace direction = BlockFace.values()[readVarInt(in)];
		float f = in.readFloat();
		float f1 = in.readFloat();
		float f2 = in.readFloat();
		boolean flag = in.readBoolean();
		boolean flag1 = in.readBoolean();

		return new MovingObjectPositionBlock(new Vector((double) blockposition.getX() + (double) f, (double) blockposition.getY() + (double) f1, (double) blockposition.getZ() + (double) f2), direction, blockposition, flag, flag1);
	}

	public static <E extends Enum<E>> void writeEnumSet(DataOutputStream out, EnumSet<E> enumset, Class<E> oclass) throws IOException {
		E[] ae = oclass.getEnumConstants();
		BitSet bitset = new BitSet(ae.length);

		for (int i = 0; i < ae.length; ++i) {
			bitset.set(i, enumset.contains(ae[i]));
		}

		writeFixedBitSet(out, bitset, ae.length);
	}

	public static <E extends Enum<E>> EnumSet<E> readEnumSet(DataInputStream in, Class<E> oclass) throws IOException {
		E[] ae = oclass.getEnumConstants();
		BitSet bitset = readFixedBitSet(in, ae.length);
		EnumSet<E> enumset = EnumSet.noneOf(oclass);

		for (int i = 0; i < ae.length; ++i) {
			if (bitset.get(i)) {
				enumset.add(ae[i]);
			}
		}

		return enumset;
	}

	public static void writeFixedBitSet(DataOutputStream out, BitSet bitset, int i) throws IOException {
		if (bitset.length() > i) {
			int j = bitset.length();
			throw new RuntimeException("BitSet is larger than expected size (" + j + ">" + i + ")");
		} else {
			byte[] abyte = bitset.toByteArray();
			out.write(Arrays.copyOf(abyte, -Math.floorDiv(-i, 8)));
		}
	}

	public static BitSet readFixedBitSet(DataInputStream in, int i) throws IOException {
		byte[] abyte = new byte[-Math.floorDiv(-i, 8)];
		in.readFully(abyte);
		return BitSet.valueOf(abyte);
	}
	
	public static void writeBlockPosition(DataOutputStream out, BlockPosition position) throws IOException {
        out.writeLong(((position.getX() & 0x3FFFFFF) << 38) | ((position.getZ() & 0x3FFFFFF) << 12) | (position.getY() & 0xFFF));
	}

	public static BlockPosition readBlockPosition(DataInputStream in) throws IOException {
		long value = in.readLong();
		int x = (int) (value >> 38);
		int y = (int) (value << 52 >> 52);
		int z = (int) (value << 26 >> 38);
		return new BlockPosition(x, y, z);
	}
	
	public static void writeUUID(DataOutputStream out, UUID uuid) throws IOException {
		out.writeLong(uuid.getMostSignificantBits());
		out.writeLong(uuid.getLeastSignificantBits());
	}

	public static UUID readUUID(DataInputStream in) throws IOException {
		return new UUID(in.readLong(), in.readLong());
	}
	
	public static void writeTag(DataOutputStream out, Tag<?> tag) throws IOException {
		if (tag == null) {
			tag = EndTag.INSTANCE;
		}
		out.writeByte(tag.getID());
		if (tag.getID() != EndTag.ID) {
			new NBTOutputStream(out).writeRawTag(tag, Tag.DEFAULT_MAX_DEPTH);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Tag<?>> T readTag(DataInputStream in, Class<T> type) throws IOException {
		byte b = in.readByte();
		if (b == EndTag.ID) {
			return type.isInstance(EndTag.INSTANCE) ? (T) EndTag.INSTANCE : null;
		}
		PushbackInputStream buffered = new PushbackInputStream(in);
		buffered.unread(b);
		return (T) new NBTInputStream(buffered).readRawTag(Tag.DEFAULT_MAX_DEPTH);
	}
	
	public static String readString(DataInputStream in, Charset charset) throws IOException {
		int length = readVarInt(in);

	    if (length == -1) {
	        throw new IOException("Premature end of stream.");
	    }

	    byte[] b = new byte[length];
	    in.readFully(b);
	    return new String(b, charset);
	}
	
	public static int getStringLength(String string, Charset charset) throws IOException {
	    byte[] bytes = string.getBytes(charset);
	    return getVarIntLength(bytes.length) + bytes.length;
	}
	
	public static void writeString(DataOutputStream out, String string, Charset charset) throws IOException {
	    byte[] bytes = string.getBytes(charset);
	    writeVarInt(out, bytes.length);
	    out.write(bytes);
	}
	
	public static int readVarInt(DataInputStream in) throws IOException {
		int i = 0;
		int j = 0;
		byte b;
		do {
			b = in.readByte();
			i |= (b & 127) << j++ * 7;
			if (j > 5) {
				throw new RuntimeException("VarInt too big");
			}
		} while ((b & 128) == 128);
		return i;
	}
	
	public static void writeVarInt(DataOutputStream out, int value) throws IOException {
		while ((value & -128) != 0) {
			out.writeByte(value & 127 | 128);
			value >>>= 7;
		}
		out.writeByte(value);
	}
	
	public static int getVarIntLength(int value) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(buffer);
	    writeVarInt(out, value);
	    return buffer.toByteArray().length;
	}
	
	public static long readVarLong(DataInputStream in) throws IOException {
	    int numRead = 0;
	    long result = 0;
	    byte read;
	    do {
	        read = in.readByte();
	        long value = (read & 0b01111111);
	        result |= (value << (7 * numRead));

	        numRead++;
	        if (numRead > 10) {
	            throw new RuntimeException("VarLong is too big");
	        }
	    } while ((read & 0b10000000) != 0);

	    return result;
	}
	
	public static void writeVarLong(DataOutputStream out, long value) throws IOException {
	    do {
	        byte temp = (byte)(value & 0b01111111);
	        // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
	        value >>>= 7;
	        if (value != 0) {
	            temp |= 0b10000000;
	        }
	        out.writeByte(temp);
	    } while (value != 0);
	}

	public static Component readComponent(DataInputStream in) throws IOException {
		// Do not use CompoundTag, as Mojang serializes a plaintext component as just a single StringTag
		Tag<?> tag = readTag(in, Tag.class);
		if (tag == null || tag instanceof EndTag) {
			throw new IllegalArgumentException("Got end-tag when trying to read Component");
		}
		JsonElement json = NbtComponentSerializer.tagComponentToJson(tag);
		return GsonComponentSerializer.gson().deserializeFromTree(json);
	}

	public static void writeComponent(DataOutputStream out, Component component) throws IOException {
		JsonElement json = GsonComponentSerializer.gson().serializeToTree(component);
		Tag<?> tag = NbtComponentSerializer.jsonComponentToTag(json);
		writeTag(out, tag);
	}

	public static void writeChunkPosition(DataOutputStream out, ChunkPosition chunkPosition) throws IOException {
		long l = (long) chunkPosition.getChunkX() & 4294967295L | ((long) chunkPosition.getChunkZ() & 4294967295L) << 32;
		out.writeLong(l);
	}

	public static void consumeHashedStack(DataInputStream in) throws IOException {
		if (in.readBoolean()) {
			DataTypeIO.readVarInt(in);
			DataTypeIO.readVarInt(in);
			int addedSize = DataTypeIO.readVarInt(in);
			for (int i = 0; i < addedSize; i++) {
				DataTypeIO.readVarInt(in);
				in.readInt();
			}
			int removedSize = DataTypeIO.readVarInt(in);
			for (int i = 0; i < removedSize; i++) {
				DataTypeIO.readVarInt(in);
			}
		}
	}

}
