package com.loohp.limbo.network;

import com.loohp.limbo.network.protocol.packets.PacketIn;
import com.loohp.limbo.network.protocol.packets.PacketOut;
import com.loohp.limbo.utils.DataTypeIO;
import com.loohp.limbo.utils.NamespacedKey;
import com.loohp.limbo.utils.Pair;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Channel implements AutoCloseable {

    private final List<Pair<NamespacedKey, ChannelPacketHandler>> handlers;
    private final AtomicBoolean valid;
    protected final DataInputStream input;
    protected final DataOutputStream output;

    public Channel(DataInputStream input, DataOutputStream output) {
        this.input = input;
        this.output = output;
        this.handlers = new CopyOnWriteArrayList<>();
        this.valid = new AtomicBoolean(true);
    }

    private void ensureOpen() {
        if (!valid.get()) {
            throw new IllegalStateException("Channel already closed!");
        }
    }

    public void addHandlerBefore(NamespacedKey key, ChannelPacketHandler handler) {
        handlers.add(0, new Pair<>(key, handler));
    }

    public void addHandlerAfter(NamespacedKey key, ChannelPacketHandler handler) {
        handlers.add(new Pair<>(key, handler));
    }

    public void removeHandler(NamespacedKey key) {
        handlers.removeIf(each -> each.getFirst().equals(key));
    }

    protected PacketIn readPacket() throws Exception {
        return readPacket(-1);
    }

    protected PacketIn readPacket(int size) throws IOException {
        PacketIn packet = null;
        do {
            ensureOpen();
            size = size < 0 ? DataTypeIO.readVarInt(input) : size;
            int packetId = DataTypeIO.readVarInt(input);
            ChannelPacketRead read = new ChannelPacketRead(size, packetId, input);
            for (Pair<NamespacedKey, ChannelPacketHandler> pair : handlers) {
                read = pair.getSecond().read(read);
                if (read == null) {
                    packet = null;
                    break;
                }
                packet = read.getReadPacket();
            }
            size = -1;
        } while (packet == null);
        return packet;
    }

    protected boolean writePacket(PacketOut packet) throws IOException {
        ensureOpen();
        ChannelPacketWrite write = new ChannelPacketWrite(packet);
        for (Pair<NamespacedKey, ChannelPacketHandler> pair : handlers) {
            write = pair.getSecond().write(write);
            if (write == null) {
                return false;
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
