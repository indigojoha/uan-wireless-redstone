package uan.wirelessredstone.mc.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import uan.wirelessredstone.mc.block.RedstoneAntennaBlock;
import uan.wirelessredstone.mc.block.entity.RedstoneAntennaBlockEntity;

public class RedstoneAntennaLinkerItem extends Item {
    public static final int WEAK_POWER = 8;
    public static final int STRONG_POWER = 16;

    private static final String KEY_POS_X = "iwrs_posX";
    private static final String KEY_POS_Y = "iwrs_posY";
    private static final String KEY_POS_Z = "iwrs_posZ";
    private static final String KEY_WORLD = "iwrs_world";
    private static final String KEY_VALUE = "iwrs_value";
    private static final String KEY_IS_TRANSMITTER = "iwrs_isTransmitter";
    private static final String KEY_HAS_STORED = "iwrs_hasStored";

    public RedstoneAntennaLinkerItem(Properties settings) {
        super(settings);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        Level world = ctx.getLevel();
        if (world.isClientSide()) return InteractionResult.SUCCESS;

        BlockPos pos = ctx.getClickedPos();
        BlockState state = world.getBlockState(pos);
        ItemStack stack = ctx.getItemInHand();

        // check if clicked block is antenna
        int extraValue = 0;
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());

        if (blockId.equals(RedstoneAntennaBlock.WEAK_SIGNATURE)) extraValue = WEAK_POWER;
        else if (blockId.equals(RedstoneAntennaBlock.STRONG_SIGNATURE)) extraValue = STRONG_POWER;
        else return InteractionResult.PASS;  // not an antenna

        CompoundTag tag = getOrCreateLinkerTag(stack);

        // if no stored pos then check unlink condition OR store
        if (!tag.getBoolean(KEY_HAS_STORED)) {
            // if clicked antenna already linked then unlink
            if (world.getBlockEntity(pos) instanceof RedstoneAntennaBlockEntity be && be.getLinkedPos() != null) {
                BlockPos linked = be.getLinkedPos();
                be.setLinkedPos(null, false);

                if (world.getBlockEntity(linked) instanceof RedstoneAntennaBlockEntity beOther) {
                    beOther.setLinkedPos(null, false);
                }

                if (ctx.getPlayer() != null) {
                    // Renamed: Text.literal -> Component.literal
                    ctx.getPlayer().sendSystemMessage(Component.literal("Antennas unlinked."));
                }
                return InteractionResult.SUCCESS;
            }

            // store first antenna
            storeBlock(stack, pos, world, extraValue, true);
            if (ctx.getPlayer() != null) {
                ctx.getPlayer().sendSystemMessage(Component.literal("First antenna selected. Distance budget: " + (extraValue + WEAK_POWER) + " or " + (extraValue + STRONG_POWER)));
            }
            return InteractionResult.SUCCESS;
        }

        // has stored pos -> use second antenna
        BlockPos oldPos = new BlockPos(
                tag.getInt(KEY_POS_X),
                tag.getInt(KEY_POS_Y),
                tag.getInt(KEY_POS_Z)
        );
        String worldKey = tag.getString(KEY_WORLD);
        int storedValue = tag.getInt(KEY_VALUE);

        // world mismatch -> cancel
        if (!world.dimension().location().toString().equals(worldKey)) {
            if (ctx.getPlayer() != null) {
                ctx.getPlayer().sendSystemMessage(Component.literal("You can't connect antennas across dimensions!"));
            }
            return InteractionResult.SUCCESS;
        }

        // clicked same block -> clear
        if (oldPos.equals(pos)) {
            clearStored(stack);
            if (ctx.getPlayer() != null) {
                ctx.getPlayer().sendSystemMessage(Component.literal("Selection cleared."));
            }
            return InteractionResult.SUCCESS;
        }

        // add signal power
        storedValue += extraValue;

        // distance check (Calculation logic unchanged)
        int dist = Math.max(Math.max(Math.abs(oldPos.getX() - pos.getX()),
                        + Math.abs(oldPos.getY() - pos.getY())),
                + Math.abs(oldPos.getZ() - pos.getZ()))
                + 1;

        if (dist > storedValue) {
            if (ctx.getPlayer() != null) {
                ctx.getPlayer().sendSystemMessage(Component.literal("Too far to link! - d=" + dist + "/" + storedValue + "."));
            }
            return InteractionResult.SUCCESS;
        }

        // link antennas
        link(world, oldPos, pos);
        clearStored(stack);
        if (ctx.getPlayer() != null) {
            ctx.getPlayer().sendSystemMessage(Component.literal("Antennas linked! - d=" + dist + "/" + storedValue + "."));
        }

        return InteractionResult.SUCCESS;
    }

    /**
     * Get (or create) the NBT tag we use inside CUSTOM_DATA.
     * Uses modern DataComponent API.
     */
    private static CompoundTag getOrCreateLinkerTag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
    }

    /**
     * Save our tag back into the CUSTOM_DATA component.
     * Uses modern DataComponent API.
     */
    private static void saveLinkerTag(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
    }

    // Renamed: World -> Level
    private void storeBlock(ItemStack stack, BlockPos pos, Level world, int value, boolean transmitter) {
        CompoundTag tag = getOrCreateLinkerTag(stack);
        tag.putInt(KEY_POS_X, pos.getX());
        tag.putInt(KEY_POS_Y, pos.getY());
        tag.putInt(KEY_POS_Z, pos.getZ());
        tag.putString(KEY_WORLD, world.dimension().location().toString());
        tag.putInt(KEY_VALUE, value);
        tag.putBoolean(KEY_IS_TRANSMITTER, transmitter);
        tag.putBoolean(KEY_HAS_STORED, true);
        saveLinkerTag(stack, tag);
    }

    private void clearStored(ItemStack stack) {
        CompoundTag tag = getOrCreateLinkerTag(stack);
        tag.remove(KEY_POS_X);
        tag.remove(KEY_POS_Y);
        tag.remove(KEY_POS_Z);
        tag.remove(KEY_WORLD);
        tag.remove(KEY_VALUE);
        tag.putBoolean(KEY_HAS_STORED, false);
        saveLinkerTag(stack, tag);
    }

    // writes link data to both antennas' block entities
    private void link(Level world, BlockPos a, BlockPos b) {
        if (world.getBlockEntity(a) instanceof RedstoneAntennaBlockEntity beA) {
            beA.setLinkedPos(b, true);
        }

        if (world.getBlockEntity(b) instanceof RedstoneAntennaBlockEntity beB) {
            beB.setLinkedPos(a, false);
        }
    }
}