package com.loohp.limbo.network.protocol.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.loohp.limbo.utils.DataTypeIO;

public class PacketPlayInResourcePackStatus extends PacketIn {
	
	public static enum EnumResourcePackStatus {
		SUCCESS,
		DECLINED,
		FAILED,
		ACCEPTED;
	}
	
	private EnumResourcePackStatus loaded;
	
	public PacketPlayInResourcePackStatus(EnumResourcePackStatus loaded) {
		this.loaded = loaded;
	}
	
	public PacketPlayInResourcePackStatus(DataInputStream in) throws IOException {
		this(toLoadedValue(DataTypeIO.readVarInt(in)));
	}
	
	public EnumResourcePackStatus getLoadedValue() {
		return loaded;
	}

	private static EnumResourcePackStatus toLoadedValue(int value) {
		switch (value) {
			case 0: 
				return EnumResourcePackStatus.SUCCESS;
			case 1: 
				return EnumResourcePackStatus.DECLINED;
			case 2: 
				return EnumResourcePackStatus.FAILED;
			case 3: 
				return EnumResourcePackStatus.ACCEPTED;
			default: 
				return EnumResourcePackStatus.FAILED;
		}
	}

}
