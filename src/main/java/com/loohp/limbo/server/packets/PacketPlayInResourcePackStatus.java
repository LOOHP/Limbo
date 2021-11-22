package com.loohp.limbo.server.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.loohp.limbo.utils.DataTypeIO;

public class PacketPlayInResourcePackStatus extends PacketIn {
	

	public enum EnumResourcePackStatus {
		SUCCESS,
		DECLINED,
		FAILED,
		ACCEPTED;
	}
	
	private int loaded;
	
	public PacketPlayInResourcePackStatus(int loaded) {
		this.loaded = loaded;
	}
	
	public PacketPlayInResourcePackStatus(DataInputStream in) throws IOException {
		this(DataTypeIO.readVarInt(in));
	}

	public EnumResourcePackStatus getLoadedValue() {
		switch (loaded) {
			case 0: return EnumResourcePackStatus.SUCCESS;
			case 1: return EnumResourcePackStatus.DECLINED;
			case 2: return EnumResourcePackStatus.FAILED;
			case 3: return EnumResourcePackStatus.ACCEPTED;
			default: return EnumResourcePackStatus.FAILED;
		}
	}
	
	public boolean isLoadedValue(EnumResourcePackStatus status) {
		return getLoadedValue() == status;
	}

}
