package com.loohp.limbo.network;

import com.loohp.limbo.network.protocol.packets.Packet;
import com.loohp.limbo.network.protocol.packets.PacketIn;
import com.loohp.limbo.network.protocol.packets.PacketOut;
import com.loohp.limbo.utils.DataTypeIO;
import com.loohp.limbo.utils.NamespacedKey;
import com.loohp.limbo.utils.Pair;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class Channel implements AutoCloseable {

    private final ClientConnection clientConnection;
    private final List<Pair<NamespacedKey, ChannelPacketHandler>> handlers;
    private final AtomicBoolean valid;
    protected DataOutputStream output;
    protected DataInputStream input;

    public Channel(ClientConnection clientConnection, DataOutputStream output, DataInputStream input) {
        this.clientConnection = clientConnection;
        this.output = output;
        this.input = input;
        this.handlers = new CopyOnWriteArrayList<>();
        this.valid = new AtomicBoolean(true);
    }

    private void ensureOpen() {
        if (!valid.get()) {
            throw new IllegalStateException("Channel already closed!");
        }
    }

    public void addBefore(NamespacedKey key, ChannelPacketHandler handler) {
        handlers.add(0, new Pair<>(key, handler));
    }

    public void addAfter(NamespacedKey key, ChannelPacketHandler handler) {
        handlers.add(new Pair<>(key, handler));
    }

    public void remove(NamespacedKey key) {
        handlers.removeIf(each -> each.getFirst().equals(key));
    }

    protected PacketIn readPacket() throws Exception {
        return readPacket(-1);
    }

    protected PacketIn readPacket(int size) throws Exception {
        PacketIn packet = null;
        do {
            ensureOpen();
            size = size < 0 ? DataTypeIO.readVarInt(input) : size;
            int packetId = DataTypeIO.readVarInt(input);
            Class<? extends PacketIn> packetType;
            switch (clientConnection.getClientState()) {
                case HANDSHAKE:
                    packetType = Packet.getHandshakeIn().get(packetId);
                    break;
                case STATUS:
                    packetType = Packet.getStatusIn().get(packetId);
                    break;
                case LOGIN:
                    packetType = Packet.getLoginIn().get(packetId);
                    break;
                case PLAY:
                    packetType = Packet.getPlayIn().get(packetId);
                    break;
                default:
                    throw new IllegalStateException("Illegal ClientState!");
            }
            if (packetType == null) {
                input.skipBytes(size - DataTypeIO.getVarIntLength(packetId));
            } else {
                Constructor<?>[] constructors = packetType.getConstructors();
                Constructor<?> constructor = Stream.of(constructors).filter(each -> each.getParameterCount() > 0 && each.getParameterTypes()[0].equals(DataInputStream.class)).findFirst().orElse(null);
                if (constructor == null) {
                    throw new NoSuchMethodException(packetType + " has no valid constructors!");
                } else if (constructor.getParameterCount() == 1) {
                    packet = (PacketIn) constructor.newInstance(input);
                } else if (constructor.getParameterCount() == 3) {
                    packet = (PacketIn) constructor.newInstance(input, size, packetId);
                } else {
                    throw new NoSuchMethodException(packetType + " has no valid constructors!");
                }
                ChannelPacketRead read = new ChannelPacketRead(packetId, packet);
                for (Pair<NamespacedKey, ChannelPacketHandler> pair : handlers) {
                    read = pair.getSecond().read(read);
                    if (read == null) {
                        packet = null;
                        break;
                    }
                    packet = read.getPacket();
                    if (!packetType.isInstance(packet)) {
                        throw new IllegalStateException("Packet Handler \"" + pair.getFirst() + "\" changed the packet type illegally!");
                    }
                }
            }
            size = -1;
        } while (packet == null);
        return packet;
    }

    protected boolean writePacket(PacketOut packet) throws IOException {
        ensureOpen();
        int packetId;
        switch (clientConnection.getClientState()) {
            case STATUS:
                packetId = Packet.getStatusOut().get(packet.getClass());
                break;
            case LOGIN:
                packetId = Packet.getLoginOut().get(packet.getClass());
                break;
            case PLAY:
                packetId = Packet.getPlayOut().get(packet.getClass());
                break;
            default:
                throw new IllegalStateException("Illegal ClientState!");
        }
        Class<? extends PacketOut> packetType = packet.getClass();
        ChannelPacketWrite write = new ChannelPacketWrite(packet);
        for (Pair<NamespacedKey, ChannelPacketHandler> pair : handlers) {
            write = pair.getSecond().write(write);
            if (write == null) {
                return false;
            }
            if (!packetType.isInstance(write.getPacket())) {
                throw new IllegalStateException("Packet Handler \"" + pair.getFirst() + "\" changed the packet type illegally!");
            }
        }
        packet = write.getPacket();
        byte[] packetByte = packet.serializePacket();
        DataTypeIO.writeVarInt(output, packetByte.length);
        output.write(packetByte);
        output.flush();
        return true;
    }

    @Override
    public synchronized void close() throws Exception {
        if (valid.compareAndSet(false, true)) {
            input.close();
            output.close();
        }
    }

}
