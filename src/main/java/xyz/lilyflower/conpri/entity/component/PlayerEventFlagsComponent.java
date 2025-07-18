package xyz.lilyflower.conpri.entity.component;

import java.util.BitSet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.entity.C2SSelfMessagingComponent;

@SuppressWarnings("UnstableApiUsage")
public class PlayerEventFlagsComponent implements C2SSelfMessagingComponent, AutoSyncedComponent {
    private final PlayerEntity player;
    private BitSet flags = new BitSet(Character.MAX_VALUE + 1);

    public PlayerEventFlagsComponent(PlayerEntity player) {
        this.player = player;
    }

    public void updateFlagState(char index, boolean value) {
        this.flags.set(index, value);
        sendC2SMessage(buf -> {
            buf.writeEnumConstant(MessageType.UPDATE_FLAG);
            buf.writeChar(index);
            buf.writeBoolean(value);
        });
    }

    public boolean getFlag(char index) {
        boolean value = false;
        try {
            value = this.flags.get(index);
        } catch (IndexOutOfBoundsException ignored) {};
        return value;
    }

    public void syncAllFlags() {
        sendC2SMessage(buf -> {
            buf.writeEnumConstant(MessageType.FULL_SYNC);
            buf.writeBitSet(this.flags);
        });
    }

    @Override
    public void handleC2SMessage(@NotNull RegistryByteBuf buffer) {
        MessageType type = buffer.readEnumConstant(MessageType.class);

        switch (type) {
            case UPDATE_FLAG -> {
                char flag = buffer.readChar();
                boolean value = buffer.readBoolean();
                this.flags.set(flag, value);
            }

            case FULL_SYNC -> {
                this.flags = buffer.readBitSet();
            }
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup lookup) {
        this.flags = BitSet.valueOf(tag.getLongArray("eventFlags").orElseGet(() -> new long[]{}));
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup lookup) {
        tag.putLongArray("eventFlags", this.flags.toLongArray());
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == this.player;
    }

    enum MessageType {
        FULL_SYNC,
        UPDATE_FLAG
    }
}
