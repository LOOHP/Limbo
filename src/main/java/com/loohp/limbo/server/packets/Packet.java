package com.loohp.limbo.server.packets;

import java.util.Map;

public class Packet {

    private static Map<Integer, Class<? extends PacketIn>> HandshakeIn;

    private static Map<Integer, Class<? extends PacketIn>> StatusIn;
    private static Map<Class<? extends PacketOut>, Integer> StatusOut;

    private static Map<Integer, Class<? extends PacketIn>> LoginIn;
    private static Map<Class<? extends PacketOut>, Integer> LoginOut;

    private static Map<Integer, Class<? extends PacketIn>> PlayIn;
    private static Map<Class<? extends PacketOut>, Integer> PlayOut;

    public static Map<Integer, Class<? extends PacketIn>> getHandshakeIn() {
        return HandshakeIn;
    }

    public static void setHandshakeIn(Map<Integer, Class<? extends PacketIn>> handshakeIn) {
        HandshakeIn = handshakeIn;
    }

    public static Map<Integer, Class<? extends PacketIn>> getStatusIn() {
        return StatusIn;
    }

    public static void setStatusIn(Map<Integer, Class<? extends PacketIn>> statusIn) {
        StatusIn = statusIn;
    }

    public static Map<Class<? extends PacketOut>, Integer> getStatusOut() {
        return StatusOut;
    }

    public static void setStatusOut(Map<Class<? extends PacketOut>, Integer> statusOut) {
        StatusOut = statusOut;
    }

    public static Map<Integer, Class<? extends PacketIn>> getLoginIn() {
        return LoginIn;
    }

    public static void setLoginIn(Map<Integer, Class<? extends PacketIn>> loginIn) {
        LoginIn = loginIn;
    }

    public static Map<Class<? extends PacketOut>, Integer> getLoginOut() {
        return LoginOut;
    }

    public static void setLoginOut(Map<Class<? extends PacketOut>, Integer> loginOut) {
        LoginOut = loginOut;
    }

    public static Map<Integer, Class<? extends PacketIn>> getPlayIn() {
        return PlayIn;
    }

    public static void setPlayIn(Map<Integer, Class<? extends PacketIn>> playIn) {
        PlayIn = playIn;
    }

    public static Map<Class<? extends PacketOut>, Integer> getPlayOut() {
        return PlayOut;
    }

    public static void setPlayOut(Map<Class<? extends PacketOut>, Integer> playOut) {
        PlayOut = playOut;
    }

}
