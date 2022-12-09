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

import com.loohp.limbo.inventory.ItemStack;
import com.loohp.limbo.location.BlockFace;
import com.loohp.limbo.location.MovingObjectPositionBlock;
import com.loohp.limbo.location.Vector;
import com.loohp.limbo.registry.Registry;
import com.loohp.limbo.world.BlockPosition;
import net.kyori.adventure.key.Key;
import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.io.NBTOutputStream;
import net.querz.nbt.tag.CompoundTag;
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
import java.util.UUID;

public class DataTypeIO {

	public static void writeItemStack(DataOutputStream out, ItemStack itemstack) throws IOException {
		if (itemstack == null || itemstack.isSimilar(ItemStack.AIR) || itemstack.amount() == 0) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			writeVarInt(out, Registry.ITEM_REGISTRY.getId(itemstack.type()));
			out.writeByte(itemstack.amount());
			writeCompoundTag(out, itemstack.nbt());
		}
	}

	public static ItemStack readItemStack(DataInputStream in) throws IOException {
		if (!in.readBoolean()) {
			return ItemStack.AIR;
		} else {
			Key key = Registry.ITEM_REGISTRY.fromId(readVarInt(in));
			byte amount = in.readByte();
			CompoundTag nbt = readCompoundTag(in);
			return new ItemStack(key, amount, nbt);
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
	}

	public static MovingObjectPositionBlock readBlockHitResult(DataInputStream in) throws IOException {
		BlockPosition blockposition = readBlockPosition(in);
		BlockFace direction = BlockFace.values()[readVarInt(in)];
		float f = in.readFloat();
		float f1 = in.readFloat();
		float f2 = in.readFloat();
		boolean flag = in.readBoolean();

		return new MovingObjectPositionBlock(new Vector((double) blockposition.getX() + (double) f, (double) blockposition.getY() + (double) f1, (double) blockposition.getZ() + (double) f2), direction, blockposition, flag);
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
	
	public static void writeCompoundTag(DataOutputStream out, CompoundTag tag) throws IOException {
		if (tag == null) {
			out.writeByte(0);
		} else {
			new NBTOutputStream(out).writeTag(tag, Tag.DEFAULT_MAX_DEPTH);
		}
	}

	public static CompoundTag readCompoundTag(DataInputStream in) throws IOException {
		byte b = in.readByte();
		if (b == 0) {
			return null;
		}
		PushbackInputStream buffered = new PushbackInputStream(in);
		buffered.unread(b);
		return (CompoundTag) new NBTInputStream(buffered).readTag(Tag.DEFAULT_MAX_DEPTH).getTag();
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
	    int numRead = 0;
	    int result = 0;
	    byte read;
	    do {
	        read = in.readByte();
	        int value = (read & 0b01111111);
	        result |= (value << (7 * numRead));

	        numRead++;
	        if (numRead > 5) {
	            throw new RuntimeException("VarInt is too big");
	        }
	    } while ((read & 0b10000000) != 0);

	    return result;
	}
	
	public static void writeVarInt(DataOutputStream out, int value) throws IOException {
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
	
	public static int getVarIntLength(int value) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(buffer);
	    do {
	        byte temp = (byte)(value & 0b01111111);
	        // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
	        value >>>= 7;
	        if (value != 0) {
	            temp |= 0b10000000;
	        }
	        out.writeByte(temp);
	    } while (value != 0);
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

}
