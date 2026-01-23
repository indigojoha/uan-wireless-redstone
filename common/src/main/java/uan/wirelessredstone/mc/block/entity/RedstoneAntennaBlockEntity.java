package uan.wirelessredstone.mc.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import uan.wirelessredstone.mc.WirelessRedstoneMod;
import uan.wirelessredstone.mc.blockentitytypes.WirelessRedstoneModBlockEntityTypes;

public class RedstoneAntennaBlockEntity extends BlockEntity {

    private static final String KEY_LINK_X = "linkX";
    private static final String KEY_LINK_Y = "linkY";
    private static final String KEY_LINK_Z = "linkZ";
    private static final String KEY_REMOTE_POWER = "remotePower";
    private static final String KEY_OUTPUT_POWER = "outputPower";
    private static final String KEY_IS_TRANSMITTER = "isTransmitter";

    private static final ResourceLocation ANTENNA_WEAK_ID = ResourceLocation.fromNamespaceAndPath(WirelessRedstoneMod.MOD_ID, "redstone_antenna_weak");
    private static final ResourceLocation ANTENNA_STRONG_ID = ResourceLocation.fromNamespaceAndPath(WirelessRedstoneMod.MOD_ID, "redstone_antenna_strong");

    @Nullable private BlockPos linkedPos;
    private int remotePower = 0;
    private int outputPower = 0;
    private boolean wasPowered = false;
    private boolean isTransmitter = false;

    public RedstoneAntennaBlockEntity(BlockPos pos, BlockState state) {
        super(WirelessRedstoneModBlockEntityTypes.REDSTONE_ANTENNA.get(), pos, state);
    }

    // ---------------------------------------
    // Linking
    // ---------------------------------------

    public void setLinkedPos(@Nullable BlockPos linkedPos, boolean transmitter) {
        boolean clearing = (linkedPos == null && this.linkedPos != null);
        this.linkedPos = linkedPos;
        this.isTransmitter = transmitter;

        if (clearing) clearRemoteAndOutput(); // unlink → stop powering
        setChanged();
    }

    @Nullable public BlockPos getLinkedPos() {
        return linkedPos;
    }

    // clear remote+output power and updates neighbors
    private void clearRemoteAndOutput() {
        if (remotePower == 0 && outputPower == 0) return;

        remotePower = 0;
        outputPower = 0;

        if (level != null && !level.isClientSide)
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());

        setChanged();
    }

    // ---------------------------------------
    // Data Persistence
    // ---------------------------------------

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        if (linkedPos != null) {
            nbt.putInt(KEY_LINK_X, linkedPos.getX());
            nbt.putInt(KEY_LINK_Y, linkedPos.getY());
            nbt.putInt(KEY_LINK_Z, linkedPos.getZ());
        }

        nbt.putInt(KEY_REMOTE_POWER, remotePower);
        nbt.putInt(KEY_OUTPUT_POWER, outputPower);
        nbt.putBoolean(KEY_IS_TRANSMITTER, isTransmitter);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        if (nbt.contains(KEY_LINK_X)) {
            linkedPos = new BlockPos(
                    nbt.getInt(KEY_LINK_X),
                    nbt.getInt(KEY_LINK_Y),
                    nbt.getInt(KEY_LINK_Z));
        } else linkedPos = null;

        remotePower = nbt.getInt(KEY_REMOTE_POWER);
        outputPower = nbt.getInt(KEY_OUTPUT_POWER);
        isTransmitter = nbt.getBoolean(KEY_IS_TRANSMITTER);
    }

    // ---------------------------------------
    // Redstone Communication Logic
    // ---------------------------------------

    public static void tick(Level level, BlockPos pos, BlockState state, RedstoneAntennaBlockEntity antenna) {
        if (level.isClientSide) return;

        int localPower = level.getBestNeighborSignal(pos);
        boolean powered = localPower > 0;

        if (antenna.wasPowered != powered) {
            antenna.wasPowered = powered;
        }

        if (antenna.isTransmitter)
            antenna.shareSignalWithLinked(level, localPower);
    }

    private void shareSignalWithLinked(Level level, int power) {
        if (linkedPos == null) return;

        BlockState st = level.getBlockState(linkedPos);

        if (!isAntennaBlock(st)) {      // if the link is invalid, remove + clear output
            setLinkedPos(null, false);
            return;
        }

        BlockEntity be = level.getBlockEntity(linkedPos);
        if (be instanceof RedstoneAntennaBlockEntity target) {
            target.receiveRemoteSignal(power);
        }
    }

    private boolean isAntennaBlock(BlockState state) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return ANTENNA_WEAK_ID.equals(id) || ANTENNA_STRONG_ID.equals(id);
    }

    // receive and output signal
    public void receiveRemoteSignal(int value) {
        remotePower = Math.max(0, Math.min(15, value));

        if (remotePower != outputPower) {
            outputPower = remotePower;

            if (level != null && !level.isClientSide)
                level.updateNeighborsAt(worldPosition, getBlockState().getBlock());

            setChanged();
        }
    }

    public int getOutputPower() {
        return outputPower;
    }

    public void onRemoved() {
        clearRemoteAndOutput();
    }
}
