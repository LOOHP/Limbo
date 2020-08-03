package com.loohp.limbo.Server.Packets;

import java.util.Map;

public class Packet {

	private static Map<Integer, Class<? extends Packet>> HandshakeIn;

	private static Map<Integer, Class<? extends Packet>> StatusIn;
	private static Map<Class<? extends Packet>, Integer> StatusOut;

	private static Map<Integer, Class<? extends Packet>> LoginIn;
	private static Map<Class<? extends Packet>, Integer> LoginOut;

	private static Map<Integer, Class<? extends Packet>> PlayIn;
	private static Map<Class<? extends Packet>, Integer> PlayOut;

	public static Map<Integer, Class<? extends Packet>> getHandshakeIn() {
		return HandshakeIn;
	}

	public static void setHandshakeIn(Map<Integer, Class<? extends Packet>> handshakeIn) {
		HandshakeIn = handshakeIn;
	}

	public static Map<Integer, Class<? extends Packet>> getStatusIn() {
		return StatusIn;
	}

	public static void setStatusIn(Map<Integer, Class<? extends Packet>> statusIn) {
		StatusIn = statusIn;
	}

	public static Map<Class<? extends Packet>, Integer> getStatusOut() {
		return StatusOut;
	}

	public static void setStatusOut(Map<Class<? extends Packet>, Integer> statusOut) {
		StatusOut = statusOut;
	}

	public static Map<Integer, Class<? extends Packet>> getLoginIn() {
		return LoginIn;
	}

	public static void setLoginIn(Map<Integer, Class<? extends Packet>> loginIn) {
		LoginIn = loginIn;
	}

	public static Map<Class<? extends Packet>, Integer> getLoginOut() {
		return LoginOut;
	}

	public static void setLoginOut(Map<Class<? extends Packet>, Integer> loginOut) {
		LoginOut = loginOut;
	}

	public static Map<Integer, Class<? extends Packet>> getPlayIn() {
		return PlayIn;
	}

	public static void setPlayIn(Map<Integer, Class<? extends Packet>> playIn) {
		PlayIn = playIn;
	}

	public static Map<Class<? extends Packet>, Integer> getPlayOut() {
		return PlayOut;
	}

	public static void setPlayOut(Map<Class<? extends Packet>, Integer> playOut) {
		PlayOut = playOut;
	}

	//===========================================

	public Packet() {

	}

}
